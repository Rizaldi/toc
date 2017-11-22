package com.gts.toc.response;

import com.google.gson.annotations.SerializedName;
import com.gts.toc.object.ObjOrder;

import java.util.ArrayList;
import java.util.List;

public class OrderResponse extends BasicResponse {

    @SerializedName("DATA")
    public List<ObjOrder> mData = new ArrayList<ObjOrder>();

    public List<ObjOrder> getResultData() {
        return mData;
    }
}
