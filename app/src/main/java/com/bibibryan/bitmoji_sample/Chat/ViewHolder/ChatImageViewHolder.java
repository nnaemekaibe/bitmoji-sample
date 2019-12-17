package com.bibibryan.bitmoji_sample.Chat.ViewHolder;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import com.bibibryan.bitmoji_sample.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ChatImageViewHolder extends ChatViewHolder {

    private final Context mContext;
    private final ImageView mImageView;
    private final View mLoadingIndicator;

    public ChatImageViewHolder(Context context, @NonNull ViewGroup root) {
        super(root);

        mContext = context;
        mImageView = root.findViewById(R.id.chat_image);
        mLoadingIndicator = root.findViewById(R.id.chat_loading);

    }

    public void setImageUrl(String imageUrl, Drawable preview){
        mImageView.setImageDrawable(preview);
        mImageView.setVisibility(preview == null ? View.GONE: View.VISIBLE);
        mLoadingIndicator.setVisibility(preview == null ? View.GONE: View.VISIBLE);
        Picasso.get().load(imageUrl).into(mImageView, new Callback() {
            @Override
            public void onSuccess() {
                mLoadingIndicator.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


    public void setDrawable(@DrawableRes int drawableResId){
        mLoadingIndicator.setVisibility(View.GONE);
        mImageView.setImageDrawable(mContext.getResources().getDrawable(drawableResId));
    }

    public void recycle(){
        Picasso.get().cancelRequest(mImageView);
    }
}
