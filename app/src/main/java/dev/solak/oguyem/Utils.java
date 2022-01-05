package dev.solak.oguyem;

import android.content.Context;
import android.net.ConnectivityManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dev.solak.oguyem.models.Food;

public class Utils {
    public static String formatTitleDate(String date) {
        try {
            Date _date = parseDate("yyyy-MM-dd", date);
            return formatDate("d MMM. EEEE", _date);
        } catch (ParseException e) {
            return date;
        }
    }

    public static String formatDateNow() {
        return formatDate("yyyy-MM-dd HH:mm", new Date());
    }

    public static Date parseDate(String format, String date) throws ParseException {
        return new SimpleDateFormat(format, Locale.getDefault()).parse(date);
    }

    public static String formatDate(String format, Date date) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

    public static String calculateMenuCalorie(List<Food> foods) {
        ArrayList<Integer> calories = new ArrayList<Integer>();

        for (Food food: foods) {
            String[] cc = food.getCalorie().split(" ");
            try {
                int calorie = Integer.parseInt(cc[0]);
                calories.add(calorie);
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

        int sum = calories.stream().mapToInt(a -> a).sum();
        return String.format("%s kcal", String.valueOf(sum));
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
