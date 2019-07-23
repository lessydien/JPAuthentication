package com.jadaperkasa.mifaredesfire.service.rest;


import android.graphics.Path;

import com.jadaperkasa.mifaredesfire.service.json.Operatorobject;
import com.jadaperkasa.mifaredesfire.service.json.PasswordObject;
import com.jadaperkasa.mifaredesfire.service.json.responseUpload;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface PostToCloud {

    @POST("operator/insert.php")
    Single<responseUpload> tambahOperator(
            @Body Operatorobject operatorobject
    );


    @POST("operator/login.php")
    Single<responseUpload> login(
            @Body Operatorobject operatorobject
    );

    @POST("operator/login_pass.php")
    Single<responseUpload> login_pass(
            @Body Operatorobject operatorobject
    );

    @POST("operator/delete.php")
    Single<responseUpload> del_op(
            @Body PasswordObject passwordObject
    );

    @POST("operator/edit_op.php")
    Single<responseUpload> edit_op(
            @Body PasswordObject passwordObject
    );

    @POST("operator/checkLoginMain.php")
    Single<responseUpload> checkMainLogin(
            @Body PasswordObject passwordObject
    );




}
