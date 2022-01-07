package dev.solak.oguyem.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentsResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("comments")
    @Expose
    private List<Comment> comments = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}