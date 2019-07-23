package com.talentcerebrumhrms.utils;

import android.app.FragmentManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by saransh on 27-10-2016.
 */

public class DatePickerEditText implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Context context;
    private EditText editText;
    private int mday;
    private int mmonth;
    private int myear;
    private DatePickerDialog dialog;
    private Calendar calendar;
    private FragmentManager fragmentManager;

    public DatePickerEditText(Context mcontext, FragmentManager fragmentManager, EditText edit) {
        this.context = mcontext;
        //  Activity act = (Activity) mcontext;
        //this.editText = (EditText) act.findViewById(edittextid);
        this.editText = edit;
        this.editText.setOnClickListener(this);
        this.calendar = Calendar.getInstance(TimeZone.getDefault());
        this.fragmentManager = fragmentManager;

        this.dialog = new DatePickerDialog();
        this.dialog = DatePickerDialog.newInstance(
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

    }

    @Override
    public void onClick(View v) {
        Calendar now = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() - 1000);
        //dialog.setMinDate(now);
        //  Log.i("editTextDatePicker", String.valueOf(System.currentTimeMillis()));
        dialog.show(fragmentManager, "Datepickerdialog");
    }


    private void updateDisplay() {

        editText.setText(new StringBuilder()
                // Month is 0 based so add 1

                .append(singleToDoubleDigitSet(mday)).append("/").append(singleToDoubleDigitSet(mmonth + 1)).append("/").append(singleToDoubleDigitSet(myear)).append(" "));
    }

    public void setMinDateToPresent() {
        Calendar now = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() - 1000);
        dialog.setMinDate(now);
    }

    public void setMaxDateToPresent() {
        Calendar now = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() - 1000);
        dialog.setMaxDate(now);
    }

    public void setMaxPickerDate(Long time) {
        Log.e("setMaxDate", String.valueOf(time));
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(time + 86399000);
        // dialog.setMaxDate();
        dialog.setMaxDate(now);
    }

    public void setMinPickerDate(Long time) {
        Log.e("setMinDate", String.valueOf(time));
        Calendar now = Calendar.getInstance();
        Log.e("calendar", String.valueOf(now));
        now.setTimeInMillis(0);
        //now.add(Calendar.DAY_OF_YEAR,-1);
        Log.e("calendar 1", String.valueOf(now));
        // dialog.setMinDate(0);
        dialog.setMinDate(now);
        now.setTimeInMillis(time);
        Log.e("calendar 2", String.valueOf(now));
        dialog.setMinDate(now);
    }

    private String singleToDoubleDigitSet(int value) {
        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        myear = year;
        mmonth = monthOfYear;
        mday = dayOfMonth;
        updateDisplay();
    }
}
