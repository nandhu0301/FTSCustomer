package com.smiligenceTestenv.techUser.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemDetails implements  Comparable< ItemDetails >, Serializable
{
    private int itemId;
    private String itemName;
    private String createDate;
    private String itemImage;
    private int itemPrice;
    String skuVariant;
    String modelName;
    String brandName;
    String descriptionUrl;

    public String getDescriptionUrl() {
        return descriptionUrl;
    }

    public ItemDetails setDescriptionUrl(String descriptionUrl) {
        this.descriptionUrl = descriptionUrl;
        return this;
    }

    public String getSkuVariant() {
        return skuVariant;
    }

    public ItemDetails setSkuVariant(String skuVariant) {
        this.skuVariant = skuVariant;
        return this;
    }

    public String getModelName() {
        return modelName;
    }

    public ItemDetails setModelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    public String getBrandName() {
        return brandName;
    }

    public ItemDetails setBrandName(String brandName) {
        this.brandName = brandName;
        return this;
    }

    public ArrayList<CategoryDetails> getCategoryDetailsArrayList() {
        return categoryDetailsArrayList;
    }

    CategoryDetails categoryDetails;
    public void setCategoryDetailsArrayList(ArrayList<CategoryDetails> categoryDetailsArrayList) {
        this.categoryDetailsArrayList = categoryDetailsArrayList;
    }

    ArrayList<CategoryDetails> categoryDetailsArrayList=new ArrayList<>();

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    private String itemQuantity;
    private int itemAvailableQuantity;
    private String itemStatus;
    private String itemInactiveDate;
    private int totalItemQtyPrice;
    private String itemBrand;
    private int MRP_Price;
    String subCategoryName;
    String categoryName;
    private int itemCounter;

    private String quantityUnits;
    private int itemBuyQuantity;
    private Boolean wishList;
    private String itemDescription;
    private String itemFeatures;
    private String itemManufacture;
    private String itemRating;
    private String itemReview;
    private int itemMaxLimitation;
    private int itemMinLimitation;
    private String sellerId;
    private String storeName;
    private String storePincode;
    private String storeAdress;
    int totalTaxPrice;



    public int getTotalTaxPrice() {
        return totalTaxPrice;
    }

    public void setTotalTaxPrice(int totalTaxPrice) {
        this.totalTaxPrice = totalTaxPrice;
    }

    int taxPrice;

    public int getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(int taxPrice) {
        this.taxPrice = taxPrice;
    }

    public ItemDetails() {
    }

    int basePrice;

    public int getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public String getInclusiveOfTax() {
        return inclusiveOfTax;
    }

    public void setInclusiveOfTax(String inclusiveOfTax) {
        this.inclusiveOfTax = inclusiveOfTax;
    }

    private String inclusiveOfTax;

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    private int tax;


    public ArrayList<ItemDetails> getRefinedItemList() {
        return refinedItemList;
    }

    public void setRefinedItemList(ArrayList<ItemDetails> refinedItemList) {
        this.refinedItemList = refinedItemList;
    }

    ArrayList<ItemDetails> refinedItemList = new ArrayList<>();

    private String storeLogo;

    public ItemDetails(String s, ArrayList<ItemDetails> refinedItemList) {

        this.categoryName = s;
        this.refinedItemList = refinedItemList;
    }

    public ArrayList<String> getImageUriList() {
        return imageUriList;
    }

    public void setImageUriList(ArrayList<String> imageUriList) {
        this.imageUriList = imageUriList;
    }

    private ArrayList<String> imageUriList = new ArrayList<>();

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    String categoryId;

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getItemCounter() {
        return itemCounter;
    }

    public void setItemCounter(int itemCounter) {
        this.itemCounter = itemCounter;
    }

    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public int getItemBuyQuantity() {
        return itemBuyQuantity;
    }

    public void setItemBuyQuantity(int itemBuyQuantity) {
        this.itemBuyQuantity = itemBuyQuantity;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }


    public int getItemAvailableQuantity() {
        return itemAvailableQuantity;
    }

    public void setItemAvailableQuantity(int itemAvailableQuantity) {
        this.itemAvailableQuantity = itemAvailableQuantity;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemInactiveDate() {
        return itemInactiveDate;
    }

    public void setItemInactiveDate(String itemInactiveDate) {
        this.itemInactiveDate = itemInactiveDate;
    }

    public int getTotalItemQtyPrice() {
        return totalItemQtyPrice;
    }

    public void setTotalItemQtyPrice(int totalItemQtyPrice) {
        this.totalItemQtyPrice = totalItemQtyPrice;
    }

    public String getItemBrand() {
        return itemBrand;
    }

    public void setItemBrand(String itemBrand) {
        this.itemBrand = itemBrand;
    }

    public int getMRP_Price() {
        return MRP_Price;
    }

    public void setMRP_Price(int MRP_Price) {
        this.MRP_Price = MRP_Price;
    }

    public CategoryDetails getCategoryDetails() {
        return categoryDetails;
    }

    public void setCategoryDetails(CategoryDetails categoryDetails) {
        this.categoryDetails = categoryDetails;
    }

    public String getQuantityUnits() {
        return quantityUnits;
    }

    public void setQuantityUnits(String quantityUnits) {
        this.quantityUnits = quantityUnits;
    }

    public Boolean getWishList() {
        return wishList;
    }

    public void setWishList(Boolean wishList) {
        this.wishList = wishList;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemFeatures() {
        return itemFeatures;
    }

    public void setItemFeatures(String itemFeatures) {
        this.itemFeatures = itemFeatures;
    }

    public String getItemManufacture() {
        return itemManufacture;
    }

    public void setItemManufacture(String itemManufacture) {
        this.itemManufacture = itemManufacture;
    }

    public String getItemRating() {
        return itemRating;
    }

    public void setItemRating(String itemRating) {
        this.itemRating = itemRating;
    }

    public String getItemReview() {
        return itemReview;
    }

    public void setItemReview(String itemReview) {
        this.itemReview = itemReview;
    }

    public int getItemMaxLimitation() {
        return itemMaxLimitation;
    }

    public void setItemMaxLimitation(int itemMaxLimitation) {
        this.itemMaxLimitation = itemMaxLimitation;
    }

    public int getItemMinLimitation() {
        return itemMinLimitation;
    }

    public void setItemMinLimitation(int itemMinLimitation) {
        this.itemMinLimitation = itemMinLimitation;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStorePincode() {
        return storePincode;
    }

    public void setStorePincode(String storePincode) {
        this.storePincode = storePincode;
    }

    public String getStoreAdress() {
        return storeAdress;
    }

    public void setStoreAdress(String storeAdress) {
        this.storeAdress = storeAdress;
    }

    @Override
    public int compareTo(ItemDetails o) {
        return 0;
    }
}
