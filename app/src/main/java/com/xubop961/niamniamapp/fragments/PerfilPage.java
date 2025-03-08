package com.xubop961.niamniamapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.bumptech.glide.Glide;
import com.xubop961.niamniamapp.R;
import com.xubop961.niamniamapp.adapters.RecipeAdapter;
import com.xubop961.niamniamapp.api.Recipe;
import com.xubop961.niamniamapp.api.RecipePreferences;
import java.util.ArrayList;
import java.util.List;

public class PerfilPage extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_perfil_page, container, false);

        // Recuperamos el TextView del username y le asignamos el nombre del usuario guardado
        TextView textUsername = view.findViewById(R.id.textUsername);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("niamniam_preferences", Context.MODE_PRIVATE);
        String loggedName = sharedPreferences.getString("logged_in_name", "Username");
        textUsername.setText(loggedName);

        // Inicializamos el RecyclerView y asignamos un LayoutManager
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cargamos las recetas guardadas en SharedPreferences
        cargarTusRecetas();

        return view;
    }

    private void cargarTusRecetas() {
        List<Recipe> recetas = RecipePreferences.getRecipes(getContext());
        if (recetas == null) {
            recetas = new ArrayList<>();
        }
        adapter = new RecipeAdapter(getContext(), recetas, recipe -> {
            // Al hacer clic, mostramos el diálogo con los detalles de la receta
            mostrarDialogoReceta(recipe);
        });
        recyclerView.setAdapter(adapter);
    }

    private void mostrarDialogoReceta(Recipe recipe) {
        // Inflamos el layout dialog_food_details (sin modificar)
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_food_details, null);

        // Referencias a las vistas del diálogo
        ImageView dialogFoodImage = dialogView.findViewById(R.id.dialogFoodImage);
        TextView dialogFoodName = dialogView.findViewById(R.id.dialogFoodName);
        TextView dialogFoodInstruction = dialogView.findViewById(R.id.dialogFoodInstruction);

        // Asignamos el nombre de la receta
        dialogFoodName.setText(recipe.getName());

        // Construimos el mensaje que incluye ingredientes e instrucciones
        String details = "Ingredients: " + recipe.getIngredients() + "\n\n"
                + "Instructions: " + recipe.getInstructions();
        dialogFoodInstruction.setText(details);

        // Cargamos la imagen usando Glide, si existe
        if (recipe.getImageUri() != null && !recipe.getImageUri().isEmpty()) {
            Glide.with(getContext())
                    .load(recipe.getImageUri())
                    .into(dialogFoodImage);
        } else {
            dialogFoodImage.setImageResource(R.drawable.coche);
        }

        // Creamos y mostramos el AlertDialog
        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}
