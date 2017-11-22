package com.gts.toc.model;

public class MstType {
	String TypeID;
	String Description;
    String Price;
    String CatCode;

    public MstType(){
    }

    public MstType(String mTypeID, String mDescription, String mPrice, String mCatCode){
    	this.TypeID         = mTypeID;
        this.Description    = mDescription;
        this.Price          = mPrice;
        this.CatCode        = mCatCode;
    }

    public String getTypeID() {
        return TypeID;
    }

    public void setTypeID(String typeID) {
        TypeID = typeID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getCatCode() {
        return CatCode;
    }

    public void setCatCode(String catCode) {
        CatCode = catCode;
    }
}
