package com.mix.melody.AdapterClass;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mix.melody.UI.FavouriteFragment;
import com.mix.melody.UI.HomeFragment;
import com.mix.melody.UI.PlayListFragment;
import com.mix.melody.UI.PlayNextFragment;


//public class ViewpagerAdpter  extends FragmentPagerAdapter {
//    public ViewpagerAdpter(@NonNull FragmentManager fm) {
//        super(fm);
//    }
//
//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//        if (position == 0) {
//            return new HomeFragment();
//        }  else if (position == 1)
//    {
//        return new FavouriteFragment();
//    }
//        else if(position==2){
//            return new PlayListFragment();
//        }
//        else {
//            return  new PlayNextFragment();
//        }
//    }
//
//
//    @Override
//    public int getCount() {
//        return 4;
//    }
//
//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//
//        if (position == 0) {
//            return "Songs";
//        }
//
//        else if (position == 1) {
//            return "Favourite";
//        }
//        else if(position==2){
//            return "Playlist";
//        }
//        else {
//            return "PlayNext";
//        }
//    }
//
//}
//


public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment(); // Return a new instance of ConectFragment for position 0
            case 1:
                return  new FavouriteFragment();
                case 2 :
                    return  new PlayListFragment();


            default:
                return new PlayNextFragment(); // Return a new instance of BusinessFragment for any other position
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Return the total number of fragments managed by the adapter, which is 3 in this case
    }
}