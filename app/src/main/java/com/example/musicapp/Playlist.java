package com.example.musicapp;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class Playlist extends ArrayAdapter<String> {

    public Playlist(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public Playlist(@NonNull Context context, int resource, String[] array) {
        super(context, resource, array);
    }


}
