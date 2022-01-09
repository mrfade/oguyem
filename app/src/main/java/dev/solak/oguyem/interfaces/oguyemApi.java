package dev.solak.oguyem.interfaces;

import java.util.Map;

import dev.solak.oguyem.models.Comment;
import dev.solak.oguyem.models.CommentsResponse;
import dev.solak.oguyem.models.InfoResponse;
import dev.solak.oguyem.models.MenuResponse;
import dev.solak.oguyem.models.MenusResponse;
import dev.solak.oguyem.models.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface oguyemApi {

    @GET("/menus")
    Call<MenusResponse> getMenus();

    @GET("/menus/{date}")
    Call<MenuResponse> getMenu(@Path("date") String date);

    @GET("/menus/{date}/comments")
    Call<CommentsResponse> getComments(@Path("date") String date);

    @Multipart
    @POST("/menus/{date}/comments")
    Call<Comment> newComment(@Path("date") String date, @PartMap Map<String, RequestBody> params);

    @DELETE("/menus/{date}/comments/{id}")
    Call<InfoResponse> deleteComment(@Path("date") String date, @Path("id") Integer id);

    @POST("/devices/register")
    Call<User> registerDevice(@Body User user);
}
