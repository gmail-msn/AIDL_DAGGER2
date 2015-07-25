package com.koolcloud.sdk.fmsc.domain.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.koolcloud.sdk.fmsc.domain.entity.PaymentInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: PaymentParamsDB.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: KoolCloud</p>
 * @author 		Teddy
 * @date 		2015-7-11
 * @version 	
 */
public class PaymentParamsDB extends BaseSqlAdapter {

	private final static String DATABASE_NAME = "PaymentsParams.db";
	private final static int DATABASE_VERSION = 3;
    private final static String PAYMENT_PARAMS_TABLE_NAME = "payment_params_table";
    
    private Context context;
    private String dbName;
    
    private static PaymentParamsDB cacheDB;
    
    //TODO:acquire institute table columns
    private final static String ACQUIRE_MERCHANT_ID = "brhMchtId";
    private final static String ACQUIRE_TERMINAL_ID = "brhTermId";
    private final static String ACQUIRE_DEVICE_NUM_OF_MERCH = "openBrh";
    private final static String ACQUIRE_IMG_NAME = "imgName";
    private final static String ACQUIRE_INSTITUTE_NAME = "openBrhName";
    private final static String ACQUIRE_MERCH_NUM_OF_MERCH = "mch_no";
    private final static String ACQUIRE_PAYMENT_ID = "paymentId";
    private final static String ACQUIRE_PAYMENT_NAME = "paymentName";
    private final static String ACQUIRE_PRINT_TYPE = "printType";
    private final static String ACQUIRE_PRODUCT_DESC = "prdtDesc";
    private final static String ACQUIRE_PRODUCT_NO = "prdtNo";
    private final static String ACQUIRE_PRODUCT_TITLE = "prdtTitle";
    private final static String ACQUIRE_PRODUCT_TYPE = "prdtType";
    private final static String ACQUIRE_TYPE_ID = "typeId";
    private final static String ACQUIRE_TYPE_NAME = "typeName";
    private final static String ACQUIRE_BRH_KEY_INDEX = "brhKeyIndex";
    private final static String ACQUIRE_BRH_MSG_TYPE = "brhMsgType";
    private final static String ACQUIRE_BRH_MCHT_MCC = "brhMchtMcc";
    private final static String ACQUIRE_TAB_TYPE_ID = "tabType";
    private final static String ACQUIRE_MSG_SEND_TYPE = "msg_send_type";

    private final static String PAYMENT_ACTIVITY_JSON = "payment_json";
  
    private PaymentParamsDB(Context ctx, int version) {
    	this.context = ctx;
    	if (dbName == null) {
    		dbName = context.getFileStreamPath(DATABASE_NAME).getAbsolutePath();
    	}
    	
		mDbHelper = new CacheHelper(ctx, DATABASE_NAME, null, version);
    } 
    
    public static PaymentParamsDB getInstance(Context ctx) {
    	if (cacheDB == null) {
    		cacheDB = new PaymentParamsDB(ctx, DATABASE_VERSION);
    	}
    	return cacheDB;
    }
  
    /**
     * @Title: insertPayment
     * @Description: TODO
     * @param paymentInfos
     * @return: void
     */
    public void insertPayment(List<PaymentInfo> paymentInfos) {
    	
    	ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
    	Cursor cursor = null;
    	try {
    		String sql = "INSERT INTO "+ PAYMENT_PARAMS_TABLE_NAME +"(" +
    				ACQUIRE_PAYMENT_ID + ", " +
    				ACQUIRE_PAYMENT_NAME + ", " +
    				ACQUIRE_BRH_KEY_INDEX + ", " +
    				ACQUIRE_MERCHANT_ID + ", " +
    				ACQUIRE_TERMINAL_ID + ", " +
    				ACQUIRE_PRINT_TYPE + ", " +
    				ACQUIRE_PRODUCT_NO + ", " +
    				ACQUIRE_PRODUCT_TITLE + ", " +
    				ACQUIRE_PRODUCT_DESC + ", " +
    				ACQUIRE_DEVICE_NUM_OF_MERCH + ", " +
    				ACQUIRE_INSTITUTE_NAME + ", " +
    				ACQUIRE_TAB_TYPE_ID + ", " +
					ACQUIRE_MSG_SEND_TYPE + ", " +
    				PAYMENT_ACTIVITY_JSON + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    		
    		if (paymentInfos != null && paymentInfos.size() > 0) {
    			for (int i = 0; i < paymentInfos.size(); i++) {
    				PaymentInfo paymentInfo = paymentInfos.get(i);
    				cursor = selectPaymentByPaymentId(paymentInfo.getPaymentId());
    				if (cursor.getCount() > 0) {
    					continue;
    				}
    				cursor.close();
    				String[] params = new String[] { paymentInfo.getPaymentId(), paymentInfo.getPaymentName(),
    						paymentInfo.getBrhKeyIndex(), paymentInfo.getBrhMchtId(),
    						paymentInfo.getBrhTermId(), paymentInfo.getPrintType(),
    						paymentInfo.getProductNo(), paymentInfo.getProductTitle(),
    						paymentInfo.getProductDesc(), paymentInfo.getDeviceNumOfMerch(),
    						paymentInfo.getInstituteName(), paymentInfo.getTypeId(),
							paymentInfo.getMsgSendType(), paymentInfo.getJsonItem()
    						
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
    	}
    }
    
    public void clearPaymentParamsTableData() {
    	String sql = "delete from " + PAYMENT_PARAMS_TABLE_NAME;
    	
    	try {
    		excuteWriteAbleSql(sql);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	//closeDB();
    }
    
    private Cursor selectPaymentByPaymentId(String paymentId) {
    	String sql = "select * from " + PAYMENT_PARAMS_TABLE_NAME + " where " + ACQUIRE_PAYMENT_ID + " = '" + paymentId + "'";
    	
    	Cursor cursor = getCursor(sql, null);
    	return cursor;
    }
    
    public int getPaymentsCount() { 
    	String sql = "select count(*) from " + PAYMENT_PARAMS_TABLE_NAME;
    	Cursor cursor = getCursor(sql, null);
    	cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
    	return count;
    }
    
    public PaymentInfo getPaymentByPaymentId(String paymentId) { 
    	String sql = "select * from " + PAYMENT_PARAMS_TABLE_NAME + " where " + ACQUIRE_PAYMENT_ID + " = '" + paymentId + "'";
    	PaymentInfo paymentInfo = null;
    	Cursor cursor = getCursor(sql, null);
    	if (cursor.getCount() > 0) {
    		cursor.moveToNext();
    		String paymentName = cursor.getString(cursor.getColumnIndex(ACQUIRE_PAYMENT_NAME));
    		String brhKeyIndex = cursor.getString(cursor.getColumnIndex(ACQUIRE_BRH_KEY_INDEX));
    		String prodtNo = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_NO));
    		String prdtTitle = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_TITLE));
    		String prdtDesc = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_DESC));
    		String openBrh = cursor.getString(cursor.getColumnIndex(ACQUIRE_DEVICE_NUM_OF_MERCH));
    		String openBrhName = cursor.getString(cursor.getColumnIndex(ACQUIRE_INSTITUTE_NAME));
    		paymentInfo = new PaymentInfo(paymentId, paymentName, brhKeyIndex, prodtNo, prdtTitle, prdtDesc, openBrh, openBrhName);
    	}
    	
    	cursor.close();
    	return paymentInfo;
    }
    
    public List<PaymentInfo> selectAllPaymentInfo() {
    	String sql = "select * from " + PAYMENT_PARAMS_TABLE_NAME + " order by " + ACQUIRE_PAYMENT_ID + " ASC";
    	List<PaymentInfo> paymentList = null;
    	Cursor cursor = null;
    	try {
    		cursor = getCursor(sql, null);
    		paymentList = new ArrayList<PaymentInfo>();
    		while (cursor.moveToNext()) {
    			String paymentId = cursor.getString(cursor.getColumnIndex(ACQUIRE_PAYMENT_ID));
    			String paymentName = cursor.getString(cursor.getColumnIndex(ACQUIRE_PAYMENT_NAME));
    			String brhKeyIndex = cursor.getString(cursor.getColumnIndex(ACQUIRE_BRH_KEY_INDEX));
    			String prdtNo = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_NO));
    			String prdtDesc = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_DESC));
    			String prdtTitle = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_TITLE));
    			String openBrh = cursor.getString(cursor.getColumnIndex(ACQUIRE_DEVICE_NUM_OF_MERCH));
    			String openBrhName = cursor.getString(cursor.getColumnIndex(ACQUIRE_INSTITUTE_NAME));
    			
    			PaymentInfo paymentInfo = new PaymentInfo(paymentId, paymentName, brhKeyIndex, prdtNo,
    					prdtTitle, prdtDesc, openBrh, openBrhName);
    			paymentList.add(paymentInfo);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		if (cursor != null) {
    			
    			cursor.close();
    		}
    	}
    	return paymentList;
    }
    
    /**
     * <p>Title: PaymentParamsDB.java </p>
     * <p>Description: </p>
     * <p>Copyright: Copyright (c) 2014</p>
     * <p>Company: KoolCloud</p>
     * @author 		Teddy
     * @date 		2015-7-11
     * @version 	
     */
    class CacheHelper extends SQLiteOpenHelper {
    	Context ctx;
		public CacheHelper(Context context, String name, CursorFactory factory, int version) {
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
			//FIXME: payment activity
			String paymentActivitySql = "CREATE TABLE IF NOT EXISTS " + PAYMENT_PARAMS_TABLE_NAME + " ("
					+ ACQUIRE_PAYMENT_ID + " varchar, " 
					+ ACQUIRE_PAYMENT_NAME + " varchar, " 
					+ ACQUIRE_BRH_KEY_INDEX + " varchar, " 
					+ ACQUIRE_MERCHANT_ID + " varchar, " 
					+ ACQUIRE_TERMINAL_ID + " varchar, " 
					+ ACQUIRE_PRINT_TYPE + " varchar, " 
					+ ACQUIRE_PRODUCT_NO + " varchar, " 
					+ ACQUIRE_PRODUCT_TITLE + " varchar, " 
					+ ACQUIRE_PRODUCT_DESC + " varchar, " 
					+ ACQUIRE_DEVICE_NUM_OF_MERCH + " varchar, " 
					+ ACQUIRE_INSTITUTE_NAME + " varchar, " 
					+ ACQUIRE_TAB_TYPE_ID + " varchar, " 
					+ ACQUIRE_MSG_SEND_TYPE + " varchar, "
					+ PAYMENT_ACTIVITY_JSON + " nvarchar, "
					+ " CONSTRAINT PK_PAYMENT_ACTIVITY PRIMARY KEY (" + ACQUIRE_PAYMENT_ID + ")" +
					");";
					
	        
	        db.execSQL(paymentActivitySql);
			setmDb(db);
		}
	}
}
