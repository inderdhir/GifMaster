package com.inderdhir.gifmaster.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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


public class MainActivityFragment extends BaseFragment implements Callback<List<GifItem>> {

    @BindView(R.id.gifs_recycler_view)
    RecyclerView mGifsRecyclerView;

    @Inject
    GiphyRetrofitService service;

    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";
    private static final String LIST_KEY = "list_key";

    private ArrayList<GifItem> mGifItemsList = new ArrayList<>();
    private Call<List<GifItem>> call;
    private GifsRecyclerViewAdapter adapter;
    private CustomLinearLayoutManager mLinearLayoutManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GifMasterApplication) getActivity().getApplication()).component().inject(this);

        mLinearLayoutManager = new CustomLinearLayoutManager(getContext());
        call = service.getTrendingGifs(0); // maybe get this through bundle too?

        if (savedInstanceState != null) {
            mGifItemsList = savedInstanceState.getParcelableArrayList(LIST_KEY);
            Parcelable savedRecyclerLayoutState =
                    savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mLinearLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
        } else {
            mGifItemsList = new ArrayList<>();
        }
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
        mGifsRecyclerView.setItemViewCacheSize(40);
        mGifsRecyclerView.setDrawingCacheEnabled(true);
        mGifsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        mGifsRecyclerView.setLayoutManager(mLinearLayoutManager);
        adapter = new GifsRecyclerViewAdapter(mGifItemsList);
        mGifsRecyclerView.setAdapter(adapter);

//        mGifsRecyclerView.addOnScrollListener();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!call.isExecuted()) {
            call.enqueue(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST_KEY, mGifItemsList);
        outState.putParcelable(RECYCLER_VIEW_STATE,
                mLinearLayoutManager.onSaveInstanceState());
    }

    @Override
    public void onResponse(final Call<List<GifItem>> call, final Response<List<GifItem>> response) {

        List<GifItem> gifItems = response.body();
        if (gifItems != null && !gifItems.isEmpty()) {
            mGifItemsList.addAll(gifItems);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(final Call<List<GifItem>> call, final Throwable t) {
        // TODO: Implement this
    }

    private static class CustomLinearLayoutManager extends LinearLayoutManager {

        private static final int EXTRA_LAYOUT_SPACE = 600;

        public CustomLinearLayoutManager(final Context context) {
            super(context);
        }

        @Override
        protected int getExtraLayoutSpace(final RecyclerView.State state) {
            return EXTRA_LAYOUT_SPACE;
        }
    }
}
