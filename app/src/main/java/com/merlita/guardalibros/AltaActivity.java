package com.merlita.guardalibros;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AltaActivity extends AppCompatActivity {
    EditText etTitulo, etAutor;
    Button bt;
    Intent upIntent;
    Spinner spCategoria, spIdioma, spFormato;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta);

        etTitulo = findViewById(R.id.etTitulo);
        etTitulo.setHint("Título del libro");
        etAutor = findViewById(R.id.etAutor);
        etAutor.setHint("Nombre del autor");
        bt = findViewById(R.id.btnGuardar);

        setupDatePicker(findViewById(R.id.etFechaInicio));
        setupDatePicker(findViewById(R.id.etFechaFin));

        configurarSpinners();
    }


    private void configurarSpinners() {
        spCategoria = findViewById(R.id.spCategoria);
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(
                this, R.array.categorias, android.R.layout.simple_list_item_1);
        spCategoria.setAdapter(adaptador);

// Configurar Spinner de Idioma
        spIdioma = findViewById(R.id.spIdioma);
        adaptador = ArrayAdapter.createFromResource(
                this, R.array.idiomas, android.R.layout.simple_list_item_1);
        spIdioma.setAdapter(adaptador);

// Configurar Spinner de Formato
        spFormato = findViewById(R.id.spFormato);
        adaptador = ArrayAdapter.createFromResource(
                this, R.array.formatos, android.R.layout.simple_list_item_1);
        spFormato.setAdapter(adaptador);

        spCategoria.setSelection(3);
    }

    public void clickVolver(View v){
        Intent i = new Intent();

        // Obtengo referencias a todos los campos
        Spinner spCategoria = findViewById(R.id.spCategoria);
        EditText etTitulo = findViewById(R.id.etTitulo);
        EditText etAutor = findViewById(R.id.etAutor);
        Spinner spIdioma = findViewById(R.id.spIdioma);
        EditText etFechaInicio = findViewById(R.id.etFechaInicio);
        EditText etFechaFin = findViewById(R.id.etFechaFin);
        EditText etPrestado = findViewById(R.id.etPrestado);
        EditText etValoracion = findViewById(R.id.etValoracion);
        Spinner spFormato = findViewById(R.id.spFormato);
        EditText etNotas = findViewById(R.id.etNotas);
        CheckBox cbFinalizado = findViewById(R.id.cbFinalizado);

        // Valido campos obligatorios (según esquema SQL)
        if (spCategoria.getSelectedItem() == null ||
                etTitulo.getText().toString().isEmpty() ||
                etAutor.getText().toString().isEmpty()) {

            Toast.makeText(this, "Complete los campos obligatorios (*)", Toast.LENGTH_SHORT).show();
            /*setResult(RESULT_CANCELED);
            finish();
            return;*/
        }

        try {
            // Convertir fechas a timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Long fechaInicioMillis = null;
            Long fechaFinMillis = null;

            if (!etFechaInicio.getText().toString().isEmpty()) {
                Date fechaInicio = sdf.parse(etFechaInicio.getText().toString());
                if (fechaInicio != null)
                    fechaInicioMillis = fechaInicio.getTime();
            }

            if (!etFechaFin.getText().toString().isEmpty()) {
                Date fechaFin = sdf.parse(etFechaFin.getText().toString());
                if (fechaFin != null) fechaFinMillis = fechaFin.getTime();
            }

            // Convertir valoración a float
            Float valoracion = null;
            if (!etValoracion.getText().toString().isEmpty()) {
                valoracion = Float.parseFloat(etValoracion.getText().toString());
                if (valoracion < 0 || valoracion > 5) {
                    throw new NumberFormatException("Valoración fuera de rango");
                }
            }

            // Preparar todos los datos para enviar
            i.putExtra("CATEGORIA", spCategoria.getSelectedItem().toString());
            i.putExtra("TITULO", etTitulo.getText().toString());
            i.putExtra("AUTOR", etAutor.getText().toString());
            //Sólo se hará si hay un ítem seleccionado, y si no se ponrá un nulo.
            i.putExtra("IDIOMA", spIdioma.getSelectedItem() != null
                    ? spIdioma.getSelectedItem().toString() : null);
            i.putExtra("FECHA_INICIO", fechaInicioMillis != null ? fechaInicioMillis : -1);
            i.putExtra("FECHA_FIN", fechaFinMillis != null ? fechaFinMillis : -1);
            i.putExtra("PRESTADO_A", etPrestado.getText().toString());
            i.putExtra("VALORACION", valoracion != null ? valoracion : -1f);
            i.putExtra("FORMATO", spFormato.getSelectedItem() != null ? spFormato.getSelectedItem().toString() : null);
            i.putExtra("NOTAS", etNotas.getText().toString());
            //Si está marcado, pondrá 1, y si no 0 (SQLite no tiene booleanos).
            i.putExtra("FINALIZADO", cbFinalizado.isChecked() ? 1 : 0);

            setResult(RESULT_OK, i);
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de fecha inválido (dd/MM/yyyy)", Toast.LENGTH_LONG).show();
            //setResult(RESULT_CANCELED);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valoración debe ser entre 0.0 y 5.0", Toast.LENGTH_LONG).show();
            //setResult(RESULT_CANCELED);
        } finally {
            finish();
        }
    }
    private void setupDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, day);
                        editText.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .format(selectedDate.getTime()));
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });
    }


}
