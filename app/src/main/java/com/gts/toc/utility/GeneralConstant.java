package com.gts.toc.utility;

import com.gts.toc.GlobalApplication;

/**
 * Created by warsono on 12/16/16.
 */

public class GeneralConstant {

    //FRAGMENT KEY CONSTANT
    public static final String FRAGMENT_TAG     = "FragmentTag";
    public static final String FRAGMENT_STATE   = "FRAGMENT_STATE";
    public static final String FRAGMENT_HOME    = "FRAGMENT_HOME";
    public static final String FRAGMENT_ORDER   = "FRAGMENT_ORDER";
    public static final String FRAGMENT_HISTORY = "FRAGMENT_HISTORY";
    public static final String FRAGMENT_TERM    = "FRAGMENT_TERM";

    //USER PARAMETER CONSTANT
    public static final String PARAM_CATEGORY   = "c";
    public static final String PARAM_TYPE       = "t";
    public static final String PARAM_ORDER      = "o";
    public static final String PARAM_HISTORY    = "h";
    public static final String PARAM_ORDERID    = "ORDER_ID";
    public static final String PARAM_ORDERLIST  = "ORDER_LIST";
    public static final String ORDER_EMPTY      = "ORDER_EMPTY";
    public static final String ORDER_NOTEMPTY   = "ORDER_NOTEMPTY";
    public static final String PAYMENT_TYPE     = "PAYMENT";
    public static final String PARAM_COST       = "SERVICE_COST";
    public static final String PARAM_LOCATION   = "LOCATION";
    public static final String PARAM_ADDRESS    = "ADDRESS";
    public static final String PARAM_TECHID     = "TECH_ID";

    public static final String PAYMENT_BOOKING  = "b";
    public static final String PAYMENT_CANCEL   = "c";
    public static final String PAYMENT_LUNAS    = "p";
    public static final String PARAMS_DISTANCE_ID   = "1";
    public static final String PARAMS_FEE_ID        = "2";
    public static final String PARAMS_CHASHBACK_ID  = "3";


    //USER TYPE CONSTANT
    public static final String USER_TECHNICIAN  = "t";
    public static final String USER_CLIENT      = "c";

    //LOCATION CONSTANT
    public static final double LOC_LATITUDE     = -6.367198300081161;
    public static final double LOC_LONGITUDE    = 106.83371342718601;

    //USER CHECKING CONSTANT
    public static final String CHECKING_USER    = "U";
    public static final String CHECKING_EMAIL   = "E";

    public static final String API_URLUPDATE    = "https://play.google.com/store/apps/details?id="+ GlobalApplication.getContext().getPackageName();
    public static final String API_HEADER       = "TOC/Android/672.1.15 Darwin/14.0.0";
    public static final String APP_KEY          = "3f30a837bb81372eced861509d55ca5d";       //MD5 drom Package Name

    public static final String DOMAIN_SERVER    = "http://technician-oncall.com";
    public static final String API_MAIN         = "/api";

//    public static final String DOMAIN_SERVER    = "http://192.168.43.140";           //FOR HOME
//    public static final String DOMAIN_SERVER        = "http://192.168.12.18:8888";      //FOR OFFICE
//    public static final String API_MAIN             = "toc/api";                       //FOR HOME & OFFICE

    public static final String API_GET_BANK         = API_MAIN+"/misc/get_rekening";
    public static final String API_USER_CHECK       = API_MAIN+"/user/check";
    public static final String API_REGISTRATION     = API_MAIN+"/user/register";
    public static final String API_LOGIN            = API_MAIN+"/user/login";
    public static final String API_UPDATE_PROFILE   = API_MAIN+"/user/profile_update";
    public static final String API_CHANGE_PASS      = API_MAIN+"/user/change_password";
    public static final String API_GET_AUTH         = API_MAIN+"/user/get_auth";
    public static final String API_RESET_PASS       = API_MAIN+"/user/reset_password";
    public static final String API_GET_PARAMETER    = API_MAIN+"/order/get_parameter";
    public static final String API_GET_ORDER        = API_MAIN+"/order/order_list";
    public static final String API_GET_ORDERDETAIL  = API_MAIN+"/order/order_detail";
    public static final String API_ADD_ORDER        = API_MAIN+"/order/add_order";
    public static final String API_CANCEL_ORDER     = API_MAIN+"/order/cancel_order";
    public static final String API_CONFIRM_PAYMENT  = API_MAIN+"/order/konfirmasi_payment";
    public static final String API_CONFIRM_PICKUP   = API_MAIN+"/order/konfirmasi_pickup";
    public static final String API_CONFIRM_ESTIMASI = API_MAIN+"/order/konfirmasi_estimasi";
    public static final String API_CONFIRM_DELIVERY = API_MAIN+"/order/konfirmasi_delivery";
    public static final String API_CONFIRM_FINISH   = API_MAIN+"/order/finish_order";
    public static final String API_ADD_CLAIM        = API_MAIN+"/order/add_claim";
    public static final String API_GET_PARAMS       = API_MAIN+"/misc/get_params";
    public static final String API_GET_BANNER       = API_MAIN+"/misc/get_banner";
    public static final String API_GET_TECH         = API_MAIN+"/user/get_position";

}
