package dev.solak.oguyem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dev.solak.oguyem.fragments.MenuWeekFragment;
import dev.solak.oguyem.models.Menu;
import dev.solak.oguyem.models.MenuResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuWeekActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 7;

    public static boolean menuFromCache = false;
    public static List<Menu> menu;
    ArrayList<List<Menu>> rows = new ArrayList<List<Menu>>();
    ArrayList<ViewPager2> viewPagers = new ArrayList<ViewPager2>();
    ArrayList<FragmentStateAdapter> pagerAdapters = new ArrayList<FragmentStateAdapter>();

    MenuResponse menuResponse;
    Date menuCacheLastUpdated;

    TextView textViewErrorMessage, textViewMenuRestored;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_week);

        textViewErrorMessage = findViewById(R.id.error_message);
        textViewMenuRestored = findViewById(R.id.menu_restored);
        swipeRefreshLayout = findViewById(R.id.swipe_container);

        // set transitions
        setWindowTransitions();
        // init api
        API.init();

        if (Utils.isNetworkConnected(this)) {
            fetchMenuFromApi();
        } else {
            restoreMenuFromCache();
            populateMenus();
            textViewError(R.string.no_internet_connection);
        }

        // setup swipe refresh
        setupSwipeRefresh();
    }

    private void fetchMenuFromApi() {
        loading = ProgressDialog.show(this, getString(R.string.menu_fetching), getString(R.string.please_wait), false, false);

        Call<MenuResponse> call = API.apiService.getMenu();
        call.enqueue(new Callback<MenuResponse>() {
            @Override
            public void onResponse(Call<MenuResponse> call, Response<MenuResponse> response) {

                if (response.isSuccessful()) {
                    menuResponse = response.body();

                    menu = menuResponse.getMenu();

                    Log.d("MenuFetch", "Success");

                    saveMenuToCache();
                    populateMenus();

                    loading.dismiss();
                } else {
                    Log.d("MenuFetch", "Failed - Request not successful!");
                    menuFetchFailed();
                }
            }

            @Override
            public void onFailure(Call<MenuResponse> call, Throwable t) {
                Log.d("MenuFetch", "Failed - Request error!");
                menuFetchFailed();
            }
        });
    }

    private void menuFetchFailed() {
        textViewError(R.string.menu_fetch_failed);
        restoreMenuFromCache();
        populateMenus();
        loading.dismiss();
    }

    private void restoreMenuFromCache() {
        menuFromCache = true;
        Log.d("MenuFetch", "Restoring...");

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String menuCache = sharedPreferences.getString("menuCache", "[]");
        String menuCacheLastUpdated = sharedPreferences.getString("menuCacheLastUpdated", Utils.formatDateNow());

        try {
            this.menuCacheLastUpdated = Utils.parseDate("yyyy-MM-dd HH:mm", menuCacheLastUpdated);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("MenuFetch", String.format("Restore Successful - Last updated: %s", menuCacheLastUpdated));
        Log.d("MenuFetch", menuCache);

        Gson gson = new Gson();
        MenuResponse obj = gson.fromJson(menuCache, MenuResponse.class);

        menu = obj.getMenu();

        textViewMenuRestored.setVisibility(View.VISIBLE);
        textViewMenuRestored.setText(String.format(getString(R.string.last_updated_at), Utils.formatDate("d MMM. HH:mm", this.menuCacheLastUpdated)));
    }

    private void saveMenuToCache() {
        Log.d("MenuFetch", "Caching...");

        String menuCacheLastUpdated = Utils.formatDateNow();

        Gson gson = new Gson();
        String json = gson.toJson(menuResponse);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("menuCache", json);
        editor.putString("menuCacheLastUpdated", menuCacheLastUpdated);
        editor.apply();

        Log.d("MenuFetch", "Cache Successful");
        Log.d("MenuFetch", json);
    }

    private void populateMenus() {
        /*rows.add(new ArrayList<Menu>(menu.subList(7, 14)));
        rows.add(new ArrayList<Menu>(menu.subList(14, 21)));
        rows.add(new ArrayList<Menu>(menu.subList(21, 28)));
        rows.add(new ArrayList<Menu>(menu.subList(28, 35)));
        rows.add(new ArrayList<Menu>(menu.subList(0, 7)));*/

        for (int i = 0; i < 5; i++) {
            // create rows
//            List<Menu> submenu = rows.get(i);
            List<Menu> submenu = new ArrayList<Menu>(menu.subList(i * 7, (i + 1) * 7));
            rows.add(submenu);
            // add viewpagers
            viewPagers.add(findViewById(getResources().getIdentifier("pager" + (i + 1), "id", getPackageName())));
            // create adapter
            FragmentStateAdapter adapter = new MenuWeekAdapter(this, i, submenu);
            // add adapter
            pagerAdapters.add(adapter);
            // set adapter
            viewPagers.get(i).setAdapter(adapter);
            // set transformer
            viewPagers.get(i).setPageTransformer(new ZoomOutPageTransformer());
        }

        for (List<Menu> mmmm : rows) {
            StringBuilder str = new StringBuilder();
            for (Menu mm : mmmm) {
                str.append(String.format(" %s", mm.getDate()));
            }

            Log.d("ROWN", String.format("RowN %s", str.toString()));
        }

        /*for (int i = 0; i < 5; i++) {

        }*/

        moveMenuToToday();
    }

    private void rePopulateMenus() {
        // clear rows
        rows.clear();
        // clear adapters
        pagerAdapters.clear();
        for (int i = 0; i < 5; i++) {
            // create rows
            List<Menu> submenu = new ArrayList<Menu>(menu.subList(i * 7, (i + 1) * 7));
            rows.add(submenu);
            // create adapter
            FragmentStateAdapter adapter = new MenuWeekAdapter(this, i, submenu);
            // add adapter
            pagerAdapters.add(adapter);
            // set adapter
            viewPagers.get(i).setAdapter(adapter);
        }

        moveMenuToToday();
    }

    private void moveMenuToToday() {
        // get today's date as string
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        // move viewpager to today's menu
        for (int i = 0; i < 7; i++) {
            if (currentDate.equals(rows.get(0).get(i).getDate())) {
                viewPagers.get(0).setCurrentItem(i);
            }
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Call<MenuResponse> call = API.apiService.getMenu();
                call.enqueue(new Callback<MenuResponse>() {
                    @Override
                    public void onResponse(Call<MenuResponse> call, Response<MenuResponse> response) {

                        if (response.isSuccessful()) {
                            menuResponse = response.body();
                            menu = menuResponse.getMenu();

                            Log.d("MenuFetch", "Success");

                            saveMenuToCache();
                            rePopulateMenus();

                            // hide error messages
                            textViewErrorMessage.setVisibility(View.GONE);
                            textViewMenuRestored.setVisibility(View.GONE);
                        } else {
                            Log.d("MenuFetch", "Failed - Request not successful!");
                            Toast.makeText(MenuWeekActivity.this, getString(R.string.menu_fetch_failed), Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<MenuResponse> call, Throwable t) {
                        Log.d("MenuFetch", "Failed - Request error!");
                        Toast.makeText(MenuWeekActivity.this, getString(R.string.menu_fetch_failed), Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });

        // Scheme colors for animation
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright, getTheme()),
                getResources().getColor(android.R.color.holo_green_light, getTheme()),
                getResources().getColor(android.R.color.holo_orange_light, getTheme()),
                getResources().getColor(android.R.color.holo_red_light, getTheme())
        );
    }

    private void setWindowTransitions() {
        /*Slide slide = new Slide();
        slide.setDuration(300);
        slide.setSlideEdge(Gravity.BOTTOM);*/

        Fade fade = new Fade();
        fade.setDuration(300);

        View decor = getWindow().getDecorView();

        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setReenterTransition(fade);
        getWindow().setExitTransition(fade);
        getWindow().setAllowReturnTransitionOverlap(false);

        ChangeBounds changeBounds = new ChangeBounds();

        getWindow().setSharedElementReenterTransition(changeBounds);
        getWindow().setSharedElementExitTransition(changeBounds);
    }

    private void textViewError(int resid) {
        textViewErrorMessage.setVisibility(View.VISIBLE);
        textViewErrorMessage.setText(resid);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        /*if (viewPagers.get(0).getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPagers.get(0).setCurrentItem(viewPagers.get(0).getCurrentItem() - 1);
        }*/
    }

    private class MenuWeekAdapter extends FragmentStateAdapter {

        ArrayList<Fragment> fms = new ArrayList<>();
        ArrayList<Long> fmIds = new ArrayList<>();
        int row = 0;
        List<Menu> menu;

        public MenuWeekAdapter(FragmentActivity fa, int row, List<Menu> menu) {
            super(fa);
            this.row = row;
            this.menu = menu;
        }

        @Override
        public Fragment createFragment(int position) {
            int evenodd = row % 2 == 1 ? position + 1 % 2 : position % 2;
            int arrPosition = (row * 7) + position;
            Fragment fragment = new MenuWeekFragment(menu.get(position), position, arrPosition, evenodd);
            fms.add(fragment);
            fmIds.add((long) fragment.hashCode());
            return fragment;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }

        /*@Override
        public long getItemId(int position) {
            // generate new
            long hashcode = createFragment(position).hashCode();
            Log.d("hashcode", String.valueOf(hashcode));
            return hashcode;
        }

        @Override
        public boolean containsItem(long itemId) {
            // false if item is changed
            Log.d("contains", String.valueOf(fmIds.contains(itemId)));
            return fmIds.contains(itemId);
        }*/

        public void onBindViewHolder2(@NonNull FragmentViewHolder holder, int position, @NonNull List<Object> payloads) {

            String tag = "f" + holder.getItemId();

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);

            if (fragment != null) {
                //manual update fragment
            } else {
                // fragment might be null, if it`s call of notifyDatasetChanged()
                // which is updates whole list, not specific fragment
                super.onBindViewHolder(holder, position, payloads);
            }
        }
    }
}
