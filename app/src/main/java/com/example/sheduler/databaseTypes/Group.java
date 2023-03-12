package com.example.sheduler.databaseTypes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Class used to write to database, load information about <b>group</b> from database
 * and transfer between activities. U can pass only group name then <code>count_weeks = 1</code> will
 * be default.
 * @author Illarionov
 * @version 1.0
 * @see android.os.Parcelable*/
public class Group implements Parcelable {
    private String name;
    private int count_weeks;

    // constructors

    public Group(String name, int count_weeks) {
        this.name = name;
        this.count_weeks = count_weeks;
    }

    public Group(String name) {
        this.name = name;
        this.count_weeks = 1;
    }

    // getters and setters

    public String getName() {
        return this.name;
    }

    public int getCount_weeks() {
        return this.count_weeks;
    }

    public void setName(String name){
        this.name = name;
    }

    // methods for Parcelable
    protected Group(Parcel in) {
        this.name = in.readString();
        this.count_weeks = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeInt(this.count_weeks);
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel parcel) {
            return new Group(parcel);
        }

        @Override
        public Group[] newArray(int i) {
            return new Group[i];
        }
    };
}
