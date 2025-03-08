package com.xubop961.niamniamapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favoritos_page, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

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
            favoritos = new ArrayList<>();
        }

        adapter = new FoodAdapter(getContext(), favoritos, meal -> {
            mostrarDialogoReceta(meal);
        });

        recyclerView.setAdapter(adapter);
    }

    private void mostrarDialogoReceta(Meals.Meal meal) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_food_details, null);

        ImageView imageView = dialogView.findViewById(R.id.dialogFoodImage);
        TextView textViewNombre = dialogView.findViewById(R.id.dialogFoodName);
        TextView textViewInstrucciones = dialogView.findViewById(R.id.dialogFoodInstruction);

        ImageButton btnAddToFavorites = dialogView.findViewById(R.id.btnAddToFavorites);
        ImageButton btnRemoveFromFavorites = dialogView.findViewById(R.id.btnRemoveFromFavorites);

        textViewNombre.setText(meal.getMealName());

        Glide.with(getContext())
                .load(meal.getMealImageUrl())
                .into(imageView);

        textViewInstrucciones.setText(meal.getInstructions());

        // Verificar si la receta ya está en favoritos
        if (estaEnFavoritos(meal)) {
            btnAddToFavorites.setVisibility(View.GONE);
            btnRemoveFromFavorites.setVisibility(View.VISIBLE);
        } else {
            btnAddToFavorites.setVisibility(View.VISIBLE);
            btnRemoveFromFavorites.setVisibility(View.GONE);
        }

        btnAddToFavorites.setOnClickListener(v -> {
            guardarEnFavoritos(meal);
            Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
            btnAddToFavorites.setVisibility(View.GONE);
            btnRemoveFromFavorites.setVisibility(View.VISIBLE);
            cargarFavoritos(); // Refrescar la lista automáticamente
        });

        btnRemoveFromFavorites.setOnClickListener(v -> {
            quitarDeFavoritos(meal);
            Toast.makeText(getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
            btnAddToFavorites.setVisibility(View.VISIBLE);
            btnRemoveFromFavorites.setVisibility(View.GONE);
            cargarFavoritos(); // Refrescar la lista automáticamente
        });

        new MaterialAlertDialogBuilder(getContext())
                .setView(dialogView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private boolean estaEnFavoritos(Meals.Meal meal) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("FAVORITOS", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String favoritosJson = sharedPreferences.getString("favoritos_lista", "[]");
        Type type = new TypeToken<List<Meals.Meal>>() {}.getType();
        List<Meals.Meal> favoritos = gson.fromJson(favoritosJson, type);

        for (Meals.Meal fav : favoritos) {
            if (fav.getMealName().equals(meal.getMealName())) {
                return true;
            }
        }
        return false;
    }

    private void guardarEnFavoritos(Meals.Meal meal) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("FAVORITOS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String favoritosJson = sharedPreferences.getString("favoritos_lista", "[]");
        Type type = new TypeToken<List<Meals.Meal>>() {}.getType();
        List<Meals.Meal> favoritos = gson.fromJson(favoritosJson, type);

        // Evitar duplicados
        for (Meals.Meal fav : favoritos) {
            if (fav.getMealName().equals(meal.getMealName())) {
                return;
            }
        }

        favoritos.add(meal);
        editor.putString("favoritos_lista", gson.toJson(favoritos));
        editor.apply();
    }

    private void quitarDeFavoritos(Meals.Meal meal) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("FAVORITOS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String favoritosJson = sharedPreferences.getString("favoritos_lista", "[]");
        Type type = new TypeToken<List<Meals.Meal>>() {}.getType();
        List<Meals.Meal> favoritos = gson.fromJson(favoritosJson, type);

        for (Meals.Meal fav : favoritos) {
            if (fav.getMealName().equals(meal.getMealName())) {
                favoritos.remove(fav);
                break;
            }
        }

        editor.putString("favoritos_lista", gson.toJson(favoritos));
        editor.apply();
    }
}
