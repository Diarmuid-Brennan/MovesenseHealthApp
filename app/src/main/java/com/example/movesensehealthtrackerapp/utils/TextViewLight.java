package com.example.movesensehealthtrackerapp.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class TextViewLight extends AppCompatTextView {
    private Context context;
    private AttributeSet attrs;

    public TextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        applyFont();
    }

    private void applyFont(){
        Typeface t = Typeface.createFromAsset(context.getAssets(), "Montserrat-Regular.ttf");
        setTypeface(t);
    }
}
