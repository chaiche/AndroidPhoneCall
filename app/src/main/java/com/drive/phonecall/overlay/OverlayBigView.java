package com.drive.phonecall.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.drive.phonecall.R;

public class OverlayBigView {

    private Context mContext;
    private View mView;

    private TextView mTxvWhich;
    private TextView mTxvStatus;
    private TextView mTxvName;
    private Button mBtnReject;
    private Button mBtnAccept;
    private Button mBtnSmall;
    private ImageView mIgvIcon;

    public OverlayBigView(Context context) {
        this.mContext = context;

        initialView();
    }

    private void initialView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.overlay_big, null);

        mTxvWhich = view.findViewById(R.id.txv_which);
        mTxvName = view.findViewById(R.id.txv_name);
        mTxvStatus = view.findViewById(R.id.txv_status);

        mBtnReject = view.findViewById(R.id.btn_reject);
        mBtnAccept = view.findViewById(R.id.btn_accept);
        mBtnSmall = view.findViewById(R.id.btn_small);

        mIgvIcon = view.findViewById(R.id.igv_back);

        mView = view;
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
        mIgvIcon.setImageBitmap(bitmap);
    }

    public void setUseReject(boolean b, View.OnClickListener listener){
        if(b){
            mBtnReject.setVisibility(View.VISIBLE);
        } else {
            mBtnReject.setVisibility(View.GONE);
        }

        mBtnReject.setOnClickListener(listener);
    }

    public void setUseAccept(boolean b, View.OnClickListener listener){
        if(b){
            mBtnAccept.setVisibility(View.VISIBLE);
        } else {
            mBtnAccept.setVisibility(View.GONE);
        }

        mBtnAccept.setOnClickListener(listener);
    }

    public void setSmallClick(View.OnClickListener listener){
        mBtnSmall.setOnClickListener(listener);
    }
}
