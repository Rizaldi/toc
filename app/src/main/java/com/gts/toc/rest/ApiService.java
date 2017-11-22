package com.gts.toc.rest;

import com.gts.toc.response.BankResponse;
import com.gts.toc.response.BannerResponse;
import com.gts.toc.response.BasicResponse;
import com.gts.toc.response.BookingResponse;
import com.gts.toc.response.MiscParamResponse;
import com.gts.toc.response.OrderDetailResponse;
import com.gts.toc.response.OrderResponse;
import com.gts.toc.response.ParameterResponse;
import com.gts.toc.response.PositionResponse;
import com.gts.toc.response.ProfileResponse;
import com.gts.toc.utility.GeneralConstant;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by warsono on 12/16/16.
 */
public interface ApiService {

    @FormUrlEncoded
    @POST(GeneralConstant.API_USER_CHECK)
    Call<BasicResponse> postChecking(
            @Field("state") String State,
            @Field("parameter") String Parameter);

    @FormUrlEncoded
    @POST(GeneralConstant.API_REGISTRATION)
    Call<BasicResponse> postRegistration(
            @Field("name") String UserName,
            @Field("email") String Email,
            @Field("phone") String Phone,
            @Field("password") String Password);

    @FormUrlEncoded
    @POST(GeneralConstant.API_LOGIN)
    Call<ProfileResponse> postLogin(
            @Field("user") String User,
            @Field("password") String Password,
            @Field("type") String TypeUser);

    @FormUrlEncoded
    @POST(GeneralConstant.API_UPDATE_PROFILE)
    Call<BasicResponse> postUpdateProfile(
            @Field("user_auth") String UserAuth,
            @Field("user_id") String UserID,
            @Field("name") String Name,
            @Field("phone") String Phone,
            @Field("address") String Address);

    @FormUrlEncoded
    @POST(GeneralConstant.API_CHANGE_PASS)
    Call<ProfileResponse> postChangePassword(
            @Field("user_auth") String UserAuth,
            @Field("user_id") String UserID,
            @Field("old_password") String OldPassword,
            @Field("new_password") String NewPassword);

    @FormUrlEncoded
    @POST(GeneralConstant.API_GET_AUTH)
    Call<ProfileResponse> getAuth(
            @Field("user_auth") String UserAuth,
            @Field("user_id") String UserID,
            @Field("email") String Email);

    @GET(GeneralConstant.API_GET_PARAMETER)
    Call<ParameterResponse> getParameter(
            @Query("auth") String UserAuth,
            @Query("id") String UserID,
            @Query("p") String Parameter);

    @GET(GeneralConstant.API_GET_ORDER)
    Call<OrderResponse> getOrder(
            @Query("auth") String UserAuth,
            @Query("id") String UserID,
            @Query("p") String Parameter);

    @GET(GeneralConstant.API_GET_ORDERDETAIL)
    Call<OrderDetailResponse> getOrderDetail(
            @Query("auth") String UserAuth,
            @Query("id") String UserID,
            @Query("o_id") String OrderID);

    @FormUrlEncoded
    @POST(GeneralConstant.API_ADD_ORDER)
    Call<BookingResponse> postCreateOrder(
            @Field("user_auth") String UserAuth,
            @Field("user_id") String UserID,
            @Field("title") String Title,
            @Field("desc") String Description,
            @Field("cost") String Cost,
            @Field("address") String Address,
            @Field("point") String Point,
            @Field("type") String Type);

    @FormUrlEncoded
    @POST(GeneralConstant.API_CANCEL_ORDER)
    Call<BasicResponse> postCancelOrder(
            @Field("user_auth") String UserAuth,
            @Field("user_id") String UserID,
            @Field("order_id") String OrderID);

    @GET(GeneralConstant.API_GET_BANK)
    Call<BankResponse> getBank(
            @Query("auth") String UserAuth,
            @Query("id") String UserID);

    @Multipart
    @POST(GeneralConstant.API_CONFIRM_PAYMENT)
    Call<ResponseBody> postConfirmPayment(
            @PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST(GeneralConstant.API_CONFIRM_PICKUP)
    Call<BasicResponse> postConfirmPickup(
            @Field("user_auth") String UserAuth,
            @Field("user_id") String UserID,
            @Field("order_id") String OrderID,
            @Field("state") String PickupState);

    @FormUrlEncoded
    @POST(GeneralConstant.API_CONFIRM_ESTIMASI)
    Call<BasicResponse> postConfirmEstimasi(
            @Field("user_auth") String UserAuth,
            @Field("user_id") String UserID,
            @Field("order_id") String OrderID,
            @Field("state") String EstimasiState);

    @FormUrlEncoded
    @POST(GeneralConstant.API_CONFIRM_DELIVERY)
    Call<BasicResponse> postConfirmDelivery(
            @Field("user_auth") String UserAuth,
            @Field("user_id") String UserID,
            @Field("order_id") String OrderID,
            @Field("state") String EstimasiState);

    @FormUrlEncoded
    @POST(GeneralConstant.API_CONFIRM_FINISH)
    Call<BasicResponse> postFinishOrder(
            @Field("user_auth") String UserAuth,
            @Field("user_id") String UserID,
            @Field("order_id") String OrderID);

    @FormUrlEncoded
    @POST(GeneralConstant.API_ADD_CLAIM)
    Call<BasicResponse> postAddClaim(
            @Field("user_auth") String UserAuth,
            @Field("user_id") String UserID,
            @Field("order_id") String OrderID,
            @Field("desc") String Description);

    @GET(GeneralConstant.API_GET_PARAMS)
    Call<MiscParamResponse> getMiscParams(
            @Query("auth") String UserAuth,
            @Query("id") String UserID);

    @GET(GeneralConstant.API_GET_BANNER)
    Call<BannerResponse> getBanner(
            @Query("auth") String UserAuth,
            @Query("id") String UserID);

    @GET(GeneralConstant.API_GET_TECH)
    Call<PositionResponse> getTechnician(
            @Query("auth") String UserAuth,
            @Query("id") String UserID,
            @Query("tech") String TechnicianID);

    @FormUrlEncoded
    @POST(GeneralConstant.API_RESET_PASS)
    Call<BasicResponse> postResetPassword(
            @Field("email") String UserAuth);
}
