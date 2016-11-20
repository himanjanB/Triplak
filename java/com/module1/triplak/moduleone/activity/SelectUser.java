package com.module1.triplak.moduleone.activity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Himanjan on 14-11-2015.
 */
public class SelectUser implements Parcelable{
    public static final Creator<SelectUser> CREATOR = new Creator<SelectUser>() {
        @Override
        public SelectUser createFromParcel(Parcel in) {
            return new SelectUser(in);
        }

        @Override
        public SelectUser[] newArray(int size) {
            return new SelectUser[size];
        }
    };
    String name;
    Bitmap thumb;
    String phone;
    String email;

    protected SelectUser(Parcel in) {
        name = in.readString();
        thumb = in.readParcelable(Bitmap.class.getClassLoader());
        phone = in.readString();
        email = in.readString();
    }


    public SelectUser() {}

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(thumb, flags);
        dest.writeString(phone);
        dest.writeString(email);
    }
}
