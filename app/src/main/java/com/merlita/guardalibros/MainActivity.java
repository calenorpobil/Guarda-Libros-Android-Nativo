package com.merlita.guardalibros;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlita.guardalibros.Excepciones.miExcepcion;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        DialogoPersonalizado.CustomDialogListener  {

    RecyclerView vistaRecycler;
    ArrayList<DatosLibros> lista = new ArrayList<DatosLibros>();
    TextView tv;
    Adaptador adaptador;
    Button btAlta;
    EditText et;
    int posicionEdicion;
    DatosLibros auxiliar;
    private final ArrayList<DatosLibros> datosVacio =
            new ArrayList<>();

    private static final int REQUEST_EXPORT_FILE = 1;
    private static final int REQUEST_IMPORT_FILE = 2;



    SQLiteDatabase db;

    Intent resultado = null;

    private void toast(miExcepcion e) {
        Toast.makeText(this, e.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        try(UsuariosSQLiteHelper usdbh =
                new UsuariosSQLiteHelper(this,
                        "DBUsuarios", null, 1);){
            db = usdbh.getWritableDatabase();

            //Crear tabla si existe:
            db.execSQL("DROP TABLE IF EXISTS bdlibros");
            usdbh.onCreate(db);

            datosParaSQL();
            rellenarLista();


            db.close();
        }

        /*
                "INSERT INTO libros (categoria, titulo, autor, idioma, fecha_lectura_ini, fecha_lectura_fin, prestado_a, valoracion, formato, notas) " +
                "VALUES ('Fantasía', 'El nombre del viento', 'Patrick Rothfuss', 'Español', NULL, NULL, NULL, 4.9, 'ePub', 'Narrativa envolvente y personajes complejos');" +
                "" +
                "INSERT INTO libros (categoria, titulo, autor, idioma, fecha_lectura_ini, fecha_lectura_fin, prestado_a, valoracion, formato, notas) " +
                "VALUES ('Ciencia', 'Breves respuestas a las grandes preguntas', 'Stephen Hawking', 'Español', 1680307200000, NULL, 'Carlos Mendoza', 4.5, 'Físico', 'Último libro del famoso físico teórico');" +
                "" +
                "INSERT INTO libros (categoria, titulo, autor, idioma, fecha_lectura_ini, fecha_lectura_fin, prestado_a, valoracion, formato, notas) " +
                "VALUES ('Filosofía', 'El mundo de Sofía', 'Jostein Gaarder', 'Español', 1682899200000, 1683936000000, NULL, 4.4, 'PDF', 'Novela sobre historia de la filosofía');" +
                "" +
                "INSERT INTO libros (categoria, titulo, autor, idioma, fecha_lectura_ini, fecha_lectura_fin, prestado_a, valoracion, formato, notas) " +
                "VALUES ('Autoayuda', 'Hábitos atómicos', 'James Clear', 'Español', 1685577600000, 1686700800000, 'María González', 4.3, 'Digital', 'Métodos prácticos para construir hábitos');" +
                "" +
                "INSERT INTO libros (categoria, titulo, autor, idioma, fecha_lectura_ini, fecha_lectura_fin, prestado_a, valoracion, formato, notas) " +
                "VALUES ('Historia', 'SPQR', 'Mary Beard', NULL, NULL, NULL, NULL, 4.2, 'ePub', 'Historia del Imperio Romano desde sus inicios');" +
                "" +
                "INSERT INTO libros (categoria, titulo, autor, idioma, fecha_lectura_ini, fecha_lectura_fin, prestado_a, valoracion, formato, notas) " +
                "VALUES ('Poesía', 'Veinte poemas de amor y una canción desesperada', 'Pablo Neruda', 'Español', 1688169600000, 1690761600000, NULL, 4.7, 'Físico', 'Clásico de la poesía amorosa en español');" +
                "\n" +
                "INSERT INTO libros (categoria, titulo, autor, idioma, fecha_lectura_ini, fecha_lectura_fin, prestado_a, valoracion, formato, notas) \n" +
                "VALUES ('Finanzas', 'Padre rico, padre pobre', 'Robert T. Kiyosaki', 'Español', 1690848000000, 1693440000000, 'David Torres', 4.0, 'Digital', 'Enfoque alternativo sobre educación financiera');");
*/
        if(lista.isEmpty()){
            auxiliar = new DatosLibros();
            datosVacio.add(auxiliar);
        }
        tv = findViewById(R.id.tvTitulo);
        btAlta = findViewById(R.id.btAlta);
        vistaRecycler = findViewById(R.id.recyclerView);
        adaptador = new Adaptador(this, this, lista);

        vistaRecycler.setLayoutManager(new LinearLayoutManager(this));
        vistaRecycler.setAdapter(adaptador);


        btAlta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                mostrarDialogo();
                //setResult(RESULT_OK, i);
                //finish();
            }
        });
    }

    private void mostrarFormularioAlta()  {
        Intent i = new Intent(MainActivity.this, AltaActivity.class);
        lanzadorAlta.launch(i);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case 121:
                //MENU --> EDITAR
                Intent i = new Intent(this, EditActivity.class);
                posicionEdicion = item.getGroupId();
                DatosLibros libro = lista.get(posicionEdicion);
                i.putExtra("_ID", libro.get_id());
                i.putExtra("CATEGORIA", libro.getCategoria());
                i.putExtra("TITULO", libro.getTitulo());
                i.putExtra("AUTOR", libro.getAutor());
                i.putExtra("IDIOMA", libro.getIdioma());
                i.putExtra("FECHA_INICIO", libro.getFecha_lectura_ini());
                i.putExtra("FECHA_FIN", libro.getFecha_lectura_fin());
                i.putExtra("PRESTADO_A", libro.getPrestado_a());
                i.putExtra("VALORACION", libro.getValoracion());
                i.putExtra("FORMATO", libro.getFormato());
                i.putExtra("NOTAS", libro.getNotas());
                i.putExtra("FINALIZADO", libro.getFinalizado());
                lanzadorEdit.launch(i);
                adaptador.notifyDataSetChanged();
                return true;
            case 122:
                //MENU --> BORRAR
                lista.remove(item.getGroupId());
                adaptador.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void datosParaSQL() {
        if(db!=null){
            try{
                db.execSQL("INSERT INTO bdlibros (categoria, titulo, autor, idioma, fecha_lectura_ini, fecha_lectura_fin, prestado_a, valoracion, formato, notas, finalizado) \n" +
                        "VALUES ('Ficción', 'Cien años de soledad', 'Gabriel García Márquez', 'Español', 1672531200000, 1673740800000, NULL, 4.8, 'Físico', " +
                        "'Obra maestra del realismo mágico latinoamericano', 1);");
                db.execSQL("INSERT INTO bdlibros (categoria, titulo, autor, idioma, fecha_lectura_ini, fecha_lectura_fin, prestado_a, valoracion, formato, notas, finalizado) " +
                        "VALUES ('No ficción', 'Sapiens: De animales a dioses', 'Yuval Noah Harari', 'Inglés', 1677984000000, 1679289600000, NULL, 4.7, 'Digital', 'Fascinante recorrido por la historia humana', 0)");
                db.execSQL("INSERT INTO bdlibros (categoria, titulo, autor, idioma, fecha_lectura_ini, fecha_lectura_fin, prestado_a, valoracion, formato, notas, finalizado) " +
                        "VALUES ('Tecnología', 'Clean Code', 'Robert C. Martin', 'Inglés', 1675987200000, 1677283200000, 'Ana Sánchez', 4.6, 'Físico', 'Fundamental para mejores prácticas de programación', 1);");
            } catch (SQLiteConstraintException e) {
                Toast.makeText(this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void rellenarLista() {
        Cursor c = db.rawQuery("select * from bdlibros;", null);

        while (c.moveToNext()) {
            int index = c.getColumnIndex("_id");
            int id = c.getInt(index);
            index = c.getColumnIndex("titulo");
            String titulo = c.getString(index);
            index = c.getColumnIndex("categoria");
            String categoria = c.getString(index);
            index = c.getColumnIndex("autor");
            String autor = c.getString(index);
            index = c.getColumnIndex("idioma");
            String idioma = c.getString(index);
            index = c.getColumnIndex("fecha_lectura_ini");
            Long fecha_lectura_ini = c.getLong(index);
            index = c.getColumnIndex("fecha_lectura_fin");
            Long fecha_lectura_fin = c.getLong(index);
            index = c.getColumnIndex("prestado_a");
            String prestado_a = c.getString(index);
            index = c.getColumnIndex("valoracion");
            Float valoracion = c.getFloat(index);
            index = c.getColumnIndex("formato");
            String formato = c.getString(index);
            index = c.getColumnIndex("notas");
            String notas = c.getString(index);
            index = c.getColumnIndex("finalizado");
            int finalizado = c.getInt(index);
            lista.add(new DatosLibros(id, categoria, titulo, autor, idioma,
                    fecha_lectura_fin, fecha_lectura_ini, prestado_a,
                    valoracion, formato, notas, finalizado));
        }
    }




    //MENU CONTEXTUAL
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item){
        switch(item.getItemId())
        {
            case 121:
                //MENU --> EDITAR
                Intent i = new Intent(this, EditActivity.class);
                posicionEdicion = item.getGroupId();
                DatosLibros libro = lista.get(posicionEdicion);
                i.putExtra("_ID", libro.get_id());
                i.putExtra("CATEGORIA", libro.getCategoria());
                i.putExtra("TITULO", libro.getTitulo());
                i.putExtra("AUTOR", libro.getAutor());
                i.putExtra("IDIOMA", libro.getIdioma());
                i.putExtra("FECHA_INICIO", libro.getFecha_lectura_ini());
                i.putExtra("FECHA_FIN", libro.getFecha_lectura_fin());
                i.putExtra("PRESTADO_A", libro.getPrestado_a());
                i.putExtra("VALORACION", libro.getValoracion());
                i.putExtra("FORMATO", libro.getFormato());
                i.putExtra("NOTAS", libro.getNotas());
                i.putExtra("FINALIZADO", libro.getFinalizado());
                lanzadorEdit.launch(i);
                adaptador.notifyDataSetChanged();
                return true;
            case 122:
                //MENU --> BORRAR
                lista.remove(item.getGroupId());
                adaptador.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }



    @Override
    public void onClick(View view) {
        tv.setText(lista.get(
                vistaRecycler.getChildAdapterPosition(view)).getTitulo());
    }

    //RECOGER EDIT ACTIVITY
    ActivityResultLauncher<Intent> lanzadorEdit = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult resultado)
                {
                    if(resultado.getResultCode()==RESULT_OK) {
                        Intent data = resultado.getData();
                        assert data != null;
                        DatosLibros editLibro = new DatosLibros(
                                data.getIntExtra("ID", -1),
                                data.getStringExtra("CATEGORIA"),
                                data.getStringExtra("TITULO"),
                                data.getStringExtra("AUTOR"),
                                data.getStringExtra("IDIOMA"),
                                data.getLongExtra("FECHA_INICIO", -1),
                                data.getLongExtra("FECHA_FIN", -1),
                                data.getStringExtra("PRESTADO_A"),
                                data.getFloatExtra("VALORACION", -1f),
                                data.getStringExtra("FORMATO"),
                                data.getStringExtra("NOTAS"),
                                data.getIntExtra("FINALIZADO", 0)
                        );

                        DatosLibros antig = lista.get(editLibro.get_id());
                        lista.set(editLibro.get_id(), editLibro);
                        editarSQL(editLibro);
                        antig = editLibro;
                        // Editar el libro

                        editarSQL(editLibro);
                        adaptador.notifyDataSetChanged();
                    }else{
                        //SIN DATOS
                    }
                }
            }
    );


    //RECOGER ALTA ACTIVITY
    ActivityResultLauncher<Intent>
            lanzadorAlta = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult resultado) {
                    if(resultado.getResultCode()==RESULT_OK) {

                        Intent data = resultado.getData();
                        assert data != null;
                        DatosLibros nuevoLibro = new DatosLibros(
                                data.getIntExtra("ID", -1),
                                data.getStringExtra("CATEGORIA"),
                                data.getStringExtra("TITULO"),
                                data.getStringExtra("AUTOR"),
                                data.getStringExtra("IDIOMA"),
                                data.getLongExtra("FECHA_INICIO", -1),
                                data.getLongExtra("FECHA_FIN", -1),
                                data.getStringExtra("PRESTADO_A"),
                                data.getFloatExtra("VALORACION", -1f),
                                data.getStringExtra("FORMATO"),
                                data.getStringExtra("NOTAS"),
                                data.getIntExtra("FINALIZADO", 0)
                        );

                        // Insertar en BD
                        int id = insertarSQL(nuevoLibro);
                        nuevoLibro.set_id(id);
                        lista.add(nuevoLibro);

                        adaptador.notifyDataSetChanged();
                    }else{
                        //SIN DATOS
                    }
                }
            });



    @Override
    public void onAltaLibroClick() {
        // Aquí va el código para el alta de libro
        mostrarFormularioAlta();
    }

    @Override
    public void onAcercaDeClick() {
        // Mostrar información de la app
    }

    @Override
    public void onOrdenarClick() {
        // Mostrar opciones de ordenación
    }

    @Override
    public void onImportarClick() {
        importData();
    }

    @Override
    public void onExportarClick() {
        exportData();
    }

    // Método para mostrar el diálogo
    private void mostrarDialogo() {
        DialogoPersonalizado dialog = new DialogoPersonalizado();
        dialog.show(getSupportFragmentManager(), "CustomDialog");
    }

    private int insertarSQL(DatosLibros libro){
        int id=-1;
        try(UsuariosSQLiteHelper usdbh =
                    new UsuariosSQLiteHelper(this,
                            "DBUsuarios", null, 1);){
            db = usdbh.getWritableDatabase();

            Date c = new Date(System.currentTimeMillis());
            long fechaSQLPrueba = c.getTime();

            db.execSQL("INSERT INTO bdlibros (categoria, titulo, autor, idioma, fecha_lectura_ini" +
                    ", fecha_lectura_fin, prestado_a, valoracion, formato, notas, finalizado) " +
                    "VALUES ('"+libro.getCategoria()+"', '"+
                    libro.getTitulo()+"', '"+libro.getAutor()+"', '"+libro.getIdioma()+"', '" +
                    libro.getFecha_lectura_ini()+"', '"+libro.getFecha_lectura_fin()+"', '"+
                    libro.getPrestado_a()+"', '"+libro.getValoracion()+"', '" +
                    libro.getFormato()+"', '"+libro.getNotas()+"', "+libro.getFinalizado()+");");

            String sql = ("SELECT _ID FROM BDLIBROS WHERE TITULO = "+libro.getTitulo());
            Cursor cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                id = cursor.getInt(0);
            }


            db.close();
        }
        return id;
    }

    private void exportData() {
        Date c = new Date(System.currentTimeMillis());
        String fileName = "libros_" + new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(c) + ".csv";
        DataExporter.exportToCSV(this, fileName);
    }

    // Para importar
    private void importData() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, REQUEST_IMPORT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMPORT_FILE && data != null) {
                Uri uri = data.getData();
                DataImporter.importFromCSV(this, uri);
            }
        }
    }



    private void editarSQL(DatosLibros libro){
        try(UsuariosSQLiteHelper usdbh =
                    new UsuariosSQLiteHelper(this,
                            "DBUsuarios", null, 1);){
            db = usdbh.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put("categoria", libro.getCategoria());
            values.put("titulo", libro.getTitulo());
            values.put("autor", libro.getAutor());
            values.put("idioma", libro.getIdioma());
            values.put("fecha_lectura_ini", libro.getFecha_lectura_ini());
            values.put("fecha_lectura_fin", libro.getFecha_lectura_fin());
            values.put("prestado_a", libro.getPrestado_a());
            values.put("valoracion", libro.getValoracion());
            values.put("formato", libro.getFormato());
            values.put("notas", libro.getNotas());
            values.put("finalizado", libro.getFinalizado() ? 1 : 0);

            // Actualizar usando el ID como condición
            db.update("bdlibros",
                    values,
                    "_id = ?",
                    new String[]{String.valueOf(libro.get_id())});
            adaptador.notifyDataSetChanged();


            db.close();
        }
    }


}