package com.merlita.guardalibros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {
    EditText etNombre, etEdad;
    Button bt;
//    Intent upIntent;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //upIntent = getParentActivityIntent();
        Bundle upIntent = this.getIntent().getExtras();

        etNombre = findViewById(R.id.etTitulo);
        etEdad = findViewById(R.id.etAutor);
        bt = findViewById(R.id.btVolver);

        

        String nombre = upIntent.getString("NOMBRE");
        String edad = upIntent.getString("EDAD");

        etNombre.setText(nombre);
        etEdad.setText(edad);



    }

    public void clickVolver(View v){
        Intent i = new Intent();
        String nombre = String.valueOf(etNombre.getText());
        String edad = String.valueOf(etEdad.getText());

        if (nombre.equals("") || edad.equals("")) {
            setResult(RESULT_OK);

        }else{
            i.putExtra("NOMBRE", nombre);
            i.putExtra("EDAD", edad);
            setResult(RESULT_OK, i);
        }


        finish();





    }
}
