package com.debjyoti.goalgator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class GoalDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "goalgator.db";

    public GoalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_GOAL_TABLE = "CREATE TABLE " + GoalContract.GoalEntry.TABLE_NAME + " (" +
                GoalContract.GoalEntry._ID + " INTEGER PRIMARY KEY, " +
                GoalContract.GoalEntry.COLUMN_ID + " TEXT NOT NULL, " +
                GoalContract.GoalEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                GoalContract.GoalEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                GoalContract.GoalEntry.COLUMN_START_DATE+ " REAL NOT NULL, " +
                GoalContract.GoalEntry.COLUMN_DUE_DATE + " REAL NOT NULL, " +
                GoalContract.GoalEntry.COLUMN_TASK + " TEXT NOT NULL, " +
                GoalContract.GoalEntry.COLUMN_FREQUENCY + " INTEGER NOT NULL, " +
                GoalContract.GoalEntry.COLUMN_TOTAL_TASKS + " INTEGER NOT NULL, " +
                GoalContract.GoalEntry.COLUMN_TASKS_DONE + " INTEGER NOT NULL, " +
                GoalContract.GoalEntry.COLUMN_TASKS_MISSED + " INTEGER NOT NULL, " +
                GoalContract.GoalEntry.COLUMN_TASKS_REMAINING + " INTEGER NOT NULL, " +
                GoalContract.GoalEntry.COLUMN_STATUS + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_GOAL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onCreate(sqLiteDatabase);
    }
}