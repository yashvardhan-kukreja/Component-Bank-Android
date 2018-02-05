package com.ieeevit.componentbank.NetworkAPIs;

import com.ieeevit.componentbank.NetworkModels.BasicModel;
import com.ieeevit.componentbank.NetworkModels.LoginModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by luci4 on 5/2/18.
 */

public interface AuthAPI {

    @POST("login")
    @FormUrlEncoded
    Call<LoginModel> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("register")
    @FormUrlEncoded
    Call<BasicModel> register(
            @Field("name") String name,
            @Field("regno") String regno,
            @Field("email") String email,
            @Field("password") String password,
            @Field("phoneno") String phoneno
    );
}
