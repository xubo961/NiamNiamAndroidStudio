package com.xubop961.niamniamapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xubop961.niamniamapp.api.Categories;
import com.xubop961.niamniamapp.api.Meals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomePage extends Fragment {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private EditText buscarRecetas;
    private ApiInterface apiService;
    private Button btnBuscar;
    // Nuevo contenedor para las categorías dinámicas
    private LinearLayout categoriesContainer;

    public HomePage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el layout del fragment (asegúrate de haber modificado fragment_home_page.xml)
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        buscarRecetas = view.findViewById(R.id.buscarRecetas);
        btnBuscar = view.findViewById(R.id.btnBuscar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Obtenemos el contenedor de categorías que debe existir en el XML (dentro del HorizontalScrollView)
        categoriesContainer = view.findViewById(R.id.categoriesContainer);

        // Cargamos las categorías dinámicamente
        loadCategories();

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

        // Se establece "chicken" por defecto
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

        String instrucciones = meal.getInstructions();
        if (instrucciones != null && !instrucciones.isEmpty()) {
            textViewInstrucciones.setText(instrucciones);
        } else {
            textViewInstrucciones.setText("No hay instrucciones disponibles.");
        }

        if (estaEnFavoritos(meal)) {
            btnAddToFavorites.setVisibility(View.GONE);
            btnRemoveFromFavorites.setVisibility(View.VISIBLE);
        } else {
            btnAddToFavorites.setVisibility(View.VISIBLE);
            btnRemoveFromFavorites.setVisibility(View.GONE);
        }

        btnAddToFavorites.setOnClickListener(v -> {
            guardarEnFavoritos(meal);
            Toast.makeText(getContext(), "Añadido a Favoritos", Toast.LENGTH_SHORT).show();
            btnAddToFavorites.setVisibility(View.GONE);
            btnRemoveFromFavorites.setVisibility(View.VISIBLE);
        });

        btnRemoveFromFavorites.setOnClickListener(v -> {
            quitarDeFavoritos(meal);
            Toast.makeText(getContext(), "Quitado de Favoritos", Toast.LENGTH_SHORT).show();
            btnAddToFavorites.setVisibility(View.VISIBLE);
            btnRemoveFromFavorites.setVisibility(View.GONE);
        });

        new MaterialAlertDialogBuilder(getContext())
                .setView(dialogView)
                .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
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

    // ======== NUEVA SECCIÓN: CATEGORÍAS DINÁMICAS ========

    /**
     * Método para cargar las categorías desde la API usando HttpURLConnection.
     */
    private void loadCategories() {
        new Thread(() -> {
            try {
                URL url = new URL("https://www.themealdb.com/api/json/v1/1/categories.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    Gson gson = new Gson();
                    final Categories categories = gson.fromJson(sb.toString(), Categories.class);

                    // Actualizamos la UI en el hilo principal
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                addCategoriesToScrollView(categories.getCategories())
                        );
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Agrega dinámicamente cada categoría como un TextView al contenedor.
     */
    private void addCategoriesToScrollView(List<Categories.Category> categoryList) {
        // Limpiar el contenedor para evitar duplicados
        categoriesContainer.removeAllViews();

        for (final Categories.Category category : categoryList) {
            TextView textView = new TextView(getContext());
            textView.setText(category.getCategoryName());
            textView.setTextSize(20);
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            textView.setTextColor(getResources().getColor(R.color.bottom_nav_color));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin = 7;
            params.setMargins(margin, margin, margin, margin);
            textView.setLayoutParams(params);

            // Al hacer clic, se busca recetas por la categoría seleccionada
            textView.setOnClickListener(v -> fetchRecipesByCategory(category.getCategoryName()));

            categoriesContainer.addView(textView);
        }
    }

    /**
     * Realiza una llamada HTTP para obtener las recetas filtradas por categoría.
     */
    private void fetchRecipesByCategory(final String categoryName) {
        new Thread(() -> {
            try {
                String urlString = "https://www.themealdb.com/api/json/v1/1/filter.php?c=" +
                        URLEncoder.encode(categoryName, "UTF-8");
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    Gson gson = new Gson();
                    final Meals meals = gson.fromJson(sb.toString(), Meals.class);

                    // Actualizamos la UI en el hilo principal para mostrar las recetas
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                updateRecipesUI(meals.getMeals())
                        );
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Actualiza el RecyclerView para mostrar las recetas obtenidas de la categoría seleccionada.
     */
    private void updateRecipesUI(List<Meals.Meal> mealsList) {
        if (mealsList != null && !mealsList.isEmpty()) {
            adapter = new FoodAdapter(getContext(), mealsList, meal -> {
                mostrarDialogoReceta(meal);
            });
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No se encontraron recetas para esta categoría", Toast.LENGTH_SHORT).show();
        }
    }
}
