package com.koolcloud.sdk.fmsc.domain.database.util;

import android.content.Context;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import com.koolcloud.sdk.fmsc.R;
import com.koolcloud.sdk.fmsc.domain.database.BankDB;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by admin on 2015/8/20.
 */
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
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database does't exist yet.
        } catch (Exception e) {

        }

        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
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
        String outFileName = BankDB.DB_PATH + BankDB.DATABASE_NAME;
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
