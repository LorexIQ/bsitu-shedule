package com.example.sheduler.databaseTypes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Pair implements Parcelable {
    private int _id;
    private String name;
    private String lecturer;
    private String time_start;
    private String time_end;
    private Integer cabinet;
    private String comment;

    // constructor
    /** U can pass null in parameter if it is "not null". Before passing parameter time_start
     * and time_end use the "checkTimeFormatHHMM" method with them.
     * @throws IllegalArgumentException if name or time_start null and if time_start
     * not fits the format HH:MM
     * @param _id not null
     * @param name not null
     * @param lecturer can be null
     * @param time_start not null
     * @param time_end can be null, if not fits the format HH:MM then this parameter will be null
     * @param  cabinet can be null
     * @param comment can be null */
    public Pair(int _id, String name, String lecturer, String time_start, String time_end, Integer cabinet, String comment){
        if (name == null || time_start == null) {
            throw new IllegalArgumentException("name and time_start cannot be null");
        } else if(checkTimeFormatHHMM(time_start) == null){
            throw new IllegalArgumentException("time_start must be in HH:MM format");
        }

        this._id = _id;
        this.name = name;
        this.lecturer = lecturer;
        this.time_start = time_start;
        if(time_end != null) {
            this.time_end = checkTimeFormatHHMM(time_end);
        }
        this.cabinet = cabinet;
        this.comment = comment;
    }

    // getters
    public int getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getLecturer() {
        return lecturer;
    }

    public String getTimeStart() {
        return time_start;
    }

    public String getTimeEnd() {
        return time_end;
    }

    public Integer getCabinet() {
        return cabinet;
    }

    public String getComment() {
        return comment;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public void setCabinet(Integer cabinet) {
        this.cabinet = cabinet;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /** If time_start fits the format HH:MM then return false */
    public boolean setTimeStartOrFalse(String time_start) {
        time_start = checkTimeFormatHHMM(time_start);
        if (time_start != null) {
            this.time_start = time_start;
            return true;
        }
        return  false;
    }

    /** If time_end fits the format HH:MM then return false */
    public boolean setTimeEndOrFalse(String time_end) {
        time_end = checkTimeFormatHHMM(time_end);
        if (time_end != null) {
            this.time_end = time_end;
            return true;
        }
        return false;
    }

    // methods
    /** If time fits the format HH:MM then return time else return null */
    public static String checkTimeFormatHHMM(String time){
        String[] HHandMM = time.split(":");
        if (HHandMM.length == 2) {
            int h = -1;
            int m = -1;
            try {
                h = Integer.parseInt(HHandMM[0]);
                m = Integer.parseInt(HHandMM[1]);
            } catch (NumberFormatException e) {
                return null;
            }
            if (h >= 0 && h < 24 && m >= 0 && m < 60 && HHandMM[1].length() == 2) {
                return time;
            }
        }
        return null;
    }

    // methods and constructor for parcelable
    public Pair(Parcel in) {
        this._id = in.readInt();
        this.name = in.readString();
        this.lecturer = in.readString();
        this.time_start = in.readString();
        this.time_end = in.readString();
        this.cabinet = in.readInt();
        this.comment = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(this._id);
        parcel.writeString(this.name);
        parcel.writeString(this.lecturer);
        parcel.writeString(this.time_start);
        parcel.writeString(this.time_end);
        parcel.writeInt(this.cabinet);
        parcel.writeString(this.comment);
    }

    public static final Parcelable.Creator<Pair> CREATOR = new Parcelable.Creator<Pair>() {
        public Pair createFromParcel(Parcel in) {
            return new Pair(in);
        }

        public Pair[] newArray(int size) {
            return new Pair[size];
        }
    };
}
