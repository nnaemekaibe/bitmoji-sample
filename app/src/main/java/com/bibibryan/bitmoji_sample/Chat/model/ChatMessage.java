package com.bibibryan.bitmoji_sample.Chat.model;

public interface ChatMessage {

    enum Type {
        IMAGE,
        TEXT
    }

    boolean isFromMe();
    Type getType();
}
