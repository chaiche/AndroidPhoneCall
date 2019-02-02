package com.drive.phonecall.overlay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class OverlayView {

    private Context mContext;
    private WindowManager mManager;
    private WindowManager.LayoutParams mSimpleParams;
    private OverlaySimpleView mSimpleView;

    private WindowManager.LayoutParams mBigParams;
    private OverlayBigView mBigView;

    private int mScreenWidth, mScreenHeight;

    public static final int CONTROL_SIMPLE = 1;
    public static final int CONTROL_BIG = 2;

    private int mControlView = CONTROL_SIMPLE;

    public OverlayView(Context context) {
        this.mContext = context;

        mManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        calWindow();
        createSimpleView();
        createBigView();
    }

    private void calWindow() {
        Display display = mManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;
    }

    public void createSimpleView(){
        mSimpleParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mSimpleParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            mSimpleParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mSimpleParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        mSimpleParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        mSimpleParams.format = PixelFormat.RGBA_8888;
        mSimpleParams.gravity = Gravity.START | Gravity.TOP;

        mSimpleParams.x = 0;
        mSimpleParams.y = mScreenHeight / 2 / 2;
        mSimpleParams.width = mScreenWidth / 3;
        mSimpleParams.height = mScreenHeight / 5;

        mSimpleView = new OverlaySimpleView(mContext);

        mSimpleView.setViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSimpleView();
                showBigView();
            }
        });

    }

    public void createBigView(){
        mBigParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBigParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            mBigParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mBigParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        mBigParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        mBigParams.format = PixelFormat.RGBA_8888;
        mBigParams.gravity = Gravity.START | Gravity.TOP;

        mBigParams.x = 0;
        mBigParams.y = 0;

        mBigParams.width = mScreenWidth;
        mBigParams.height = mScreenHeight;

        mBigView = new OverlayBigView(mContext);

        mBigView.setSmallClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeBigView();
                showSimpleView();
            }
        });
    }

    public void setUi(String which,
                      String name,
                      String status){
        mSimpleView.setWhich(which);
        mSimpleView.setName(name);
        mSimpleView.setStatus(status);

        mBigView.setWhich(which);
        mBigView.setName(name);
        mBigView.setStatus(status);
    }

    public void setAcceptControl(boolean useAccept, View.OnClickListener listener){
        mBigView.setUseAccept(useAccept, listener);
    }

    public void setRejectControl(boolean useReject, View.OnClickListener listener){
        mBigView.setUseReject(useReject, listener);
    }

    public void showSimpleView(){
        if (mManager != null && mSimpleView != null && mSimpleView.getView().getWindowToken() == null) {
            mManager.addView(mSimpleView.getView(), mSimpleParams);
        }
    }

    public void removeSimpleView() {
        if (mManager != null && mSimpleView.getView() != null && mSimpleView.getView().getWindowToken() != null) {
            mManager.removeView(mSimpleView.getView());
        }
    }

    public void showBigView(){
        if (mManager != null && mBigView != null && mBigView.getView().getWindowToken() == null) {
            mManager.addView(mBigView.getView(), mBigParams);
        }
    }

    public void removeBigView(){
        if (mManager != null && mBigView.getView() != null && mBigView.getView().getWindowToken() != null) {
            mManager.removeView(mBigView.getView());
        }
    }

    public void show() {
        showBigView();
    }

    public void hide() {
        removeBigView();
        removeSimpleView();
    }


    public void onDestroy() {
        hide();
    }
}
