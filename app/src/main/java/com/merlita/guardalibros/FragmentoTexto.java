package com.example.proyectobotonesidiomahora;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class Fragmento2Botones extends DialogFragment {



    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder ventana = new AlertDialog.Builder(getActivity());
        ventana.setTitle("DIALOGO CON FRAGMENTO");
        ventana.setMessage("Dialogo que utiliza un fragmento");
        ventana.setIcon(R.drawable.baseline_check_box_24);
        ventana.setNegativeButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(),"SI", Toast.LENGTH_SHORT).show();
            }
        });
        ventana.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(),"NO", Toast.LENGTH_SHORT).show();
            }
        });
        return  ventana.create();
    }
}
