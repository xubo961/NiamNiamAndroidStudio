package com.xubop961.niamniamapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xubop961.niamniamapp.R;
import com.xubop961.niamniamapp.adapters.FoodAdapter;
import com.xubop961.niamniamapp.api.ApiClient;
import com.xubop961.niamniamapp.api.ApiInterface;
import com.xubop961.niamniamapp.api.Meals;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.lang.reflect.Type;
import java.util.List;

public class HomePage extends Fragment {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private EditText buscarRecetas;
    private ApiInterface apiService;
    private Button btnBuscar;

    public HomePage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        buscarRecetas = view.findViewById(R.id.buscarRecetas);
        btnBuscar = view.findViewById(R.id.btnBuscar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiInterface.class);

        btnBuscar.setOnClickListener(v -> {
            String ingrediente = buscarRecetas.getText().toString().trim();
            if (!ingrediente.isEmpty()) {
                buscarRecetasPorIngrediente(ingrediente);
            } else {
                Toast.makeText(getContext(), "Por favor, ingrese un ingrediente para buscar", Toast.LENGTH_SHORT).show();
            }
        });

        //  Establecido que salga CHICKEN por defecto
        //  Tal vez se cambie en un futuro ;) jejeje
        buscarRecetasPorIngrediente("chicken");

        return view;
    }

    private void buscarRecetasPorIngrediente(String ingrediente) {
        apiService.getMealsByIngredient(ingrediente).enqueue(new Callback<Meals>() {
            @Override
            public void onResponse(@NonNull Call<Meals> call, @NonNull Response<Meals> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Meals.Meal> meals = response.body().getMeals();
                    if (meals != null && !meals.isEmpty()) {
                        adapter = new FoodAdapter(getContext(), meals, meal -> {
                            mostrarDialogoReceta(meal);
                        });
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "No se encontraron recetas para este ingrediente", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error en la respuesta de la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Meals> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoReceta(Meals.Meal meal) {
        // Llamada para obtener los detalles de la receta por su mealId
        apiService.getMealDetails(meal.getMealId()).enqueue(new Callback<Meals>() {
            @Override
            public void onResponse(@NonNull Call<Meals> call, @NonNull Response<Meals> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Meals.Meal recipeDetails = response.body().getMeals().get(0);  // Receta obtenida

                    // Ahora mostramos los detalles de la receta
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    View dialogView = inflater.inflate(R.layout.dialog_food_details, null);

                    ImageView imageView = dialogView.findViewById(R.id.dialogFoodImage);
                    TextView textViewNombre = dialogView.findViewById(R.id.dialogFoodName);
                    TextView textViewInstrucciones = dialogView.findViewById(R.id.dialogFoodInstruction);

                    ImageButton btnAddToFavorites = dialogView.findViewById(R.id.btnAddToFavorites);
                    ImageButton btnRemoveFromFavorites = dialogView.findViewById(R.id.btnRemoveFromFavorites);

                    textViewNombre.setText(recipeDetails.getMealName());

                    // Cargar la imagen de la receta
                    Glide.with(getContext())
                            .load(recipeDetails.getMealImageUrl())
                            .into(imageView);

                    // Mostrar instrucciones de la receta
                    String instrucciones = recipeDetails.getInstructions();
                    if (instrucciones != null && !instrucciones.isEmpty()) {
                        textViewInstrucciones.setText(instrucciones);
                    } else {
                        textViewInstrucciones.setText("No hay instrucciones disponibles.");
                    }

                    // Gestionar visibilidad de los botones de favoritos
                    if (estaEnFavoritos(recipeDetails)) {
                        btnAddToFavorites.setVisibility(View.GONE);  // Ocultar "Añadir a Favoritos"
                        btnRemoveFromFavorites.setVisibility(View.VISIBLE);  // Mostrar "Quitar de Favoritos"
                    } else {
                        btnAddToFavorites.setVisibility(View.VISIBLE);  // Mostrar "Añadir a Favoritos"
                        btnRemoveFromFavorites.setVisibility(View.GONE);  // Ocultar "Quitar de Favoritos"
                    }

                    // Gestión de favoritos
                    btnAddToFavorites.setOnClickListener(v -> {
                        guardarEnFavoritos(recipeDetails);
                        Toast.makeText(getContext(), "Añadido a Favoritos", Toast.LENGTH_SHORT).show();
                        btnAddToFavorites.setVisibility(View.GONE);
                        btnRemoveFromFavorites.setVisibility(View.VISIBLE);
                    });

                    btnRemoveFromFavorites.setOnClickListener(v -> {
                        quitarDeFavoritos(recipeDetails);
                        Toast.makeText(getContext(), "Quitado de Favoritos", Toast.LENGTH_SHORT).show();
                        btnAddToFavorites.setVisibility(View.VISIBLE);
                        btnRemoveFromFavorites.setVisibility(View.GONE);
                    });

                    // Mostrar el diálogo
                    new MaterialAlertDialogBuilder(getContext())
                            .setView(dialogView)
                            .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                            .show();

                } else {
                    Toast.makeText(getContext(), "Error al obtener los detalles de la receta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Meals> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
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

