package com.talentcerebrumhrms.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.talentcerebrumhrms.activity.MainActivity;
import com.talentcerebrumhrms.adapter.PeopleAdapter;
import com.talentcerebrumhrms.datatype.PeopleDataType;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.AppController;
import com.talentcerebrumhrms.utils.DrawerButtonListerner;
import com.talentcerebrumhrms.utils.SlidePanelListener;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitHelper;

import static com.talentcerebrumhrms.utils.Utility.dpToPx;

/**
 * Created by saransh on 09-11-2016.
 * <p>
 * people fragment gives directory of company employees.
 */

public class PeopleFragment extends Fragment implements SlidePanelListener {

    View rootview;
    Toolbar toolbar;
    private static final String KEY_HEADER_POSITIONING = "key_header_mode";

    private static final String KEY_MARGINS_FIXED = "key_margins_fixed";

    private ViewHolder mViews;

    private static PeopleAdapter mAdapter;

    private int mHeaderDisplay;

    private boolean mAreMarginsFixed;
    ImageButton search_back_button, search_remove_button;
    static EditText search_edittext;
    LinearLayout searchbar;
    RecyclerView recyclerView;
    static ArrayList<PeopleDataType> dataArray;
    private SlidingUpPanelLayout slideuppanel;
    DrawerButtonListerner listerner;
    TextView Name, desig_div;
    LinearLayout call, message, mail, scrollStopper;
    private float offsetFinalValue;
    private static final String TAG = "PeopleFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_people, null);
        setHasOptionsMenu(true);
        toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        search_back_button = (ImageButton) rootview.findViewById(R.id.search_back_button);
        search_remove_button = (ImageButton) rootview.findViewById(R.id.search_remove_button);
        search_edittext = (EditText) rootview.findViewById(R.id.search_edittext);
        searchbar = (LinearLayout) rootview.findViewById(R.id.search_layout);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);

        search_back_button.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        search_remove_button.setColorFilter(ContextCompat.getColor(getActivity(), R.color.grey), PorterDuff.Mode.SRC_ATOP);
        dataArray = new ArrayList<>(AppController.people_data_array);
        slideuppanel = (SlidingUpPanelLayout) rootview.findViewById(R.id.sliding_layout);
        scrollStopper = (LinearLayout) rootview.findViewById(R.id.scrollStopperView);

        call = (LinearLayout) rootview.findViewById(R.id.call);
        message = (LinearLayout) rootview.findViewById(R.id.message);
        mail = (LinearLayout) rootview.findViewById(R.id.mail);
        Name = (TextView) rootview.findViewById(R.id.text);
        desig_div = (TextView) rootview.findViewById(R.id.desig_div);
        AutofitHelper.create(desig_div);
        listerner = (MainActivity) getActivity();

        return rootview;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        slideuppanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        if (savedInstanceState != null) {
            mHeaderDisplay = savedInstanceState.getInt(KEY_HEADER_POSITIONING, getResources().getInteger(R.integer.default_header_display));
            mAreMarginsFixed = savedInstanceState.getBoolean(KEY_MARGINS_FIXED, getResources().getBoolean(R.bool.default_margins_fixed));
        } else {
            mHeaderDisplay = getResources().getInteger(R.integer.default_header_display);
            mAreMarginsFixed = getResources().getBoolean(R.bool.default_margins_fixed);
        }
        //lets recyclerview scroll
        scrollStopper.setVisibility(View.GONE);

        mViews = new ViewHolder(view);
        mViews.initViews(new LayoutManager(getActivity()));
        mAdapter = new PeopleAdapter(getActivity(), mHeaderDisplay, dataArray, this);
        mAdapter.setMarginsFixed(mAreMarginsFixed);
        mAdapter.setHeaderDisplay(mHeaderDisplay);
        mViews.setAdapter(mAdapter);


        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("AlertActivity", "before text changed ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("AlertActivity", "on text changed ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search_edittext.getText().toString().toLowerCase(Locale.getDefault());
                Log.e("AlertActivity", "after text changed ");
                mAdapter.filter(text);
                mViews.notifychange();
            }
        });

        search_remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_edittext.setText("");

            }
        });

        search_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slidepanelcheck())
                    getActivity().onBackPressed();
                else {

                    searchBarSetEnabled(true);
                    slideuppanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }

            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                // Log.e("ontouch", "true");
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_edittext.getWindowToken(), 0);
                return false;
            }
        });

        slideuppanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listerner.hamburgerEnabled(true);
                searchBarSetEnabled(true);
                slideuppanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

            }
        });

        slideuppanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                offsetFinalValue = slideOffset;
                if (slideOffset <= 0.1)
                    scrollStopper.setVisibility(View.GONE);
                else if (slideOffset >= 0.1)
                    scrollStopper.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
                if (slideuppanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    listerner.hamburgerEnabled(true);
                    slideuppanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                } else if (slideuppanel.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
                    if (offsetFinalValue < 0.5) {
                        scrollStopper.setVisibility(View.GONE);
                        slideuppanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    } else {
                        scrollStopper.setVisibility(View.VISIBLE);
                        slideuppanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                } else if (slideuppanel.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                    searchBarSetEnabled(true);
                    listerner.hamburgerEnabled(true);
                    scrollStopper.setVisibility(View.GONE);
                } else if (slideuppanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
                    searchBarSetEnabled(false);
            }
        });


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_HEADER_POSITIONING, mHeaderDisplay);
        outState.putBoolean(KEY_MARGINS_FIXED, mAreMarginsFixed);
    }

    @Override
    public void slideUpPanelAction(@NonNull String name, @NonNull final String phone, @NonNull String division, @NonNull String designation, @NonNull final String email) {
        Log.e("slideUpPanelAction", "called");
        Name.setText(name);
        this.desig_div.setText(designation + getString(R.string.pipe) + division);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Answers.getInstance().logCustom(new CustomEvent("Person No. Called"));
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Answers.getInstance().logCustom(new CustomEvent("Person Messaged"));
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + phone));
                startActivity(sendIntent);
            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Answers.getInstance().logCustom(new CustomEvent("Person Mailed"));
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email.trim(), null));
                // intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                //  intent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));

            }
        });


        if (toolbar.getVisibility() == View.GONE) {

            //keyboard visible check
            //no other method to check if keyboard is visible
            // have to remove listener otherwise keeps listening when the purpose finishes and creates problems.
            final View activityRootView = rootview.findViewById(R.id.rootview);
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                    if (heightDiff > dpToPx(getActivity(), 200)) { // if more than 200 dp, it's probably a keyboard...
                        // ... do something here
                        Log.e("softkeyboardup", "yes");
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(search_edittext.getWindowToken(), 0);
                        // activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        Log.e("softkeyboardup", "no");
                        activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (slideuppanel.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                            Log.e("slideUpPanelAction", "if");
                            listerner.hamburgerEnabled(false);
                            searchBarSetEnabled(false);
                            scrollStopper.setVisibility(View.VISIBLE);
                            slideuppanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

                        } else {
                            listerner.hamburgerEnabled(true);
                            Log.e("slideUpPanelAction", "else");
                            slideuppanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                        }
                    }
                }
            });


            // showToolbar();
        } else {
            if (slideuppanel.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                Log.e("slideUpPanelAction", "if");
                listerner.hamburgerEnabled(false);
                scrollStopper.setVisibility(View.VISIBLE);
                slideuppanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            } else {
                listerner.hamburgerEnabled(true);
                Log.e("slideUpPanelAction", "else");
                slideuppanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

            }
        }
    }

    private static class ViewHolder {

        private final RecyclerView mRecyclerView;


        public ViewHolder(View view) {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        }

        void initViews(LayoutManager lm) {
            mRecyclerView.setLayoutManager(lm);
        }

        void notifychange() {
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        public void setAdapter(RecyclerView.Adapter<?> adapter) {
            mRecyclerView.setAdapter(adapter);
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_people, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search_people:
                removeToolbar(item);
                listerner.hamburgerEnabled(false);
                Log.e("people", "search");
                Answers.getInstance().logCustom(new CustomEvent("People Search Clicked"));
                //  invalidateOptionsMenu(); to refresh menu
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void showToolbar() {

        //animation fade in and drop down
        AnimationSet anim = new AnimationSet(true);
        Animation animate = new TranslateAnimation(0, 0, 0, toolbar.getTop());
        animate.setDuration(200);
        // animate.setFillAfter(true);
        anim.addAnimation(animate);
        Animation fade = new AlphaAnimation(0.0f, 1.0f);
        fade.setDuration(200);
        anim.addAnimation(fade);
        search_edittext.setText("");
        toolbar.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.e("animation sh listener", "start");

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("animation sh listener", "end");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.e("animation sh listener", "repeat");
            }
        });

        toolbar.setVisibility(View.VISIBLE);
        searchbar.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search_edittext.getWindowToken(), 0);
    }

    private void removeToolbar(final MenuItem menuItem) {
        //animation up and fade out
        AnimationSet anim = new AnimationSet(true);
        Animation animate = new TranslateAnimation(0, 0, 0, -toolbar.getHeight());
        animate.setDuration(200);
        // animate.setFillAfter(true);
        anim.addAnimation(animate);
        Animation fade = new AlphaAnimation(1.0f, 0.0f);
        fade.setDuration(200);


        anim.addAnimation(fade);
        toolbar.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //  Log.e("animation rm listener","start");
                menuItem.setEnabled(false);
                // AlertActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Log.e("animation rm listener","end");
                menuItem.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Log.e("animation rm listener","repeat");
            }
        });
        toolbar.setVisibility(View.GONE);
        // toolbar.animate().alpha(0.0f).setDuration(300);
        searchbar.setVisibility(View.VISIBLE);
        search_edittext.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(search_edittext, InputMethodManager.SHOW_IMPLICIT);
    }

    public boolean slidepanelcheck() {
        if (slideuppanel.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN)
            return false;
        else
            return true;

    }

    public void searchBarSetEnabled(boolean value) {
        if (value) {
            search_edittext.setEnabled(true);
            search_remove_button.setEnabled(true);
        } else {
            search_edittext.setEnabled(false);
            search_remove_button.setEnabled(false);
        }

    }

    public void backPressPeople() {
        if (slideuppanel.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            listerner.hamburgerEnabled(true);
            Log.e("backpresspeople", "true");
            slideuppanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
    }

    public static void updateadapter() {
        dataArray = new ArrayList<>(AppController.people_data_array);
        if (mAdapter != null) {
            mAdapter.updateAdapter(dataArray);
            if (!search_edittext.getText().toString().equalsIgnoreCase("")) {
                String text = search_edittext.getText().toString().toLowerCase(Locale.getDefault());
                mAdapter.filter(text);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("onResume", "true");
        if (slideuppanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED
                && searchbar.getVisibility() == View.VISIBLE) {

            Log.e("checcccck", "true");
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        }

    }
}
