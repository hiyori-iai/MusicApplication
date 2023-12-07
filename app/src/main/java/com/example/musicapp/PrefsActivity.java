package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class PrefsActivity extends AppCompatActivity {

    private static final String TAG = "CPTR320";
    private static final String OPT_LOOPING = "looping";
    private static final boolean OPT_LOOPING_OFF = true;
    private static final String OPT_SHUFFLING = "shuffling";
    private static final boolean OPT_SHUFFLING_OFF = true;

    //private static final String OPT_IMMUT = "immutable";
    //private static final boolean OPT_IMMUT_OFF = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);

        getSupportActionBar();

        if(findViewById(R.id.settings_container) != null){
            if(savedInstanceState != null){
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.settings_container, new PrefsFragment()).commit(); //passing the message without intent
        }
    }

    /**
     * get the current value of the looping option in settings
     * @param context
     * @return
     */
    public static boolean getLooping(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_LOOPING, OPT_LOOPING_OFF);
    }

    /**
     * get the current value of the shuffling option in settings
     * @param context
     * @return
     */
    public static boolean getShuffling(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_SHUFFLING, OPT_SHUFFLING_OFF);
    }


    /**
     * get the current value of the immutable option in settings
     * @param context
     * @return
     */
    //public static boolean getImmutable(Context context){
    //   return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_IMMUT, OPT_IMMUT_OFF);
    // }
}