package com.xubop961.niamniamapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
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
import java.util.ArrayList;
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
    // Contenedor para las categorías dinámicas
    private LinearLayout categoriesContainer;
    // Variable para almacenar la categoría seleccionada actualmente
    private String selectedCategory = null;

    public HomePage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el layout del fragment (asegúrate de haber modificado fragment_home_page.xml)
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        buscarRecetas = view.findViewById(R.id.buscarRecetas);
        btnBuscar = view.findViewById(R.id.btnBuscar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

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

    // Búsqueda por ingredientes separados (por comas) y obtención de la intersección de resultados
    private void buscarRecetasPorIngrediente(String ingrediente) {
        // Separamos los ingredientes ingresados (por ejemplo: "pollo, tomate")
        String[] ingredientsArray = ingrediente.split(",");
        List<String> ingredientList = new ArrayList<>();
        for (String ing : ingredientsArray) {
            if (!ing.trim().isEmpty()) {
                ingredientList.add(ing.trim());
            }
        }

        if (ingredientList.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, ingrese al menos un ingrediente", Toast.LENGTH_SHORT).show();
            return;
        }

        final int totalCalls = ingredientList.size();
        final int[] completedCalls = {0}; // Contador mutable
        final List<List<Meals.Meal>> results = new ArrayList<>();

        // Realizamos una llamada a la API para cada ingrediente
        for (String ing : ingredientList) {
            apiService.getMealsByIngredient(ing).enqueue(new Callback<Meals>() {
                @Override
                public void onResponse(@NonNull Call<Meals> call, @NonNull Response<Meals> response) {
                    List<Meals.Meal> mealsForIngredient;
                    if (response.isSuccessful() && response.body() != null) {
                        mealsForIngredient = response.body().getMeals();
                    } else {
                        mealsForIngredient = new ArrayList<>();
                    }
                    synchronized (results) {
                        results.add(mealsForIngredient);
                    }
                    synchronized (completedCalls) {
                        completedCalls[0]++;
                        if (completedCalls[0] == totalCalls) {
                            procesarResultados(results);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Meals> call, @NonNull Throwable t) {
                    // En caso de error, agregamos una lista vacía
                    synchronized (results) {
                        results.add(new ArrayList<>());
                    }
                    synchronized (completedCalls) {
                        completedCalls[0]++;
                        if (completedCalls[0] == totalCalls) {
                            procesarResultados(results);
                        }
                    }
                }
            });
        }
    }

    // Procesa los resultados una vez completadas todas las llamadas y actualiza el RecyclerView
    private void procesarResultados(List<List<Meals.Meal>> results) {
        List<Meals.Meal> intersection = intersectResults(results);
        if (!intersection.isEmpty()) {
            adapter = new FoodAdapter(getContext(), intersection, meal -> {
                mostrarDialogoReceta(meal);
            });
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No se encontraron recetas para los ingredientes seleccionados", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Meals.Meal> intersectResults(List<List<Meals.Meal>> results) {
        List<Meals.Meal> intersection = new ArrayList<>();
        if (results == null || results.isEmpty()) {
            return intersection;
        }
        // Utilizamos la primera lista como base
        List<Meals.Meal> baseList = results.get(0);
        for (Meals.Meal meal : baseList) {
            boolean presentInAll = true;
            // Comprobamos que la receta esté presente en cada una de las demás listas
            for (int i = 1; i < results.size(); i++) {
                List<Meals.Meal> list = results.get(i);
                boolean found = false;
                for (Meals.Meal m : list) {
                    if (m.getMealId().equals(meal.getMealId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    presentInAll = false;
                    break;
                }
            }
            if (presentInAll) {
                intersection.add(meal);
            }
        }
        return intersection;
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

    // ======== SECCIÓN: CATEGORÍAS DINÁMICAS ========

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
     * Al tocar una categoría se pinta con "color_Primary" y las demás con "color_UnselectedButton".
     * Si se vuelve a pulsar la categoría seleccionada, se deselecciona y se restablece el estado por defecto.
     */
    private void addCategoriesToScrollView(List<Categories.Category> categoryList) {
        // Limpiar el contenedor para evitar duplicados
        categoriesContainer.removeAllViews();

        for (final Categories.Category category : categoryList) {
            TextView textView = new TextView(getContext());
            textView.setText(category.getCategoryName());
            textView.setTextSize(23);
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            // Inicialmente, asignamos el color de no seleccionado
            textView.setTextColor(getResources().getColor(R.color.color_UnselectedButton));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin = 15;
            params.setMargins(margin, margin, margin, margin);
            textView.setLayoutParams(params);

            // Al hacer clic: se actualizan los colores y se busca la receta de la categoría seleccionada.
            // Además, si se pulsa de nuevo la categoría ya seleccionada, se deselecciona.
            textView.setOnClickListener(v -> {
                if (selectedCategory != null && selectedCategory.equals(category.getCategoryName())) {
                    // Si ya estaba seleccionada, se deselecciona.
                    selectedCategory = null;
                    // Resetea el color de todos los elementos a "unselected"
                    for (int i = 0; i < categoriesContainer.getChildCount(); i++) {
                        View child = categoriesContainer.getChildAt(i);
                        if (child instanceof TextView) {
                            ((TextView) child).setTextColor(getResources().getColor(R.color.color_UnselectedButton));
                        }
                    }
                    // En este ejemplo se carga "chicken" como valor por defecto.
                    buscarRecetasPorIngrediente("chicken");
                } else {
                    // Si se selecciona una nueva categoría, se actualiza la variable y se marcan los colores.
                    selectedCategory = category.getCategoryName();
                    // Resetear todos los elementos a "unselected"
                    for (int i = 0; i < categoriesContainer.getChildCount(); i++) {
                        View child = categoriesContainer.getChildAt(i);
                        if (child instanceof TextView) {
                            ((TextView) child).setTextColor(getResources().getColor(R.color.color_UnselectedButton));
                        }
                    }
                    // Se asigna el color seleccionado al TextView pulsado
                    textView.setTextColor(getResources().getColor(R.color.color_Priamry));
                    // Llamamos a la función para obtener las recetas de la categoría seleccionada
                    fetchRecipesByCategory(category.getCategoryName());
                }
            });

            categoriesContainer.addView(textView);
        }
    }

    /**
     * Realiza una llamada HTTP para obtener las recetas filtradas por la categoría seleccionada.
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
