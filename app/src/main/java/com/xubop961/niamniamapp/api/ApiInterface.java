package com.xubop961.niamniamapp.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("filter.php")
    Call<Meals> getMealsByIngredient(@Query("i") String ingredient);

    // Nueva función para obtener detalles de una receta por su mealId
    @GET("lookup.php")
    Call<Meals> getMealDetails(@Query("i") String mealId);
}
