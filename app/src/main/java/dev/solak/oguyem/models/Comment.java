package dev.solak.oguyem.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Comment {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("menu_id")
    @Expose
    private Integer menuId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("upvotes")
    @Expose
    private Integer upvotes;
    @SerializedName("downvotes")
    @Expose
    private Integer downvotes;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("user_vote")
    @Expose
    private Integer userVote = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }

    public Integer getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(Integer downvotes) {
        this.downvotes = downvotes;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Integer getUserVote() {
        return userVote;
    }

    public void setUserVote(Integer userVote) {
        this.userVote = userVote;
    }

}