package dev.solak.oguyem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.solak.oguyem.interfaces.oguyemApi;
import dev.solak.oguyem.models.Food;
import dev.solak.oguyem.models.Menu;
import dev.solak.oguyem.models.MenuResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static dev.solak.oguyem.Utils.formatTitleDate;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "https://oguyem-api.solak.dev/";
    public static final int COLOR_PRIMARY = Color.rgb(41, 128, 185);

    int displayHeight, displayWidth;

    LinearLayout row1, row2, row3, row4, row5;
    HorizontalScrollView hsv1, hsv2, hsv3, hsv4, hsv5;

    Typeface montserrat, lato;

    public static List<Menu> _menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        row1 = findViewById(R.id.row1);
        row2 = findViewById(R.id.row2);
        row3 = findViewById(R.id.row3);
        row4 = findViewById(R.id.row4);
        row5 = findViewById(R.id.row5);

        hsv1 = findViewById(R.id.hsv1);
        hsv2 = findViewById(R.id.hsv2);
        hsv3 = findViewById(R.id.hsv3);
        hsv4 = findViewById(R.id.hsv4);
        hsv5 = findViewById(R.id.hsv5);

        loadFonts();
        calculateDisplayMetrics();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        oguyemApi apiService = retrofit.create(oguyemApi.class);
        Call<MenuResponse> call = apiService.getMenu();

        call.enqueue(new Callback<MenuResponse>() {
            @Override
            public void onResponse(Call<MenuResponse> call, Response<MenuResponse> response) {

                if (response.isSuccessful()) {
                    MenuResponse menuResponse = response.body();

                    _menu = menuResponse.getMenu();

                    populateMenuScreen(menuResponse.getMenu());
                } else {
                    Log.d("Yo", "Boo!");
                    return;
                }
            }

            @Override
            public void onFailure(Call<MenuResponse> call, Throwable t) {
                Log.d("Yo", "Errror!");
            }

        });


    }

    private void loadFonts() {
        montserrat = ResourcesCompat.getFont(this, R.font.montserrat);
        lato = ResourcesCompat.getFont(this, R.font.lato);
    }

    private void calculateDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayHeight = displayMetrics.heightPixels;
        displayWidth = displayMetrics.widthPixels;
    }

    private void populateMenuScreen(List<Menu> menu) {
        int evenodd = 0;
        for (Menu day : menu) {

            String dateFormatted = Utils.formatTitleDate(day.getDate());

            // Create Layout
            LinearLayout layout = new LinearLayout(getApplicationContext());
            // Create Layout Params
            int marginRL = 20, marginTB = 20;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(displayWidth - marginRL * 2, 750);
            layoutParams.setMargins(marginRL, marginTB, marginRL, marginTB);
            layout.setLayoutParams(layoutParams);
            layout.setOrientation(LinearLayout.VERTICAL);

            layout.setBackgroundColor(evenodd++ % 2 == 0 ? Color.rgb(236, 240, 241) : Color.WHITE);

            // Create Date TextView
            TextView textViewDate = new TextView(getApplicationContext());
            textViewDate.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textViewDate.setPadding(20, 50, 20, 50);
            textViewDate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textViewDate.setBackgroundColor(COLOR_PRIMARY);
            textViewDate.setTextColor(Color.WHITE);
            textViewDate.setTextSize(20);
            textViewDate.setTypeface(montserrat);
            textViewDate.setText(dateFormatted);
            layout.addView(textViewDate);

            Log.d("Success", day.getDate());

            // Create Inner Layout
            LinearLayout innerLayout = new LinearLayout(getApplicationContext());
            innerLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
            innerLayout.setGravity(Gravity.TOP);
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setPadding(20, 20, 20, 20);

            int i = 0;
            for (Food food : day.getFoodList()) {
                // Create Row Layout
                RelativeLayout rowLayout = new RelativeLayout(getApplicationContext());
                RelativeLayout.LayoutParams params_left = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams params_right = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params_left.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                params_right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

                TextView textViewName = new TextView(getApplicationContext());
                textViewName.setLayoutParams(params_left);
                textViewName.setText(food.getName());
                textViewName.setTextSize(16);
                textViewName.setTypeface(lato);

                TextView textViewCalorie = new TextView(getApplicationContext());
                textViewCalorie.setLayoutParams(params_right);
                textViewCalorie.setText(food.getCalorie());
                textViewCalorie.setTextSize(16);
                textViewCalorie.setTypeface(lato);

                rowLayout.addView(textViewName);
                rowLayout.addView(textViewCalorie);
                innerLayout.addView(rowLayout);

                Log.d("Success", food.getName());
            }

            if (day.isWeekend()) {
                TextView textView = new TextView(getApplicationContext());
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setText("Hafta Sonu");
                textView.setTextSize(16);
                textView.setTextColor(Color.RED);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                innerLayout.setGravity(Gravity.CENTER);

                innerLayout.addView(textView);
            } else if (day.getFoodList().isEmpty()) {
                TextView textView = new TextView(getApplicationContext());
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setText("Menü Hazır Değil");
                textView.setTextSize(16);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                innerLayout.setGravity(Gravity.CENTER);

                innerLayout.addView(textView);
            }
            if (day.isHoliday()) {
                TextView textView = new TextView(getApplicationContext());
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setText("Tatil!: " + day.getHolidayTitle());
                textView.setTextSize(16);
                textView.setTextColor(Color.RED);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                innerLayout.setGravity(Gravity.CENTER);

                innerLayout.addView(textView);
            }

            // add inner layout
            layout.addView(innerLayout);

            if (evenodd <= 7)
                row1.addView(layout);
            else if (evenodd <= 14)
                row2.addView(layout);
            else if (evenodd <= 21)
                row3.addView(layout);
            else if (evenodd <= 28)
                row4.addView(layout);
            else if (evenodd <= 35)
                row5.addView(layout);
        }
    }

    public void btnOnClick(View view) {
        Intent intent = new Intent(this, MenuWeekActivity.class);
        startActivity(intent);
    }
}