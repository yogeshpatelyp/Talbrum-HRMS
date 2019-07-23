package com.talentcerebrumhrms.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by saransh on 21-03-2016.
 */
class FontsOverride {

    static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    private static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
