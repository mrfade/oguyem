package dev.solak.oguyem.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import java.util.ArrayList;
import java.util.List;

import dev.solak.oguyem.fragments.MenuWeekFragment;
import dev.solak.oguyem.models.Menu;

public class MenuWeekAdapter extends FragmentStateAdapter {

    FragmentActivity activity;
    ArrayList<Fragment> fms = new ArrayList<>();
    ArrayList<Long> fmIds = new ArrayList<>();
    int row = 0;
    List<Menu> menu;

    public MenuWeekAdapter(FragmentActivity fa, int row, List<Menu> menu) {
        super(fa);
        this.activity = fa;
        this.row = row;
        this.menu = menu;
    }

    @NonNull
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
        return menu.size();
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

        FragmentManager manager = ((AppCompatActivity)activity).getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(tag);

        if (fragment != null) {
            //manual update fragment
        } else {
            // fragment might be null, if it`s call of notifyDatasetChanged()
            // which is updates whole list, not specific fragment
            super.onBindViewHolder(holder, position, payloads);
        }
    }
}
