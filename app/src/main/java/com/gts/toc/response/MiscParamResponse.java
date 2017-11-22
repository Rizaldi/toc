package com.gts.toc.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MiscParamResponse extends BasicResponse {

    @SerializedName("DATA")
    public List<ObjParameter> mData = new ArrayList<ObjParameter>();

    public List<ObjParameter> getResultData() {
        return mData;
    }

    public class ObjParameter {
        @SerializedName("PARAMS_ID")
        private String ParamsID;

        @SerializedName("PARAMS_NAME")
        private String ParamsName;

        @SerializedName("PARAMS_VALUE")
        private String Paramsvalue;

        public String getParamsID() {
            return ParamsID;
        }

        public String getParamsName() {
            return ParamsName;
        }

        public String getParamsvalue() {
            return Paramsvalue;
        }
    }
}
