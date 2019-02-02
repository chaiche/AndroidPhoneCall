package com.drive.phonecall.activity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drive.phonecall.BaseActivity;
import com.drive.phonecall.R;
import com.drive.phonecall.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class QAActivity extends BaseActivity {

    private RecyclerView mRv;
    private Toolbar mTb;

    @Override
    protected void initial(Bundle savedInstanceState) {

        mTb = findViewById(R.id.toolbar);

        setSupportActionBar(mTb);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mRv = findViewById(R.id.rv);

        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.viewType = RecyclerViewAdapter.VIEW_TYPE_Q;
        item.text = "如何使用這個軟體？";
        items.add(item);

        item = new Item();
        item.viewType = RecyclerViewAdapter.VIEW_TYPE_A;
        item.text = "開啟APP後，點選啟動服務。當有人使用電話或FB或LINE打電話來時，會顯示接聽介面，即可使用。";
        items.add(item);

        item = new Item();
        item.viewType = RecyclerViewAdapter.VIEW_TYPE_Q;
        item.text = "使用這個軟體是透過什麼方式來接聽電話的？";
        items.add(item);

        item = new Item();
        item.viewType = RecyclerViewAdapter.VIEW_TYPE_A;
        item.text = "通知欄中有接聽掛斷的按鈕是使用該方式來實現，故欲使用此軟體請把FB和LINE通知打開。";
        items.add(item);

        item = new Item();
        item.viewType = RecyclerViewAdapter.VIEW_TYPE_Q;
        item.text = "明明有顯示通知，為什麼都沒有接聽介面？";
        items.add(item);

        item = new Item();
        item.viewType = RecyclerViewAdapter.VIEW_TYPE_A;
        item.text = "可以試著重啟動程式試試，如果再無接聽介面，煩請重新開機嘗試。";
        items.add(item);

        item = new Item();
        item.viewType = RecyclerViewAdapter.VIEW_TYPE_Q;
        item.text = "服務啟動後，是否可以關閉程式？";
        items.add(item);

        item = new Item();
        item.viewType = RecyclerViewAdapter.VIEW_TYPE_A;
        item.text = "可以，如欲關閉服務，可以點選程式中關閉服務按鈕，或者在通知欄點選關閉服務。";
        items.add(item);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter();
        adapter.setItems(items);

        int dp8 = ViewUtils.dp2px(this, 8);
        mRv.setPadding(dp8, dp8, dp8, dp8);
        mRv.setClipToPadding(false);

        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(adapter);
    }

    @Override
    protected View onCreateView() {
        return View.inflate(this, R.layout.activity_qa, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int VIEW_TYPE_Q = 1;
        public static final int VIEW_TYPE_A = 2;
        private List<Item> mItems;

        class AnswerViewHolder extends RecyclerView.ViewHolder {

            TextView txv;
            AnswerViewHolder(LinearLayout lin) {
                super(lin);

                txv = new TextView(lin.getContext());
                txv.setTextSize(24);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END | Gravity.CENTER;
                txv.setLayoutParams(params);
                lin.addView(txv);
            }
        }

        class QuestionViewHolder extends RecyclerView.ViewHolder {

            TextView txv;
            QuestionViewHolder(LinearLayout lin) {
                super(lin);

                txv = new TextView(lin.getContext());
                txv.setTextSize(32);
                txv.setTypeface(null, Typeface.BOLD);
                lin.addView(txv);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType){
                case VIEW_TYPE_A:
                    LinearLayout lin = new LinearLayout(parent.getContext());
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    int dp24 = ViewUtils.dp2px(parent.getContext(), 24);
                    params.setMargins(dp24, 0, 0, 0);
                    lin.setLayoutParams(params);
                    return new AnswerViewHolder(lin);
            }

            LinearLayout lin = new LinearLayout(parent.getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int dp12 = ViewUtils.dp2px(parent.getContext(), 12);
            params.setMargins(dp12, dp12, dp12, dp12);
            lin.setLayoutParams(params);
            return new QuestionViewHolder(lin);
        }

        @SuppressLint("CheckResult")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof AnswerViewHolder) {
                AnswerViewHolder holder = (AnswerViewHolder) viewHolder;
                holder.txv.setText(mItems.get(position).text);
            } else if (viewHolder instanceof QuestionViewHolder) {
                QuestionViewHolder holder = (QuestionViewHolder) viewHolder;
                holder.txv.setText(mItems.get(position).text);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mItems.get(position).viewType;
        }

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.size();
        }

        private void setItems(List<Item> items) {
            this.mItems = items;
        }
    }

    private class Item{
        int viewType;
        String text;
    }
}
