package com.bibibryan.bitmoji_sample.Chat.model;

import androidx.annotation.DrawableRes;

public class ChatImageMessage implements ChatMessage {
    private final boolean mIsFromMe;
    private final int mDrawableResId;


    public ChatImageMessage(boolean isFromMe, @DrawableRes int drawableResId) {
        mIsFromMe = isFromMe;
        mDrawableResId = drawableResId;
    }


    @Override
    public boolean isFromMe() {
        return mIsFromMe;
    }

    @Override
    public Type getType() {
        return Type.IMAGE;
    }

    @DrawableRes
    public int getDrawableResId() {
        return mDrawableResId;
    }
}
