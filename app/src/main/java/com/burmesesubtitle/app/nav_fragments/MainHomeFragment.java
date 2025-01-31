package com.burmesesubtitle.app.nav_fragments;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.burmesesubtitle.app.fragments.TvSeriesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.burmesesubtitle.app.MainActivity;
import com.burmesesubtitle.app.R;
import com.burmesesubtitle.app.fragments.HomeFragment;
import com.burmesesubtitle.app.fragments.LiveTvFragment;
import com.burmesesubtitle.app.fragments.MoviesFragment;

import static android.content.Context.MODE_PRIVATE;

public class MainHomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private LinearLayout bottomNavBar;
    private ImageButton btnMovies,btnTvSeries;
    private FloatingActionButton btnHome;
    private Fragment fragment=null;
    private MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();

        return inflater.inflate(R.layout.fragment_main_home, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavBar = view.findViewById(R.id.bottom_nav_layout);

        SharedPreferences sharedPreferences = activity.getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            bottomNavBar.setBackgroundColor(getResources().getColor(R.color.nav_head_bg));
        }

        btnHome=view.findViewById(R.id.home);
        btnTvSeries=view.findViewById(R.id.tv_series);
        btnMovies=view.findViewById(R.id.movies);

        btnHome.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        btnMovies.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
        btnTvSeries.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));

        btnMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment=new MoviesFragment();
                loadFragment(fragment);

                btnMovies.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                btnTvSeries.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnHome.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey_40)));


            }
        });

        btnTvSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment=new TvSeriesFragment();
                loadFragment(fragment);

                btnMovies.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnTvSeries.setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary));
                btnHome.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey_40)));
            }
        });

//        btnLiveTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fragment=new LiveTvFragment();
//                loadFragment(fragment);
//
//                btnLiveTv.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
//                btnMovies.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
//                btnHome.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
//            }
//        });


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment=new HomeFragment();
                loadFragment(fragment);

                btnMovies.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnTvSeries.setColorFilter(getActivity().getResources().getColor(R.color.grey_40));
                btnHome.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            }
        });

//        bottomNavigationView = view.findViewById(R.id.navigation);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment fragment=null;
//                switch (item.getItemId()) {
//                    case R.id.navigation_home:
//                        fragment=new HomeFragment();
//                        break;
//
//                    case R.id.navigation_livetv:
//                        fragment=new LiveTvFragment();
//                        break;
//
//                    case R.id.navigation_movie:
//                        fragment=new MoviesFragment();
//                        break;
//
//                    case R.id.navigation_tvseries:
//                        fragment=new TvSeriesFragment();
//                        break;
//
//                }
//
//                loadFragment(fragment);
//                return true;
//            }
//        });

        loadFragment(new HomeFragment());

    }


    //----load fragment----------------------
    private boolean loadFragment(Fragment fragment){

        if (fragment!=null){

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();

            return true;
        }
        return false;

    }


}