package com.gts.toc.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BankResponse extends BasicResponse {

    @SerializedName("DATA")
    public List<ObjBank> mData = new ArrayList<ObjBank>();

    public List<ObjBank> getResultData() {
        return mData;
    }

    public class ObjBank {
        @SerializedName("BANK_ID")
        private String BankID;

        @SerializedName("BANK_NAME")
        private String BankName;

        @SerializedName("ACCOUNT_NAME")
        private String AccountName;

        @SerializedName("REKENING_NO")
        private String ReceningNo;

        public String getBankID() {
            return BankID;
        }

        public String getBankName() {
            return BankName;
        }

        public String getAccountName() {
            return AccountName;
        }

        public String getReceningNo() {
            return ReceningNo;
        }
    }
}
