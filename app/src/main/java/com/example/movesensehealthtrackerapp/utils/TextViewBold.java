package com.example.movesensehealthtrackerapp.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

public class TextViewBold extends AppCompatTextView {
    private Context context;
    private AttributeSet attrs;

    public TextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        applyFont();
    }

    private void applyFont(){
        Typeface t = Typeface.createFromAsset(context.getAssets(), "Montserrat-Bold.ttf");
        setTypeface(t);
    }
}
