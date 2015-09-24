package com.koolcloud.sdk.fmsc.domain.database.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.koolcloud.sdk.fmsc.domain.database.BankDB;
import com.koolcloud.sdk.fmsc.util.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Bug exist during copy db to /data/data/${package}/databases/*.db
 * waiting for fixing
 */
@Deprecated
public class DataBaseUtils {

    private static String dbPath;
    public static void createFromRawDataBase(Context context, String dataBaseFullPath, int rawDBId) throws IOException {
        dbPath = dataBaseFullPath;
        if (!checkDataBase()) {
            //do nothing - database already exist
            try {
                copyDataBase(context, "", rawDBId);
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    public static void createFromAssetDataBase(Context context, String dataBaseFullPath, String assetDBName) throws IOException {
        dbPath = dataBaseFullPath;
        if (!checkDataBase()) {
            //do nothing - database already exist
            try {
                copyDataBase(context, assetDBName, -1);
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private static boolean checkDataBase() {

        File dbfile = new File(dbPath);
        if (dbfile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private static void copyDataBase(Context context, String assetDBName, int rawDBId) throws IOException {
        //Open your local db as the input stream
        InputStream myInput = null;
        if (!TextUtils.isEmpty(assetDBName)) {
            myInput = context.getAssets().open(assetDBName);
        } else {
            myInput = context.getResources().openRawResource(rawDBId);
        }
        // Path to the just created empty db
//        String outFileName = BankDB.DB_PATH + BankDB.DATABASE_NAME;
        String outFileName;
        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            outFileName = context.getApplicationInfo().dataDir + "/databases/" + BankDB.DATABASE_NAME;
        } else {
            outFileName = "/data/data/" + context.getPackageName() + "/databases/" + BankDB.DATABASE_NAME;
        }
        Logger.i("path:" + outFileName);
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
}
