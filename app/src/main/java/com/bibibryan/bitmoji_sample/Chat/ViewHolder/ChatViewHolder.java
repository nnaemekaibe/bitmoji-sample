package com.bibibryan.bitmoji_sample.Chat.ViewHolder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bibibryan.bitmoji_sample.R;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    private final ViewGroup mRoot;
    private final View mSpacer;

    private boolean mIsFromMe = true;

    public ChatViewHolder(@NonNull ViewGroup root) {
        super(root);

        mRoot = root;
        mSpacer = root.findViewById(R.id.chat_spacer);
    }


    public void setIsFromMe(boolean isFromMe){
        if(mSpacer == null || isFromMe == mIsFromMe){
            return;
        }

        if(isFromMe){
            mRoot.removeView(mSpacer);
            mRoot.addView(mSpacer, 0);
        }else{
            mSpacer.bringToFront();
        }

        mIsFromMe = isFromMe;
    }
}
