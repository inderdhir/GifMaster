package com.inderdhir.gifmaster.ui.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.inderdhir.gifmaster.R;
import com.inderdhir.gifmaster.core.GifMasterApplication;
import com.inderdhir.gifmaster.core.GiphyRetrofitService;
import com.inderdhir.gifmaster.model.GifItem;
import com.inderdhir.gifmaster.ui.adapter.CustomGridLayoutManager;
import com.inderdhir.gifmaster.ui.adapter.GifsRecyclerViewAdapter;
import com.inderdhir.gifmaster.util.BundleKeys;
import com.inderdhir.gifmaster.util.StringUtils;
import com.inderdhir.gifmaster.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainFragment extends BaseFragment implements Callback<List<GifItem>>,
        SwipeRefreshLayout.OnRefreshListener, TextView.OnEditorActionListener,
        View.OnClickListener, TextWatcher {

    @Inject
    GiphyRetrofitService service;

    @BindView(R.id.network_error_layout)
    ViewGroup mNetworkErrorLayout;
    @BindView(R.id.network_error_gif_view)
    SimpleDraweeView mNetworkErrorGifView;
    @BindView(R.id.clear_image)
    ImageView mClearImage;
    @BindView(R.id.back_to_top_text)
    TextView mBackToTopText;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.search_gifs_edit_text)
    EditText mSearchGifsEditTextView;
    @BindView(R.id.gifs_recycler_view)
    RecyclerView mGifsRecyclerView;

    private static final int GIF_FETCH_LIMIT = 25;
    private static final int GIF_INFINITE_SCROLL_THRESHOLD = 5;
    private static final int BACK_TO_TOP_THRESHOLD = 15;

    private View rootView;
    private ArrayList<GifItem> mGifItemsList;
    private Call<List<GifItem>> currentGifsRequest;
    private GifsRecyclerViewAdapter adapter;
    private RecyclerView.OnScrollListener mScrollListener;
    private CustomGridLayoutManager mGridLayoutManager;
    private Random mRandom;
    private boolean isSearching;
    private boolean isLoadingItems = true;
    private String mCurrentSearchQuery;
    private int mPreviousItemsTotal;
    private int mTotalItems;
    private boolean mClearAndLoadNew;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GifMasterApplication) getActivity().getApplication()).component().inject(this);

        mRandom = new Random();
        if (Utils.isLandscape(getContext())) {
            mGridLayoutManager = new CustomGridLayoutManager(getContext(), 3);
        } else {
            mGridLayoutManager = new CustomGridLayoutManager(getContext(), 1);
        }
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    mTotalItems = linearLayoutManager.getItemCount();
                    final int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    // Only available in API >= 21
                    if (mBackToTopText != null) {
                        if (lastVisibleItem < BACK_TO_TOP_THRESHOLD) {
                            mBackToTopText.setVisibility(View.GONE);
                        } else {
                            mBackToTopText.setVisibility(View.VISIBLE);
                        }
                    }

                    if (!isLoadingItems && mTotalItems <= (lastVisibleItem + GIF_INFINITE_SCROLL_THRESHOLD)) {
                        mPreviousItemsTotal += GIF_FETCH_LIMIT;
                        mClearAndLoadNew = false;
                        makeAppropriateRequest(true);
                    }
                }
            }
        };

        currentGifsRequest = service.getTrendingGifs(GIF_FETCH_LIMIT, mPreviousItemsTotal);

        if (savedInstanceState != null) {
            mGifItemsList = savedInstanceState.getParcelableArrayList(BundleKeys.LIST_KEY);
            Parcelable glmState =
                    savedInstanceState.getParcelable(BundleKeys.GLM_STATE_KEY);
            if (glmState != null) {
                mGridLayoutManager.onRestoreInstanceState(glmState);
            }
            isSearching = savedInstanceState.getBoolean(BundleKeys.IS_SEARCHING_KEY);
            isLoadingItems = savedInstanceState.getBoolean(BundleKeys.IS_LOADING_ITEMS_KEY);
            mCurrentSearchQuery = savedInstanceState.getString(BundleKeys.SEARCH_QUERY_KEY);
            mPreviousItemsTotal = savedInstanceState.getInt(BundleKeys.PREVIOUS_ITEMS_TOTAL_KEY);
            mTotalItems = savedInstanceState.getInt(BundleKeys.TOTAL_ITEMS_KEY);
        } else {
            mClearAndLoadNew = true;
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

        if (mBackToTopText != null) {
            mBackToTopText.setOnClickListener(this);
        }
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSearchGifsEditTextView.setOnEditorActionListener(this);
        mGifsRecyclerView.setHasFixedSize(true);
        mGifsRecyclerView.setItemViewCacheSize(40);
        mGifsRecyclerView.setDrawingCacheEnabled(true);
        mGifsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        mGifsRecyclerView.setLayoutManager(mGridLayoutManager);
        adapter = new GifsRecyclerViewAdapter(getContext(), mGifItemsList);
        mGifsRecyclerView.setAdapter(adapter);
        mGifsRecyclerView.addOnScrollListener(mScrollListener);
        mSearchGifsEditTextView.addTextChangedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        makeAppropriateRequest(false);
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
        outState.putParcelableArrayList(BundleKeys.LIST_KEY, mGifItemsList);
        if (mGridLayoutManager != null) {
            outState.putParcelable(BundleKeys.GLM_STATE_KEY,
                    mGridLayoutManager.onSaveInstanceState());
        }
        outState.putBoolean(BundleKeys.IS_SEARCHING_KEY, isSearching);
        outState.putBoolean(BundleKeys.IS_LOADING_ITEMS_KEY, isLoadingItems);
        outState.putString(BundleKeys.SEARCH_QUERY_KEY, mCurrentSearchQuery);
        outState.putInt(BundleKeys.PREVIOUS_ITEMS_TOTAL_KEY, mPreviousItemsTotal);
        outState.putInt(BundleKeys.TOTAL_ITEMS_KEY, mTotalItems);
    }

    @OnClick(R.id.retry_button)
    public void retryButtonClicked() {
        mClearAndLoadNew = true;
        makeAppropriateRequest(false);
    }

    //region Retrofit Callback
    @Override
    public void onResponse(final Call<List<GifItem>> call,
                           final Response<List<GifItem>> response) {
        if (response.isSuccessful()) {
            if (mNetworkErrorLayout.getVisibility() != View.GONE) {
                mNetworkErrorLayout.setVisibility(View.GONE);
                mSearchGifsEditTextView.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            }

            if (isSearching) {
                Utils.hideSoftKeyboard(getActivity(), rootView);
            }
            isLoadingItems = false;
            mSwipeRefreshLayout.setRefreshing(false);
            List<GifItem> gifItems = response.body();
            if (gifItems != null && !gifItems.isEmpty()) {
                mGifItemsList.addAll(gifItems);
                if (mClearAndLoadNew) {
                    mClearAndLoadNew = false;
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.notifyItemRangeChanged(mPreviousItemsTotal - 1, GIF_FETCH_LIMIT);
                }
            }
        } else {
            showFailure();
        }
    }

    @Override
    public void onFailure(final Call<List<GifItem>> call, final Throwable t) {
        showFailure();
    }
    //endregion

    //region SwipeRefresh
    @Override
    public void onRefresh() {
        mClearAndLoadNew = true;
        makeAppropriateRequest(false);
    }
    //endregion

    //region View.OnClickListener
    @Override
    public void onClick(final View view) {
        mGifsRecyclerView.scrollToPosition(0);
    }
    //endregion

    //region EditText
    @Override
    public boolean onEditorAction(final TextView textView, final int actionId,
                                  final KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            mCurrentSearchQuery = textView.getText().toString();
            isSearching = true;
            mClearAndLoadNew = true;
            makeAppropriateRequest(false);
            return true;
        }
        return false;
    }

    @OnClick(R.id.clear_image)
    public void clearClicked() {
        mSearchGifsEditTextView.setText("");
        isSearching = false;
        mClearAndLoadNew = true;
        makeAppropriateRequest(false);
    }
    //endregion

    //region TextWatcher
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (StringUtils.isNullOrEmpty(charSequence.toString())) {
            mClearImage.setVisibility(View.GONE);
        } else {
            mClearImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
    //endregion

    //region Private methods
    private void makeAppropriateRequest(boolean loadMore) {
        if (loadMore || mClearAndLoadNew ||
                (currentGifsRequest != null && !currentGifsRequest.isExecuted())) {
            if (mClearAndLoadNew) {
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

    private void showFailure() {
        // Show one of three GIFs randomly
        @DrawableRes int imageResourceId = R.drawable.network_error_1;
        switch (mRandom.nextInt(3)) {
            case 1:
                imageResourceId = R.drawable.network_error_2;
                break;
            case 2:
                imageResourceId = R.drawable.network_error_3;
                break;
        }
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(imageResourceId).build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(imageRequest.getSourceUri())
                .setAutoPlayAnimations(true)
                .build();
        mNetworkErrorGifView.setController(controller);

        mNetworkErrorLayout.setVisibility(View.VISIBLE);
        mSearchGifsEditTextView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.GONE);

        isLoadingItems = false;
        mSwipeRefreshLayout.setRefreshing(false);
    }
    //endregion
}
