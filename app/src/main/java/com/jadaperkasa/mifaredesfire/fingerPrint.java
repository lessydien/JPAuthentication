package com.jadaperkasa.mifaredesfire;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.jadaperkasa.mifaredesfire.common.mainApp;
import com.jadaperkasa.mifaredesfire.service.json.Operatorobject;
import com.jadaperkasa.mifaredesfire.service.json.responseUpload;
import com.jadaperkasa.mifaredesfire.service.repository.RestRepo;
import com.jadaperkasa.mifaredesfire.tools.InternetService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class fingerPrint extends AppCompatActivity {

    private Snackbar mysnack;
    private String idktp;
    private InternetService internetService;
    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View dialogView;
    private AlertDialog dialogcreate;
    private  EditText pass1  ;
    private int action=0;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_print);
        Executor executor = Executors.newSingleThreadExecutor();
        ImageView button = findViewById(R.id.finger);
        FragmentActivity activity = this;
        internetService =  new InternetService();
        dialog = new AlertDialog.Builder(fingerPrint.this);

        bottomNavigationView =  findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.admin:
                                    tampilSnack("Tap KTP untuk Login Admin");
                                    action = 0;
                                    return true;
                                case R.id.station:
                                    tampilSnack("Tap KTP untuk Login Station Officer");
                                    action = 1;
                                    return true;

                                case R.id.field:
                                    tampilSnack("Tap KTP untuk Login Field Officer");
                                    action = 2;
                                    return true;
                            }




                        return false;
                    }
                });


        final BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    // user clicked negative button
                } else {
                    //TODO: Called when an unrecoverable error has been encountered and the operation is complete.
                }
            }
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                closeIntent("ADMIN",action);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //TODO: Called when a biometric is valid but not recognized.
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setDescription(getString(R.string.biometric_description))
                .setNegativeButtonText(getString(R.string.biometric_cancelled))
                .build();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFingerprintAvailable(getApplicationContext())){
                    tampilSnack("Perangkat tidak mendukung Finger Print");

                    // Toast.makeText(this,"Sensor Finger Print tidak ditemukan!",Toast.LENGTH_SHORT).show();
                    // finish();
                }
                else{
                    biometricPrompt.authenticate(promptInfo);
                }


            }
        });

        mainApp.setNfcAdapter(NfcAdapter.getDefaultAdapter(this));
        if (!mainApp.getNfcAdapter().isEnabled()) {
            //tampilSnack("NFC Belum aktif");
            checkNFC("NFC BELUM AKTIF");
        }
        else{
            checkConnection();
        }

    }



    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public static boolean isFingerprintAvailable(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected() || fingerprintManager.hasEnrolledFingerprints();
    }

    public void onResume() {
        super.onResume();
        if (mainApp.getNfcAdapter() == null) {
            mainApp.setNfcAdapter(NfcAdapter.getDefaultAdapter(this));
        }
        mainApp.enableNfcForegroundDispatch(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        mainApp.disableNfcforegroundDispatch(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        mainApp.setPendingIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            mainApp.setTag(tag);
            String[] techList = tag.getTechList();
            chooseCardTye(techList[0]);

        }
    }

    private void chooseCardTye(String card) {
        switch (card) {
            case mainApp.ISODEP: {
                mainApp.setMyIsodep(mainApp.getTag());
                idktp = mainApp.byte2HexString(mainApp.getUID());

                if(dialogcreate != null) {
                    if (dialogcreate.isShowing()) {
                        dialogcreate.dismiss();
                    }

                }

                checkAcc();
                //checkEktpID();
                break;
            }

            case mainApp.NFCA: {
                tampilSnack("Kartu Tidak dikenali");
                break;
            }
        }
    }

    public void tampilSnack(String s) {
        mysnack =  Snackbar.make(findViewById(R.id.top_coordinator),
                s,Snackbar.LENGTH_LONG);
        mysnack.getView().setBackgroundColor(Color.RED);
        mysnack.show();
    }


    public void checkAcc(){
        if (internetService.isOnline()) {
            RestRepo restRepo =  new RestRepo();
            Operatorobject operatorobject = new Operatorobject(idktp,"",1,"","",
                    "",action);

            Single<responseUpload>callUpload = restRepo.login_pass(operatorobject);

            callUpload.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(
                            new SingleObserver<responseUpload>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(responseUpload responseupload) {
                                    DialogLoginForm();

                                }

                                @Override
                                public void onError(Throwable e) {
                                    tampilSnack("Kartu tidak dikenali!");


                                }
                            });

        }
        else{
            tampilSnack("No Internet");
        }
    }


    private void DialogLoginForm() {
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.login, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.ic_login);
        dialog.setTitle("Login : "+ getAksi());


        pass1    = (EditText) dialogView.findViewById(R.id.pass1);

        dialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mypass= pass1.getText().toString();

                if (internetService.isOnline()) {
                    RestRepo restRepo =  new RestRepo();
                    Operatorobject operatorobject = new Operatorobject(idktp,mypass,1,"","",
                            "",action);

                    Single<responseUpload>callUpload = restRepo.login(operatorobject);

                    callUpload.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                            subscribe(
                                    new SingleObserver<responseUpload>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(responseUpload responseupload) {
                                            closeIntent(responseupload.getNama(),action);

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            tampilSnack("Gagal Login");


                                        }
                                    });


                    dialog.dismiss();

                }
                else{
                    tampilSnack("No Internet");
                }

            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogcreate = dialog.create();

       if (!dialogcreate.isShowing()){
            dialogcreate.show();
        }

    }

    public void checkConnection(){
        internetService.setContext(this);
        if (!internetService.checkConnection()){
            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),mainApp.HALWIRELESS);
        }

    }

    public void openNFCSetting(){
        startActivityForResult(new Intent(Settings.ACTION_NFC_SETTINGS),mainApp.HALNFC);
    }

    public void checkNFC(String s1) {
        // setup the alert builderView

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Informasi");
        builder.setMessage(s1);
        // add the buttons
        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openNFCSetting();
            }
        });
        // builder.setNegativeButton("Cancel", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mainApp.HALNFC) {
            checkConnection();
        }
        else{
            if (requestCode == mainApp.HALWIRELESS){
                startActivityForResult(new Intent(Settings.ACTION_SETTINGS),mainApp.MOBILE);
            }
        }
    }

    public void closeIntent(String s, int hak_akses){
        Intent replyIntent = new Intent();
        replyIntent.putExtra("nama",s);
        replyIntent.putExtra("idktp",idktp);
        replyIntent.putExtra("status",1);
        replyIntent.putExtra("akses",hak_akses);
        setResult(RESULT_OK,replyIntent);
        finish();

    }

    public void onDestroy() {
        super.onDestroy();
        // The activity is being created.
        if (dialogcreate != null) {
            if (dialogcreate.isShowing()) {
                dialogcreate.dismiss();
                dialogcreate = null;
                dialog=null;
                dialogView = null;
            }

        }
    }

    public String getAksi() {
        if(action == 0){
            return "Admin";
        }
        else {
            if(action == 1) {
                return "Station Officer";
            }
            else{
                return "Field Officer";
            }
        }

    }
}