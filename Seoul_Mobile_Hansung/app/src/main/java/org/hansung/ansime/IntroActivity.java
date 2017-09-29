package org.hansung.ansime;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.hansung.ansime.intro.SampleSlide;

/**
 * Created by 호영 on 2016-10-19.
 * Intro Activity.
 * 도움말을 보여주는 Activity Class.
 */
public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        addSlide(SampleSlide.newInstance(R.layout.fragment_intro1));
        addSlide(SampleSlide.newInstance(R.layout.fragment_intro2));
        addSlide(SampleSlide.newInstance(R.layout.fragment_intro3));
        addSlide(SampleSlide.newInstance(R.layout.fragment_intro4));
        addSlide(SampleSlide.newInstance(R.layout.fragment_intro5));

        setBarColor(Color.parseColor("#ffc619"));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
