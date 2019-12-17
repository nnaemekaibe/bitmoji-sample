package com.bibibryan.bitmoji_sample.Chat;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.bibibryan.bitmoji_sample.Chat.ViewHolder.ChatImageViewHolder;
import com.bibibryan.bitmoji_sample.Chat.ViewHolder.ChatTextViewHolder;
import com.bibibryan.bitmoji_sample.Chat.ViewHolder.ChatViewHolder;
import com.bibibryan.bitmoji_sample.Chat.model.ChatImageMessage;
import com.bibibryan.bitmoji_sample.Chat.model.ChatImageUrlMessage;
import com.bibibryan.bitmoji_sample.Chat.model.ChatMessage;
import com.bibibryan.bitmoji_sample.Chat.model.ChatTextMessage;
import com.bibibryan.bitmoji_sample.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private final List<ChatMessage> mMessages = new ArrayList<>();


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        switch (getMessageType(viewType)){
            case TEXT:
                return new ChatTextViewHolder((ViewGroup) View.inflate(context, R.layout.chat_text_view, null));
            case IMAGE:
                return new ChatImageViewHolder(context, (ViewGroup) View.inflate(context, R.layout.chat_image_view, null));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = getMessage(position);

        holder.setIsFromMe(message.isFromMe());

        if(message instanceof ChatImageMessage){
            ((ChatImageViewHolder) holder).setDrawable(((ChatImageMessage) message).getDrawableResId());
        }else if(message instanceof ChatImageUrlMessage){
            ((ChatImageViewHolder) holder).setImageUrl(((ChatImageUrlMessage) message).getImageUrl(), ((ChatImageUrlMessage) message).getPreview());
        }else if(message instanceof ChatTextMessage){
            ((ChatTextViewHolder) holder).setText(((ChatTextMessage) message).getText());
        }


    }

    private ChatMessage getMessage(int position) {
        return mMessages.get(getPositionInMessages(position));
    }

    private int getPositionInMessages(int position) {
        return mMessages.size() - 1 - position;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getMessage(position).getType().ordinal();
    }

    @Override
    public long getItemId(int position) {
        return getPositionInMessages(position);
    }

    @Override
    public void onViewRecycled(@NonNull ChatViewHolder holder) {
        if(holder instanceof ChatImageViewHolder){
            ((ChatImageViewHolder) holder).recycle();
        }
    }

    public void add(ChatMessage message){
        mMessages.add(message);
        notifyDataSetChanged();
    }


    private static ChatMessage.Type getMessageType(int viewType){
        return ChatMessage.Type.values()[viewType];
    }



}
