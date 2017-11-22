package com.gts.toc.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gts.toc.GlobalApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by warsono on 12/16/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION 		= 1;					//WHILE UP DATABASE_VERSION MUST BE SET CASE onUpgrade FUNCTION
    private static final String DATABASE_NAME 		= "toc";		        // Database Name
    public SQLiteDatabase db;

    //TABLE USER
    private static final String TABLE_USER 	        = "Mst_User";
    private static final String FIELD_USER_ID       = "user_id";
    private static final String FIELD_USER_NAME     = "user_name";
    private static final String FIELD_USER_AUTH     = "user_auth";
    private static final String FIELD_USER_EMAIL    = "email";
    private static final String FIELD_USER_PHONE    = "phone";
    private static final String FIELD_USER_ADDRESS  = "address";
    private static final String FIELD_USER_IMAGE    = "image";

    //TABLE CATEGORY
    private static final String TABLE_CATEGORY 	    = "Mst_Category";
    private static final String FIELD_CAT_ID        = "cat_id";
    private static final String FIELD_CAT_DESC      = "cat_description";

    //TABLE TYPE
    private static final String TABLE_TYPE 	        = "Mst_Type";
    private static final String FIELD_TYPE_ID       = "type_id";
    private static final String FIELD_TYPE_DESC     = "type_description";
    private static final String FIELD_PRICE         = "price";
    private static final String FIELD_CAT_CODE      = "cat_code";

    //TABLE REKENING
    private static final String TABLE_REKENING 	    = "Mst_Rekening";
    private static final String FIELD_BANK_ID       = "bank_id";
    private static final String FIELD_BANK_NAME     = "bank_name";
    private static final String FIELD_ACCOUNT_NAME  = "account_name";
    private static final String FIELD_REC_NO        = "rec_no";

    //TABLE PARAMS
    private static final String TABLE_PARAMS 	    = "Mst_Params";
    private static final String FIELD_PARAMS_ID     = "params_id";
    private static final String FIELD_PARAMS_NAME   = "params_name";
    private static final String FIELD_PARAMS_VALUE  = "params_value";

    public DatabaseHandler() {
        super(GlobalApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREATE TABLE USER
        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
                + FIELD_USER_ID + " TEXT PRIMARY KEY, "
                + FIELD_USER_NAME + " TEXT, "
                + FIELD_USER_AUTH + " TEXT, "
                + FIELD_USER_EMAIL + " TEXT, "
                + FIELD_USER_PHONE + " TEXT, "
                + FIELD_USER_ADDRESS + " TEXT, "
                + FIELD_USER_IMAGE+ " TEXT " + ")";
        db.execSQL(CREATE_TABLE_USER);

        //CREATE TABLE CATEGORY
        String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + "("
                + FIELD_CAT_ID + " TEXT PRIMARY KEY, "
                + FIELD_CAT_DESC+ " TEXT " + ")";
        db.execSQL(CREATE_TABLE_CATEGORY);

        //CREATE TABLE TYPE
        String CREATE_TABLE_TYPE = "CREATE TABLE " + TABLE_TYPE + "("
                + FIELD_TYPE_ID + " TEXT PRIMARY KEY, "
                + FIELD_TYPE_DESC + " TEXT, "
                + FIELD_PRICE + " TEXT, "
                + FIELD_CAT_CODE+ " TEXT " + ")";
        db.execSQL(CREATE_TABLE_TYPE);

        //CREATE TABLE TYPE
        String CREATE_TABLE_REKENING = "CREATE TABLE " + TABLE_REKENING + "("
                + FIELD_BANK_ID + " TEXT PRIMARY KEY, "
                + FIELD_BANK_NAME + " TEXT, "
                + FIELD_ACCOUNT_NAME + " TEXT, "
                + FIELD_REC_NO+ " TEXT " + ")";
        db.execSQL(CREATE_TABLE_REKENING);

        //CREATE TABLE PARAMS
        String CREATE_TABLE_PARAMS = "CREATE TABLE " + TABLE_PARAMS + "("
                + FIELD_PARAMS_ID + " TEXT PRIMARY KEY, "
                + FIELD_PARAMS_NAME + " TEXT, "
                + FIELD_PARAMS_VALUE+ " TEXT " + ")";
        db.execSQL(CREATE_TABLE_PARAMS);
    }

    public void open(){
        db = getWritableDatabase();
    }

    public void close(){
        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	switch(oldVersion) {
	        default:
	    }
    }

    public void AddUser(MstUser User) {
        SQLiteDatabase db 		= this.getWritableDatabase();
        ContentValues values 	= new ContentValues();
        values.put(FIELD_USER_ID, User.getUserID());
        values.put(FIELD_USER_NAME, User.getUserName());
        values.put(FIELD_USER_AUTH, User.getUserAuth());
        values.put(FIELD_USER_EMAIL, User.getEmail());
        values.put(FIELD_USER_PHONE, User.getPhone());
        values.put(FIELD_USER_ADDRESS, User.getAddress());
        values.put(FIELD_USER_IMAGE, User.getImage());
        db.insert(TABLE_USER, null, values);
    }
    public void UpdateUser (MstUser User) {
        SQLiteDatabase db 		= this.getWritableDatabase();
        ContentValues values 	= new ContentValues();
        values.put(FIELD_USER_NAME, User.getUserName());
        values.put(FIELD_USER_AUTH, User.getUserAuth());
        values.put(FIELD_USER_EMAIL, User.getEmail());
        values.put(FIELD_USER_PHONE, User.getPhone());
        values.put(FIELD_USER_ADDRESS, User.getAddress());
        values.put(FIELD_USER_IMAGE, User.getImage());
        db.update(TABLE_USER, values, FIELD_USER_ID+ " = ?" ,
                new String[] {User.getUserID()});
    }
    public List<MstUser> GetUser() {
        List<MstUser> UserList      = new ArrayList<MstUser>();
        String selectQuery 		    = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db 		    = this.getWritableDatabase();
        Cursor cursor 		        = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MstUser User = new MstUser();
                User.setUserID(cursor.getString(0));
                User.setUserName(cursor.getString(1));
                User.setUserAuth(cursor.getString(2));
                User.setEmail(cursor.getString(3));
                User.setPhone(cursor.getString(4));
                User.setAddress(cursor.getString(5));
                User.setImage(cursor.getString(6));
                UserList.add(User);
            } while (cursor.moveToNext());
        }
        return UserList;
    }
    public void DeleteUser(String UserID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, FIELD_USER_ID + " = ?",
                new String[]{UserID});
    }

    public void AddCategory(MstCategory Category) {
        SQLiteDatabase db 		= this.getWritableDatabase();
        ContentValues values 	= new ContentValues();
        values.put(FIELD_CAT_ID, Category.getCatID());
        values.put(FIELD_CAT_DESC, Category.getDescription());
        db.insert(TABLE_CATEGORY, null, values);
    }
    public void UpdateCategory (MstCategory Category) {
        SQLiteDatabase db 		= this.getWritableDatabase();
        ContentValues values 	= new ContentValues();
        values.put(FIELD_CAT_DESC, Category.getDescription());
        db.update(TABLE_CATEGORY, values, FIELD_CAT_ID+ " = ?" ,
                new String[] {Category.getCatID()});
    }
    public List<MstCategory> GetCategory(String CatID) {
        List<MstCategory> CatList   = new ArrayList<MstCategory>();
        String selectQuery 		    = "SELECT  * FROM " + TABLE_CATEGORY+ " WHERE " + FIELD_CAT_ID + " = '" + CatID + "'";
        SQLiteDatabase db 		    = this.getWritableDatabase();
        Cursor cursor 		        = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MstCategory Cat = new MstCategory();
                Cat.setCatID(cursor.getString(0));
                Cat.setDescription(cursor.getString(1));
                CatList.add(Cat);
            } while (cursor.moveToNext());
        }
        return CatList;
    }
    public List<MstCategory> GetAllCategory() {
        List<MstCategory> CatList   = new ArrayList<MstCategory>();
        String selectQuery 		    = "SELECT  * FROM " + TABLE_CATEGORY;
        SQLiteDatabase db 		    = this.getWritableDatabase();
        Cursor cursor 		        = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MstCategory Cat = new MstCategory();
                Cat.setCatID(cursor.getString(0));
                Cat.setDescription(cursor.getString(1));
                CatList.add(Cat);
            } while (cursor.moveToNext());
        }
        return CatList;
    }

    public void AddType(MstType Type) {
        SQLiteDatabase db 		= this.getWritableDatabase();
        ContentValues values 	= new ContentValues();
        values.put(FIELD_TYPE_ID, Type.getTypeID());
        values.put(FIELD_TYPE_DESC, Type.getDescription());
        values.put(FIELD_PRICE, Type.getPrice());
        values.put(FIELD_CAT_CODE, Type.getCatCode());
        db.insert(TABLE_TYPE, null, values);
    }
    public void UpdateType (MstType Type) {
        SQLiteDatabase db 		= this.getWritableDatabase();
        ContentValues values 	= new ContentValues();
        values.put(FIELD_TYPE_DESC, Type.getDescription());
        values.put(FIELD_PRICE, Type.getPrice());
        values.put(FIELD_CAT_CODE, Type.getCatCode());
        db.update(TABLE_TYPE, values, FIELD_TYPE_ID+ " = ?" ,
                new String[] {Type.getTypeID()});
    }
    public List<MstType> GetType(String TypeID) {
        List<MstType> TypeList       = new ArrayList<MstType>();
        String selectQuery 		    = "SELECT  * FROM " + TABLE_TYPE+ " WHERE " + FIELD_TYPE_ID + " = '" + TypeID + "'";
        SQLiteDatabase db 		    = this.getWritableDatabase();
        Cursor cursor 		        = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MstType Type = new MstType();
                Type.setTypeID(cursor.getString(0));
                Type.setDescription(cursor.getString(1));
                Type.setPrice(cursor.getString(2));
                Type.setCatCode(cursor.getString(3));
                TypeList.add(Type);
            } while (cursor.moveToNext());
        }
        return TypeList;
    }
    public List<MstType> GetAllTypeBycatCode(String CatCode) {
        List<MstType> TypeList       = new ArrayList<MstType>();
        String selectQuery 		    = "SELECT  * FROM " + TABLE_TYPE+ " WHERE " + FIELD_CAT_CODE + " = '" + CatCode + "'";
        SQLiteDatabase db 		    = this.getWritableDatabase();
        Cursor cursor 		        = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MstType Type = new MstType();
                Type.setTypeID(cursor.getString(0));
                Type.setDescription(cursor.getString(1));
                Type.setPrice(cursor.getString(2));
                Type.setCatCode(cursor.getString(3));
                TypeList.add(Type);
            } while (cursor.moveToNext());
        }
        return TypeList;
    }
    public List<MstType> GetAllType () {
        List<MstType> TypeList      = new ArrayList<MstType>();
        String selectQuery 		    = "SELECT  * FROM " + TABLE_TYPE;
        SQLiteDatabase db 		    = this.getWritableDatabase();
        Cursor cursor 		        = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MstType Type = new MstType();
                Type.setTypeID(cursor.getString(0));
                Type.setDescription(cursor.getString(1));
                Type.setPrice(cursor.getString(2));
                Type.setCatCode(cursor.getString(3));
                TypeList.add(Type);
            } while (cursor.moveToNext());
        }
        return TypeList;
    }
    public void AddBank(MstBank Bank) {
        SQLiteDatabase db 		= this.getWritableDatabase();
        ContentValues values 	= new ContentValues();
        values.put(FIELD_BANK_ID, Bank.getBankID());
        values.put(FIELD_BANK_NAME, Bank.getBankName());
        values.put(FIELD_ACCOUNT_NAME, Bank.getAccountName());
        values.put(FIELD_REC_NO, Bank.getRecNo());
        db.insert(TABLE_REKENING, null, values);
    }
    public void Updatebank (MstBank Bank) {
        SQLiteDatabase db 		= this.getWritableDatabase();
        ContentValues values 	= new ContentValues();
        values.put(FIELD_BANK_NAME, Bank.getBankName());
        values.put(FIELD_ACCOUNT_NAME, Bank.getAccountName());
        values.put(FIELD_REC_NO, Bank.getRecNo());
        db.update(TABLE_REKENING, values, FIELD_BANK_ID+ " = ?" ,
                new String[] {Bank.getBankID()});
    }
    public List<MstBank> GetBankbyID(String BankID) {
        List<MstBank> BankList       = new ArrayList<MstBank>();
        String selectQuery 		    = "SELECT  * FROM " + TABLE_REKENING+ " WHERE " + FIELD_BANK_ID + " = '" + BankID + "'";
        SQLiteDatabase db 		    = this.getWritableDatabase();
        Cursor cursor 		        = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MstBank Bank = new MstBank();
                Bank.setBankID(cursor.getString(0));
                Bank.setBankName(cursor.getString(1));
                Bank.setAccountName(cursor.getString(2));
                Bank.setRecNo(cursor.getString(3));
                BankList.add(Bank);
            } while (cursor.moveToNext());
        }
        return BankList;
    }
    public List<MstBank> GetAllBank () {
        List<MstBank> BankList       = new ArrayList<MstBank>();
        String selectQuery 		    = "SELECT  * FROM " + TABLE_REKENING;
        SQLiteDatabase db 		    = this.getWritableDatabase();
        Cursor cursor 		        = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MstBank Bank = new MstBank();
                Bank.setBankID(cursor.getString(0));
                Bank.setBankName(cursor.getString(1));
                Bank.setAccountName(cursor.getString(2));
                Bank.setRecNo(cursor.getString(3));
                BankList.add(Bank);
            } while (cursor.moveToNext());
        }
        return BankList;
    }

    public void AddParams(MstParams Params) {
        SQLiteDatabase db 		= this.getWritableDatabase();
        ContentValues values 	= new ContentValues();
        values.put(FIELD_PARAMS_ID, Params.getParamsID());
        values.put(FIELD_PARAMS_NAME, Params.getParamsName());
        values.put(FIELD_PARAMS_VALUE, Params.getParamsValue());
        db.insert(TABLE_PARAMS, null, values);
    }
    public void UpdateParams (MstParams Params) {
        SQLiteDatabase db 		= this.getWritableDatabase();
        ContentValues values 	= new ContentValues();
        values.put(FIELD_PARAMS_NAME, Params.getParamsName());
        values.put(FIELD_PARAMS_VALUE, Params.getParamsValue());
        db.update(TABLE_PARAMS, values, FIELD_PARAMS_ID+ " = ?" ,
                new String[] {Params.getParamsID()});
    }
    public List<MstParams> GetParams(String ParamsID) {
        List<MstParams> ParamsList     = new ArrayList<MstParams>();
        String selectQuery 		    = "SELECT  * FROM " + TABLE_PARAMS+ " WHERE " + FIELD_PARAMS_ID + " = '" + ParamsID + "'";
        SQLiteDatabase db 		    = this.getWritableDatabase();
        Cursor cursor 		        = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MstParams Params = new MstParams();
                Params.setParamsID(cursor.getString(0));
                Params.setParamsName(cursor.getString(1));
                Params.setParamsValue(cursor.getString(2));
                ParamsList.add(Params);
            } while (cursor.moveToNext());
        }
        return ParamsList;
    }
    public List<MstParams> GetAllParams() {
        List<MstParams> ParamsList     = new ArrayList<MstParams>();
        String selectQuery 		    = "SELECT  * FROM " + TABLE_PARAMS;
        SQLiteDatabase db 		    = this.getWritableDatabase();
        Cursor cursor 		        = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MstParams Params = new MstParams();
                Params.setParamsID(cursor.getString(0));
                Params.setParamsName(cursor.getString(1));
                Params.setParamsValue(cursor.getString(2));
                ParamsList.add(Params);
            } while (cursor.moveToNext());
        }
        return ParamsList;
    }
}
