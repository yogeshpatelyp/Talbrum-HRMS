package com.talentcerebrumhrms.datatype;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by Harshit on 21-Jun-17.
 */

public class ChartValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public ChartValueFormatter() {
        mFormat = new DecimalFormat("#"); // use no decimal
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (value == 0)
            return "";
        else if (Math.ceil(value) == value)
            return String.valueOf(mFormat.format(value));
        else
            return String.valueOf(value);
    }
}
