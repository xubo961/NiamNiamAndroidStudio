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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xubop961.niamniamapp.R;
import com.xubop961.niamniamapp.adapters.FoodAdapter;
import com.xubop961.niamniamapp.api.Meals;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritosPage extends Fragment {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favoritos_page, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cargarFavoritos();

        return view;
    }

    private void cargarFavoritos() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("FAVORITOS", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String favoritosJson = sharedPreferences.getString("favoritos_lista", "[]");
        Type type = new TypeToken<List<Meals.Meal>>() {}.getType();
        List<Meals.Meal> favoritos = gson.fromJson(favoritosJson, type);

        if (favoritos == null) {
            favoritos = new ArrayList<>(); // Evitar null pointer exception
        }

        adapter = new FoodAdapter(getContext(), favoritos, meal -> {
            mostrarDialogoReceta(meal); // Este método debe existir o reemplázalo con tu lógica
        });

        recyclerView.setAdapter(adapter);
    }

    private void mostrarDialogoReceta(Meals.Meal meal) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_food_details, null);

        ImageView imageView = dialogView.findViewById(R.id.dialogFoodImage);
        TextView textViewNombre = dialogView.findViewById(R.id.dialogFoodName);
        TextView textViewInstrucciones = dialogView.findViewById(R.id.dialogFoodInstruction);
        Button btnAddToFavorites = dialogView.findViewById(R.id.btnAddToFavorites);

        textViewNombre.setText(meal.getMealName());

        Glide.with(getContext())
                .load(meal.getMealImageUrl())
                .into(imageView);

        textViewInstrucciones.setText(meal.getInstructions());

        new MaterialAlertDialogBuilder(getContext())
                .setView(dialogView)
                .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
