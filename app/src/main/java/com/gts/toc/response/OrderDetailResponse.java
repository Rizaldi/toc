package com.gts.toc.response;

import com.google.gson.annotations.SerializedName;
import com.gts.toc.object.ObjOrder;

public class OrderDetailResponse extends BasicResponse {

    @SerializedName("DATA")
    public ObjOrder mData;

    public ObjOrder getResultData() {
        return mData;
    }
}
