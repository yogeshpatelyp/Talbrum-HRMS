package com.talentcerebrumhrms.utils;


import androidx.annotation.NonNull;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Harshit on 08-May-17.
 */

public interface ResponseListener {

    void onResponse(@NonNull JSONObject jsonObject);

    void onError(@NonNull VolleyError volleyError);
}
