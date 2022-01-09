package dev.solak.oguyem.classes;

import java.io.IOException;

import dev.solak.oguyem.interfaces.oguyemApi;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
//    public static final String BASE_URL = "https://oguyem-api.solak.dev/";
    public static final String BASE_URL = "https://oguyem-apiv2.solak.dev/";

    public static oguyemApi apiService;

    public static void init() {
        // Define the interceptor, add authentication headers
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request();
                HttpUrl url = newRequest.url().newBuilder().addQueryParameter("device_id", User.user.getDeviceId()).build();
                newRequest = newRequest.newBuilder().url(url).build();
                return chain.proceed(newRequest);
            }
        };

        // init HttpLoggingInterceptor
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Add the interceptor to OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        builder.networkInterceptors().add(httpLoggingInterceptor);
        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(oguyemApi.class);
    }
}
