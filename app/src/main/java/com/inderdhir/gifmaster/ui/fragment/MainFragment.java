package com.inderdhir.gifmaster.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.inderdhir.gifmaster.R;
import com.inderdhir.gifmaster.core.GifMasterApplication;
import com.inderdhir.gifmaster.core.GiphyRetrofitService;
import com.inderdhir.gifmaster.model.GifItem;
import com.inderdhir.gifmaster.ui.adapter.GifsRecyclerViewAdapter;
import com.inderdhir.gifmaster.util.StringUtils;
import com.inderdhir.gifmaster.util.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainFragment extends BaseFragment implements Callback<List<GifItem>>,
        SwipeRefreshLayout.OnRefreshListener, TextView.OnEditorActionListener {

    @Inject
    GiphyRetrofitService service;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.search_gifs_edit_text)
    EditText mSearchGifsEditTextView;
    @BindView(R.id.gifs_recycler_view)
    RecyclerView mGifsRecyclerView;

    private static final int GIF_FETCH_LIMIT = 25;
    private static final int GIF_INFINITE_SCROLL_THRESHOLD = 5;
    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";
    private static final String LIST_KEY = "list_key";

    private View rootView;
    private ArrayList<GifItem> mGifItemsList;
    private Call<List<GifItem>> currentGifsRequest;
    private GifsRecyclerViewAdapter adapter;
    private RecyclerView.OnScrollListener mScrollListener;
    private CustomLinearLayoutManager mLinearLayoutManager;
    private boolean isSearching = false;
    private boolean isLoadingItems = true;
    private String mCurrentSearchQuery;
    private int mPreviousItemsTotal;
    private int mTotalItemCount;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GifMasterApplication) getActivity().getApplication()).component().inject(this);

        mLinearLayoutManager = new CustomLinearLayoutManager(getContext());
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    mTotalItemCount = linearLayoutManager.getItemCount();
                    final int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoadingItems && mTotalItemCount <= (lastVisibleItem + GIF_INFINITE_SCROLL_THRESHOLD)) {
                        mPreviousItemsTotal += GIF_FETCH_LIMIT;
                        makeAppropriateRequest(true, false);
                    }
                }
            }
        };

        currentGifsRequest = service.getTrendingGifs(GIF_FETCH_LIMIT, mPreviousItemsTotal);

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
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSearchGifsEditTextView.setOnEditorActionListener(this);
        mGifsRecyclerView.setHasFixedSize(true);
        mGifsRecyclerView.setItemViewCacheSize(40);
        mGifsRecyclerView.setDrawingCacheEnabled(true);
        mGifsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        mGifsRecyclerView.setLayoutManager(mLinearLayoutManager);
        adapter = new GifsRecyclerViewAdapter(mGifItemsList);
        mGifsRecyclerView.setAdapter(adapter);
        mGifsRecyclerView.addOnScrollListener(mScrollListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        makeAppropriateRequest(false, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (currentGifsRequest != null) {
            currentGifsRequest.cancel();
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
    public void onResponse(final Call<List<GifItem>> call,
                           final Response<List<GifItem>> response) {
        if (isSearching) {
            Utils.hideSoftKeyboard(getActivity(), rootView);
        }
        isLoadingItems = false;
        mSwipeRefreshLayout.setRefreshing(false);
        List<GifItem> gifItems = response.body();
        if (gifItems != null && !gifItems.isEmpty()) {
            mGifItemsList.addAll(gifItems);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(final Call<List<GifItem>> call, final Throwable t) {
        // TODO: Implement this
        isLoadingItems = false;
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        makeAppropriateRequest(false, true);
    }

    @Override
    public boolean onEditorAction(final TextView textView, final int actionId,
                                  final KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            mCurrentSearchQuery = textView.getText().toString();
            isSearching = true;
            makeAppropriateRequest(false, true);
            return true;
        }
        return false;
    }

    private void makeAppropriateRequest(boolean loadMore, boolean clearAndLoadNew) {
        if (loadMore || clearAndLoadNew || (currentGifsRequest != null && !currentGifsRequest.isExecuted())) {
            if (clearAndLoadNew) {
                mPreviousItemsTotal = 0;
                mGifItemsList.clear();
                mGifsRecyclerView.scrollToPosition(0);
            }

            isLoadingItems = true;
            if (isSearching && !StringUtils.isNullOrEmpty(mCurrentSearchQuery)) {
                currentGifsRequest = service.searchForGifs(mCurrentSearchQuery,
                        GIF_FETCH_LIMIT, mPreviousItemsTotal);
                currentGifsRequest.enqueue(this);
            } else {
                isSearching = false;
                Utils.hideSoftKeyboard(getActivity(), rootView);
                currentGifsRequest = service.getTrendingGifs(GIF_FETCH_LIMIT, mPreviousItemsTotal);
                currentGifsRequest.enqueue(this);
            }
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
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
