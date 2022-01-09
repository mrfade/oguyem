package dev.solak.oguyem.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.solak.oguyem.MenuDayActivity;
import dev.solak.oguyem.R;
import dev.solak.oguyem.classes.Utils;
import dev.solak.oguyem.models.Food;
import dev.solak.oguyem.models.Menu;

public class MenuWeekFragment extends Fragment {

    private static final String ARG_PARAM1 = "dayMenu";

    private Menu menu;
    private int textViewDateId;
    private int position, arrPosition, evenodd;
    LinearLayout layout, innerLayout, contentLayout, menuLayout;
    CardView cardView;
    TextView textViewDate, textViewWeekend, textViewHoliday, textViewMenuNotReady, textViewMenuCalorie;

    public MenuWeekFragment() {
        // Required empty public constructor
    }

    public MenuWeekFragment(Menu menu, int position, int arrPosition, int evenodd) {
        this.menu = menu;
        this.position = position;
        this.arrPosition = arrPosition;
        this.evenodd = evenodd;
    }

    public static MenuWeekFragment newInstance(Menu dayMenu) {
        MenuWeekFragment fragment = new MenuWeekFragment();
        Bundle args = new Bundle();
//        args.putParcelable(ARG_PARAM1, (Parcelable) dayMenu);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            menu = (Menu) getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_week, container, false);

        layout = view.findViewById(R.id.layout);
        cardView = view.findViewById(R.id.card_view);
        innerLayout = view.findViewById(R.id.inner_layout);
        contentLayout = view.findViewById(R.id.content_layout);
        menuLayout = view.findViewById(R.id.menu_layout);

        textViewDate = view.findViewById(R.id.date);
        textViewWeekend = view.findViewById(R.id.textViewWeekend);
        textViewHoliday = view.findViewById(R.id.textViewHoliday);
        textViewMenuNotReady = view.findViewById(R.id.textViewMenuNotReady);
        textViewMenuCalorie = view.findViewById(R.id.textViewMenuCalorie);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                if (menu.isWeekend() || menu.isHoliday() || menu.getFoodList().isEmpty()) return;

                Intent intent = new Intent(getActivity(), MenuDayActivity.class);
                intent.putExtra("arrPosition", arrPosition);

                /*Pair<View, String> p1 = Pair.create((View)view.findViewById(R.id.date), "dayTitle");
                Pair<View, String> p2 = Pair.create((View)view.findViewById(R.id.menu_layout), "menuList");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1, p2);
                startActivity(intent, options.toBundle());*/

//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view.findViewById(R.id.layout), "menuLayout").toBundle());

                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view.findViewById(R.id.date), "dayTitle").toBundle());

                Log.d("click", "fragment");
            }
        });

        String dateFormatted = Utils.formatTitleDate(menu.getDate());
        textViewDate.setText(dateFormatted);
        String menuCalorie = Utils.calculateMenuCalorie(menu.getFoodList());
        textViewMenuCalorie.setText(String.format("Toplam kalori: %s", menuCalorie));

        Log.d("Success", menu.getDate());

        int i = 0;
        List<Food> foodList = new ArrayList<Food>(menu.getFoodList());
        Collections.reverse(foodList);
        for (Food food : foodList) {
            View item = inflater.inflate(R.layout.fragment_menu_item, container, false);
            TextView name = item.findViewById(R.id.name);
            TextView calorie = item.findViewById(R.id.calorie);

            name.setText(food.getName());
            calorie.setText(food.getCalorie());

            menuLayout.addView(item, 0);

            Log.d("Success", food.getName());
        }

        if (menu.isWeekend()) {
            textViewWeekend.setText("Hafta Sonu");
            textViewWeekend.setVisibility(View.VISIBLE);
        } else if (menu.getFoodList().isEmpty()) {
            textViewMenuNotReady.setText("Menü Hazır Değil");
            textViewMenuNotReady.setVisibility(View.VISIBLE);
        }
        if (menu.isHoliday()) {
            textViewHoliday.setText(String.format("Tatil!: %s", menu.getHolidayTitle()));
            textViewHoliday.setVisibility(View.VISIBLE);
        }

        if (menu.isWeekend() || menu.isHoliday() || menu.getFoodList().isEmpty()) {
            menuLayout.setVisibility(View.GONE);
            contentLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
            contentLayout.setGravity(Gravity.CENTER);
        }

        return view;
    }
}