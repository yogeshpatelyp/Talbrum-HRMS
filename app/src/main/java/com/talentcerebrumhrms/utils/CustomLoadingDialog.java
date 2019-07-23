package com.talentcerebrumhrms.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.talentcerebrumhrms.R;
import com.victor.loading.rotate.RotateLoading;

/**
 * Created by saransh on 19-10-2016.
 */

public class CustomLoadingDialog extends Dialog {
    private RotateLoading rotateLoading;

    private TextView head;

    public CustomLoadingDialog(Context context, int themeResId, String body) {
        super(context, themeResId);

        setContentView(R.layout.dialog_custom);
        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading);
        head = (TextView) findViewById(R.id.head);
        head.setText(body);
        rotateLoading.start();
    }
}
