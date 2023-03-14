package com.example.sheduler.db.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.sheduler.db.DatabaseShedulerBSITU;

/**Class for work with database
 * @author Illarionov
 * @version 0.1*/
public class ManagerSQLite {
    private Context context;
    private DatabaseShedulerBSITU databaseSheduler;
    private SQLiteDatabase db;

    public ManagerSQLite(Context context) {
        this.context = context;
        databaseSheduler = new DatabaseShedulerBSITU(context);
    }

    public void open() {
        db = databaseSheduler.getWritableDatabase();
    }

    public void close() {
        databaseSheduler.close();
    }
}
