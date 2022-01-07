package dev.solak.oguyem.interfaces;

import dev.solak.oguyem.models.CommentsResponse;
import dev.solak.oguyem.models.MenuResponse;
import dev.solak.oguyem.models.MenusResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface oguyemApi {

    @GET("/menus")
    Call<MenusResponse> getMenus();

    @GET("/menus/{date}")
    Call<MenuResponse> getMenu(@Path("date") String date);

    @GET("/menus/{date}/comments")
    Call<CommentsResponse> getComments(@Path("date") String date);
}
