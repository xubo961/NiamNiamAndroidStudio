package com.xubop961.niamniamapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.xubop961.niamniamapp.R;
import com.xubop961.niamniamapp.api.Meals;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private Context context;
    private List<Meals.Meal> meals;
    private OnItemClickListener onItemClickListener;

    public FoodAdapter(Context context, List<Meals.Meal> meals, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.meals = meals;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_fodd_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meals.Meal meal = meals.get(position);
        holder.textViewNombre.setText(meal.getMealName());

        Glide.with(context)
                .load(meal.getMealImageUrl())
                .placeholder(R.drawable.coche) // Imagen de carga seguramente se cambie jeje
                .error(R.drawable.coche) // Imagen en caso de error lo mismo ;)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            onItemClickListener.onItemClick(meal);
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
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

    public interface OnItemClickListener {
        void onItemClick(Meals.Meal meal);
    }
}

