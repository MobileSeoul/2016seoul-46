package org.hansung.ansime.dialog;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by 호영 on 2016-09-15.
 * Loading Dialog Manager Class.
 */
public class LoadingDialogManager {
    private Context context;
    private ProgressDialog mProgressDialog;

    public LoadingDialogManager(Context context) {
        this.context = context;
    }

    // 다이얼로그 시작 함수
    public void Loading(String str) {
        mProgressDialog = ProgressDialog.show(context, null, str, true, false);
    }

    // 다이얼로그 제거 함수
    public void LoadingEnd() {
        mProgressDialog.dismiss();
    }

}
