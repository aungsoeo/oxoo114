package com.burmesesubtitle.app.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.NativeAdLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.ixidev.gdpr.GDPRChecker;
import com.burmesesubtitle.app.DetailsActivity;
import com.burmesesubtitle.app.ItemMovieActivity;
import com.burmesesubtitle.app.ItemSeriesActivity;
import com.burmesesubtitle.app.ItemTVActivity;
import com.burmesesubtitle.app.MainActivity;
import com.burmesesubtitle.app.R;
import com.burmesesubtitle.app.adapters.GenreAdapter;
import com.burmesesubtitle.app.adapters.GenreHomeAdapter;
import com.burmesesubtitle.app.adapters.HomePageAdapter;
import com.burmesesubtitle.app.adapters.LiveTvHomeAdapter;
import com.burmesesubtitle.app.models.CommonModels;
import com.burmesesubtitle.app.models.GenreModel;
import com.burmesesubtitle.app.utils.ApiResources;
import com.burmesesubtitle.app.utils.ads.BannerAds;
import com.burmesesubtitle.app.utils.Constants;
import com.burmesesubtitle.app.utils.NetworkInst;
import com.burmesesubtitle.app.utils.ToastMsg;
import com.burmesesubtitle.app.utils.VolleySingleton;
import com.burmesesubtitle.app.utils.ads.FanAds;
import com.burmesesubtitle.app.utils.ads.StartappAds;
import com.squareup.picasso.Picasso;
import com.startapp.android.publish.adsCommon.AutoInterstitialPreferences;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.burmesesubtitle.app.Config;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    ViewPager viewPager;
    CirclePageIndicator indicator;

    private List<CommonModels> listSlider = new ArrayList<>();

    private Timer timer;

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerViewMovie, recyclerViewTv, recyclerViewTvSeries, recyclerViewGenre;
    private RecyclerView genreRv;
    private RecyclerView countryRv;
    private GenreAdapter genreAdapter;
    private GenreAdapter countryAdapter;
    private RelativeLayout genreLayout, countryLayout;
    private HomePageAdapter adapterMovie, adapterSeries;
    private LiveTvHomeAdapter adapterTv;
    private List<CommonModels> listMovie = new ArrayList<>();
    private List<CommonModels> listTv = new ArrayList<>();
    private List<CommonModels> listSeries = new ArrayList<>();
    private List<CommonModels> genreList = new ArrayList<>();
    private List<CommonModels> countryList = new ArrayList<>();
    private ApiResources apiResources;
    private Button btnMoreMovie, btnMoreTv, btnMoreSeries;

    private int checkPass = 0;

    private SliderAdapter sliderAdapter;

    private VolleySingleton singleton;
    private TextView tvNoItem;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView scrollView;

    private RelativeLayout adView, adView1;
    private NativeAdLayout nativeAdView;

    private List<GenreModel> listGenre = new ArrayList<>();

    private GenreHomeAdapter genreHomeAdapter;
    private View sliderLayout;

    private MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();

        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

//        if (Constants.IS_ENABLE_AD.equals("1")) {
//            if (Constants.ACTIVE_AD_NETWORK.equals("startapp")) {
//
//                SharedPreferences startappPreference = getContext().getSharedPreferences(Constants.APP_CONFIG, MODE_PRIVATE);
//                StartAppSDK.init(getContext(), startappPreference.getString(Constants.STARTAPP_APP_ID, ""), true);
//
//            }
//        }

        super.onViewCreated(view, savedInstanceState);

//        getActivity().setTitle(getResources().getString(R.string.app_title));
        getActivity().setTitle("  ");
        ((MainActivity)getActivity()).showImage();
        apiResources = new ApiResources();

        singleton = new VolleySingleton(getActivity());

        adView = view.findViewById(R.id.adView);
        adView1 = view.findViewById(R.id.adView1);
        nativeAdView = view.findViewById(R.id.nativeAdContainer);
        btnMoreSeries = view.findViewById(R.id.btn_more_series);
//        btnMoreTv = view.findViewById(R.id.btn_more_tv);
        btnMoreMovie = view.findViewById(R.id.btn_more_movie);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        viewPager = view.findViewById(R.id.viewPager);
        indicator = view.findViewById(R.id.indicator);
        tvNoItem = view.findViewById(R.id.tv_noitem);
        coordinatorLayout = view.findViewById(R.id.coordinator_lyt);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        scrollView = view.findViewById(R.id.scrollView);
        sliderLayout = view.findViewById(R.id.slider_layout);
        genreRv = view.findViewById(R.id.genre_rv);
        countryRv = view.findViewById(R.id.country_rv);
        genreLayout = view.findViewById(R.id.genre_layout);
        countryLayout = view.findViewById(R.id.country_layout);

        if (!Constants.IS_GENRE_SHOW) {
            genreLayout.setVisibility(View.GONE);
        }
        if (!Constants.IS_COUNTRY_SHOW) {
            countryLayout.setVisibility(View.GONE);
        }

        sliderAdapter = new SliderAdapter(getActivity(), listSlider);
        viewPager.setAdapter(sliderAdapter);
        indicator.setViewPager(viewPager);

        //----init timer slider--------------------
        timer = new Timer();


        //----btn click-------------
        btnClick();

        // --- genre recycler view ---------
        genreRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        genreRv.setHasFixedSize(true);
        genreRv.setNestedScrollingEnabled(false);
        genreAdapter = new GenreAdapter(getActivity(), genreList, "genre");
        genreRv.setAdapter(genreAdapter);

        // --- genre recycler view ---------
        countryRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        countryRv.setHasFixedSize(true);
        countryRv.setNestedScrollingEnabled(false);
        countryAdapter = new GenreAdapter(getActivity(), countryList, "country");
        countryRv.setAdapter(countryAdapter);

        //----featured tv recycler view-----------------
        recyclerViewTv = view.findViewById(R.id.recyclerViewTv);
        recyclerViewTv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTv.setHasFixedSize(true);
        recyclerViewTv.setNestedScrollingEnabled(false);
        adapterTv = new LiveTvHomeAdapter(getContext(), listTv, "MainActivity");
        recyclerViewTv.setAdapter(adapterTv);


        //----movie's recycler view-----------------
        recyclerViewMovie = view.findViewById(R.id.recyclerView);
        recyclerViewMovie.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMovie.setHasFixedSize(true);
        recyclerViewMovie.setNestedScrollingEnabled(false);
        adapterMovie = new HomePageAdapter(getContext(), listMovie);
        recyclerViewMovie.setAdapter(adapterMovie);

        //----series's recycler view-----------------
        recyclerViewTvSeries = view.findViewById(R.id.recyclerViewTvSeries);
        recyclerViewTvSeries.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTvSeries.setHasFixedSize(true);
        recyclerViewTvSeries.setNestedScrollingEnabled(false);
        adapterSeries = new HomePageAdapter(getActivity(), listSeries);
        recyclerViewTvSeries.setAdapter(adapterSeries);

        //----genre's recycler view--------------------
        recyclerViewGenre = view.findViewById(R.id.recyclerView_by_genre);
        recyclerViewGenre.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGenre.setHasFixedSize(true);
        recyclerViewGenre.setNestedScrollingEnabled(false);
        genreHomeAdapter = new GenreHomeAdapter(getContext(), listGenre);
        recyclerViewGenre.setAdapter(genreHomeAdapter);


        shimmerFrameLayout.startShimmer();


        if (new NetworkInst(getContext()).isNetworkAvailable()) {

            if (Constants.IS_GENRE_SHOW) {
                getAllGenre();
            }
            if (Constants.IS_COUNTRY_SHOW) {
                getAllCountry();
            }
            getFeaturedTV();
            getSlider(apiResources.getSlider());
            getLatestSeries();
            getLatestMovie();
            getDataByGenre();


        } else {
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                recyclerViewMovie.removeAllViews();
                recyclerViewTv.removeAllViews();
                recyclerViewTvSeries.removeAllViews();
                recyclerViewGenre.removeAllViews();

                genreList.clear();
                countryList.clear();
                listMovie.clear();
                listSeries.clear();
                listSlider.clear();
                listTv.clear();
                listGenre.clear();


                if (new NetworkInst(getContext()).isNetworkAvailable()) {
                    if (Constants.IS_GENRE_SHOW) {
                        getAllGenre();
                    }
                    if (Constants.IS_COUNTRY_SHOW) {
                        getAllCountry();
                    }
                    getFeaturedTV();
                    getSlider(apiResources.getSlider());
                    getLatestSeries();
                    getLatestMovie();
                    getDataByGenre();
                } else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                }
            }
        });


        getAdDetails(new ApiResources().getAdDetails());


    }

    private void loadAd() {
//        SharedPreferences preferences = getContext().getSharedPreferences("appConfig", MODE_PRIVATE);

//        if (Constants.IS_ENABLE_AD.equals("1")){
//            if (Constants.ACTIVE_AD_NETWORK.equals("admob")){
//                BannerAds.ShowBannerAds(activity, adView);
//                BannerAds.ShowBannerAds(activity, adView1);
//            }else if (Constants.ACTIVE_AD_NETWORK.equals("fan")){
//                FanAds.showBanner(activity, adView);
//                FanAds.showBanner(activity, adView1);
//                //FanAds.showNativeAd(activity, nativeAdView);
//             }else if (Constants.ACTIVE_AD_NETWORK.equals("startapp")){
//
//                StartAppSDK.setUserConsent (getContext(),
//                        "pas",
//                        System.currentTimeMillis(),
//                        true);
//                StartAppAd.setAutoInterstitialPreferences(
//                        new AutoInterstitialPreferences()
//                                .setSecondsBetweenAds(60)
//                );
//
//                StartAppAd startAppAd = new StartAppAd(getContext());
//                startAppAd.loadAd();
//                startAppAd.showAd();
//
//                StartappAds.showBannerAd(getContext(), adView);
//                StartappAds.showBannerAd(getContext(), adView1);
//            }
//        }

    }

    private void btnClick() {

        btnMoreMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemMovieActivity.class);
                intent.putExtra("url", apiResources.getGet_movie());
                intent.putExtra("title", "Movies");
                getActivity().startActivity(intent);
            }
        });

//        btnMoreTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), ItemTVActivity.class);
//                intent.putExtra("url", apiResources.getGet_live_tv());
//                intent.putExtra("title", "Live TV");
//                getActivity().startActivity(intent);
//            }
//        });
        btnMoreSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemSeriesActivity.class);
                intent.putExtra("url", apiResources.getTvSeries());
                intent.putExtra("title", "TV Series");
                getActivity().startActivity(intent);
            }
        });

    }


    private void getAdDetails(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject jsonObject = response.getJSONObject("admob");

                    ApiResources.adStatus = jsonObject.getString("status");
                    ApiResources.adMobBannerId = jsonObject.getString("admob_banner_ads_id");
                    ApiResources.adMobInterstitialId = jsonObject.getString("admob_interstitial_ads_id");
                    ApiResources.adMobPublisherId = jsonObject.getString("admob_publisher_id");


                    new GDPRChecker()
                            .withContext(activity)
                            .withPrivacyUrl(Config.TERMS_URL) // your privacy url
                            .withPublisherIds(ApiResources.adMobPublisherId) // your admob account Publisher id
                            .withTestMode("9424DF76F06983D1392E609FC074596C") // remove this on real project
                            .check();

                    loadAd();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);


    }


    private void getDataByGenre() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, new ApiResources().getGenreMovieURL(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {

                    try {

                        JSONObject jsonObject = response.getJSONObject(i);

                        GenreModel models = new GenreModel();

                        models.setName(jsonObject.getString("name"));
                        models.setId(jsonObject.getString("genre_id"));
                        JSONArray jsonArray = jsonObject.getJSONArray("videos");
                        //listGenreMovie.clear();
                        List<CommonModels> listGenreMovie = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {

                            JSONObject movieObject = jsonArray.getJSONObject(j);

                            CommonModels commonModels = new CommonModels();

                            commonModels.setId(movieObject.getString("videos_id"));
                            commonModels.setTitle(movieObject.getString("title"));

                            if (movieObject.getString("is_tvseries").equals("0")) {
                                commonModels.setVideoType("movie");
                            } else {
                                commonModels.setVideoType("tvseries");
                            }


                            commonModels.setReleaseDate(movieObject.getString("release"));
                            commonModels.setQuality(movieObject.getString("video_quality"));
                            commonModels.setImageUrl(movieObject.getString("thumbnail_url"));

                            listGenreMovie.add(commonModels);

                        }


                        models.setList(listGenreMovie);

                        listGenre.add(models);
                        genreHomeAdapter.notifyDataSetChanged();
//                        Log.e("LIST 2 SIZE ::", String.valueOf(listGenreMovie.size()));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);


    }

    private void getSlider(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    swipeRefreshLayout.setRefreshing(false);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    coordinatorLayout.setVisibility(View.GONE);


                    if (response.getString("slider_type").equals("disable")) {
                        sliderLayout.setVisibility(View.GONE);
                    } else if (response.getString("slider_type").equals("movie")) {

                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CommonModels models = new CommonModels();
                            models.setImageUrl(jsonObject.getString("poster_url"));
                            models.setTitle(jsonObject.getString("title"));
                            models.setVideoType("movie");
                            models.setId(jsonObject.getString("videos_id"));

                            listSlider.add(models);
                        }

                    } else {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CommonModels models = new CommonModels();
                            models.setImageUrl(jsonObject.getString("image_link"));
                            models.setTitle(jsonObject.getString("title"));
                            models.setVideoType("image");
                            listSlider.add(models);

                        }
                    }

                    sliderAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);

            }
        });

        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);


    }


    private void getLatestSeries() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getLatestTvSeries(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setTitle(jsonObject.getString("title"));
                        models.setVideoType("tvseries");
                        models.setReleaseDate(jsonObject.getString("release"));
                        models.setQuality(jsonObject.getString("video_quality"));
                        models.setId(jsonObject.getString("videos_id"));
                        listSeries.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterSeries.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }

    private void getLatestMovie() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getLatest_movie(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setTitle(jsonObject.getString("title"));
                        models.setVideoType("movie");
                        models.setReleaseDate(jsonObject.getString("release"));
                        models.setQuality(jsonObject.getString("video_quality"));
                        models.setId(jsonObject.getString("videos_id"));
                        listMovie.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterMovie.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }

    private void getFeaturedTV() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getGet_featured_tv(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setTitle(jsonObject.getString("tv_name"));
                        models.setVideoType("tv");
                        models.setId(jsonObject.getString("live_tv_id"));
                        listTv.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterTv.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }

    @Override
    public void onStart() {
        super.onStart();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 5000, 5000);
    }

    //----timer for auto slide------------------
    private class SliderTimer extends TimerTask {

        @Override
        public void run() {

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() < listSlider.size() - 1) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }

        }
    }

    //----adapter for slider-------------
    public class SliderAdapter extends PagerAdapter {

        private Context context;
        private List<CommonModels> list;

        public SliderAdapter(Context context, List<CommonModels> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_slider, null);

            View lyt_parent = view.findViewById(R.id.lyt_parent);

            final CommonModels models = list.get(position);

            TextView textView = view.findViewById(R.id.textView);

            textView.setText(models.getTitle());

            ImageView imageView = view.findViewById(R.id.imageview);

            Picasso.get().load(models.getImageUrl()).into(imageView);


            ViewPager viewPager = (ViewPager) container;
            viewPager.addView(view, 0);

            lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (models.getVideoType().equals("movie")) {

                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra("vType", models.getVideoType());
                        intent.putExtra("id", models.getId());
                        startActivity(intent);

                    } else {

                    }
                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewPager viewPager = (ViewPager) container;
            View view = (View) object;
            viewPager.removeView(view);
        }
    }

    private void getAllGenre() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getAllGenre(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (String.valueOf(response).length() < 10) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                } else {
                    coordinatorLayout.setVisibility(View.GONE);
                }

                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setId(jsonObject.getString("genre_id"));
                        models.setTitle(jsonObject.getString("name"));
                        models.setImageUrl(jsonObject.getString("image_url"));
                        genreList.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //Toast.makeText(activity, "size:" + genreList.size(), Toast.LENGTH_SHORT).show();
                genreAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                new ToastMsg(getActivity()).toastIconError(getString(R.string.fetch_error));

                coordinatorLayout.setVisibility(View.VISIBLE);
            }
        });
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);


    }

    private void getAllCountry() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getAllCountry(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);


                if (String.valueOf(response).length() < 10) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                } else {
                    coordinatorLayout.setVisibility(View.GONE);
                }

                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setTitle(jsonObject.getString("name"));
                        models.setId(jsonObject.getString("country_id"));
                        models.setImageUrl(jsonObject.getString("image_url"));
                        countryList.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                countryAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                new ToastMsg(getActivity()).toastIconError(getString(R.string.fetch_error));
                coordinatorLayout.setVisibility(View.VISIBLE);

            }
        });
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);

    }


}
