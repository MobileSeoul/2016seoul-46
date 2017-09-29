package org.hansung.ansime.intro;

import android.content.Context;
import android.content.SharedPreferences;

import org.hansung.ansime.MainActivity;

/**
 * Created by 호영 on 2016-10-15.
 */

public class IntroData {

    private SharedPreferences appData;

    private boolean first_started;


    public IntroData(Context context) {
        appData = context.getSharedPreferences("introData", 0);
        load();
    }

    public boolean isFirst_started() {
        return first_started;
    }

    public void setFirst_started(boolean first_started) {
        this.first_started = first_started;

        SharedPreferences.Editor editor = appData.edit();
        editor.putBoolean("FIRST_STARTED", first_started);
        editor.apply();
    }

    private void load() {
        first_started = appData.getBoolean("FIRST_STARTED", true);
    }

}

