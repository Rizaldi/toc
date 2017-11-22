package com.gts.toc.response;

import com.google.gson.annotations.SerializedName;

public class BookingResponse extends BasicResponse {

    @SerializedName("ORDER_ID")
    private String mOrderId;

    public String getOrderId() {
        return mOrderId;
    }
}
