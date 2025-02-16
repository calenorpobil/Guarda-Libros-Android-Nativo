package com.merlita.guardalibros;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class Adaptador extends RecyclerView.Adapter<Adaptador.MiContenedor>
    implements View.OnClickListener {
    private Context context;
    private ArrayList<DatosLibros> lista;
    private ArrayList<DatosLibros> librosOriginal; // Lista original sin filtrar
    View.OnClickListener escuchador;

    @NonNull
    @Override
    public MiContenedor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflador.inflate(R.layout.text_row_item, parent, false);

        return new MiContenedor(v);
    }

    //PONER VALORES
    @Override
    public void onBindViewHolder(@NonNull MiContenedor holder, int position) {
        DatosLibros libro = lista.get(position);
        holder.tvTitulo.setText(libro.getTitulo());
        holder.tvAutor.setText(libro.getAutor());
        holder.tvFecha.setText(libro.getFechaFinDate().toString());
        holder.tvFormato.setText(libro.getFormato());
        holder.rbEstrellas.setRating(libro.getValoracion());
        String prestado = libro.getPrestado_a();
        if(prestado!=null)
            holder.cbPrestado.setChecked(!prestado.isEmpty());
        holder.cbFinalizado.setChecked(libro.getFinalizado());
        holder.cbNotas.setChecked(!libro.getNotas().isEmpty());
        if (libro.getIdioma().equals("Español"))
            holder.tvBandera.setText("\uD83C\uDDEA\uD83C\uDDF8");
        if (libro.getIdioma().equals("Inglés"))
            holder.tvBandera.setText("\uD83C\uDDEC\uD83C\uDDE7");
        if (libro.getIdioma().equals("Alemán"))
            holder.tvBandera.setText("\uD83C\uDDE9\uD83C\uDDEA");
        holder.imagen.setImageResource(R.mipmap.libro);




    }

    public Adaptador(View.OnClickListener escuchador,
                     Context context, ArrayList<DatosLibros> lista) {
        super();
        this.escuchador = escuchador;
        this.context = context;
        this.lista = lista;
        this.librosOriginal = new ArrayList<>(lista);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public void onClick(View view) {
        if(escuchador!=null)
            escuchador.onClick(view);
    }

    public static class MiContenedor extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener
    {
        TextView tvTitulo, tvAutor, tvFecha, tvFormato, tvBandera;
        RatingBar rbEstrellas;
        CheckBox cbNotas, cbFinalizado, cbPrestado;
        ImageView imagen;

        public MiContenedor(@NonNull View itemView) {
            super(itemView);

            tvTitulo = (TextView) itemView.findViewById(R.id.tvTitulo);
            tvAutor = (TextView) itemView.findViewById(R.id.tvEdad);
            tvBandera = (TextView) itemView.findViewById(R.id.tvBandera);
            tvFecha = (TextView) itemView.findViewById(R.id.tvFecha);
            tvFormato = (TextView) itemView.findViewById(R.id.tvFormato);
            rbEstrellas = (RatingBar) itemView.findViewById(R.id.rbEstrellas);
            imagen = (ImageView) itemView.findViewById(R.id.imageView);
            cbNotas = (CheckBox) itemView.findViewById(R.id.checkBoxNotas);
            cbFinalizado = (CheckBox) itemView.findViewById(R.id.checkBoxFinalizado);
            cbPrestado = (CheckBox) itemView.findViewById(R.id.checkBoxPrestado);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                        ContextMenu.ContextMenuInfo contextMenuInfo)
        {
            contextMenu.add(getAdapterPosition(), 121, 0, "EDITAR");
            contextMenu.add(getAdapterPosition(), 122, 1, "BORRAR");
        }
    }


    //METODOS PARA ORDENAR
    public void ordenarPorTitulo() {
        Collections.sort(lista, (l1, l2) -> l1.getTitulo().
                compareToIgnoreCase(l2.getTitulo()));
        notifyDataSetChanged();
    }

    public void ordenarPorAutor() {
        Collections.sort(lista, (l1, l2) -> l1.getAutor().
                compareToIgnoreCase(l2.getAutor()));
        notifyDataSetChanged();
    }

    public void ordenarPorFechaInicio() {
        Collections.sort(lista, (l1, l2) -> {
            if (l1.getFecha_lectura_ini() == null && l2.getFecha_lectura_ini() == null) return 0;
            if (l1.getFecha_lectura_ini() == null) return 1;
            if (l2.getFecha_lectura_ini() == null) return -1;
            // Más reciente primero:
            return l2.getFecha_lectura_ini().compareTo(l1.getFecha_lectura_ini());
        });
        notifyDataSetChanged();
    }
    public void ordenarPorFinalizado() {
        Collections.sort(lista, (l1, l2) -> {
            if (!l1.getFinalizado() && !l2.getFinalizado()) return 0;
            if (!l1.getFinalizado() && l2.getFinalizado()) return 1;
            return -1;
        });
        notifyDataSetChanged();
    }

    public void resetearOrden() {
        lista = new ArrayList<>(librosOriginal);
        notifyDataSetChanged();
    }



    public Adaptador(@NonNull Context context) {
        super();
    }



}
