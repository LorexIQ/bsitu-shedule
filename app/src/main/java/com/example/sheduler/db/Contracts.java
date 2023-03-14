package com.example.sheduler.db;

import android.provider.BaseColumns;

/** Contract class is a container for constants that define names for tables and columns.
 * The class also has constants that provide a SQL query for creating and deleting.
 * @author Illarionov
 * @version 1.0
 * @see BaseColumns
 * */
public class Contracts {
    private Contracts(){}

    /** Contract for database */
    public static class DB{
        public static final String DATABASE_NAME = "sheduler.db";
        public static final int DATABASE_VERSION = 1;
        public static final String SQL_FOREIGN_KEY_ON = "PRAGMA foreign_keys=ON";
    }

    /** Contract for table groups */
    public static class Group implements BaseColumns {
        public static final String TABLE_NAME = "group_sh";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_WEEKS = "counter_weeks";

        public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_WEEKS + " INTEGER, " +
                "UNIQUE(" + COLUMN_NAME + ", " + COLUMN_WEEKS + ")" +
                ");";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /** Contract for table days */
    public static class Day implements BaseColumns {
        public static final String TABLE_NAME = "day";
        public static final String COLUMN_NAME = "name";
        public static final String PARITY_DAY = "parity_day";
        public static final String GROUP_ID = "group_id";

        public static String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                PARITY_DAY + " INTEGER NOT NULL, " +
                GROUP_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY ("+GROUP_ID+") REFERENCES " + Group.TABLE_NAME + "("+Group._ID+") " +
                "ON UPDATE CASCADE ON DELETE CASCADE" +
                ");";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /** Contract for table pair */
    public static class Pair implements BaseColumns {
        public static final String TABLE_NAME = "pair";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LECTURER = "lecturer";
        public static final String COLUMN_TIME_START = "time_start";
        public static final String COLUMN_TIME_END = "time_end";
        public static final String COLUMN_CABINET = "cabinet";
        public static final String COLUMN_COMMENT = "comment";

        public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_LECTURER + " TEXT, " +
                COLUMN_TIME_START + " TEXT NOT NULL, " +
                COLUMN_TIME_END + " TEXT, " +
                COLUMN_CABINET + " INTEGER, " +
                COLUMN_COMMENT + " TEXT " +
                ");";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /** Contract for table shedules */
    public static class Shedule {
        public static final String TABLE_NAME = "shedule";
        public static final String COLUMN_DAY_ID = "day_id";
        public static final String COLUMN_PAIR_ID = "pair_id";

        public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" +
                COLUMN_DAY_ID + " INTEGER NOT NULL, " +
                COLUMN_PAIR_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY ("+COLUMN_DAY_ID+") REFERENCES "+Day.TABLE_NAME+" ("+Day._ID+") " +
                "ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY ("+COLUMN_PAIR_ID+") REFERENCES "+Pair.TABLE_NAME+" ("+Pair._ID+") " +
                "ON UPDATE CASCADE ON DELETE CASCADE " +
                "UNIQUE(\"day_id\", \"pair_id\")" +
                ");";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
