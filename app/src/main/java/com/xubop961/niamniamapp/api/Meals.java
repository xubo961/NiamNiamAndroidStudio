package com.xubop961.niamniamapp.api;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Meals {
    @SerializedName("meals")
    List<Meal> meals = new ArrayList<>();

    public class Meal {
        @SerializedName("strMeal")
        public String mealName;

        @SerializedName("strMealThumb")
        public String mealImageUrl;

        @SerializedName("idMeal")
        public String mealId;

        @SerializedName("strInstructions")
        public String instructions;

        public String getMealName() {
            return mealName;
        }

        public String getMealImageUrl() {
            return mealImageUrl;
        }

        public String getMealId() {
            return mealId;
        }

        public String getInstructions() {
            return instructions;
        }
    }

    public List<Meal> getMeals() {
        return meals;
    }
}
