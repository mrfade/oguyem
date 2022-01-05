package dev.solak.oguyem;

import dev.solak.oguyem.interfaces.oguyemApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    public static final String BASE_URL = "https://oguyem-api.solak.dev/";

    public static oguyemApi apiService;

    public static void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(oguyemApi.class);
    }
}
