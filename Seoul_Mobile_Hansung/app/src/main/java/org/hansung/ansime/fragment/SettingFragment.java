package org.hansung.ansime.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.hansung.ansime.IntroActivity;
import org.hansung.ansime.MainActivity;
import org.hansung.ansime.R;
import org.hansung.ansime.dto.AppData;

/**
 * Created by 호영 on 2016-10-20.
 * Setting Fragment.
 * 정보, 도움말 보거나 Setting 값을 설정하는 Fragment.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private final int view_id = R.layout.fragment_setting;
    private LinearLayout phoneNumber_layout;
    private LinearLayout volume_layout;
    private TextView phoneNumText, volumeText;
    private TextView[] infoText;
    private Integer[] infoTextId = {R.id.infoText0, R.id.infoText1, R.id.infoText2, R.id.infoText3};
    private DisplayMetrics dm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setView(inflater.inflate(view_id, container, false));

        init();
        init_Event();

        return getView();
    }

    @Override
    void init() {
        // dp 값을 얻기위한 변수
        dm = getResources().getDisplayMetrics();

        phoneNumber_layout = (LinearLayout) getView().findViewById(R.id.phoneNumber_layout);
        volume_layout = (LinearLayout) getView().findViewById(R.id.volume_layout);
        phoneNumText = (TextView) getView().findViewById(R.id.phoneNumText);
        volumeText = (TextView) getView().findViewById(R.id.volumeText);

        infoText = new TextView[4];
        for (int i = 0; i < infoText.length; i++) {
            infoText[i] = (TextView) getView().findViewById(infoTextId[i]);
        }
        phoneNumText.setText(MainActivity.APPDATA.getPhoneNumber());
        volumeText.setText(String.format("%d", MainActivity.APPDATA.getVolume()));
    }

    @Override
    void init_Event() {
        phoneNumber_layout.setOnClickListener(this);
        volume_layout.setOnClickListener(this);
        for (TextView anInfoText : infoText) {
            anInfoText.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.infoText0:
                // 여성 안심 지킴이 집이란?
                showInfoDialog(0);
                break;
            case R.id.infoText1:
                // 여성 안심 귀가 서비스란?
                showInfoDialog(1);
                break;
            case R.id.infoText2:
                // 안심이란?
                showInfoDialog(2);
                break;
            case R.id.infoText3:
                // 도움말 보기
                Intent intent = new Intent(getActivity(), IntroActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.phoneNumber_layout:
                // 지인 전화 번호 설정
                showPhoneNumberDialog();
                break;
            case R.id.volume_layout:
                // 경보기 음량 설정
                showVolumeDialog();
                break;
        }
    }

    // 정보를 보여주는 다이얼로그 함수
    private void showInfoDialog(int i) {
        final LinearLayout infoLayout = new LinearLayout(getActivity());
        final ImageView infoImg = new ImageView(getActivity());
        int paddingSize = Math.round(10 * dm.density);
        infoLayout.setPadding(paddingSize, paddingSize, paddingSize, 0);
        Integer src = 0;
        if (i == 0) {
            src = R.drawable.info0;
            infoImg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.round(250 * dm.density)));
        } else if (i == 1) {
            src = R.drawable.info1;
            infoImg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.round(300 * dm.density)));
        } else {
            src = R.drawable.info2;
            infoImg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.round(300 * dm.density)));
        }
        infoImg.setImageResource(src);
        infoLayout.addView(infoImg);
        new AlertDialog.Builder(getActivity()).setIcon(R.drawable.logo).setTitle(this.infoText[i].getText()).setNegativeButton("뒤로가기", null).setView(infoLayout).setCancelable(true).show();
    }

    // 지인 전화 번호 설정 다이얼로그 함수
    private void showPhoneNumberDialog() {
        final LinearLayout pLayout = new LinearLayout(getActivity());
        final EditText phoneNumInput = new EditText(getActivity());
        int paddingSize = Math.round(20 * dm.density);
        pLayout.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
        phoneNumInput.setText(MainActivity.APPDATA.getPhoneNumber());
        int inputTextSize = Math.round(240 * dm.density);
        phoneNumInput.setWidth(inputTextSize);
        pLayout.addView(phoneNumInput);
        new AlertDialog.Builder(getActivity()).setIcon(R.drawable.logo).setTitle("지인 전화 번호 등록하기").setPositiveButton("변경", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = phoneNumInput.getText().toString().trim();
                MainActivity.APPDATA.setPhoneNumber(value);
                if (value.length() == 0) {
                    Toast.makeText(getActivity(), "지인 번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                    showPhoneNumberDialog();
                } else {
                    MainActivity.APPDATA.setPhoneNumber(value);
                    phoneNumText.setText(MainActivity.APPDATA.getPhoneNumber());
                }
            }
        }).setNegativeButton("취소", null).setView(pLayout).setCancelable(true).show();
    }

    // 음료를 설정 다이얼로그 함수
    private void showVolumeDialog() {
        final LinearLayout vLayout = new LinearLayout(getActivity());
        int paddingSize = Math.round(20 * dm.density);
        vLayout.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
        vLayout.setOrientation(LinearLayout.VERTICAL);
        final SeekBar volumeInput = new SeekBar(getActivity());
        final TextView progressText = new TextView(getActivity());
        volumeInput.setProgress(MainActivity.APPDATA.getVolume());
        progressText.setGravity(Gravity.CENTER);
        vLayout.addView(volumeInput);
        vLayout.addView(progressText);

        volumeInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MainActivity.APPDATA.setVolume(progress);
                progressText.setText(String.format("음량 : %d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        volumeInput.setMax(15);
        volumeInput.setProgress(MainActivity.APPDATA.getVolume());
        System.out.println("SettingFragment.onClick - " + MainActivity.APPDATA.getVolume());
        new AlertDialog.Builder(getActivity()).setIcon(R.drawable.logo).setTitle("경보기 음량 조절").setPositiveButton("변경", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                volumeText.setText(String.format("%d", MainActivity.APPDATA.getVolume()));
            }
        }).setNegativeButton("취소", null).setView(vLayout).setCancelable(true).show();
    }
}
