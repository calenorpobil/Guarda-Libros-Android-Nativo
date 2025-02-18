package com.merlita.guardalibros;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataImporter {

    public static void importFromCSV(Context context, Uri fileUri) {
        new AsyncTask<Void, Void, Integer>() {
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Importando datos...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                int importedRows = 0;
                UsuariosSQLiteHelper dbHelper =
                        new UsuariosSQLiteHelper(context,
                                "DBUsuarios", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    // Saltar encabezados
                    reader.readLine();

                    String line;
                    db.beginTransaction();

                    while ((line = reader.readLine()) != null) {
                        String[] row = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                        ContentValues values = new ContentValues();
                        values.put("_id", row[0]);
                        values.put("categoria", row[1]);
                        values.put("titulo", row[2]);
                        values.put("autor", row[3]);
                        values.put("idioma", row[4].isEmpty() ? null : row[4]);
                        values.put("fecha_lectura_ini", row[5].isEmpty() ? null : row[5]);
                        values.put("fecha_lectura_fin", row[6].isEmpty() ? null : row[6]);
                        values.put("prestado_a", row[7].isEmpty() ? null : row[7]);
                        values.put("valoracion", row[8].isEmpty() ? null : row[8]);
                        values.put("formato", row[9].isEmpty() ? null : row[9]);
                        values.put("notas", row[10].isEmpty() ? null : row[10]);
                        values.put("finalizado", row[11].equals("1") ? 1 : 0);

                        // Insertar o actualizar
                        long result = db.insertWithOnConflict("bdlibros",
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);

                        if (result != -1) importedRows++;
                    }

                    db.setTransactionSuccessful();
                    db.endTransaction();
                    reader.close();

                } catch (Exception e) {
                    Log.e("ImportError", e.getMessage(), e);
                    return -1;
                }
                return importedRows;
            }

            @Override
            protected void onPostExecute(Integer result) {
                progressDialog.dismiss();
                if (result > 0) {
                    Toast.makeText(context,
                            "Importación exitosa: " + result + " registros",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context,
                            "Error en la importación",
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}