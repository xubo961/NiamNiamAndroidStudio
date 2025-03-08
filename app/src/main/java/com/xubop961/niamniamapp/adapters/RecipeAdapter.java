package com.xubop961.niamniamapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.xubop961.niamniamapp.R;
import com.xubop961.niamniamapp.api.Recipe;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    // Interfaz para manejar clics en los ítems
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private Context context;
    private List<Recipe> recipeList;
    private OnRecipeClickListener listener;

    public RecipeAdapter(Context context, List<Recipe> recipeList, OnRecipeClickListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        notifyDataSetChanged();
    }

    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos el layout "item_food_card.xml"
        View view = LayoutInflater.from(context).inflate(R.layout.rv_fodd_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        // Asignamos el nombre de la receta
        holder.foodName.setText(recipe.getName());
        // Usamos Glide para cargar la imagen desde la URI guardada
        Glide.with(context)
                .load(recipe.getImageUri())
                .into(holder.foodImage);
        // Listener para detectar clics en el ítem
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView foodImage;
        TextView foodName;

        public ViewHolder(View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodName = itemView.findViewById(R.id.foodName);
        }
    }
}
