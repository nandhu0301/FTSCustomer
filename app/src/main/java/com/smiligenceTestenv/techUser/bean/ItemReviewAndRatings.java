package com.smiligenceTestenv.techUser.bean;

public class ItemReviewAndRatings
{
    String itemId;
    String orderId;
    String review;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    String itemName;
    int stars;

    public String getRatingReviewId() {
        return ratingReviewId;
    }

    public void setRatingReviewId(String ratingReviewId) {
        this.ratingReviewId = ratingReviewId;
    }

    String ratingReviewId;

    public String getItemRatingReviewStatus() {
        return itemRatingReviewStatus;
    }

    public void setItemRatingReviewStatus(String itemRatingReviewStatus) {
        this.itemRatingReviewStatus = itemRatingReviewStatus;
    }

    String itemRatingReviewStatus;

    public String getStarred() {
        return starred;
    }

    public void setStarred(String starred) {
        this.starred = starred;
    }

    String starred;
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}
