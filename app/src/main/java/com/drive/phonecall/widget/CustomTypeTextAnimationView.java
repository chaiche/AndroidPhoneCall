package com.drive.phonecall.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

@SuppressWarnings("FieldCanBeLocal")
public class CustomTypeTextAnimationView extends android.support.v7.widget.AppCompatTextView {

    private String mShowText;

    private long mDelay = 100;

    private AnimationDownListener mAnimationDownListener;

    public CustomTypeTextAnimationView(Context context) {
        super(context);
    }

    public CustomTypeTextAnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTypeTextAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOneWordDelay(long delay){
        this.mDelay = delay;
    }

    public void startPlayText(String text) {
        startPlayText(text, null);
    }

    public void startPlayText(String text, AnimationDownListener listener) {
        this.mAnimationDownListener = listener;

        if(TextUtils.isEmpty(text)){
            text = "";
        }

        setText(null);
        removeCallbacks(mRb);
        removeCallbacks(mDoneRb);

        mShowText = text;
        post(mRb);
    }

    private Runnable mRb = new Runnable() {
        @Override
        public void run() {
            int currentLength = getText().toString().length();
            if (currentLength < mShowText.length()) {
                setText(mShowText.substring(0, currentLength + 1));
                postDelayed(mRb, mDelay);
            } else {
                postDelayed(mDoneRb, mDelay);
            }
        }
    };

    private Runnable mDoneRb = new Runnable() {
        @Override
        public void run() {
            if (mAnimationDownListener != null) {
                mAnimationDownListener.done();
            }
        }
    };


    public interface AnimationDownListener {
        void done();
    }
}