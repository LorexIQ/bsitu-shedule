package com.example.sheduler.databaseTypes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Class used to write to database, load information about <b>day</b> from database
 * and transfer between activity.
 * @author Illarionov
 * @version 1.0
 * @see android.os.Parcelable */
public class Day implements Parcelable {
    private String name;
    private int parity_day;
    private int group_id;

    // constructor
    public Day(String name, int parity_day, int group_id) {
        this.name = name;
        this.parity_day = parity_day;
        this.group_id = group_id;
    }

    // getters
    public String getName() {
        return name;
    }

    public int getParity_day() {
        return parity_day;
    }

    public int getGroup_id() {
        return group_id;
    }

    // methods and constructor for Parcelable
    protected Day(Parcel in) {
        this.name = in.readString();
        this.parity_day = in.readInt();
        this.group_id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeInt(this.parity_day);
        parcel.writeInt(this.group_id);
    }

    public static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
}
