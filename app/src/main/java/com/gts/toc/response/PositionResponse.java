package com.gts.toc.response;

import com.google.gson.annotations.SerializedName;
import com.gts.toc.object.ObjPosition;

public class PositionResponse extends BasicResponse {

    @SerializedName("DATA")
    public ObjPosition mData;

    public ObjPosition getResultData() {
        return mData;
    }
}
