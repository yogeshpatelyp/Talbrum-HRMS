package com.talentcerebrumhrms.datatype;

/**
 * Created by saransh on 18-11-2016.
 */

public class ChatDataType {
    //sender type can be user/bot;
    //sender type 1=user, 2= assistant, 9= default value

    private String chat_text;
    private int senderType;

    public void setChat(String value) {
        this.chat_text = value;
    }

    public void setSenderType(Integer value) {
        this.senderType = value;
    }

    public String getChat() {
        return this.chat_text;
    }

    public Integer getSenderType() {
        return this.senderType;
    }
}
