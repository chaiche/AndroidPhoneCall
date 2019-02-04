package com.drive.phonecall.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.drive.phonecall.BaseActivity;
import com.drive.phonecall.R;
import com.drive.phonecall.call.CallService;
import com.drive.phonecall.data.SpData;
import com.drive.phonecall.utils.LanguageUtils;
import com.drive.phonecall.utils.SystemUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.btn_control_service) Button mBtnControlService;
    @BindView(R.id.sp_language) Spinner mSpinnerLanguage;

    private CallStateReceive mCallStateReceive;
    private SpData mSpData;

    @Override
    protected void initial(Bundle savedInstanceState) {

        initView();
    }

    private void initView(){
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(
                this, R.array.language_array, android.R.layout.simple_spinner_item );
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLanguage.setAdapter(languageAdapter);

        mSpData = SpData.getInstance(this);

        String language = LanguageUtils.getDefaultLanguage(this);
        if (language.contains("en")) {
            mSpinnerLanguage.setSelection(1, false);
        } else {
            mSpinnerLanguage.setSelection(0, false);
        }

        mSpinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String language = "zh-TW";
                switch (i){
                    case 1:
                        language = "en";
                        break;
                }

                LanguageUtils.setLanguage(MainActivity.this, language);
                mSpData.putStringValue(SpData.LANGUAGE, language);

                Intent intentLanguage = new Intent(LanguageUtils.ACTION_CHANGE_LANGUAGE);
                sendBroadcast(intentLanguage);

                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected View onCreateView() {
        return View.inflate(this, R.layout.activity_main, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        mCallStateReceive = new CallStateReceive();
        registerReceiver(mCallStateReceive, new IntentFilter(CallService.ACTION_SERVICE_STATE_CHANGE));

        boolean isRunning = SystemUtils.isServiceRunning(this, CallService.class);
        changeState(isRunning);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCallStateReceive != null) {
            unregisterReceiver(mCallStateReceive);
            mCallStateReceive = null;
        }
    }

    private class CallStateReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (CallService.ACTION_SERVICE_STATE_CHANGE.equals(intent.getAction())) {
                final boolean isUp = intent.getBooleanExtra(CallService.EXTRA_SERVICE_STATE_CHANGE, false);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeState(isUp);
                    }
                });
            }
        }
    }

    private void changeState(boolean isUp) {
        mBtnControlService.setEnabled(true);

        if (isUp) {
            mBtnControlService.setText(R.string.disable_service);
            mBtnControlService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBtnControlService.setEnabled(false);

                    Intent it = new Intent(MainActivity.this, CallService.class);
                    stopService(it);
                }
            });


        } else {
            mBtnControlService.setText(R.string.enable_service);
            mBtnControlService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBtnControlService.setEnabled(false);

                    Intent it = new Intent(MainActivity.this, CallService.class);
                    startService(it);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
