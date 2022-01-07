package dev.solak.oguyem.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MenusResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("menu")
    @Expose
    private List<Menu> menu = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Menu> getMenu() {
        return menu;
    }

    public void setMenu(List<Menu> menu) {
        this.menu = menu;
    }

}
