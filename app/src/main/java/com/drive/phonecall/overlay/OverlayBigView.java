package com.drive.phonecall.overlay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drive.phonecall.R;

public class OverlayBigView {

    private Context mContext;
    private View mView;

    private TextView mTxvWhich;
    private TextView mTxvStatus;
    private TextView mTxvName;
    private ImageView mIgvAccept;
    private LinearLayout mLinAccept;
    private ImageView mIgvReject;
    private LinearLayout mLinReject;
    private ImageView mIgvBack;

    public OverlayBigView(Context context) {
        this.mContext = context;

        initialView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initialView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.overlay_big, null);

        mTxvWhich = view.findViewById(R.id.txv_which);
        mTxvName = view.findViewById(R.id.txv_name);
        mTxvStatus = view.findViewById(R.id.txv_status);

        mLinAccept = view.findViewById(R.id.lin_accept);
        mIgvAccept = view.findViewById(R.id.igv_accept);
        mLinReject = view.findViewById(R.id.lin_reject);
        mIgvReject = view.findViewById(R.id.igv_reject);

        mIgvBack = view.findViewById(R.id.igv_back);

        mView = view;

        mIgvReject.setOnTouchListener(new View.OnTouchListener() {
            private Rect rect;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mIgvReject.getBackground().setColorFilter(Color.argb(50, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mIgvReject.getBackground().setColorFilter(Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);;
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                        mIgvReject.getBackground().setColorFilter(Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);;
                    }
                }
                return false;
            }
        });

        mIgvAccept.setOnTouchListener(new View.OnTouchListener() {
            private Rect rect;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mIgvAccept.getBackground().setColorFilter(Color.argb(50, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mIgvAccept.getBackground().setColorFilter(Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())){
                        mIgvAccept.getBackground().setColorFilter(Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
                    }
                }
                return false;
            }
        });
    }


    public View getView(){
        return mView;
    }

    public void setName(String name){
        mTxvName.setText(name);
    }

    public void setWhich(String which){
        mTxvWhich.setText(which);
    }

    public void setStatus(String status){
        mTxvStatus.setText(status);
    }

    public void setIcon(Bitmap bitmap){

    }

    public void setUseReject(boolean b, View.OnClickListener listener){
        if(b){
            mLinReject.setVisibility(View.VISIBLE);
        } else {
            mLinReject.setVisibility(View.GONE);
        }

        mIgvReject.setOnClickListener(listener);
    }

    public void setUseAccept(boolean b, View.OnClickListener listener){
        if(b){
            mLinAccept.setVisibility(View.VISIBLE);
        } else {
            mLinAccept.setVisibility(View.GONE);
        }

        mIgvAccept.setOnClickListener(listener);
    }

    public void setSmallClick(View.OnClickListener listener){
        mIgvBack.setOnClickListener(listener);
    }
}
