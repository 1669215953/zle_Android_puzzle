package com.example.puzzle.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RankDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RankList.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "RankTable";
    public static final String COL_ID = "ID";
    public static final String COL_RANK = "Rank";
    public static final String COL_PLAYER_NAME = "PlayerName";
    public static final String COL_TIME_TAKEN = "TimeTaken";
    public static final String COL_DATE = "Date";

    public RankDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_RANK + " INTEGER," +
                COL_PLAYER_NAME + " TEXT," +
                COL_TIME_TAKEN + " INTEGER," +
                COL_DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 添加新数据的方法
    public boolean addNewRecord(String playerName, long timeTaken) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_RANK, -1); // Temporary rank, will be updated later
        contentValues.put(COL_PLAYER_NAME, playerName);
        contentValues.put(COL_TIME_TAKEN, timeTaken);
        contentValues.put(COL_DATE, getCurrentDateOnly());

        long newRowId = db.insert(TABLE_NAME, null, contentValues);

        if (newRowId != -1) {
            updateRanks(); // After insertion, update the ranks
            return true;
        } else {
            return false;
        }
    }

    // 查询时长的方法
    public long getTimeByRank(int rank) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_TIME_TAKEN}, COL_RANK + "=?", new String[]{String.valueOf(rank)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            long timeTaken = cursor.getLong(0);
            cursor.close();
            return timeTaken;
        }
        return -1; // Return -1 if not found
    }

    // 查询玩家姓名的方法
    public String getPlayerNameByRank(int rank) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_PLAYER_NAME}, COL_RANK + "=?", new String[]{String.valueOf(rank)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String playerName = cursor.getString(0);
            cursor.close();
            return playerName;
        }
        return null; // Return null if not found
    }

    // 新增方法：清除排行榜所有数据
    public void clearAllRankData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // 删除表中所有记录
        db.close();
    }

    // 更新排名的方法，确保只保留前10名
    private void updateRanks() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_TIME_TAKEN + " ASC, " + COL_DATE + " ASC", null);

        if (cursor != null && cursor.getCount() > 10) {
            // Delete the 11th record
            cursor.moveToPosition(10);
            @SuppressLint("Range") long idToDelete = cursor.getLong(cursor.getColumnIndex(COL_ID));
            db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(idToDelete)});
        }

        // Re-rank the records
        int rank = 1;
        while (cursor.moveToNext()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_RANK, rank++);
            @SuppressLint("Range") long idToUpdate = cursor.getLong(cursor.getColumnIndex(COL_ID));
            db.update(TABLE_NAME, contentValues, COL_ID + "=?", new String[]{String.valueOf(idToUpdate)});
        }
        cursor.close();
    }

    // 获取当前日期的字符串表示（几月几日）
    private String getCurrentDateOnly() {
        // Java 8及以上版本推荐使用的日期时间API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"));
        }
        return "0";
    }
}