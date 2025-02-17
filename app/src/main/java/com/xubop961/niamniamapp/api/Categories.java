package com.xubop961.niamniamapp.api;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Categories {
    @SerializedName("categories")
    List<Category> categories = new ArrayList<>();

    public static class Category {
        @SerializedName("idCategory")
        private String idCategory;

        @SerializedName("strCategory")
        private String categoryName;

        @SerializedName("strCategoryThumb")
        private String categoryThumb;

        @SerializedName("strCategoryDescription")
        private String categoryDescription;

        public String getIdCategory() {
            return idCategory;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getCategoryThumb() {
            return categoryThumb;
        }

        public String getCategoryDescription() {
            return categoryDescription;
        }
    }

    public List<Category> getCategories() {
        return categories;
    }
}
