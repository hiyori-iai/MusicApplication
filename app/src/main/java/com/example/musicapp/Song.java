package com.example.musicapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Song implements Parcelable {

    private int id;
    private String artist;
    private String title;
    private int picture;
    protected Song(Parcel in) {
        title = in.readString();
        artist = in.readString();
        id = in.readInt();
        picture = in.readInt();
    }

    public Song(String title, int resid, String artist, int picture) {
        this.title = title;
        this.artist = artist;
        this.picture = picture;
        this.id = resid;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeInt(id);
        dest.writeInt(picture);
    }

    public int getId() {
        return this.id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public int getPicture() {
        return picture;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
