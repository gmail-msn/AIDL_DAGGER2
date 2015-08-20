package com.koolcloud.sdk.fmsc.domain.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.koolcloud.sdk.fmsc.R;
import com.koolcloud.sdk.fmsc.domain.database.util.DataBaseUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <p>Title: BankDB.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: KoolCloud</p>
 * @author 		Teddy
 * @date 		2015-08-19
 * @version 	
 */
public class BankDB extends BaseSqlAdapter {

	public static String DB_PATH = "/data/data/com.koolcloud.sdk.fmsc/databases/";
	public final static String DATABASE_NAME = "bank_table.db";
	private final static int DATABASE_VERSION = 1;
    private final static String BANK_TABLE_NAME = "bank_table";

    private Context context;
    private String dbName;

    private static BankDB bankDB;

    //TODO:acquire institute table columns
    public final static String ISSUER_ID_COL = "ISSUER_ID";
    public final static String ISSUER_NAME_COL = "ISSUER_NAME";
    public final static String ISSUER_NAME_EN_COL = "ISSUER_NAME_EN";
    public final static String BACK_UP_COL = "BAK";

    private BankDB(Context ctx, int version) {
    	this.context = ctx;
		try {
			DataBaseUtils.createFromRawDataBase(context, BankDB.DB_PATH + BankDB.DATABASE_NAME, R.raw.bank_table);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	if (dbName == null) {
    		dbName = context.getFileStreamPath(DATABASE_NAME).getAbsolutePath();
    	}
    	
		mDbHelper = new BankDBHelper(ctx, DATABASE_NAME, null, version);
    } 
    
    public static BankDB getInstance(Context ctx) {
    	if (bankDB == null) {
    		bankDB = new BankDB(ctx, DATABASE_VERSION);
    	}
    	return bankDB;
    }
    
    /**
     * @Title: insertBankData
     * @Description: TODO
     * @param bankDataList
     * @return: void
     */
    public void insertBankData(JSONArray bankDataList) {
    	
    	ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
    	Cursor cursor = null;
    	try {
    		String sql = "INSERT INTO "+ BANK_TABLE_NAME +"(" +
                    ISSUER_ID_COL + ", " +
                    ISSUER_NAME_COL + ", " +
                    ISSUER_NAME_EN_COL + ", " +
                    BACK_UP_COL + ") VALUES(?, ?, ?, ?)";
    		
    		if (bankDataList != null && bankDataList.length() > 0) {
				for (int i = 0; i < bankDataList.length(); i++) {
					JSONObject jsonObj = bankDataList.getJSONObject(i);
					cursor = selectBankByIssuerId(jsonObj.getString("code"));
					if (cursor.getCount() > 0) {
						continue;
					}
					cursor.close();
					String[] params = new String[] {
                        jsonObj.getString("code"), jsonObj.getString("name"), "", ""
                    };
					sqlList.add(new SQLEntity(sql, params));
				}
				excuteSql(sqlList);
			}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		if (cursor != null) {
    			cursor.close();
    		}
    		closeDB();
    	}
    }

    public Cursor selectBankByIssuerId(String issuerId) {
    	String sql = "select * from " + BANK_TABLE_NAME + " where " + ISSUER_ID_COL + " = '" + issuerId + "'";
    	Cursor cursor = getCursor(sql, null);
    	return cursor; 
    }

    public String getBankNameByIssuerId(String issuerId) {
        String bankName = "";
		String sql = "";
		if (!TextUtils.isEmpty(issuerId)) {
			sql = "select * from " + BANK_TABLE_NAME + " where " + ISSUER_ID_COL + " = '" + issuerId.trim() + "'";
		} else {
			return "";
		}
        Cursor cursor = getCursor(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            bankName = cursor.getString(cursor.getColumnIndex(ISSUER_NAME_COL));
        }
        if (cursor != null) {
            cursor.close();
        }
        return bankName;
    }
    
    public void clearBankTableData() {
    	String sql = "delete from " + BANK_TABLE_NAME;

    	try {
			excuteWriteAbleSql(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}

		closeDB();
    }

    class BankDBHelper extends SQLiteOpenHelper {
    	Context ctx;
		public BankDBHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
			ctx = context;
		}

		@Override
		
		public void onCreate(SQLiteDatabase db) {
			createTables(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
		
		private void createTables(SQLiteDatabase db) {
			//FIXME: create table
			String createBankTableSql = "CREATE TABLE IF NOT EXISTS " + BANK_TABLE_NAME + " ("
					+ ISSUER_ID_COL + " varchar, "
					+ ISSUER_NAME_COL + " varchar, "
					+ ISSUER_NAME_EN_COL + " varchar, "
					+ BACK_UP_COL + " varchar, "
					+ " CONSTRAINT PK_BANK_RECORD PRIMARY KEY (" + ISSUER_ID_COL + ")" +
					");";
	        
	        db.execSQL(createBankTableSql);
			setmDb(db);
		}
	}
}
