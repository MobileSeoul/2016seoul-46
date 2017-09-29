package org.hansung.ansime.fragment;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

/**
 * Created by 호영 on 2016-09-13.
 * Base Fragment.
 * Fragment 생성시 상속시켜서 사용하는 Class.
 */
public abstract class BaseFragment extends Fragment {
    private View view;

    abstract void init();

    abstract void init_Event();

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

}
