package com.example.musicapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerActivity extends AppCompatActivity {

    public final String TAG = "CPTR312";
    private final String PLAYER_POSITION_KEY = "CURR_POSITION";
    private final String PLAYER_STATE_KEY = "CURR_STATE";


    //Media Player related
    final MediaPlayer mediaPlayer = new MediaPlayer();
    private int currPosition = 0; //indication of the player on the track
    private boolean prepared = false;

    // UI related
    private SeekBar seekBar;
    private TextView title, artist;
    private ImageButton playButton, pauseButton, exitButton, nextButton, prevButton, rwdButton, fwdButton;
    private ImageView imageView;

    // Timer related
    private Timer timer;
    private TimerTask task;

    //database related
    private MusicDatabase dbase;
    public static boolean shuffle, loop;
    private String[] title_playlist;

    //rotation related
    private static boolean appInitiated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Log.d(TAG, "In onCreate...");

        title = findViewById(R.id.songTitle);
        artist = findViewById(R.id.artist);
        imageView = findViewById(R.id.imageView);
        seekBar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        exitButton = findViewById(R.id.exitButton);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.previous_button);
        rwdButton = findViewById(R.id.rewind_button);
        fwdButton = findViewById(R.id.forward_button);

        Intent intent = getIntent();
        appInitiated = intent.getBooleanExtra(MusicApp.APP_INITIATED, false);
        intent.removeExtra(MusicApp.APP_INITIATED);
        if (appInitiated){
            Log.d(TAG, "App initiated");
        } else {
            Log.d(TAG, "Self initiated");
        }
        dbase = intent.getParcelableExtra(MusicApp.EXTRA_MESSAGE);

        shuffle = intent.getBooleanExtra(MusicApp.SHUFFLE_SETTING, false);
        loop = intent.getBooleanExtra(MusicApp.LOOP_SETTING, false);
        setUpPanel();


        //separately having those methods so that we can call them not only when the activity is created(onCreate) but when we want to

        setUpSongArray();
        setUpMediaPlayer();
        setUpSeekBar();
        setUpTimer();
        setUpButtons();
    }

    protected void onResume() {
        super.onResume();
        Log.d(TAG, "In onResume...");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop called...");
        View view = findViewById(R.id.playerview);
        Log.d(TAG, "Rotation is " + view.getRotation());
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy called...");
        //save the current state and play location
        getPreferences(MODE_PRIVATE).edit().putInt(PLAYER_POSITION_KEY,
                mediaPlayer.getCurrentPosition()).commit();
        getPreferences(MODE_PRIVATE).edit().putBoolean(PLAYER_STATE_KEY,
                mediaPlayer.isPlaying()).commit();
        prepared = false;
        mediaPlayer.reset();
    }

    private void setUpSeekBar() {
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "OnSeekBar listener...");
                int curr = seekBar.getProgress();
                mediaPlayer.seekTo(curr);
            }
        });
    }

    private void setUpButtons() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Media player play requested...");
                if (prepared) {
                    mediaPlayer.start();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        pauseButton.setOnClickListener(view -> { //syntactic sugar
            Log.d(TAG, "Pause requested...");
            if (prepared && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });
        nextButton.setOnClickListener(view -> {
            Log.d(TAG, "Next song requested...");
            if (prepared) {
                playNext();
            }
        });
        prevButton.setOnClickListener(view -> {
            Log.d(TAG, "Previous song requested...");
            if (prepared) {
                playPrev();
            }
        });
        fwdButton.setOnClickListener(view -> {
            Log.d(TAG, "Forward 10 sec...");
            if (prepared) {
                fwd10();
            }
        });
        rwdButton.setOnClickListener(view -> {
            Log.d(TAG, "Rewind 10 sec...");
            if (prepared) {
                rwd10();
            }
        });
        exitButton.setOnClickListener(view -> {
            Log.d(TAG, "Exit requested...");
            mediaPlayer.stop();
            mediaPlayer.release();
        });
    }

    private void fwd10() {
        currPosition += 5000;
        mediaPlayer.seekTo(currPosition);
        seekBar.setProgress(currPosition);
    }

    private void rwd10() {
        currPosition -= 5000;
        if (currPosition <= 0)
            playPrev();
        mediaPlayer.seekTo(currPosition);
        seekBar.setProgress(currPosition);
    }

    private void setUpMediaPlayer() {
        Log.d(TAG, "curr pos = " + currPosition + ", and curr state is " + mediaPlayer.isPlaying());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                boolean wasPlaying = false;
                if (!appInitiated) {
                    Log.d(TAG, "!appInitiated");
                    currPosition = getPreferences(MODE_PRIVATE).getInt(PLAYER_POSITION_KEY, 0);
                    wasPlaying = getPreferences(MODE_PRIVATE).getBoolean(PLAYER_STATE_KEY, false);
                }
                Log.d(TAG, "OnPrepared called...");
                prepared = true;
                //Calibrate the seekbar
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setMin(0);
                mediaPlayer.seekTo(currPosition);
                seekBar.setProgress(currPosition);
                //for rotation
                if(appInitiated)
                    mediaPlayer.start();
                else if(wasPlaying) {
                    Log.d(TAG," Player was active..");
                    mediaPlayer.start();
                } else {
                    Log.d(TAG," Player was inactive..");
                }

            }
        });
        int resid = dbase.getSongByTitle(dbase.getSelection()).getId();
        AssetFileDescriptor afd = getResources().openRawResourceFd(resid);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(afd);
            mediaPlayer.prepareAsync();
            afd.close();
        } catch (IllegalStateException e) {
            Log.d(TAG, "Illegal State Exception when setting data source!");

        } catch (IOException e) {
            Log.d(TAG, "IO Exception when setting data source!");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Illegal Argument Exception when setting data source!");

        } catch (SecurityException e) {
            Log.d(TAG, "Security Exception when setting data source!");

        }


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "song ended!");
                playNext();
            }
        });
    }

    private void setUpTimer() {
        task = new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    currPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currPosition);
                }
            }
        };

        timer = new Timer();
        timer.schedule(task, 50, 200);
    }

    private String[] setUpSongArray() {
        String[] title_unshuffled = dbase.getSongTitles();
        String[] title_shuffled = shuffleArrayInitial(title_unshuffled);
        //title_unshuffled = getResources().getStringArray(R.array.SongList);
        //shuffle = getPreferences(MODE_PRIVATE).getBoolean(KEY_SHUFFLE, SHUFFLE_FALSE);
        if (shuffle)
            title_playlist = title_shuffled;
        else
            title_playlist = title_unshuffled;
        return title_playlist;
    }

    private String[] shuffleArray(String[] that) {
        String songName = dbase.getSelection();
        String[] unshuffled = Arrays.copyOf(that, that.length);
        List<String> list = Arrays.asList(unshuffled);
        Collections.shuffle(list);
        return list.toArray(new String[0]);
    }

    private String[] shuffleArrayInitial(String[] that) {
        String songName = dbase.getSelection();
        String[] unshuffled = Arrays.copyOf(that, that.length);
        ArrayList<String> arrlist = new ArrayList<>(Arrays.asList(unshuffled));
        arrlist.remove(songName);
        Collections.shuffle(arrlist);
        arrlist.add(0, songName);
        return arrlist.toArray(new String[0]);
    }

    private void playNext() {
        Log.d(TAG, "In playNext");
        String songName = dbase.getSelection();
        //getIndexFromArray (=n)
        int index = indexOf(title_playlist, songName);
        if ((index + 1) % title_playlist.length == 0) {
            if (shuffle)
                title_playlist = shuffleArray(title_playlist); // shuffle
            if (!loop) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
        index++;
        index %= title_playlist.length;
        String nextTitle = title_playlist[index];
        Song nextSong = dbase.getSongByTitle(nextTitle);
        dbase.setSelection(nextTitle);
        setUpPanel();
        mediaPlayer.reset();
        currPosition = 0;
        setUpMediaPlayer();
    }

    private void playPrev() {
        Log.d(TAG, "In playPrev");
        String songName = dbase.getSelection();
        int index = indexOf(title_playlist, songName);
        if (index % title_playlist.length == 0) {
            if (!loop) {
                mediaPlayer.stop();
                mediaPlayer.release();
            } else if (shuffle)
                title_playlist = shuffleArray(title_playlist); // shuffle
        }
        index--;
        index = (index + title_playlist.length) % title_playlist.length;
        String nextTitle = title_playlist[index];
        Song nextSong = dbase.getSongByTitle(nextTitle);
        dbase.setSelection(nextTitle);
        setUpPanel();
        mediaPlayer.reset();
        currPosition = 0;
        setUpMediaPlayer();
    }

    private int indexOf(String[] arr, String title) {
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equalsIgnoreCase(title))
                return i;
            else index = -1;
        }
        setUpMediaPlayer();
        return index;
    }

    private void setUpPanel() {
        String songName = dbase.getSelection();
        title.setText(songName); //get the title of the song selected
        Song song = dbase.getSongByTitle(songName);
        String artistName = song.getArtist();
        artist.setText(artistName);
        imageView.setImageDrawable(getDrawable(song.getPicture()));
    }


}