package com.example.a5smessenger.Manager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import com.example.a5smessenger.Manager.Model.global;
import com.example.a5smessenger.Manager.Model.CParam;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static int version=1;
    private static final String DATABASE_NAME="5S_Messenger";
    private static final String TABLE_PARAM="S_HH_PARAM";
    private static final String PARAM_KEY="PARAM_KEY";
    private static final String PARAM_VALUE="PARAM_VALUE";

    public static DatabaseHelper isInstance=null;
    public static DatabaseHelper getInstance(){
        if(isInstance==null){
            isInstance=new DatabaseHelper(global.getAppContext());
        }
        return isInstance;
    }

    private String SQLquery="CREATE TABLE "+ TABLE_PARAM +" ("+
            PARAM_KEY +" TEXT, "+
            PARAM_VALUE + " TEXT)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLquery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long createParam(CParam ccValue) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PARAM_KEY, ccValue.getKey());
        values.put(PARAM_VALUE, ccValue.getValue());
        long todo_id = db.insert(TABLE_PARAM, null, values);
        db.close();
        return todo_id;
    }

    public long updateParam(CParam ccValue) {

        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PARAM_VALUE, ccValue.getValue());

        // updating row
        long a = db.update(TABLE_PARAM, values, PARAM_KEY + " = '" + ccValue.getKey() + "'", null);
        db.close();
        return a;
    }

    public CParam getParamByKey(String primaryKey) {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PARAM + " WHERE "
                + PARAM_KEY + " = ?";
        CParam cParam = new CParam();
        Cursor c = db.rawQuery(selectQuery,
                new String[]{String.valueOf(primaryKey)});
        if (c != null && c.moveToFirst()) {
            do {
                cParam.setKey(c.getString(c.getColumnIndex(PARAM_KEY)));
                cParam.setValue(c.getString(c.getColumnIndex(PARAM_VALUE)));
            } while (c.moveToNext());
        }
        c.close();

        return cParam;
    }

    public int deteleParam(String Paramkey){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete(TABLE_PARAM,PARAM_KEY+"=?",new String[] {Paramkey});
    }

    public boolean checkExistsParam(String primaryKey) {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PARAM + " WHERE "
                + PARAM_KEY + " = ?";

        Cursor c = db.rawQuery(selectQuery,
                new String[]{String.valueOf(primaryKey)});
        // looping through all rows and adding to list
        if (c != null && c.moveToFirst()) {
            return true;
        }
        c.close();
        return false;
    }
}
