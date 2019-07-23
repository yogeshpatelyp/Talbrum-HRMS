package com.talentcerebrumhrms.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.AppController;

/**
 * Created by Harshit on 08-Jun-17.
 */

public class TermsOfUseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_web_view, null);

        WebView web = (WebView) rootview.findViewById(R.id.web);
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(AppController.serverUrl + "/static/terms");
        return rootview;
    }
}
