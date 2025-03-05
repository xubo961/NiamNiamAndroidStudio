package com.xubop961.niamniamapp.api;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xubop961.niamniamapp.api.Recipe;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipePreferences {
    private static final String PREFS_NAME = "recipes_prefs";
    private static final String KEY_RECIPES = "recipes";

    // Guarda una receta (se agrega a la lista ya existente)
    public static void saveRecipe(Context context, Recipe recipe) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_RECIPES, "[]");
        Type type = new TypeToken<List<Recipe>>() {}.getType();
        List<Recipe> recipeList = gson.fromJson(json, type);
        if (recipeList == null) {
            recipeList = new ArrayList<>();
        }
        recipeList.add(recipe);
        String updatedJson = gson.toJson(recipeList);
        prefs.edit().putString(KEY_RECIPES, updatedJson).apply();
    }

    // Recupera la lista de recetas
    public static List<Recipe> getRecipes(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_RECIPES, "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Recipe>>() {}.getType();
        List<Recipe> recipeList = gson.fromJson(json, type);
        return recipeList != null ? recipeList : new ArrayList<>();
    }
}
