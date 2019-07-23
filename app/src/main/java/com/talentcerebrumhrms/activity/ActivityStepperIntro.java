package com.talentcerebrumhrms.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.talentcerebrumhrms.R;

/**
 * Created by Anish on 15-Sep-17.
 */

public class ActivityStepperIntro extends AppCompatActivity {
   // SessionManagerSlider session;
    private static final int MAX_STEP = 4;
    private ViewPager viewPager;

    private MyViewPagerAdapter myViewPagerAdapter;
    private Button btnNext;
    private String about_title_array[] = {
            "Know Your Home Screen",
            "Learn To Practice",
            "Thought if anyone has a Question",
            "Meet Quiz-zo"
    };
    private String about_description_array[] = {
            "Navigate through available courses, chapters and videos, all from one place.",
            "Watch Video Lessons, Like, Add to Favourite, or Download and watch offline.",
            "Ask your Questions or Answer few if you are good enough.",
            "A game that pays real money, but it is'nt easy - Try it.",
    };
    private int about_images_array[] = {
            R.drawable.alert,
            R.drawable.alert,
            R.drawable.alert,
            R.drawable.alert
    };
String str;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = ActivityStepperIntro.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(ActivityStepperIntro.this, R.color.colorAccent));
        }
        setContentView(R.layout.activity_stepper_wizard_light);
       /* session = new SessionManagerSlider(ActivityStepperIntro.this);

            HashMap<String, String> user = session.getIntroSession();


            str = user.get(SessionManagerTwo.KEY_INTRO);
if (str!=null){
    if(str.equalsIgnoreCase("Endntro")){
        Intent intent = new Intent( getApplicationContext() , com.learningapp.freeshiksha.ActivityLogin.class);
        startActivity(intent);
        finish();

    }else{
        initComponent();
    }
}else{
    initComponent();
}
*/
        initComponent();



//        Tools.setSystemBarColor(this, R.color.grey_20);
    }

    private void initComponent() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        btnNext = (Button) findViewById(R.id.btn_next);

        // adding bottom dots
        bottomProgressDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager.getCurrentItem() + 1;
                if (current < MAX_STEP) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {

                    //session.createIntroSession("Endntro");
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ActivityStepperIntro.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                    Intent i = new Intent(ActivityStepperIntro.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i, bndlanimation);
                    //finish();
                }
            }
        });

        findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ActivityStepperIntro.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                Intent i = new Intent(ActivityStepperIntro.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i, bndlanimation);
            }
        });

    }


    private void bottomProgressDots(int current_index) {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[MAX_STEP];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current_index].setImageResource(R.drawable.shape_circle);
            dots[current_index].setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            bottomProgressDots(position);

            if (position == about_title_array.length - 1) {
                btnNext.setText("GOT IT");
                btnNext.setBackgroundColor(getResources().getColor(R.color.orange));
                btnNext.setTextColor(Color.WHITE);

            } else {
                btnNext.setText("NEXT");
                btnNext.setBackgroundColor(getResources().getColor(R.color.grey_10));
                btnNext.setTextColor(getResources().getColor(R.color.grey_90));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_stepper_wizard, container, false);
            ((TextView) view.findViewById(R.id.title)).setText(about_title_array[position]);
            ((TextView) view.findViewById(R.id.description)).setText(about_description_array[position]);
            ((ImageView) view.findViewById(R.id.image)).setImageResource(about_images_array[position]);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return about_title_array.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }




}
