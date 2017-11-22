package com.gts.toc.rest;

import android.app.ProgressDialog;
import android.content.Context;

import com.gts.toc.R;
import com.gts.toc.model.DatabaseHandler;
import com.gts.toc.model.MstUser;
import com.gts.toc.object.ObjUser;
import com.gts.toc.response.BasicResponse;
import com.gts.toc.response.ProfileResponse;
import com.gts.toc.utility.GeneralConstant;
import com.gts.toc.view.Dialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserTask {
    static DatabaseHandler mDataBase    = new DatabaseHandler();
    static ProgressDialog mLoadingDialog;

    public static void onUserChecking(final Runnable mSuccessAction, final Runnable mFailedAction,
                                      String State, String Parameter) {

        ApiService apiService                   = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> mRequest  = apiService.postChecking(State, Parameter);
        mRequest.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> mResponse) {
                BasicResponse Result    = mResponse.body();
                int ResultCode       = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 202:
                        if (mFailedAction != null)
                            mFailedAction.run();
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {}
        });
    }

    public static void onRegistration(final Context mContext, final Runnable mSuccessAction, final Runnable mFailedAction,
                                      String UserName, String Email, String Phone, String Password) {
        mLoadingDialog = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService                   = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> mRequest  = apiService.postRegistration(UserName, Email, Phone, Password);
        mRequest.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> mResponse) {
                BasicResponse Result    = mResponse.body();
                int ResultCode       = Result.getResultState();
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
//for development
    public static void onLogin(final Context mContext, final Runnable mSuccessAction, final Runnable mFailedAction,
                               String User, String Password) {
        mLoadingDialog = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService                       = ApiClient.getClient().create(ApiService.class);
        Call<ProfileResponse> mRequest    = apiService.postLogin(User, Password, GeneralConstant.USER_CLIENT);
        mRequest.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> mResponse) {
                ProfileResponse Result  = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        ProfileResponse.ObjUser UserData    = Result.getResultData();
                        mDataBase.AddUser(
                                new MstUser(
                                        UserData.getUserID(),
                                        UserData.getUserName(),
                                        UserData.getUserAuth(),
                                        UserData.getEmail(),
                                        UserData.getPhone(),
                                        UserData.getAddress(),
                                        UserData.getImage()
                                ));
                        mLoadingDialog.dismiss();
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 202:
                        mLoadingDialog.dismiss();
                        if (mFailedAction != null)
                            mFailedAction.run();
                        break;
                    case 203:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_notactive));
                        break;
                    case 204:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_noregistered));
                        break;
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                mLoadingDialog.dismiss();
                Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
            }
        });
    }

    public static void onUpdateProfile(final Context mContext, final Runnable mSuccessAction, final Runnable mFailedAction,
                                       final String Name, final String Phone, final String Address) {
        final ObjUser mUserinfo   = new ObjUser();
        mLoadingDialog      = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService                   = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> mRequest  = apiService.postUpdateProfile(mUserinfo.UserAuth, mUserinfo.UserID, Name, Phone, Address);
        mRequest.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> mResponse) {
                BasicResponse Result    = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        mDataBase.UpdateUser(
                                new MstUser(
                                        mUserinfo.UserID,
                                        Name,
                                        mUserinfo.UserAuth,
                                        mUserinfo.Email,
                                        Phone,
                                        Address,
                                        mUserinfo.Image
                                ));
                        mLoadingDialog.dismiss();
                        if (mSuccessAction != null)
                            mSuccessAction.run();
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
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                mLoadingDialog.dismiss();
                Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
            }
        });
    }

    public static void onResetPassword(final Context mContext, final Runnable mSuccessAction, String Email) {
        mLoadingDialog = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService           = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> mRequest    = apiService.postResetPassword(Email);
        mRequest.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> mResponse) {
                BasicResponse Result    = mResponse.body();
                int ResultCode       = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        mLoadingDialog.dismiss();
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    case 204:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_noregistered));
                        break;
                    default:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
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

    public static void onChangePassword(final Context mContext, final Runnable mSuccessAction, final Runnable mFailedAction,
                                        String OldPassword, String NewPassword) {
        final ObjUser mUserinfo   = new ObjUser();
        mLoadingDialog      = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.msg_loading));

        ApiService apiService                       = ApiClient.getClient().create(ApiService.class);
        Call<ProfileResponse> mRequest    = apiService.postChangePassword(mUserinfo.UserAuth, mUserinfo.UserID, OldPassword, NewPassword);
        mRequest.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> mResponse) {
                ProfileResponse Result  = mResponse.body();
                int ResultCode       = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        ProfileResponse.ObjUser UserData    = Result.getResultData();
                        mDataBase.UpdateUser(
                                new MstUser(
                                        UserData.getUserID(),
                                        mUserinfo.UserName,
                                        UserData.getUserAuth(),
                                        mUserinfo.Email,
                                        mUserinfo.Phone,
                                        mUserinfo.Address,
                                        mUserinfo.Image
                                ));
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
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_change_password_notmatch));
                        break;
                    case 204:
                        mLoadingDialog.dismiss();
                        Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_user_noregistered));
                        break;
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                mLoadingDialog.dismiss();
                Dialog.InformationDialog(mContext, mContext.getResources().getString(R.string.msg_internet_problem));
            }
        });
    }

    public static void onGetAuth(final Runnable mSuccessAction, String Email) {
        final ObjUser mUserinfo                     = new ObjUser();
        ApiService apiService                       = ApiClient.getClient().create(ApiService.class);
        Call<ProfileResponse> mRequest    = apiService.getAuth(mUserinfo.UserAuth, mUserinfo.UserID, Email);
        mRequest.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> mResponse) {
                ProfileResponse Result  = mResponse.body();
                int ResultCode          = Result.getResultState();
                switch (ResultCode) {
                    case 200:
                        ProfileResponse.ObjUser UserData    = Result.getResultData();
                        mDataBase.UpdateUser(
                                new MstUser(
                                        UserData.getUserID(),
                                        mUserinfo.UserName,
                                        UserData.getUserAuth(),
                                        mUserinfo.Email,
                                        mUserinfo.Phone,
                                        mUserinfo.Address,
                                        mUserinfo.Image
                                ));
                        if (mSuccessAction != null)
                            mSuccessAction.run();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {}
        });
    }
}
