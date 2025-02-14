package com.xubop961.niamniamapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xubop961.niamniamapp.R;
import com.xubop961.niamniamapp.adapters.FoodAdapter;
import com.xubop961.niamniamapp.api.Meals;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class PerfilPage extends Fragment {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil_page, container, false);
    }

    private void cargarTusRecetas() {
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

    private void mostrarDialogoReceta(Meals.Meal meal) {LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_food_details, null);

        ImageView imageView = dialogView.findViewById(R.id.dialogFoodImage);
        TextView textViewNombre = dialogView.findViewById(R.id.dialogFoodName);
        TextView textViewInstrucciones = dialogView.findViewById(R.id.dialogFoodInstruction);

        textViewNombre.setText(meal.getMealName());

        Glide.with(getContext())
                .load(meal.getMealImageUrl())
                .into(imageView);

        textViewInstrucciones.setText(meal.getInstructions());
    }
}