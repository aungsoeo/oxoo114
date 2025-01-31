package com.burmesesubtitle.app.nav_fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.burmesesubtitle.app.MainActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.burmesesubtitle.app.R;
import com.burmesesubtitle.app.adapters.CountryAdapter;
import com.burmesesubtitle.app.models.CommonModels;
import com.burmesesubtitle.app.utils.ApiResources;
import com.burmesesubtitle.app.utils.Constants;
import com.burmesesubtitle.app.utils.ads.BannerAds;
import com.burmesesubtitle.app.utils.NetworkInst;
import com.burmesesubtitle.app.utils.SpacingItemDecoration;
import com.burmesesubtitle.app.utils.ToastMsg;
import com.burmesesubtitle.app.utils.Tools;
import com.burmesesubtitle.app.utils.ads.FanAds;
import com.burmesesubtitle.app.utils.ads.PopUpAds;
import com.burmesesubtitle.app.utils.ads.StartappAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GenreFragment extends Fragment {

    ShimmerFrameLayout shimmerFrameLayout;
    private ApiResources apiResources;
    private RecyclerView recyclerView;
    private List<CommonModels> list = new ArrayList<>();
    private CountryAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private TextView tvNoItem;

    private RelativeLayout adView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.layout_country,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.genre));
        ((MainActivity)getActivity()).hideImage();

        adView=view.findViewById(R.id.adView);
        coordinatorLayout=view.findViewById(R.id.coordinator_lyt);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_view_container);
        swipeRefreshLayout=view.findViewById(R.id.swipe_layout);
        recyclerView=view.findViewById(R.id.recyclerView);
        tvNoItem=view.findViewById(R.id.tv_noitem);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 10), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new CountryAdapter(getContext(), list,"genre");
        recyclerView.setAdapter(mAdapter);

        apiResources=new ApiResources();

        shimmerFrameLayout.startShimmer();



        if (new NetworkInst(getContext()).isNetworkAvailable()){
            getAllGenre();
        }else {
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                coordinatorLayout.setVisibility(View.GONE);

                recyclerView.removeAllViews();
                list.clear();
                mAdapter.notifyDataSetChanged();

                if (new NetworkInst(getContext()).isNetworkAvailable()){
                    getAllGenre();
                }else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });

//        loadAd();

    }

    private void loadAd(){
//        if (Constants.IS_ENABLE_AD.equals("1")){
//            if (Constants.ACTIVE_AD_NETWORK.equals("admob")){
//                BannerAds.ShowBannerAds(getContext(), adView);
//                PopUpAds.ShowInterstitialAds(getContext());
//            }else if (Constants.ACTIVE_AD_NETWORK.equals("fan")){
//                FanAds.showBanner(getContext(), adView);
//                FanAds.showInterstitialAd(getContext());
//            }else if (Constants.ACTIVE_AD_NETWORK.equals("startapp")){
//                StartappAds.showBannerAd(getContext(), adView);
//            }
//        }

    }


    private void getAllGenre(){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, apiResources.getAllGenre(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (String.valueOf(response).length()<10){
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }else {
                    coordinatorLayout.setVisibility(View.GONE);
                }

                for (int i=0;i<response.length();i++){

                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        CommonModels models =new CommonModels();
                        models.setId(jsonObject.getString("genre_id"));
                        models.setTitle(jsonObject.getString("name"));
                        list.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
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
