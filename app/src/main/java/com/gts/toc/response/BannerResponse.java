package com.gts.toc.response;

import com.google.gson.annotations.SerializedName;
import com.gts.toc.object.ObjBanner;

import java.util.ArrayList;
import java.util.List;

public class BannerResponse extends BasicResponse {

    @SerializedName("DATA")
    public List<ObjBanner> mData = new ArrayList<ObjBanner>();

    public List<ObjBanner> getResultData() {
        return mData;
    }
}
