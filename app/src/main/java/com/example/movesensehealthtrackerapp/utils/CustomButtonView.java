package com.example.movesensehealthtrackerapp.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

public class CustomButtonView extends AppCompatButton {

    private Context context;
    private AttributeSet attrs;

    public CustomButtonView(@NonNull Context context, @Nullable AttributeSet attrs) {
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
