package com.gts.toc.response;

import com.google.gson.annotations.SerializedName;

public class BasicResponse {

    @SerializedName("STATUS")
    private int mResultState;

    @SerializedName("MESSAGE")
    private String mResultMessage;

    public int getResultState() {
        return mResultState;
    }

    public String getResultMessage() {
        return mResultMessage;
    }

}
