package cn.njupt.assignment.tou.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Collections;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.adapter.TestAdapter;
import cn.njupt.assignment.tou.utils.FileUtil;
import cn.njupt.assignment.tou.utils.OkHttpUtil;
import cn.njupt.assignment.tou.viewmodel.HistoryRecordViewModel;

public class TestActivity extends AppCompatActivity {

    AutoCompleteTextView textView;
    HistoryRecordViewModel historyRecordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        initView();
    }

    private void initView() {
        historyRecordViewModel = new ViewModelProvider(this).get(HistoryRecordViewModel.class);
        textView = findViewById(R.id.auto_searchBar);
        TestAdapter adapter = new TestAdapter(this);
        textView.setAdapter(adapter);
        textView.setThreshold(2);

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println("--------------" + s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("+++++++++++++++++++++++++++++++++:" + s.toString());
                if (s.toString().equals("") || s.toString() == null) {
                    adapter.clearHistoryRecords();
                } else {
                    historyRecordViewModel.getFuzzySearchInfo(s.toString()).observe(TestActivity.this, historyRecords -> {
                        Collections.sort(historyRecords);
                        adapter.setHistoryRecords(historyRecords);
                    });
                }
                //刷新视图
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                textView.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                        disableShowInput(textView);
                    }
                    return false;
                });
            }
        });


        showInput(textView);
    }


    public static void showInput(final EditText et) {
        et.post(() -> {
            et.requestFocus();
            InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        });
    }

    public static void disableShowInput(EditText view) {
        //关闭输入法键盘，如果需要
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}