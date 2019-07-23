package com.jadaperkasa.mifaredesfire.service.repository;

import android.util.Log;

import com.jadaperkasa.mifaredesfire.service.json.Operatorobject;
import com.jadaperkasa.mifaredesfire.service.json.PasswordObject;
import com.jadaperkasa.mifaredesfire.service.json.responseUpload;
import com.jadaperkasa.mifaredesfire.service.rest.PostToCloud;
import com.jadaperkasa.mifaredesfire.service.rest.retroofitInstance;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestRepo {


    private PostToCloud status_upload;


    public RestRepo() {
        status_upload = retroofitInstance.getRetrofitInstance().create(PostToCloud.class);
    }




    public Single<responseUpload> tambahOperator(Operatorobject operatorobject) {

        return  status_upload.tambahOperator(operatorobject);


    }


    public Single<responseUpload> login(Operatorobject operatorobject) {

        return status_upload.login(operatorobject);

    }

    public Single<responseUpload> login_pass(Operatorobject operatorobject) {

        return status_upload.login_pass(operatorobject);

    }

    public Single<responseUpload> del_op(PasswordObject passwordObject) {

        return status_upload.del_op(passwordObject);

    }

    public Single<responseUpload> edit_op(PasswordObject passwordObject) {

        return status_upload.edit_op(passwordObject);

    }

    public Single<responseUpload> checkMainLogin(PasswordObject passwordObject) {

        return status_upload.checkMainLogin(passwordObject);

    }
}
