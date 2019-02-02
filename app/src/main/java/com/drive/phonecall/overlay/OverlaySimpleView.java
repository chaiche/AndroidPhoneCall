package com.drive.phonecall.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.drive.phonecall.R;

public class OverlaySimpleView{

    private Context mContext;
    private View mView;

    private TextView mTxvWhich;
    private TextView mTxvStatus;
    private TextView mTxvName;

    public OverlaySimpleView(Context context) {
        this.mContext = context;

        initialView();
    }

    private void initialView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.overlay_simple, null);

        mTxvWhich = view.findViewById(R.id.txv_which);
        mTxvName = view.findViewById(R.id.txv_name);
        mTxvStatus = view.findViewById(R.id.txv_status);

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

    public void setViewClickListener(View.OnClickListener listener) {
        mView.setOnClickListener(listener);
    }
}
