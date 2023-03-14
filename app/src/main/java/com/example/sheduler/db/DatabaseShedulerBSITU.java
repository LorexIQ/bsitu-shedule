package com.example.sheduler.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

class DatabaseShedulerBSITU extends SQLiteOpenHelper {

    public DatabaseShedulerBSITU(@NonNull Context context) {
        super(context, Contracts.DB.DATABASE_NAME, null, Contracts.DB.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contracts.DB.SQL_FOREIGN_KEY_ON);

        db.execSQL(Contracts.Group.CREATE_TABLE);
        db.execSQL(Contracts.Day.CREATE_TABLE);
        db.execSQL(Contracts.Pair.CREATE_TABLE);
        db.execSQL(Contracts.Shedule.CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Contracts.DB.SQL_FOREIGN_KEY_ON);

        db.execSQL(Contracts.Group.CREATE_TABLE);
        db.execSQL(Contracts.Day.CREATE_TABLE);
        db.execSQL(Contracts.Pair.CREATE_TABLE);
        db.execSQL(Contracts.Shedule.CREATE_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
