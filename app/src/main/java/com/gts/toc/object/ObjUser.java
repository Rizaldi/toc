package com.gts.toc.object;

import com.gts.toc.model.DatabaseHandler;
import com.gts.toc.model.MstUser;

import java.util.List;

public class ObjUser {
    private DatabaseHandler mDataBase = new DatabaseHandler();
    public String UserID;
    public String UserName;
    public String UserAuth;
    public String Email;
    public String Phone;
    public String Address;
    public String Image;

    public ObjUser(){
        super();
        mDataBase               = new DatabaseHandler();
        List<MstUser> dataUser  = mDataBase.GetUser();
        if (dataUser.size() > 0){
            MstUser User    = dataUser.get(0);
            UserID          = User.getUserID();
            UserName        = User.getUserName();
            UserAuth        = User.getUserAuth();
            Email           = User.getEmail();
            Phone           = User.getPhone();
            Address         = User.getAddress();
            Image           = User.getImage();
        }else{
            UserID          = "";
            UserName        = "";
            UserAuth        = "";
            Email           = "";
            Phone           = "";
            Address         = "";
            Image           = "";
        }
    }
}
