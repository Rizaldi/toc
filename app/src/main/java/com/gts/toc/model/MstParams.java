package com.gts.toc.model;

public class MstParams {
	String ParamsID;
	String ParamsName;
    String ParamsValue;

    public MstParams(){
    }

    public MstParams(String mParamsID, String mParamsName, String mParamsValue){
    	this.ParamsID       = mParamsID;
        this.ParamsName     = mParamsName;
        this.ParamsValue    = mParamsValue;
    }

    public String getParamsID() {
        return ParamsID;
    }

    public void setParamsID(String mParamsID) {
        ParamsID = mParamsID;
    }

    public String getParamsName() {
        return ParamsName;
    }

    public void setParamsName(String mParamsName) {
        ParamsName = mParamsName;
    }

    public String getParamsValue() {
        return ParamsValue;
    }

    public void setParamsValue(String mParamsValue) {
        ParamsValue = mParamsValue;
    }
}
