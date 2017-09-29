package org.hansung.ansime.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.hansung.ansime.MainActivity;
import org.hansung.ansime.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.POWER_SERVICE;
import static org.hansung.ansime.MainActivity.GPS;

/**
 * Created by 동교 on 2016-10-04.
 * Alarm Fragment.
 */
public class AlarmFragment extends BaseFragment implements View.OnClickListener {
    private final int view_id = R.layout.fragment_alarm;
    private ImageView alarmImage, send, police, alarm;
    private boolean alarm_flag = false;
    private SoundPool mSoundPool;
    private int sound_id;
    private int stop_id;
    private Context mContext; //볼륨 조절을 위한 startActivity의 컨텍스트
    private int repeatImage = 0;
    private Handler mHandler = null;
    private String[] items = {"여성 안심 귀가 서비스 메시지 보내기", "경찰에게 연락하기"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setView(inflater.inflate(view_id, container, false));

        init();
        init_Event();

        return getView();
    }

    @Override
    void init() {
        alarmImage = (ImageView) getView().findViewById(R.id.alarmImage);
        send = (ImageView) getView().findViewById(R.id.send);
        police = (ImageView) getView().findViewById(R.id.police);
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound_id = mSoundPool.load(getActivity(), R.raw.alarm, 1);
        mContext = getActivity();
        alarm = (ImageView) getView().findViewById(R.id.alarm);
    }

    @Override
    void init_Event() {
        send.setOnClickListener(this);
        police.setOnClickListener(this);
        alarm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                // 사람 아이콘 클릭
                // GPS 체크
                ((MainActivity) getActivity()).checkGPS();
                // Log.v("test", "Send 이미지 클릭");
                // 저장된 번호로 메시지 전송
                if (MainActivity.APPDATA.getPhoneNumber() == null) {
                    showSetPhoneNumberDialog();
                } else {
                    // 저장된 변호가 없다면 다이얼로그를 이용해서 입력 받음
                    showMsgDialog("지인에게 메시지 보내기", MainActivity.APPDATA.getPhoneNumber());
                }
                break;
            case R.id.police:
                // 경찰 아이콘 클릭
                // GPS 체크
                ((MainActivity) getActivity()).checkGPS();
                showPoliceDialog();
                break;
            case R.id.alarm:
                // 경보기 아이콘 클릭
                // GPS 체크
                ((MainActivity) getActivity()).checkGPS();
                // 경보기를 사용했을 때를 기록 시킴
                MainActivity.APPDATA.setType(1);

                // 음량 관련 변수
                AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, MainActivity.APPDATA.getVolume(), AudioManager.FLAG_PLAY_SOUND);

                if (alarm_flag) {
                    alarm_flag = false;
                    alarmImage.setImageResource(R.drawable.alarm_d);
                    mSoundPool.stop(stop_id);
                    mHandler.removeMessages(0);
                } else {
                    alarm_flag = true;
                    // 핸들러를 이용해서 움직이는 아이콘 구현
                    mHandler = new Handler() {
                        public void handleMessage(Message msg) {
                            repeatImage = repeatImage % 3;
                            if (repeatImage == 0) {
                                alarmImage.setImageResource(R.drawable.alarm_s_00);
                            } else if (repeatImage == 1) {
                                alarmImage.setImageResource(R.drawable.alarm_s_01);
                            } else {
                                alarmImage.setImageResource(R.drawable.alarm_s_02);
                            }
                            repeatImage++;
                            mHandler.sendEmptyMessageDelayed(0, 50);
                        }
                    };
                    mHandler.sendEmptyMessage(0);

                    // 음악 재생 play(id, 왼쪽 볼륨, 오른쪽 볼륨, 우선순위, 반복횟수(-1 무한반복), 재생속도(0.5~2.0));
                    stop_id = mSoundPool.play(sound_id, 1, 1, 0, -1, 2f);
                }
                break;
        }
    }

    // 경찰 다이얼로그 함수
    private void showPoliceDialog() {
        AlertDialog.Builder policeDialog = new AlertDialog.Builder(getActivity());
        policeDialog.setTitle("경찰에게 긴급상황 알리기");
        policeDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    // send message
                    // 22시 ~ 1시까지만 사용 가능
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfNow = new SimpleDateFormat("HH");
                    int curHour = Integer.parseInt(sdfNow.format(date));
                    if (curHour >= 22 || curHour <= 1)
                        showMsgDialog(items[0], "120");
                    else
                        Toast.makeText(getActivity(), "밤 10시 ~ 새벽 1시에 이용 가능합니다.", Toast.LENGTH_LONG).show();
                } else {
                    //call police
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:112"));
                    startActivity(intent);
                }
            }
        });
        policeDialog.setIcon(R.drawable.police);
        policeDialog.setCancelable(true);
        policeDialog.create().show();
    }

    // 메세시 보내기 다이얼로그 함수
    private void showMsgDialog(final String title, final String phoneNumber) {
        AlertDialog.Builder msgDialog = new AlertDialog.Builder(getActivity());
        msgDialog.setTitle(title);

        final String msg = "현재 위치 : " + GPS.getAddress() + "\n" + "목표 안심지킴이집 : " + GPS.getTargetAddress();
        msgDialog.setMessage(msg + "\n\n" + "해당 경로 메시지를 보내시겠습니까?").setCancelable(true).setPositiveButton("보내기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String[] partTargetAddressArray = GPS.getTargetAddress().split(" ");
                String TargetAddressURL = "";
                for (int i = 0; i < partTargetAddressArray.length; i++) {
                    TargetAddressURL += partTargetAddressArray[i];
                    if (i < partTargetAddressArray.length - 1)
                        TargetAddressURL += "%20";
                }
                if (title.equals(items[0])) {
                    sendSMS(phoneNumber, "안심이 알림 서비스 입니다.\n\n" + msg + "\n" + "https://m.map.naver.com/search2/search.nhn?query=" + TargetAddressURL + "\n\n" + "해당 경로로 이동 중입니다.\n여성 안심 귀가 서비스를 요청합니다.");
                } else {
                    sendSMS(phoneNumber, "안심이 알림 서비스 입니다.\n\n" + msg + "\n" + "https://m.map.naver.com/search2/search.nhn?query=" + TargetAddressURL + "\n\n" + "해당 경로로 이동 중입니다.\n데리러와주세요.");
                }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        msgDialog.setIcon(R.drawable.send);
        msgDialog.setCancelable(true);
        msgDialog.create().show();
    }

    // 메세시 전송 함수
    private void sendSMS(String phoneNumber, String message) {
        String SEND = "SMS_SEND";

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getActivity(), "서비스 요청 성공", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getActivity(), "메시지 보내기 실패...", Toast.LENGTH_LONG).show();
                }
            }
        }, new IntentFilter(SEND));

        SmsManager sms = SmsManager.getDefault();
        sms.sendMultipartTextMessage(phoneNumber, null, sms.divideMessage(message), null, null);
        Toast.makeText(getActivity(), "서비스 요청 성공", Toast.LENGTH_LONG).show();
    }

    // 전화번호 등록 다이얼로그 함수
    private void showSetPhoneNumberDialog() {
        final DisplayMetrics dm = getResources().getDisplayMetrics();
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
                if (value.length() == 0) {
                    Toast.makeText(getActivity(), "지인 번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                    showSetPhoneNumberDialog();
                } else MainActivity.APPDATA.setPhoneNumber(value);

            }
        }).setNegativeButton("취소", null).setView(pLayout).setCancelable(true).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Fragment 가 일시정지하거나 종료할때 처리
        if (alarm_flag) {
            alarm_flag = false;
            alarmImage.setImageResource(R.drawable.alarm_d);
            mSoundPool.stop(stop_id);
            mHandler.removeMessages(0);
        }
    }
}
