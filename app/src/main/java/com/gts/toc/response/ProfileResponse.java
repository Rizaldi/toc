package com.gts.toc.response;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse extends BasicResponse {

    @SerializedName("DATA")
    public ObjUser mData;

    public ObjUser getResultData() {
        return mData;
    }

    public class ObjUser {
        @SerializedName("USER_ID")
        private String UserID;

        @SerializedName("USER_NAME")
        private String UserName;

        @SerializedName("USER_AUTH")
        private String UserAuth;

        @SerializedName("EMAIL")
        private String Email;

        @SerializedName("PHONE")
        private String Phone;

        @SerializedName("ADDRESS")
        private String Address;

        @SerializedName("IMAGE")
        private String Image;

        public String getUserID() {
            return UserID;
        }

        public String getUserName() {
            return UserName;
        }

        public String getUserAuth() {
            return UserAuth;
        }

        public String getEmail() {
            return Email;
        }

        public String getPhone() {
            return Phone;
        }

        public String getAddress() {
            return Address;
        }

        public String getImage() {
            return Image;
        }
    }
}
