package com.fiture.platform.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class Msg implements Parcelable {

    private String msg;
    private long time;
    private String from;
    private String to;

    public Msg(String msg) {
        this.msg = msg;
    }

    public Msg(String msg, long time) {
        this.msg = msg;
        this.time = time;
    }

    public Msg(String msg, long time, String from) {
        this.msg = msg;
        this.time = time;
        this.from = from;
    }

    public Msg(String msg, long time, String from, String to) {
        this.msg = msg;
        this.time = time;
        this.from = from;
        this.to = to;
    }

    protected Msg(Parcel in) {
        msg = in.readString();
        time = in.readLong();
        from = in.readString();
        to = in.readString();
    }

    public static final Creator<Msg> CREATOR = new Creator<Msg>() {
        @Override
        public Msg createFromParcel(Parcel in) {
            return new Msg(in);
        }

        @Override
        public Msg[] newArray(int size) {
            return new Msg[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msg);
        dest.writeLong(time);
        dest.writeString(from);
        dest.writeString(to);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
