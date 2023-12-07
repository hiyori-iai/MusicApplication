package com.example.musicapp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class MusicDatabase implements Parcelable {

    private String selection;
    private HashMap<String, Song> database = new HashMap<String, Song>(); //Song has to be Parcelable too
    private List<String> titleList = new ArrayList<>();
    public MusicDatabase(Context context){
        Song song1 = new Song(context.getString(R.string.title1), R.raw.brakhageintro, context.getString(R.string.artist1), R.drawable.song1_picture);
        addToDatabase(song1.getTitle(), song1);
        Song song2 = new Song(context.getString(R.string.title2), R.raw.brakhagethisloveinstrumental, context.getString(R.string.artist2), R.drawable.song2_picture);
        addToDatabase(song2.getTitle(), song2);
        Song song3 = new Song(context.getString(R.string.title3), R.raw.eatersgoodbyeprettypeople, context.getString(R.string.artist3), R.drawable.song3_picture);
        addToDatabase(song3.getTitle(), song3);
        Song song4 = new Song(context.getString(R.string.title4), R.raw.guyomkawaiinuigurumi, context.getString(R.string.artist4), R.drawable.song4_picture);
        addToDatabase(song4.getTitle(), song4);
        Song song5 = new Song(context.getString(R.string.title5), R.raw.jazzatmladostclubarana, context.getString(R.string.artist5), R.drawable.song5_picture);
        addToDatabase(song5.getTitle(), song5);
        Song song6 = new Song(context.getString(R.string.title6), R.raw.jazzatmladostclubblubossa, context.getString(R.string.artist6), R.drawable.song6_picture);
        addToDatabase(song6.getTitle(), song6);
    }

    private void addToDatabase(String title, Song song){
        titleList.add(title);
        database.put(title, song);
    }
    public String getSelection(){
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    protected MusicDatabase(Parcel in) {
        selection = in.readString(); //data management type is like queue not stack FIFO
        database = in.readHashMap(getClass().getClassLoader());
        titleList = in.readArrayList(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(selection);
        dest.writeMap(database);
        dest.writeList(titleList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MusicDatabase> CREATOR = new Creator<MusicDatabase>() {
        @Override
        public MusicDatabase createFromParcel(Parcel in) {
            return new MusicDatabase(in);
        }

        @Override
        public MusicDatabase[] newArray(int size) {
            return new MusicDatabase[size];
        }
    };

    public void addSong(String title, Song song) {
        database.put(title, song);
    }

    public Song getSongByTitle(String title){
        return database.get(title);
    }
    public  String[] getSongTitles(){
        //return database.keySet().toArray(new String[0]);
        return titleList.toArray(new String[0]);
    }
}
