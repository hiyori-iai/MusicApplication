package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MusicApp extends AppCompatActivity {

    private final String TAG = "CPTR320";
    public final static String EXTRA_MESSAGE = "STRING_EXTRA";
    public final static String SHUFFLE_SETTING = "SHUFFLE";
    public final static String LOOP_SETTING = "LOOP";

    private final String PLAYER_POSITION_KEY = "CURR_POSITION";
    private final String PLAYER_STATE_KEY = "CURR_STATE";
    public static final String APP_INITIATED = "APP_INIT";

    private View currentSelection = null;

    //Media Player related
    final MediaPlayer mediaPlayer = new MediaPlayer();
    private MusicDatabase dbase;
    public static boolean shuffle, loop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.play_list);
        dbase = createDatabase();
        String[] array = dbase.getSongTitles();
        Playlist playlist = new Playlist(this, android.R.layout.simple_list_item_1, array); //int = which array location we want the default to be at = android.R..
        listView.setAdapter(playlist);
        listView.setBackgroundColor(Color.WHITE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.animate().setDuration(1500).alpha(0).withEndAction(new Runnable() { //adding animation //alpha=transparency
                    @Override
                    public void run() { //when the animation is done, this code will run
                        String content = (String) parent.getItemAtPosition(position);
                        currentSelection = view;
                        dbase.setSelection(content);
                        Log.d(TAG, "Index clicked is " + position + " set to " + content);
                        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, dbase); //putting database into intent
                        shuffle = PrefsActivity.getShuffling(MusicApp.this);
                        loop = PrefsActivity.getLooping(MusicApp.this);
                        intent.putExtra(SHUFFLE_SETTING, shuffle);
                        intent.putExtra(LOOP_SETTING, loop);
                        intent.putExtra(APP_INITIATED, true);
                        startActivity(intent);
                    }
                });
            }
        });

    }

    protected void onResume(){
        super.onResume();
        if (currentSelection != null)
            currentSelection.setAlpha(1.0f); //sets the transparency back to 1.0f
    }

    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "OnDestroy called...");

        //save the current state and play location
        getPreferences(MODE_PRIVATE).edit().putInt(PLAYER_POSITION_KEY,
                mediaPlayer.getCurrentPosition()).commit();
        getPreferences(MODE_PRIVATE).edit().putBoolean(PLAYER_STATE_KEY,
                mediaPlayer.isPlaying()).commit();
        mediaPlayer.reset();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        if(id==R.id.settings){
            Intent i = new Intent(this, PrefsActivity.class); //creating intent to switch to new activity
            startActivity(i);
            return true;
        }
        if(id==R.id.about){
            Intent i = new Intent(this, AboutActivity.class); //creating intent to switch to new activity
            startActivity(i);
            return true;
        }
        return false;
    }

    private MusicDatabase createDatabase(){
        MusicDatabase db = new MusicDatabase(this);
        // add all other songs as well in here
        return db;
    }


}