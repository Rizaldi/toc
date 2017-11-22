package com.gts.toc.model;

public class MstCategory {
	String CatID;
	String Description;

    public MstCategory(){
    }

    public MstCategory(String mCatID, String mDescription){
    	this.CatID          = mCatID;
        this.Description    = mDescription;
    }

    public String getCatID() {
        return CatID;
    }

    public void setCatID(String catID) {
        CatID = catID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
