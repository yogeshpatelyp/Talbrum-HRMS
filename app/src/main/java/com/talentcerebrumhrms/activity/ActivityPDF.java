package com.talentcerebrumhrms.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.fragment.PdfRendererBasicFragment;

import java.io.File;

public class ActivityPDF extends AppCompatActivity {

    public static final String FRAGMENT_PDF_RENDERER_BASIC = "pdf_renderer_basic";
    File file,file2;
    String PDFPATH,PDFCAT;

    ImageView btnDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfrender);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PdfRendererBasicFragment(),
                            FRAGMENT_PDF_RENDERER_BASIC)
                    .commit();
        }


        btnDelete = (ImageView) findViewById(R.id.imageView4);
        btnDelete.setImageDrawable(getResources().getDrawable(R.drawable.close_24dp));
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ActivityPDF.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                Intent i = new Intent(ActivityPDF.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i, bndlanimation);
            }
        });
        ImageView imgback = (ImageView)findViewById(R.id.imageback) ;
        TextView txtback = (TextView) findViewById(R.id.txtback) ;
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //txtback.setText("Reading Book");

    }



}
