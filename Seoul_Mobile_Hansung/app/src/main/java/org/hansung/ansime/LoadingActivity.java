package org.hansung.ansime;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import org.hansung.ansime.intro.IntroData;

import java.util.ArrayList;

/**
 * Created by 호영 on 2016-09-21.
 * Loading Activity.
 * 퍼미션, 네트워크, Gps 관련 기능을 제어해주는 Activity Class.
 */
public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loding);
        getSupportActionBar().hide(); // 액션바 숨김
        requestPermission(); // 퍼미션 요청
    }

    // 퍼미션 요청 함수
    public void requestPermission() {
        // 버전 체크 마시멜로 이전 버전만 함수 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            // permission 사용하는 권한 유무 확인
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없으면 리스트에 추가
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.SEND_SMS);
            }
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CALL_PHONE);
            }
            if (permissions.size() == 0) {
                // 퍼미션이 모두 허가 되어 있으면 실행
                start();
            } else {
                // 허가되지 않은 퍼미션을 추가 하는 작업
                String[] permissionArray = new String[permissions.size()];
                for (int i = 0; i < permissions.size(); i++) {
                    permissionArray[i] = permissions.get(i);
                }
                // 퍼미션 요청 (한번에 복수 퍼미션을 Request 하는것도 가능)
                requestPermissions(permissionArray, 0);
            }
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            start();
        }
    }

    // 퍼미션 요청에 대한 결과를 캐치해주는 리스너
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int count = 0;
        // 퍼미션 사용이 허가된 경우
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                count++;
            }
        }
        if (count == permissions.length) {
            // 내가 원하는 퍼미션의 갯수와 허가된 퍼미션의 갯수가 일치하면 실행
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            start();
        } else {
            finish();
        }
    }

    // 시작 함수
    private void start() {
        // 네트워크 연결 체크
        if (isNetworkConnected()) {
            // 지연 시작
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (!((LocationManager) LoadingActivity.this.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        new AlertDialog.Builder(LoadingActivity.this).setMessage("안심이에서 내 위치 정보를 사용하려면, 단말기의 설정에서 \"위치 서비스\" 사용을 허용해주세요.").setTitle("위치 서비스 사용").setIcon(R.drawable.logo).setPositiveButton("설정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                exit();
                            }
                        }).show();
                    } else {
                        IntroData introData = new IntroData(LoadingActivity.this);
                        Intent intent = null;
                        if (introData.isFirst_started()) {
                            introData.setFirst_started(false);
                            intent = new Intent(LoadingActivity.this, IntroActivity.class);
                        } else {
                            intent = new Intent(LoadingActivity.this, MainActivity.class);
                        }

                        startActivity(intent);
                        finish();
                    }
                }
            }, 500);
        } else {
            new AlertDialog.Builder(LoadingActivity.this).setMessage("네트워크 상태를 확인 후, 다시 시도해 주세요.").setTitle("네트워크 상태 확인").setIcon(R.drawable.logo).setPositiveButton("다시 시도", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    start();
                }
            }).setNegativeButton("종료하기", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    exit();
                }
            }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!((LocationManager) this.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            exit();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // 네트워크 체크 함수
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    // Activity 종료 함수
    public void exit() {
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}