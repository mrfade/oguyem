package dev.solak.oguyem.interfaces;

import java.util.List;

import dev.solak.oguyem.models.MenuResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface oguyemApi {

    @GET("/")
    Call<MenuResponse> getMenu();
}
