package com.inderdhir.gifmaster.ui.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.inderdhir.gifmaster.R;
import com.inderdhir.gifmaster.model.GifItem;

import java.util.List;


public class GifsRecyclerViewAdapter extends RecyclerView.Adapter<GifsRecyclerViewAdapter.ViewHolder> {

    private List<GifItem> mGifItems;

    public GifsRecyclerViewAdapter(@NonNull final List<GifItem> mGifItems) {
        this.mGifItems = mGifItems;
    }

    @Override
    public GifsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gif_item, parent, false);
        return new ViewHolder(v, (SimpleDraweeView) v.findViewById(R.id.gif_image_view));
    }

    @Override
    public void onBindViewHolder(GifsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.bind(mGifItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mGifItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView mGifImageView;

        public ViewHolder(final View itemView, final SimpleDraweeView gifImageView) {
            super(itemView);
            mGifImageView = gifImageView;
        }

        public void bind(GifItem gifItem) {
            String url = gifItem.getUrl().replaceAll("\\/", "/");
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(url))
                    .setTapToRetryEnabled(true)
                    .setRetainImageOnFailure(true)
                    .setAutoPlayAnimations(true)
                    .build();
            mGifImageView.setController(controller);
        }
    }
}
