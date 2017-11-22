package com.gts.toc.object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by warsono on 11/04/16.
 */

public class ObjOrderDetail {
    @SerializedName("LOG_TIME")
    private String OrderTime;

    @SerializedName("LOG_STATE")
    private String OrderState;

    @SerializedName("LOG_DESC")
    private String OrderDesc;

    @SerializedName("LOG_MESSAGE")
    private String OrderMessage;

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public String getOrderState() {
        return OrderState;
    }

    public void setOrderState(String orderState) {
        OrderState = orderState;
    }

    public String getOrderDesc() {
        return OrderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        OrderDesc = orderDesc;
    }

    public String getOrderMessage() {
        return OrderMessage;
    }

    public void setOrderMessage(String orderMessage) {
        OrderMessage = orderMessage;
    }
}
