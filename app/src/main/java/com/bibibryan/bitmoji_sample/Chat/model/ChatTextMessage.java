package com.bibibryan.bitmoji_sample.Chat.model;

public class ChatTextMessage implements ChatMessage {
    private final boolean mIsFromMe;
    private final String mText;


    public ChatTextMessage(boolean isFromMe, String text) {
        mIsFromMe = isFromMe;
        mText = text;
    }

    @Override
    public boolean isFromMe() {
        return mIsFromMe;
    }

    @Override
    public Type getType() {
        return Type.TEXT;
    }

    public String getText() {
        return mText;
    }

}
