package com.gts.toc.model;

public class MstUser {
	String UserID;
	String UserName;
    String UserAuth;
    String Email;
    String Phone;
    String Address;
    String Image;

    public MstUser(){
    }

    public MstUser(String mUserID, String mUserName, String mUserAuth,
                   String mEmail, String mPhone, String mAddress, String mImage){
    	this.UserID     = mUserID;
        this.UserName   = mUserName;
        this.UserAuth   = mUserAuth;
        this.Email      = mEmail;
        this.Phone      = mPhone;
        this.Address    = mAddress;
        this.Image      = mImage;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserAuth() {
        return UserAuth;
    }

    public void setUserAuth(String userAuth) {
        UserAuth = userAuth;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
