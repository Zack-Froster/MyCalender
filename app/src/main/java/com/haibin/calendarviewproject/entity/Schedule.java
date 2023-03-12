package com.haibin.calendarviewproject.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Schedule implements Parcelable {
    private int id;
    private String info;
    private long startDateTime;
    private long endDateTime;
    private int statement;
    private int impday;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    public long getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(long startDateTime) {
        this.startDateTime = startDateTime;
    }

    public long getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(long endDateTime) {
        this.endDateTime = endDateTime;
    }

    public int getStatement() {
        return statement;
    }

    public void setStatement(int statement) {
        this.statement = statement;
    }

    public int getImpday() {
        return impday;
    }

    public void setImpday(int impday) {
        this.impday = impday;
    }

    public Schedule(){}

    public Schedule(String info, long startDateTime, long endDateTime, int statement, int impday) {
        this.info = info;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.statement = statement;
        this.impday = impday;
    }

    public Schedule(int id, String info, long startDateTime, long endDateTime, int statement, int impday) {
        this.id = id;
        this.info = info;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.statement = statement;
        this.impday = impday;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(info);
        parcel.writeLong(startDateTime);
        parcel.writeLong(endDateTime);
        parcel.writeInt(statement);
        parcel.writeInt(impday);
    }
    public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel source) {
            //从Parcel中读取数据
            //此处read顺序依据write顺序
            return new Schedule(source.readInt(), source.readString(), source.readLong(), source.readLong(), source.readInt(), source.readInt());
        }
        @Override
        public Schedule[] newArray(int size) {

            return new Schedule[size];
        }

    };
}
