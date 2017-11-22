package com.gts.toc.model;

public class MstBank {
	String BankID;
	String BankName;
    String AccountName;
    String RecNo;

    public MstBank(){
    }

    public MstBank(String mBankID, String mBankName, String mAccountName, String mRecNo){
    	this.BankID         = mBankID;
        this.BankName       = mBankName;
        this.AccountName    = mAccountName;
        this.RecNo          = mRecNo;
    }

    public String getBankID() {
        return BankID;
    }

    public void setBankID(String bankID) {
        BankID = bankID;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public String getRecNo() {
        return RecNo;
    }

    public void setRecNo(String recNo) {
        RecNo = recNo;
    }
}
