package dev.solak.oguyem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.solak.oguyem.models.Food;
import dev.solak.oguyem.models.Menu;

public class MenuDayActivity extends AppCompatActivity {

    private int arrPosition;
    private Menu menu;

    LinearLayout layout, innerLayout, menuLayout;
    CardView cardView;
    TextView textViewDate, textViewMenuCalorie;

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