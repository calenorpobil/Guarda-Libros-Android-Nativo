package com.merlita.guardalibros;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class DataExporter {

    public static void exportToCSV(Context context, String fileName) {
        UsuariosSQLiteHelper dbHelper =
                new UsuariosSQLiteHelper(context,
                        "DBUsuarios", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {

            File exportDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            if (!exportDir.exists()) exportDir.mkdirs();

            File file = new File(exportDir, fileName);
            file.createNewFile();

            OutputStream outputStream = context.getContentResolver().
                    openOutputStream(Uri.fromFile(exportDir));
            BufferedWriter reader = new BufferedWriter(new OutputStreamWriter(outputStream));

            CSVWriter csvWriter = new CSVWriter(new FileWriter(file));

            // Escribir encabezados
            String[] headers = {"_id", "categoria", "titulo", "autor", "idioma",
                    "fecha_lectura_ini", "fecha_lectura_fin", "prestado_a",
                    "valoracion", "formato", "notas", "finalizado"};
            csvWriter.writeNext(headers);

            // Consultar datos
            Cursor cursor = db.rawQuery("SELECT * FROM bdlibros", null);
            if (cursor.moveToFirst()) {
                do {
                    String[] row = new String[12];
                    row[0] = cursor.getString(0); // _id
                    row[1] = cursor.getString(1); // categoria
                    row[2] = cursor.getString(2); // titulo
                    row[3] = cursor.getString(3); // autor
                    row[4] = cursor.getString(4); // idioma
                    row[5] = cursor.getString(5); // fecha_ini
                    row[6] = cursor.getString(6); // fecha_fin
                    row[7] = cursor.getString(7); // prestado_a
                    row[8] = cursor.getString(8); // valoracion
                    row[9] = cursor.getString(9); // formato
                    row[10] = cursor.getString(10); // notas
                    row[11] = cursor.getString(11); // finalizado

                    csvWriter.writeNext(row);
                } while (cursor.moveToNext());
            }

            csvWriter.close();
            cursor.close();

            // Compartir archivo
            Uri fileUri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".provider",
                    file);

            Intent shareIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            shareIntent.setType("text/csv");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, "Exportar datos"));

        } catch (Exception e) {
            Log.e("ExportError", e.getMessage(), e);
            Toast.makeText(context, "Error al exportar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}