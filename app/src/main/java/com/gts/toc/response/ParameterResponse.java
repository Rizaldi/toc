package com.gts.toc.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ParameterResponse extends BasicResponse {

    @SerializedName("DATA")
    public List<ObjParameter> mData = new ArrayList<ObjParameter>();

    public List<ObjParameter> getResultData() {
        return mData;
    }

    public class ObjParameter {
        @SerializedName("ID")
        private String ID;

        @SerializedName("DESCRIPTION")
        private String Description;

        @SerializedName("PRICE")
        private String Price;

        @SerializedName("CATEGORY")
        private String Category;

        public String getID() {
            return ID;
        }

        public String getDescription() {
            return Description;
        }

        public String getPrice() {
            return Price;
        }

        public String getCategory() {
            return Category;
        }
    }
}
