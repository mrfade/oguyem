package dev.solak.oguyem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.solak.oguyem.adapters.CommentsAdapter;
import dev.solak.oguyem.models.Comment;
import dev.solak.oguyem.models.CommentsResponse;
import dev.solak.oguyem.models.Food;
import dev.solak.oguyem.models.Menu;
import dev.solak.oguyem.models.MenusResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuDayActivity extends AppCompatActivity {

    private int arrPosition;
    private Menu menu;
    private List<Comment> comments;
    private CommentsAdapter commentsAdapter;

    LinearLayout layout, innerLayout, menuLayout;
    CardView cardView, commentsCardView;
    TextView textViewDate, textViewMenuCalorie;
    ProgressBar progressBar;
    ListView commentsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_day);

        setWindowTransitions();

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        arrPosition = intent.getIntExtra("arrPosition", 0);
        menu = MenuWeekActivity.menu.get(arrPosition);

        layout = findViewById(R.id.layout);
        cardView = findViewById(R.id.card_view);
        innerLayout = findViewById(R.id.inner_layout);
        menuLayout = findViewById(R.id.menu_layout);
        textViewDate = findViewById(R.id.date);
        textViewMenuCalorie = findViewById(R.id.menuCalorie);

        progressBar = findViewById(R.id.progressBar);
        commentsCardView = findViewById(R.id.comments_card_view);
        commentsListView = findViewById(R.id.comments_list);

        String formattedDate = Utils.formatTitleDate(menu.getDate());
        textViewDate.setText(formattedDate);
        String menuCalorie = Utils.calculateMenuCalorie(menu.getFoodList());
        textViewMenuCalorie.setText(String.format("Toplam kalori: %s", menuCalorie));

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int i = 0;
        List<Food> foodList = new ArrayList<Food>(menu.getFoodList());
        Collections.reverse(foodList);
        for (Food food : foodList) {
            View item = inflater.inflate(R.layout.fragment_menu_item, null, false);
            TextView name = item.findViewById(R.id.name);
            TextView calorie = item.findViewById(R.id.calorie);

            name.setText(food.getName());
            calorie.setText(food.getCalorie());

            menuLayout.addView(item, 0);
        }

        fetchCommentsFromApi();
    }

    private void fetchCommentsFromApi() {
        progressBar.setVisibility(View.VISIBLE);

        Call<CommentsResponse> call = API.apiService.getComments(menu.getDate());
        call.enqueue(new Callback<CommentsResponse>() {
            @Override
            public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {

                if (response.isSuccessful()) {
                    CommentsResponse commentsResponse = response.body();

                    comments = commentsResponse.getComments();

                    Log.d("CommentsFetch", "Success");

                    commentsCardView.setVisibility(View.VISIBLE);
                    populateComments();

                    progressBar.setVisibility(View.GONE);
                } else {
                    Log.d("CommentsFetch", "Failed - Request not successful!");
                    commentsFetchFailed();
                }
            }

            @Override
            public void onFailure(Call<CommentsResponse> call, Throwable t) {
                Log.d("CommentsFetch", "Failed - Request error!");
                commentsFetchFailed();
            }
        });
    }

    private void commentsFetchFailed() {
        Toast.makeText(MenuDayActivity.this, R.string.comment_fetch_failed, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    private void populateComments() {
        commentsAdapter = new CommentsAdapter(comments, MenuDayActivity.this);
        commentsListView.setAdapter(commentsAdapter);
    }

    private void setWindowTransitions() {
        /*// here we are initializing
        // fade animation.
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();

        // here also we are excluding status bar,
        // action bar and navigation bar from animation.
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        // below methods are used for adding
        // enter and exit transition.
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);*/

        Slide slide = new Slide();
        slide.setDuration(300);
        slide.setSlideEdge(Gravity.BOTTOM);

        View decor = getWindow().getDecorView();

        slide.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);

        /*getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);*/
        getWindow().setAllowEnterTransitionOverlap(false);

        ChangeBounds changeBounds = new ChangeBounds();

        getWindow().setSharedElementEnterTransition(changeBounds);
        getWindow().setSharedElementExitTransition(changeBounds);
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finishAfterTransition();
    }
}