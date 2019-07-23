package com.jadaperkasa.mifaredesfire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.jadaperkasa.mifaredesfire.common.mainApp;
import com.jadaperkasa.mifaredesfire.service.json.Operatorobject;
import com.jadaperkasa.mifaredesfire.service.json.PasswordObject;
import com.jadaperkasa.mifaredesfire.service.json.responseUpload;
import com.jadaperkasa.mifaredesfire.service.repository.RestRepo;
import com.jadaperkasa.mifaredesfire.tools.InternetService;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View dialogView;
    private EditText txt_no_ktp_buat, txt_nama, txt_pass;
    private String s_ktp_buat, s_ktp_new, s_nama, s_pass,s_no_ktp, s_pass_lama, s_pass_baru;
    private int int_acc;
    private Spinner tipe_account;
    private  ArrayAdapter<CharSequence> adapter;

    private InternetService internetService  ;
    private TextView statusOline, statusOP;
    private boolean online = false;
    private boolean newuser = false;
    private Snackbar mysnack;
    private boolean edituser=false;

    private SharedPreferences myPref;
    private String statusLog ="sLog";
    private AlertDialog dialogcreate;
    private EditText t_pass_baru, t_pass_lama;
    private  BottomNavigationView bottomNavigationView;
    private int action=0;
    private int state=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusOline = findViewById(R.id.statusLogin);
        statusOP = findViewById(R.id.statusOperation);
        internetService =  new InternetService();

        myPref = getSharedPreferences(statusLog,MODE_PRIVATE);

        bottomNavigationView =  findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if(online) {

                            switch (item.getItemId()) {
                                case R.id.adduser:
                                    tampilSnack("Tap KTP untuk menambah User");
                                    state=1;

                                    return true;
                                case R.id.edituser:
                                    dialogEditForm();
                                   state=2;
                                    return true;

                                case R.id.deluser:
                                    tampilSnack("Tap KTP untuk menghapus User");
                                    state=3;
                                    return true;
                            }

                        }
                        else{

                            tampilSnack("Mohon login dengan tap KTP!");
                        }


                        return false;
                    }
                });
        startLogin();

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
                if(!(state==2)) {
                    checkEktpID();
                }
                else{
                    tampilSnack("Mode Edit Hanya Bisa Mengubah Password Account Sendiri!");
                }
                break;
            }

            case mainApp.NFCA: {
                tampilSnack("Kartu Tidak dikenali");
                break;
            }
        }
    }

    private void checkEktpID() {
        s_ktp_new = mainApp.byte2HexString(mainApp.getUID());

        if (dialogcreate != null) {
            if (dialogcreate.isShowing()) {
                dialogcreate.dismiss();
                dialogcreate = null;
                dialog=null;
                dialogView = null;
            }

        }

        //if (!s_ktp_new.equals(s_ktp_buat)) {
//        if(!edituser) {
            checkAcc();
//        }
//        else{
//            tampilSnack("Mode Edit Hanya Bisa Mengubah Password Account Sendiri!");
//        }
     //   }
      //  else{
      //      tampilSnack("KTP yang di TAP sama dengan yang dipergunakan untuk Login!");
    //    }

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

    public void dialogDelForm(final responseUpload responseupload) {
        // setup the alert builderView

        dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Delete User");
        dialog.setMessage("Yakin ingin menghapus user " + responseupload.getNama() + "?");
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.ic_warning);

        dialog.setPositiveButton("HAPUS", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (internetService.isOnline()) {
                    RestRepo restRepo =  new RestRepo();
                    PasswordObject passwordObject = new PasswordObject("","",s_ktp_new,
                            responseupload.getNo_akses());

                    Single<responseUpload>callUpload = restRepo.del_op(passwordObject);

                    callUpload.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                            subscribe(
                                    new SingleObserver<responseUpload>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(responseUpload responseupload) {
                                            tampilSnack( "User  Telah Dihapus");

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            tampilSnack("User Gagal Dihapus!");


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




    public void dialogCheckAdd(final responseUpload responseupload) {
        // setup the alert builderView

        dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Informasi");
        dialog.setMessage("User sudah terdaftar sebagai: " + responseupload.getAkses());
        dialog.setCancelable(true);

        dialog.setIcon(R.drawable.ic_warning);

        dialog.setPositiveButton("LANJUT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialogcreate.dismiss();
                dialogAddForm(responseupload);

            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogcreate.dismiss();
            }
        });

        dialogcreate = dialog.create();
        if (!dialogcreate.isShowing()){
            dialogcreate.show();
        }
    }




    private void dialogAddForm(final responseUpload responseupload) {
        dialog = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.add_user, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.ic_person);
        dialog.setTitle("Registrasi");


        txt_nama    = (EditText) dialogView.findViewById(R.id.nama);
        txt_pass    = (EditText) dialogView.findViewById(R.id.pass);
        txt_no_ktp_buat  = (EditText) dialogView.findViewById(R.id.idKTP);
        tipe_account = (Spinner) dialogView.findViewById(R.id.tipe);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.tipe1, android.R.layout.simple_spinner_item);
        txt_nama.setText(responseupload.getNama());
        txt_no_ktp_buat.setText(responseupload.getEktp());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipe_account.setAdapter(adapter);

        dialog.setPositiveButton("SIMPAN", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                s_nama    = txt_nama.getText().toString();
                s_no_ktp    = txt_no_ktp_buat.getText().toString();
                s_pass  = txt_pass.getText().toString();
                int_acc = tipe_account.getSelectedItemPosition();
                if(!checkAkses(responseupload.getNo_akses(),int_acc)){
                    if (internetService.isOnline()) {
                        RestRepo restRepo =  new RestRepo();
                        Operatorobject operatorobject = new Operatorobject(s_ktp_new,s_pass,1,s_ktp_buat,s_nama,
                                s_no_ktp,int_acc);

                        Single<responseUpload>callUpload = restRepo.tambahOperator(operatorobject);

                        callUpload.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                                subscribe(
                                        new SingleObserver<responseUpload>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(responseUpload responseUpload) {
                                                tampilSnack("User: "+s_nama +" Berhasil ditambahkan");
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                tampilSnack("User: "+s_nama +" Gagal ditambahkan");

                                            }
                                        });


                        dialog.dismiss();
                    }
                    else{
                        tampilSnack("Server is not connected!");
                        dialog.dismiss();
                    }
                }
                else{
                    tampilSnack("Mohon Pilih Tipe Lain!");
                    dialog.dismiss();

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

        dialogcreate.show();
    }


    private void dialogEditForm() {
        dialog = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.edit_user, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.ic_edit);
        dialog.setTitle("Ganti Password");


        t_pass_lama    =  dialogView.findViewById(R.id.pass_lama);
        t_pass_baru    =  dialogView.findViewById(R.id.pass_baru);

        t_pass_lama.setText("");
        t_pass_baru.setText("");

        dialog.setPositiveButton("SIMPAN", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                s_pass_lama    = t_pass_lama.getText().toString();
                s_pass_baru    = t_pass_baru.getText().toString();

                if (internetService.isOnline()) {
                    RestRepo restRepo =  new RestRepo();
                    PasswordObject passwordObject = new PasswordObject(s_pass_lama,s_pass_baru,s_ktp_buat,action);

                    Single<responseUpload>callUpload = restRepo.edit_op(passwordObject);

                    callUpload.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                            subscribe(
                                    new SingleObserver<responseUpload>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(responseUpload responseUpload) {
                                            tampilSnack("Berhasil diubah!");
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            tampilSnack("Gagal diubah!");

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

        dialogcreate.show();
    }

    public void kosong(){
        txt_nama.setText("");
        txt_pass.setText("");
        txt_no_ktp_buat.setText("");

    }



    public void startLogin(){
        Intent intent = new Intent(this, fingerPrint.class);
        startActivityForResult(intent,mainApp.HALLOGIN );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mainApp.HALLOGIN){
            statusOline.setText("Logout");
            statusOline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_logout, 0, 0, 0);
            statusOP.setText(data.getStringExtra("nama"));
            statusOP.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person, 0, 0, 0);
            SharedPreferences.Editor prefEdit = myPref.edit();
            prefEdit.putString("idktp",data.getStringExtra("idktp"));
            prefEdit.apply();
            s_ktp_buat = data.getStringExtra("idktp");
            online = true;
            action = data.getIntExtra("akses",0);
            bottomNavigationView.getMenu().clear();
            if( action == 0){
                bottomNavigationView.inflateMenu(R.menu.bottom_navigation_main);
            }
            else{
                bottomNavigationView.inflateMenu(R.menu.bottom_navigation_main2);
            }


        }
        if (requestCode == mainApp.HALNFC) {
            //checkConnection();
        }
        else{
            if (requestCode == mainApp.HALWIRELESS){
               // startLogin();
            }
        }
    }


    public void setStatusOk() {
        newuser = true;

    }

    public void setStatusGagal(){
        newuser = false;
    }





    public void tampilSnack(String s) {
        mysnack =  Snackbar.make(findViewById(R.id.top_coordinator),
                s,Snackbar.LENGTH_LONG);
        mysnack.getView().setBackgroundColor(Color.RED);
        mysnack.show();
    }

    @Override
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
        SharedPreferences.Editor prefEdit = myPref.edit();
        prefEdit.clear();
        prefEdit.apply();



    }


    public void logoutAction(View view) {
        SharedPreferences.Editor prefEdit = myPref.edit();
        prefEdit.clear();
        prefEdit.apply();
        startLogin();
        online = false;

    }


    public void checkAcc(){
        if (internetService.isOnline()) {
            RestRepo restRepo =  new RestRepo();
            PasswordObject passwordObject = new PasswordObject("","",s_ktp_new,6);

            Single<responseUpload>callUpload = restRepo.checkMainLogin(passwordObject);

            callUpload.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(
                            new SingleObserver<responseUpload>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(responseUpload responseupload) {
                                    if(state==1){
                                        if(responseupload.getNo_akses() == 7) {
                                            tampilSnack("Kartu udah terdaftar di semua tipe!");

                                        }
                                        else {

                                            if(responseupload.getNo_akses() == 8) {
                                                dialogAddForm(responseupload);

                                            }
                                            else {
                                                dialogCheckAdd(responseupload);

                                            }

                                        }


                                    }
                                    else {

                                        if(state==3){
                                            if(responseupload.getNo_akses() != 8){
                                                dialogDelForm(responseupload);
                                            }
                                            else{
                                                tampilSnack("Katu tidak terdaftar!");
                                            }


                                        }


                                    }

                                }

                                @Override
                                public void onError(Throwable e) {
                                    tampilSnack("Kartu sudah terdaftar!");

                                }
                            });

        }
        else{
            tampilSnack("No Internet");
        }
    }

    public boolean checkAkses(int hak_akses, int dropdown){
        if(hak_akses ==8) {
            return false;
        }
        else{
            return (dropdown==0 &&   (hak_akses==0 || hak_akses==3||hak_akses==5)) ||
                    (dropdown==1 &&  (hak_akses==1 || hak_akses==3||hak_akses==6)) ||
                    (dropdown==2 &&  (hak_akses==2 || hak_akses==5||hak_akses==6));
        }

    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        startLogin();
//    }
}


