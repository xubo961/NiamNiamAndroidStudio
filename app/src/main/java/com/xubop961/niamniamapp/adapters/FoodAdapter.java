package com.xubop961.niamniamapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.xubop961.niamniamapp.R;
import com.xubop961.niamniamapp.Recetas;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private List<Recetas> recetas;

    public FoodAdapter(List<Recetas> recetas) {
        this.recetas = recetas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_fodd_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recetas receta = recetas.get(position);
        holder.textViewNombre.setText(receta.getNombre());
        holder.imageView.setImageResource(receta.getImagen());
    }

    @Override
    public int getItemCount() {
        return recetas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.foodName);
            imageView = itemView.findViewById(R.id.foodImage);
        }
    }
}
