package com.ieeevit.componentbank.NetworkAPIs;

import com.ieeevit.componentbank.NetworkModels.AllComponentsModel;
import com.ieeevit.componentbank.NetworkModels.BasicModel;
import com.ieeevit.componentbank.NetworkModels.GetIssuersModel;
import com.ieeevit.componentbank.NetworkModels.GetMemberComponentsModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by luci4 on 5/2/18.
 */

public interface MemberAPI {


    @POST("delete")
    Call<BasicModel> delete(
            @Header("x-access-token") String token
    );

    @POST("requestComponent")
    @FormUrlEncoded
    Call<BasicModel> requestComponent(
            @Header("x-access-token") String token,
            @Field("id") String componentId,
            @Field("quantity") String quantity
    );

    @GET("getAllComponents")
    Call<AllComponentsModel> getAllComponents(
            @Header("x-access-token") String token
    );

    @POST("getIssuers")
    @FormUrlEncoded
    Call<GetIssuersModel> getIssuers(
            @Header("x-access-token") String token,
            @Field("id") String componentId
    );

    @GET("getIssuedComponents")
    Call<GetMemberComponentsModel> getIssuedComponents(
            @Header("x-access-token") String token
    );

    @GET("getHistory")
    Call<GetMemberComponentsModel> getHistory(
            @Header("x-access-token") String token
    );

    @GET("getRequestedComponents")
    Call<GetMemberComponentsModel> getRequestedComponents(
            @Header("x-access-token") String token
    );


}
