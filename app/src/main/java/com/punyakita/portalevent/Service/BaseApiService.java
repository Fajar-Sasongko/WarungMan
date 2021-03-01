package com.punyakita.portalevent.Service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BaseApiService  {

    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> loginRequest(@Field("email") String email,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> registerRequest(@Field("nama") String nama,
                                       @Field("email") String email,
                                       @Field("password") String password);

    @FormUrlEncoded
    @POST("cancelOrder")
    Call<ResponseBody> cancelOrderRequest(@Header("Authorization") String token,
                                           @Field("purchase_id") String purchase_id);

    @FormUrlEncoded
    @POST("storePayment")
    Call<ResponseBody> storePaymentRequest(@Header("Authorization") String token,
                                           @Field("qr_code") String qr_code,
                                           @Field("purchase_id") String purchase_id,
                                           @Field("note") String note,
                                           @Field("total_price") String total_price);

    @FormUrlEncoded
    @POST("storeCart")
    Call<ResponseBody> storeCartRequest(@Header("Authorization") String token,
                                        @Field("seller_id") String seller_id,
                                        @Field("item_id") String item_id,
                                        @Field("amount") String amount);

    @GET("itemDetail/{item_id}")
    Call<ResponseBody> itemDetailRequest(@Header("Authorization") String token,
                                         @Path("item_id") String item_id);

    @GET("deleteCart/{item_id}")
    Call<ResponseBody> deleteCartRequest(@Header("Authorization") String token,
                                         @Path("item_id") String item_id);


    @GET("cartList")
    Call<ResponseBody> cartListRequest(@Header("Authorization") String token,
                                       @Query("purchase_id") String purchase_id);

    @GET("transactionList")
    Call<ResponseBody> transactionListRequest(@Header("Authorization") String token);

    @GET("itemList")
    Call<ResponseBody> itemListRequest(@Header("Authorization") String token);
}
