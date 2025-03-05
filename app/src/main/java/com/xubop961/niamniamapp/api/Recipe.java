package com.xubop961.niamniamapp.api;

public class Recipe {
    private String name;
    private String ingredients;
    private String instructions;
    private String imageUri;

    public Recipe(String name, String ingredients, String instructions, String imageUri) {
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageUri = imageUri;
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getImageUri() {
        return imageUri;
    }
}
