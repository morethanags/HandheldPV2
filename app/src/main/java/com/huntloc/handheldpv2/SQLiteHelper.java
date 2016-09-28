package com.huntloc.handheldpv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PV2DB";

    private static final String TABLE_PERSONNEL = "Personnel";
    private static final String KEY_INTERNAL_CODE = "internalCode";
    private static final String KEY_PRINTED_CODE = "printedCode";
    private static final String KEY_PORTRAIT = "portrait";
    private static final String KEY_NAME = "name";
    private static final String KEY_PBIP = "PBIP";
    private static final String KEY_HAZARDOUSGOODS = "hazardousGoods";
    private static final String KEY_PORTSECURITY = "portSecurity";
    private static final String KEY_PBIP_COLOR = "PBIPColor";
    private static final String KEY_PBIP_CODE = "PBIPCode";
    private static final String KEY_CLEARANCE = "clearance";

    private static final String TABLE_JOURNAL = "Journal";
    private static final String KEY_GUID = "guid";
    private static final String KEY_CREDENTIAL = "credential";
    private static final String KEY_LOG = "log";
    private static final String KEY_DOOR = "door";
    private static final String KEY_TIME = "time";
    private static final String KEY_SENT = "sent";
    private static final String KEY_PERSONNEL = "personnel";
    private static final String KEY_DESC = "descLog";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PERSONNEL_TABLE = "CREATE TABLE Personnel ( internalCode TEXT PRIMARY KEY, printedCode TEXT, portrait TEXT, name TEXT, PBIP TEXT, hazardousGoods TEXT, portSecurity TEXT, PBIPColor TEXT, PBIPCode TEXT, clearance TEXT)";
        db.execSQL(CREATE_PERSONNEL_TABLE);

        String CREATE_JOURNAL_TABLE = "CREATE TABLE Journal ( guid TEXT PRIMARY KEY, credential TEXT, log TEXT, door TEXT, time TEXT, sent INTEGER, personnel TEXT, descLog TEXT )";
        db.execSQL(CREATE_JOURNAL_TABLE);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PERSONNEL);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_JOURNAL);
        this.onCreate(db);
    }
    public void addPersonnel(Personnel personnel) {
        Log.d("insert Personnel", personnel.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_INTERNAL_CODE, personnel.getInternalCode());
        values.put(KEY_PRINTED_CODE, personnel.getPrintedCode());
        values.put(KEY_PORTRAIT, personnel.getPortrait());
        values.put(KEY_NAME, personnel.getName());
        if(personnel.getPBIP()!=null){
            values.put(KEY_PBIP, personnel.getPBIP());
        }
        if(personnel.getHazardousGoods()!=null){
            values.put(KEY_HAZARDOUSGOODS, personnel.getHazardousGoods());
        }
        if(personnel.getPortSecurity()!=null){
            values.put(KEY_PORTSECURITY, personnel.getPortSecurity());
        }

        if(personnel.getPBIPColor()!=null){
            values.put(KEY_PBIP_COLOR, personnel.getPBIPColor());
        }
        if(personnel.getPBIPCode()!=null){
            values.put(KEY_PBIP_CODE, personnel.getPBIPCode());
        }

        values.put(KEY_CLEARANCE, personnel.getClearance());

        db.insert(TABLE_PERSONNEL, null, values);
        db.close();
    }
    public void deletePersonnel() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_PERSONNEL);
        db.close();
    }
    public Personnel getPersonnel(long internalCode) {
        Personnel personnel = null;
        String query = "SELECT  * FROM " + TABLE_PERSONNEL + " where "+ KEY_INTERNAL_CODE +" = '"+internalCode+"'";
        //Log.d("query", query);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("select personnel count", cursor.getCount()+"");

        if (cursor.moveToFirst()) {
            personnel = new Personnel(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.isNull(4)?null:cursor.getString(4),
                    cursor.isNull(5)?null:cursor.getString(5),
                    cursor.isNull(6)?null:cursor.getString(6),
                    cursor.isNull(7)?null:cursor.getString(7),
                    cursor.isNull(8)?null:cursor.getString(8),
                    cursor.getString(9)
            );
            Log.d("select personnel", personnel.toString());
        }
        db.close();
        return personnel;
    }
    public Personnel getPersonnel(String printedCode) {
        Personnel personnel = null;
        String query = "SELECT  * FROM " + TABLE_PERSONNEL + " where "+ KEY_PRINTED_CODE +" = '"+printedCode+"'";
        //Log.d("query", query);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("select personnel count", cursor.getCount()+"");

        if (cursor.moveToFirst()) {
            personnel = new Personnel(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.isNull(4)?null:cursor.getString(4),
                    cursor.isNull(5)?null:cursor.getString(5),
                    cursor.isNull(6)?null:cursor.getString(6),
                    cursor.isNull(7)?null:cursor.getString(7),
                    cursor.isNull(8)?null:cursor.getString(8),
                    cursor.getString(9)
            );
            Log.d("select personnel", personnel.toString());
        }
        db.close();
        return personnel;
    }

    public void addJournal(Journal journal) {
        Log.d("insert journal", journal.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_GUID, journal.getGuid());
        values.put(KEY_CREDENTIAL, journal.getCredential());
        values.put(KEY_LOG, journal.getLog());
        values.put(KEY_DOOR, journal.getDoor());
        values.put(KEY_TIME, journal.getTime());
        values.put(KEY_SENT, journal.isSent() ? 1 : 0);
        values.put(KEY_PERSONNEL, journal.getPersonnel());
        values.put(KEY_DESC, journal.getDescLog());
        db.insert(TABLE_JOURNAL, null, values);
        db.close();
    }
    public List<Journal> getJournal(String log) {
        List<Journal> toReturn = new LinkedList<>();
        String query = log!=null?"SELECT  * FROM " + TABLE_JOURNAL + " where "+ KEY_SENT +" = 0 and " + KEY_LOG + "='"+log+"'":"SELECT  * FROM " + TABLE_JOURNAL + " where "+ KEY_SENT +" = 0 ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Journal journal = null;
        if (cursor.moveToFirst()) {
            do {
                journal = new Journal(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),Long.parseLong(cursor.getString(4)),cursor.getInt(5) == 1 ? true : false, cursor.getString(6), cursor.getString(7));
                toReturn.add(journal);
            } while (cursor.moveToNext());

        }
        //Log.d("getJournal("+log+")", toReturn.size()+" Journal");
        db.close();
        return toReturn;
    }
    public void deleteRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_JOURNAL);
        Log.d("deleteRecords()", "[]");
        db.close();
    }
    public void updateJournal(Journal journal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_SENT, "1");
        db.update(TABLE_JOURNAL, cv, KEY_GUID + "='" + journal.getGuid()+"'", null);
        db.close();
        Log.d("updateRecord", "[id=" + journal.getGuid() + "]");
    }
}
