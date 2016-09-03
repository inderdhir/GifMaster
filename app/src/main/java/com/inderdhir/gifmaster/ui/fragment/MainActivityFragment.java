package com.inderdhir.gifmaster.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inderdhir.gifmaster.R;
import com.inderdhir.gifmaster.adapter.GifsRecyclerViewAdapter;
import com.inderdhir.gifmaster.core.GifMasterApplication;
import com.inderdhir.gifmaster.core.GiphyRetrofitService;
import com.inderdhir.gifmaster.model.GifItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivityFragment extends BaseFragment {

    @BindView(R.id.gifs_recycler_view)
    RecyclerView mGifsRecyclerView;

    @Inject
    GiphyRetrofitService service;

    private List<GifItem> mGifItems = new ArrayList<>();
    private Call<List<GifItem>> call;
    private GifsRecyclerViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GifMasterApplication) getActivity().getApplication()).component().inject(this);
        call = service.getTrendingGifs(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGifsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mGifsRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new GifsRecyclerViewAdapter(mGifItems);
        mGifsRecyclerView.setAdapter(adapter);

//        mGifsRecyclerView.addOnScrollListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        getTrendingGifs();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (call != null) {
            call.cancel();
        }
    }

    private void getTrendingGifs() {
        call.enqueue(new Callback<List<GifItem>>() {
            @Override
            public void onResponse(final Call<List<GifItem>> call, final Response<List<GifItem>> response) {
                List<GifItem> gifItems = response.body();
                if (gifItems != null && !gifItems.isEmpty()) {
                    mGifItems.addAll(gifItems);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(final Call<List<GifItem>> call, final Throwable t) {

            }
        });
    }
}
