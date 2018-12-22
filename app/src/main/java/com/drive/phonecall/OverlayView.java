package com.drive.phonecall;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class OverlayView {

    private Context mContext;
    private WindowManager mManager;
    private WindowManager.LayoutParams mContentParams;
    private ScrollView mScrollView;
    private LinearLayout mContent;
    private int mScreenWidth, mScreenHeight;

    public OverlayView(Context context) {
        this.mContext = context;

        createView();
    }

    private void calWindow() {
        Display display = mManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;
    }

    private void createView() {
        mManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        calWindow();

        mContentParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContentParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            mContentParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mContentParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        mContentParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        mContentParams.format = PixelFormat.RGBA_8888;
        mContentParams.gravity = Gravity.START | Gravity.TOP;

        mContentParams.x = mScreenWidth / 2;
        mContentParams.x = 0;
        mContentParams.y = mScreenHeight / 2 / 2;
        mContentParams.width = mScreenWidth / 3;
        mContentParams.height = mScreenHeight / 2;

        mScrollView = new ScrollView(mContext);
        mScrollView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mContent = new LinearLayout(mContext);
        mContent.setOrientation(LinearLayout.VERTICAL);
        mContent.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContent.setBackgroundColor(Color.RED);
        mScrollView.addView(mContent);
    }

    public Button addButton(String text) {
        Button btn = new Button(mContext);
        btn.setText(text);
        mContent.addView(btn);
        return btn;
    }

    public TextView addTextView() {
        TextView txv = new TextView(mContext);
        txv.setTextColor(Color.BLACK);
        mContent.addView(txv);
        return txv;
    }

    public void show() {
        if (mManager != null && mScrollView.getWindowToken() == null) {
            mManager.addView(mScrollView, mContentParams);
        }
    }

    public void hide() {
        if (mManager != null && mScrollView != null && mScrollView.getWindowToken() != null) {
            mManager.removeView(mScrollView);
        }
    }


    public void onDestroy() {
        hide();
    }
}
