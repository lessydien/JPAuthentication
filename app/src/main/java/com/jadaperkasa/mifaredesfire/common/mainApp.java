package com.jadaperkasa.mifaredesfire.common;

import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class mainApp extends Application {

    private static NfcAdapter myNfcAdapter;
    private static Context myContext;
    private static ComponentName myPendingComponentName;
    private static byte[] myUID;
    private static Tag myTag = null;
    private static MifareClassic myMifare;
    private static IsoDep myIsodep;
    private static Intent pendingIntent;

    public static final String ISODEP = "android.nfc.tech.IsoDep";
    public static final String NFCA = "android.nfc.tech.NfcA";

    public static final int SECTORNUM = 1;
    public static final int BLOCKNUM =  4;
    public static final int BLOCKTRAILER = 7;

    public static final int HALNFC = 2;
    public static final int HALWIRELESS = 3;
    public static final int HALLOGIN = 1;
    public static final int MOBILE = 0;


    public static final String SUCCEED = "\u2713";
    public static final String FAILED = "\u1763";

    public static final String HIJAU = "#00FF00";
    public static final String MERAH = "#FF0000";

    public static final int MOTOR = 2000;
    public static final int MOBIL = 5000;




    @Override
    public void onCreate() {
        super.onCreate();
        myContext = getApplicationContext();
    }

    public static SharedPreferences getPreference(){
        return PreferenceManager.getDefaultSharedPreferences(myContext);
    }



    public static void setPendingComponentActivity(ComponentName pendingActivity){
        myPendingComponentName = pendingActivity;
    }

    public static ComponentName getPendingComponentName(){

        return myPendingComponentName;
    }

    public static byte[] getUID(){
        return myUID;
    }


    public static boolean isBBCValid(byte[] uid, byte bcc){
        return calcBCC(uid) == bcc;
    }

    public  static byte calcBCC(byte[] uid)  throws IllegalArgumentException{
        if (uid.length != 4){
            throw new IllegalArgumentException("UID Length is not 4 bytes");
        }

        byte bcc = uid[0];
        for(int i=0; i< uid.length;i++){
            bcc = (byte) (bcc ^ uid[i]);
        }

        return bcc;
    }

    /*
    ---------- set NFC Adapter ----------
     */
    public static NfcAdapter getNfcAdapter() {
        return myNfcAdapter;
    }

    public static void setNfcAdapter(NfcAdapter nfcAdapter) {
        myNfcAdapter = nfcAdapter;
    }

    /*
     -------------- end NFC adapter ------------------------
     */

    /*
   ---------- set PendingIntent ----------
    */
    public static void setPendingIntent(Intent intent) {
        pendingIntent = intent;
    }

    public static Intent getPendingIntent() {
        return pendingIntent;
    }

    /*
     -------------- end PendingIntent ------------------------

    /*
  ---------- set TAG Adapter ----------
   */
    public static void setTag(Tag tag) {
        myTag = tag;
        myUID = myTag.getId();
    }

    public static Tag getTag() {
        return myTag;
    }

    /*
     -------------- end tag adapter ------------------------
     */

    /*
      ---------- set MIfare Adapter ----------
            */
    public static void setMyMifare(Tag tag) {
        myMifare = MifareClassic.get(tag);
    }

    public static MifareClassic getmyMifare() {
        return myMifare;
    }

    /*
     -------------- end mifare adapter ------------------------
     */

    // -------- set Isodep -------
    public static void setMyIsodep(Tag tag) {
        myIsodep = IsoDep.get(tag);
    }

    public static IsoDep getMyIsodep() {
        return myIsodep;
    }


    // ------------ end isodep ------



    /*
    -------------------------- set aplikasi stack -----------
    * */

    public static void enableNfcForegroundDispatch(AppCompatActivity targetActivity) {
        if (myNfcAdapter != null && myNfcAdapter.isEnabled()) {

            Intent intent = new Intent(targetActivity,targetActivity.getClass()).addFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            PendingIntent pendingIntent = PendingIntent.getActivity(targetActivity,
                    0, intent, 0);
            myNfcAdapter.enableForegroundDispatch(
                    targetActivity, pendingIntent, new IntentFilter[]{ndef}, new String[][] {
                            new String[] { NfcA.class.getName() } });
        }
    }

    public static void disableNfcforegroundDispatch(AppCompatActivity appActivity) {
        if (myNfcAdapter != null && myNfcAdapter.isEnabled()) {
            myNfcAdapter.disableForegroundDispatch(appActivity);
        }
    }

    /*
    *  ----------------------end-------------------------------------
    * */


    public static void ConnectmyMifare() {

        try {
            myMifare.connect();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void ConnectmyIsodep() {

        try {
            myIsodep.connect();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static byte[] ReadfromIsodep(byte[] apduCommand) {

        byte[] data=null;
        if (!myIsodep.isConnected()) {
            ConnectmyIsodep();
        }

        try {

            data = myIsodep.transceive(apduCommand);
            myIsodep.close();
        }

        catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    public static void WritetoTag(int sector, int block, byte[] data) {


        if (!myMifare.isConnected()) {
            ConnectmyMifare();
        }

        try {
            boolean authA = myMifare.authenticateSectorWithKeyA(sector, MifareClassic.KEY_DEFAULT);
            myMifare.writeBlock(block,data);
            myMifare.close();
        }

        catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void WritetoTagcustomKey(int sector, int block, byte[] data, byte[] key) {


        if (!myMifare.isConnected()) {
            ConnectmyMifare();
        }

        try {
            boolean authA = myMifare.authenticateSectorWithKeyA(sector, key);
            myMifare.writeBlock(block,data);
            myMifare.close();
        }

        catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static byte[] ReadfromTag(int sector, int block) {

        byte[] data=null;
        if (!myMifare.isConnected()) {
            ConnectmyMifare();
        }

        try {
            boolean authA = myMifare.authenticateSectorWithKeyA(sector, MifareClassic.KEY_DEFAULT);
            data = myMifare.readBlock(block);
            myMifare.close();
        }

        catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static byte[] ReadfromTagcustomKey(int sector, int block,byte[] key) {

        byte[] data=null;
        if (!myMifare.isConnected()) {
            ConnectmyMifare();
        }

        try {
            boolean authA = myMifare.authenticateSectorWithKeyA(sector, key);
            data = myMifare.readBlock(block);
            myMifare.close();
        }

        catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    public static boolean checkcustomKey(int sector, byte[] key) {
        boolean authA =false;
        if (!myMifare.isConnected()) {
            ConnectmyMifare();
        }

        try {
            authA = myMifare.authenticateSectorWithKeyA(sector, key);
            myMifare.close();
        }

        catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return authA;
    }

    public static boolean checkdefaultKey(int sector) {

        boolean authA =false;
        if (!myMifare.isConnected()) {
            ConnectmyMifare();
        }

        try {
             authA = myMifare.authenticateSectorWithKeyA(sector, MifareClassic.KEY_DEFAULT);
            myMifare.close();
        }

        catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return authA;
    }


    public static String byte2HexString(byte[] bytes) {
        StringBuilder ret = new StringBuilder();
        if (bytes != null) {
            for (Byte b : bytes) {
                ret.append(String.format("%02X", b.intValue() & 0xFF));
            }
        }
        return ret.toString();
    }


    public static byte[] createHash(byte[] uidx) {
        byte[] mydata =new byte[6];
        byte[] salt = new byte[]{(byte)10,(byte)76,(byte)19,(byte)0,(byte)50,(byte)67,(byte)222,
                (byte)7,(byte)128,(byte)105,(byte)255,(byte)29,(byte)255,(byte)25,(byte)227,(byte)22};
        int panjang=0;
        final String MD5 = "MD5";
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(salt);
            byte messageDigest[] = digest.digest(uidx);

            for (int i=0 ; i<6;i++) {
                mydata[i] = messageDigest[i];
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //return mydata;
        return mydata;
    }

    public static byte[] writingTrailer(byte[] hashkey) {
        byte[] mydata =new byte[]{(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)255,
                (byte)7,(byte)128,(byte)105,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};

            for (int i=0 ; i<6;i++) {
                mydata[i] = hashkey[i];
            }
        return mydata;
    }

    public static byte[] resetTrailer(){
        byte[] mydata =new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,
                (byte)7,(byte)128,(byte)105,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};

        return mydata;
    }



    public static String tanggalSekarang(){
        String tanggal;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        tanggal = dateFormat.format(date); //2016/11/16 12:08:43
        return tanggal;
    }

    public static void removeText(final TextView textView) {
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("");
            }
        }, 2000);
    }

    public static byte[] StringToByte(String pesan){
        // 16 bytes of block data
        byte[] datakirim = new byte[]{'F','F','F','F','F','F','F',
                'F','F','F','F','F','F','F','F','F'};
        int panjangPesan ;
        if(pesan.isEmpty()) {
            panjangPesan=0;
            datakirim[0] = '0';
        }
        else {
            panjangPesan= pesan.length();
            String pjC =  String.valueOf(panjangPesan);
            datakirim[0] = (byte) pjC.charAt(0);
        }




        for (int i=1;i<=panjangPesan;i++){
            datakirim[i] = (byte) pesan.charAt(i-1);
        }
        return datakirim;
    }

    public static String byteToString(byte[] cekdata){
        String pesanParse=null;
        try{
            pesanParse = new String(cekdata,"UTF-8");
        }
        catch(UnsupportedEncodingException ue){
            ue.printStackTrace();
        }


        int im = Character.getNumericValue(pesanParse.charAt(0));
        String saldoTulis;
        if (im ==0) {
            saldoTulis = "0";
        }
        else {
            saldoTulis = pesanParse.substring(1,im+1);
        }



        return saldoTulis;
    }


    public static byte[] StringToBytePark(String pesan){
        // 16 bytes of block data
        byte[] datakirim = new byte[]{'F','F','F','F','F','F','F',
                'F','F','F','F','F','F','F','F','F'};
        datakirim[0] = '1';

        for (int i=1;i<=10;i++){
            datakirim[i] = (byte) pesan.charAt(i-1);
        }
        return datakirim;
    }

    public static String byteToStringPark(byte[] cekdata){
        String pesanParse=null;
        try{
            pesanParse = new String(cekdata,"UTF-8");
        }
        catch(UnsupportedEncodingException ue){
            ue.printStackTrace();
        }


        String saldoTulis = pesanParse.substring(0,11);

        return saldoTulis;
    }

    public static byte[] StringToByteTime(String pesan){
        // 16 bytes of block data
        byte[] datakirim = new byte[]{'F','F','F','F','F','F','F',
                'F','F','F','F','F','F','F','F','F'};

        String pesan1 = pesan.substring(11,19);

        for (int i=0;i<8;i++){
            datakirim[i] = (byte) pesan1.charAt(i);
        }
        return datakirim;
    }

    public static byte[] StringToByteStatusPark(){
        // 16 bytes of block data
        byte[] datakirim = new byte[]{'0','F','F','F','F','F','F',
                'F','F','F','F','F','F','F','F','F'};
        return datakirim;
    }



    public static String byteToStringTime(byte[] cekdata){
        String pesanParse=null;
        try{
            pesanParse = new String(cekdata,"UTF-8");
        }
        catch(UnsupportedEncodingException ue){
            ue.printStackTrace();
        }


        String saldoTulis = pesanParse.substring(0,8);

        return saldoTulis;
    }




    public static int getDifferenceTime(String waktu1){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d2 = new Date();
        Date d1 = null;
        try {
           d1 = dateFormat.parse(waktu1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = d2.getTime() - d1.getTime();
        long diffHours = diff / (60 * 60 * 1000);
        long diffmod = diff %  (60 * 60 * 1000);
        if (diffmod !=0) {
            diffHours = diffHours + 1;
        }

        return (int) diffHours;
    }
    public static long timeToLong(String waktu1){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = dateFormat.parse(waktu1);
            d2 = dateFormat.parse("2019-06-22 03:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long dtime  = (d1.getTime() - d2.getTime()) / 1000;
        return dtime;
    }





}
