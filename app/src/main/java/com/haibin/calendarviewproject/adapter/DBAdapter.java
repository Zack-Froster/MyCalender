package com.haibin.calendarviewproject.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.haibin.calendarviewproject.entity.Schedule;

import java.util.ArrayList;
import java.util.List;

public class DBAdapter {
    private static final String DB_NAME = "calendar.db";
    private static final String DB_TABLE = "schedule";
    private static final int DB_VERSION = 1;
    private static final String MAIN_KEY_ID = "id";
    private static final String KEY_INFO = "info";
    private static final String KEY_START_DATETIME = "start_datetime";
    private static final String KEY_END_DATETIME = "end_datetime";
    private static final String KEY_STATEMENT = "statement";
    private static final String KEY_IMPDAY = "impday";

    private SQLiteDatabase db;
    private final Context context;
    private DBOpenHelper dbOpenHelper;
    public static class DBOpenHelper extends SQLiteOpenHelper {
        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        private static final String DB_CREATE = "create table " + DB_TABLE + " (" + MAIN_KEY_ID + " integer primary key autoincrement," +  KEY_INFO + " text," + KEY_START_DATETIME + " integer," + KEY_END_DATETIME + " integer," + KEY_STATEMENT + " integer,"+ KEY_IMPDAY + " integer);";

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(sqLiteDatabase);
        }
    }
    public DBAdapter(Context context){
        this.context = context;
    }
    public void open() throws SQLException{
        dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbOpenHelper.getWritableDatabase();
        }catch (SQLException e){
            db = dbOpenHelper.getReadableDatabase();
        }
    }
    public void close() {
        if(db != null) {
            db.close();
            db = null;
        }
    }
    public long insert(Schedule schedule){
        ContentValues insertValues = new ContentValues();
        insertValues.put(KEY_INFO, schedule.getInfo());
        insertValues.put(KEY_START_DATETIME, schedule.getStartDateTime());
        insertValues.put(KEY_END_DATETIME, schedule.getEndDateTime());
        insertValues.put(KEY_STATEMENT, schedule.getStatement());
        insertValues.put(KEY_IMPDAY, schedule.getImpday());

        return db.insert(DB_TABLE, null, insertValues);
    }

    public long deleteAll(){
        return db.delete(DB_TABLE, null, null);
    }
    public long deleteOneById(int id) {
        return db.delete(DB_TABLE, MAIN_KEY_ID + "=" + id, null);
    }
    public long update(int id, Schedule schedule){
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_INFO, schedule.getInfo());
        updateValues.put(KEY_START_DATETIME, schedule.getStartDateTime());
        updateValues.put(KEY_END_DATETIME, schedule.getEndDateTime());
        updateValues.put(KEY_STATEMENT, schedule.getStatement());
        updateValues.put(KEY_IMPDAY, schedule.getImpday());

        return db.update(DB_TABLE, updateValues, MAIN_KEY_ID + "=" + id, null );
    }
    public List<Schedule> selectAll() {
        Cursor cursor = db.query(DB_TABLE, new String []{MAIN_KEY_ID, KEY_INFO, KEY_START_DATETIME, KEY_END_DATETIME, KEY_STATEMENT, KEY_IMPDAY}, null, null, null, null, null);
        return ConvertToSchedule(cursor);
    }

    public List<Schedule> selectAllByInfo(String info) {
        Cursor cursor = db.query(DB_TABLE, new String[] {MAIN_KEY_ID, KEY_INFO, KEY_START_DATETIME, KEY_END_DATETIME, KEY_STATEMENT, KEY_IMPDAY}, KEY_INFO + " like '%"+ info + "%'", null, null, null, null);
        return ConvertToSchedule(cursor);
    }

    public Schedule selectOneById(int id) {
        Cursor cursor = db.query(DB_TABLE, new String[]{MAIN_KEY_ID, KEY_INFO, KEY_START_DATETIME, KEY_END_DATETIME, KEY_STATEMENT, KEY_IMPDAY}, MAIN_KEY_ID + "=" + id, null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            return ConvertToSchedule(cursor).get(0);
        }else {
            return null;
        }
    }

    public List<Schedule> selectAllByDate(long date) {
        Cursor cursor = db.query(DB_TABLE, new String[]{MAIN_KEY_ID, KEY_INFO, KEY_START_DATETIME, KEY_END_DATETIME, KEY_STATEMENT, KEY_IMPDAY}, "(" + KEY_START_DATETIME + "+28800000)/1000-(" + KEY_START_DATETIME + "+28800000)/1000%86400<=" + (date+28800000)/1000 + " and (" + KEY_END_DATETIME + "+28800000)/1000-(" + KEY_END_DATETIME + "+28800000)/1000%86400+86399" + ">=" + (date+28800000)/1000, null, null, null, null);
        return ConvertToSchedule(cursor);
    }


    public int selectStatementsByTd(int id) {
        Cursor cursor = db.query(DB_TABLE, new String[]{KEY_STATEMENT}, MAIN_KEY_ID + "=" + id , null, null, null, null);
        return ConvertToInteger(cursor).get(0);
    }


    public int selectLastItemId(){
        Cursor cursor = db.query(DB_TABLE, new String[]{MAIN_KEY_ID}, null, null, null, null, MAIN_KEY_ID + " asc");
        return ConvertToInt(cursor);
    }


    private List<Schedule> ConvertToSchedule(Cursor cursor) {
        int resultCounts = cursor.getCount();
        cursor.moveToFirst();
        List<Schedule> scheduleList = new ArrayList<>();
        for(int i = 0; i < resultCounts; i++){
            Schedule schedule = new Schedule();
            schedule.setId(cursor.getInt(0));
            schedule.setInfo(cursor.getString(1));
            schedule.setStartDateTime(cursor.getLong(2));
            schedule.setEndDateTime(cursor.getLong(3));
            schedule.setStatement(cursor.getInt(4));
            schedule.setImpday(cursor.getInt(5));
            scheduleList.add(schedule);
            cursor.moveToNext();
        }
        return scheduleList;
    }
    private List<Integer> ConvertToInteger(Cursor cursor) {
        int resultCounts = cursor.getCount();
        cursor.moveToFirst();
        List<Integer> statements = new ArrayList<>();
        for(int i = 0; i < resultCounts; i++){
            statements.add(cursor.getInt(0));
            cursor.moveToNext();
        }
        return statements;
    }

    private int ConvertToInt(Cursor cursor) {
        cursor.moveToLast();
        int id = cursor.getInt(0);
        return id;
    }
}
