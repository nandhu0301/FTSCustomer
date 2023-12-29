package com.smiligenceTestenv.techUser.bean;


public class Ingredients {


    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    int itemId;
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    String categoryId;
    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    String ingredientName;

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    String ingredientId;



    public String getIngredientUri() {
        return ingredientUri;
    }

    public void setIngredientUri(String ingredientUri) {
        this.ingredientUri = ingredientUri;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    String ingredientUri;
    String createdDate;

}
