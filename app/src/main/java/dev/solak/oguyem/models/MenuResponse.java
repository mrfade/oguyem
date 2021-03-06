package dev.solak.oguyem.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MenuResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("menu")
    @Expose
    private Menu menu;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

}
