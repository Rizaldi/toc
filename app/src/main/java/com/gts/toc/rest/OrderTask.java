package com.gts.toc.rest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.gts.toc.Activity.PaymentActivity;
import com.gts.toc.R;
import com.gts.toc.model.DatabaseHandler;
import com.gts.toc.model.MstBank;
import com.gts.toc.model.MstCategory;
import com.gts.toc.model.MstParams;
import com.gts.toc.model.MstType;
import com.gts.toc.object.ObjUser;
import com.gts.toc.response.BankResponse;
import com.gts.toc.response.BasicResponse;
import com.gts.toc.response.BookingResponse;
import com.gts.toc.response.MiscParamResponse;
import com.gts.toc.response.ParameterResponse;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.utility.Utility;
import com.gts.toc.view.Dialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderTask {
    static DatabaseHandler mDataBase    = new DatabaseHandler();
    static ProgressDialog mLoadingDialog;

    public static void onGetParameter(final Runnable mSuccessAction, final Runnable mFailedAction, final String Parameter) {
        final ObjUser mUserinfo                     = new ObjUser();
        ApiService apiService                       = ApiClient.getClient().create(ApiService.class);
        Call<ParameterResponse> mRequest  = apiService.getParameter(mUserinfo.UserAuth, mUserinfo.UserID, Parameter);
        mRequest.enqueue(new Callback<ParameterResponse>() {
            @Override
            public void onResponse(Call<ParameterResponse> call, Response<ParameterResponse> mResponse) {
                ParameterResponse Result    = mResponse.body();
                int ResultCode              = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        List<ParameterResponse.ObjParameter> ListParameter    = Result.getResultData();
                        int Count = ListParameter.size();
                        switch (Parameter) {
                            case GeneralConstant.PARAM_CATEGORY:
                                for (int i=0; i < Count; i++){
                                    ParameterResponse.ObjParameter mParam = ListParameter.get(i);
                                    if (mDataBase.GetCategory(mParam.getID()).size() > 0)
                                        mDataBase.UpdateCategory(new MstCategory(mParam.getID(), mParam.getDescription()));
                                    else
                                        mDataBase.AddCategory(new MstCategory(mParam.getID(), mParam.getDescription()));
                                }
                                break;
                            case GeneralConstant.PARAM_TYPE:
                                for (int i=0; i < Count; i++){
                                    ParameterResponse.ObjParameter mParam = ListParameter.get(i);
                                    if (mDataBase.GetType(mParam.getID()).size() > 0)
                                        mDataBase.UpdateType(new MstType(
                                                mParam.getID(),
                                                mParam.getDescription(),
                                                mParam.getPrice(),
                                                mParam.getCategory()));
                                    else
                                        mDataBase.AddType(new MstType(
                                                mParam.getID(),
                                                mParam.getDescription(),
                                                mParam.getPrice(),
                                                mParam.getCategory()));
                                }
                                break;
                        }
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 202:
                        if (mFailedAction != null)
                            mFailedAction.run();
                        break;
                    case 203:
                        break;
                    case 204:
                        break;
                }
            }

            @Override
            public void onFailure(Call<ParameterResponse> call, Throwable t) {}
        });
    }
    public static void onCreateOrder(final Context mContext, final Runnable mSuccessAction, final Runnable mFailedAction,
                                       final String Title, final String Description, final String Cost,
                                final String Address, final String Point, final String Type) {
        final ObjUser mUserinfo = new ObjUser();
        mLoadingDialog          = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService                   = ApiClient.getClient().create(ApiService.class);
        Call<BookingResponse> mRequest  = apiService.postCreateOrder(
                mUserinfo.UserAuth,
                mUserinfo.UserID,
                Title,
                Description,
                Cost,
                Address,
                Point,
                Type);
        mRequest.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> mResponse) {
                BookingResponse Result  = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        mLoadingDialog.dismiss();
                        if (mSuccessAction != null)
                            mSuccessAction.run();

                        if (Utility.isNightTime()){
                            Dialog.ActionDialog(mContext, mContext.getResources().getString(R.string.msg_order_night), onGotoPayment(mContext, Result.getOrderId()));
                        }else{
                            Dialog.ActionDialog(mContext, mContext.getResources().getString(R.string.msg_order_success), onGotoPayment(mContext, Result.getOrderId()));
                        }
                        break;
                    case 202:
                        mLoadingDialog.dismiss();
                        if (mFailedAction != null)
                            mFailedAction.run();
                    case 204:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_noregistered));
                        break;
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                mLoadingDialog.dismiss();
                Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
            }
        });
    }
    private static Runnable onGotoPayment(final Context mContext, final String OrderId){
        Runnable aRunnable = new Runnable(){
            public void run(){
                int ServiceCost = getBookingFee();
                Intent intent  = new Intent(mContext, PaymentActivity.class);
                intent.putExtra(GeneralConstant.PARAM_ORDERID, OrderId);
                intent.putExtra(GeneralConstant.PARAM_COST, ServiceCost);
                intent.putExtra(GeneralConstant.PAYMENT_TYPE, GeneralConstant.PAYMENT_BOOKING);
                mContext.startActivity(intent);
            }
        };
        return aRunnable;
    }
    private static int getBookingFee(){
        List<MstParams> ParamsList = mDataBase.GetParams(GeneralConstant.PARAMS_FEE_ID);
        if (ParamsList.size() > 0){
            MstParams mParams = ParamsList.get(0);
            if (mParams.getParamsValue() != null){
                return Integer.parseInt(mParams.getParamsValue());
            }else{
                return 20000;
            }
        }else{
            return 20000;
        }
    }
    public static void onCancelOrder(final Context mContext, final Runnable mSuccessAction, final String OrderID) {
        final ObjUser mUserinfo = new ObjUser();
        mLoadingDialog          = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService                   = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> mRequest  = apiService.postCancelOrder(
                mUserinfo.UserAuth,
                mUserinfo.UserID,
                OrderID);
        mRequest.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> mResponse) {
                BasicResponse Result    = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        mLoadingDialog.dismiss();
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 202:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_key_invalid));
                        break;
                    case 203:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_order_invalid));
                        break;
                    case 204:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_noregistered));
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                mLoadingDialog.dismiss();
                Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
            }
        });
    }
    public static void onGetBank(final Runnable mSuccessAction, final Runnable mFailedAction) {
        final ObjUser mUserinfo                     = new ObjUser();
        ApiService apiService                       = ApiClient.getClient().create(ApiService.class);
        Call<BankResponse> mRequest       = apiService.getBank(mUserinfo.UserAuth, mUserinfo.UserID);
        mRequest.enqueue(new Callback<BankResponse>() {
            @Override
            public void onResponse(Call<BankResponse> call, Response<BankResponse> mResponse) {
                BankResponse Result     = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        List<BankResponse.ObjBank> ListParameter    = Result.getResultData();
                        int Count = ListParameter.size();
                        for (int i=0; i < Count; i++){
                            BankResponse.ObjBank mParam = ListParameter.get(i);
                            if (mDataBase.GetBankbyID(mParam.getBankID()).size() > 0)
                                mDataBase.Updatebank(new MstBank(
                                        mParam.getBankID(),
                                        mParam.getBankName(),
                                        mParam.getAccountName(),
                                        mParam.getReceningNo()));
                            else
                                mDataBase.AddBank(new MstBank(
                                        mParam.getBankID(),
                                        mParam.getBankName(),
                                        mParam.getAccountName(),
                                        mParam.getReceningNo()));
                        }
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 202:
                        if (mFailedAction != null)
                            mFailedAction.run();
                        break;
                    case 203:
                        break;
                    case 204:
                        break;
                }
            }

            @Override
            public void onFailure(Call<BankResponse> call, Throwable t) {}
        });
    }
    public static void UploadPageAttachment(final Context mContext, String mOrderID, String mType, String mRekening,
                                            String mNominal, String mDescription,
                                            byte[] mBitmapData, final Runnable mSuccessAction){

        mLoadingDialog  = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        String mTimeStamp   = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        String timeStamp    = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
        String mFileName    = "IMG_" + timeStamp + ".jpg";
        File FileSource                         = Utility.createFileFromBitmap(mBitmapData, mFileName);
        final ObjUser mUserinfo                 = new ObjUser();
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        requestBodyMap.put("user_auth", RequestBody.create(MediaType.parse("text/plain"), mUserinfo.UserAuth));
        requestBodyMap.put("user_id", RequestBody.create(MediaType.parse("text/plain"), mUserinfo.UserID));
        requestBodyMap.put("order_id", RequestBody.create(MediaType.parse("text/plain"), mOrderID));
        requestBodyMap.put("type", RequestBody.create(MediaType.parse("text/plain"), mType));
        requestBodyMap.put("date", RequestBody.create(MediaType.parse("text/plain"), mTimeStamp));
        requestBodyMap.put("rekening", RequestBody.create(MediaType.parse("text/plain"), mRekening));
        requestBodyMap.put("nominal", RequestBody.create(MediaType.parse("text/plain"), mNominal));
        requestBodyMap.put("description", RequestBody.create(MediaType.parse("text/plain"), mDescription));

        String fileName     = "image\"; filename=\"" + mFileName;
        RequestBody Body    = RequestBody.create(MediaType.parse("multipart/form-data"), FileSource);
        requestBodyMap.put(fileName, Body);

        ApiService apiService                   = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> mRequest   = apiService.postConfirmPayment(requestBodyMap);
        mRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> mResponse) {
                try {
                    String StrResponse      = mResponse.body().string();
                    Gson mGsonBookResponse  = new Gson();
                    BasicResponse Result    = mGsonBookResponse.fromJson(StrResponse, BasicResponse.class);
                    int ResultCode          = Result.getResultState();

                    switch (ResultCode) {
                        case 200:
                            mLoadingDialog.dismiss();
                            if (mSuccessAction != null)
                                mSuccessAction.run();
                            break;
                        case 202:
                            mLoadingDialog.dismiss();
                            Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_key_invalid));
                            break;
                        case 203:
                            mLoadingDialog.dismiss();
                            Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_order_invalid));
                            break;
                        case 204:
                            mLoadingDialog.dismiss();
                            Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_noregistered));
                            break;
                    }
                } catch (IOException e) {
                    mLoadingDialog.dismiss();
                    Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mLoadingDialog.dismiss();
                Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
            }
        });
    }

    public static void onUpdatePickupState(final Context mContext, final Runnable mSuccessAction,
                                          final String OrderID, final String PickupState) {
        final ObjUser mUserinfo = new ObjUser();
        mLoadingDialog          = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService           = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> mRequest    = apiService.postConfirmPickup(
                mUserinfo.UserAuth,
                mUserinfo.UserID,
                OrderID,
                PickupState);
        mRequest.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> mResponse) {
                BasicResponse Result    = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        mLoadingDialog.dismiss();
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 202:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_key_invalid));
                        break;
                    case 203:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_order_invalid));
                        break;
                    case 204:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_noregistered));
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                mLoadingDialog.dismiss();
                Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
            }
        });
    }
    public static void onUpdateEstimasiState(final Context mContext, final Runnable mSuccessAction,
                                           final String OrderID, final String EstimasiState) {
        final ObjUser mUserinfo = new ObjUser();
        mLoadingDialog          = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService           = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> mRequest    = apiService.postConfirmEstimasi(
                mUserinfo.UserAuth,
                mUserinfo.UserID,
                OrderID,
                EstimasiState);
        mRequest.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> mResponse) {
                BasicResponse Result    = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        mLoadingDialog.dismiss();
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 202:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_key_invalid));
                        break;
                    case 203:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_order_invalid));
                        break;
                    case 204:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_noregistered));
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                mLoadingDialog.dismiss();
                Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
            }
        });
    }
    public static void onUpdateDeliveryState(final Context mContext, final Runnable mSuccessAction,
                                             final String OrderID, final String DeliveryState) {
        final ObjUser mUserinfo = new ObjUser();
        mLoadingDialog          = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService           = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> mRequest    = apiService.postConfirmDelivery(
                mUserinfo.UserAuth,
                mUserinfo.UserID,
                OrderID,
                DeliveryState);
        mRequest.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> mResponse) {
                BasicResponse Result    = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        mLoadingDialog.dismiss();
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 202:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_key_invalid));
                        break;
                    case 203:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_order_invalid));
                        break;
                    case 204:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_noregistered));
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                mLoadingDialog.dismiss();
                Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
            }
        });
    }
    public static void onFinishOrder(final Context mContext, final Runnable mSuccessAction, final String OrderID) {
        final ObjUser mUserinfo = new ObjUser();
        mLoadingDialog          = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService           = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> mRequest    = apiService.postFinishOrder(
                mUserinfo.UserAuth,
                mUserinfo.UserID,
                OrderID);
        mRequest.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> mResponse) {
                BasicResponse Result    = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        mLoadingDialog.dismiss();
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 202:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_key_invalid));
                        break;
                    case 203:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_order_invalid));
                        break;
                    case 204:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_noregistered));
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                mLoadingDialog.dismiss();
                Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
            }
        });
    }
    public static void onAddClaim(final Context mContext, final Runnable mSuccessAction, final Runnable mFailedAction,
                                     final String OrderID, final String Description) {
        final ObjUser mUserinfo = new ObjUser();
        mLoadingDialog          = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService                   = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> mRequest  = apiService.postAddClaim(
                mUserinfo.UserAuth,
                mUserinfo.UserID,
                OrderID,
                Description);
        mRequest.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> mResponse) {
                BasicResponse Result    = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        mLoadingDialog.dismiss();
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 202:
                        mLoadingDialog.dismiss();
                        if (mFailedAction != null)
                            mFailedAction.run();
                    case 203:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_order_invalid));
                        break;
                    case 204:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_noregistered));
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                mLoadingDialog.dismiss();
                Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
            }
        });
    }

    public static void onGetMiscParams(final Runnable mSuccessAction, final Runnable mFailedAction) {
        final ObjUser mUserinfo                     = new ObjUser();
        ApiService apiService                       = ApiClient.getClient().create(ApiService.class);
        Call<MiscParamResponse> mRequest            = apiService.getMiscParams(mUserinfo.UserAuth, mUserinfo.UserID);
        mRequest.enqueue(new Callback<MiscParamResponse>() {
            @Override
            public void onResponse(Call<MiscParamResponse> call, Response<MiscParamResponse> mResponse) {
                MiscParamResponse Result    = mResponse.body();
                int ResultCode              = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        List<MiscParamResponse.ObjParameter> ListParameter    = Result.getResultData();
                        int Count = ListParameter.size();
                        for (int i=0; i < Count; i++){
                            MiscParamResponse.ObjParameter mParam = ListParameter.get(i);
                            if (mDataBase.GetParams(mParam.getParamsID()).size() > 0)
                                mDataBase.UpdateParams(new MstParams(
                                        mParam.getParamsID(),
                                        mParam.getParamsName(),
                                        mParam.getParamsvalue()));
                            else
                                mDataBase.AddParams(new MstParams(
                                        mParam.getParamsID(),
                                        mParam.getParamsName(),
                                        mParam.getParamsvalue()));
                        }
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 202:
                        if (mFailedAction != null)
                            mFailedAction.run();
                        break;
                    case 203:
                        break;
                    case 204:
                        break;
                }
            }

            @Override
            public void onFailure(Call<MiscParamResponse> call, Throwable t) {
                if (mFailedAction != null)
                    mFailedAction.run();
            }
        });
    }
}
