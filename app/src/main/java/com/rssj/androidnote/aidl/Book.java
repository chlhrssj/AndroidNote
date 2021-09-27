package com.rssj.androidnote.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Create by rssj on 2021/6/8
 */
public class Book implements Parcelable {

    String name;
    long id;
    String tag;
    int sum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.id);
        dest.writeString(this.tag);
        dest.writeInt(this.sum);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.id = source.readLong();
        this.tag = source.readString();
        this.sum = source.readInt();
    }

    public Book() {
    }

    protected Book(Parcel in) {
        this.name = in.readString();
        this.id = in.readLong();
        this.tag = in.readString();
        this.sum = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
