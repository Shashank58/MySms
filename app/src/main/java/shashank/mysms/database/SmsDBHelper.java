package shashank.mysms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import shashank.mysms.model.Sms;

/**
 * Created by shashankm on 15/01/16.
 */
public class SmsDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Sms.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SMS_TABLE = "messages";
    private static final String SMS_ID = "_id";
    private static final String SMS_ADDRESS = "address";
    private static final String SMS_BODY = "body";
    private static final String SMS_DATE = "date";
    private static final String SMS_TIME = "time";
    private static final String SMS_MONTH = "month";
    private static final String SMS_IS_SPAM = "isspam";

    public SmsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public int getCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "SELECT * from "+SMS_TABLE;
        Cursor c = db.rawQuery(countQuery, null);
        c.moveToFirst();
        db.close();
        Log.e("Sms db helper", "Count: " + c.getCount());
        if (c.getCount() > 0) {
            return c.getInt(0);
        }
        else {
            c.close();
            return 0;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SMS_TABLE + "(" +
                SMS_ID + " INTEGER PRIMARY KEY, " +
                SMS_ADDRESS + " TEXT, " + SMS_BODY +
                " TEXT, " + SMS_TIME + " TEXT, " + SMS_MONTH
                + " INTEGER, " + SMS_DATE + " INTEGER, " +
                SMS_IS_SPAM + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SMS_TABLE);
        onCreate(db);
    }

    public void insertValues(List<Sms> allSmsList){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (Sms sms : allSmsList) {
            contentValues.put(SMS_ID, sms.getId());
            contentValues.put(SMS_ADDRESS, sms.getAddress());
            contentValues.put(SMS_BODY, sms.getBody());
            contentValues.put(SMS_TIME, sms.getTime());
            contentValues.put(SMS_MONTH, sms.getMonth());
            contentValues.put(SMS_DATE, sms.getDate());
            if (sms.getisSpam())
                contentValues.put(SMS_IS_SPAM, 1);
            else
                contentValues.put(SMS_IS_SPAM, 0);
            db.insert(SMS_TABLE, null, contentValues);
        }
        db.close();
    }

    public void deleteTask(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE from " + SMS_TABLE +
                " where " + SMS_ID + "='" + id + "'");
        db.close();
    }

    public List<Sms> getAllSms(){
        ArrayList<Sms> smsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + SMS_TABLE, null );
        if (res.moveToLast()){
            do {
                boolean isSpam;
                if (res.getInt(6) == 1)
                    isSpam = true;
                else
                    isSpam = false;
                Sms sms = new Sms();
                sms.setId(res.getLong(0));
                sms.setAddress(res.getString(1));
                sms.setBody(res.getString(2));
                sms.setTime(res.getString(3));
                sms.setMonth(res.getInt(4));
                sms.setDate(res.getInt(5));
                sms.setIsSpam(isSpam);
                smsList.add(sms);
            } while (res.moveToPrevious());
        }
        db.close();
        res.close();
        return smsList;
    }
}
