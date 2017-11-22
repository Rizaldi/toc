package com.gts.toc.object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by warsono on 11/04/16.
 */

public class ObjPosition {
    @SerializedName("POSITION")
    private String Position;

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }
}
