package cn.njupt.assignment.tou.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.adapter.AutoMatchingAdapter;
import cn.njupt.assignment.tou.entity.HistoryRecord;
import cn.njupt.assignment.tou.viewmodel.HistoryRecordViewModel;

public class AutoMatchingActivity extends AppCompatActivity {

    HistoryRecordViewModel historyRecordViewModel;

    EditText historySearch;

    TextView cancelSearch;

    RelativeLayout historyPage;

    AutoMatchingAdapter autoMatchingAdapter;

    RecyclerView recordOfSearch;

    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_matching);
        historyRecordViewModel = new ViewModelProvider(this).get(HistoryRecordViewModel.class);

        historySearch = findViewById(R.id.history_page_search);
        cancelSearch = findViewById(R.id.history_page_cancel);
        historyPage = findViewById(R.id.history_search_page);
        recordOfSearch = findViewById(R.id.history_search_page_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recordOfSearch.setLayoutManager(linearLayoutManager);


        autoMatchingAdapter = new AutoMatchingAdapter(this);
        recordOfSearch.setAdapter(autoMatchingAdapter);

        historySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println("--------------"+s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("+++++++++++++++++++++++++++++++++:"+s.toString());
                if (s.toString().equals("")||s.toString()==null){
                    autoMatchingAdapter.clearHistoryRecords();
                }else{
                    historyRecordViewModel.getFuzzySearchInfo(s.toString()).observe(AutoMatchingActivity.this,historyRecords -> {
//                        HistoryRecord record = historyRecords.get(0);
//                        historySearch.setText(record.getHurl());
//                        Selection.setSelection(historySearch.getText(),historySearch.length());
                        Collections.sort(historyRecords);
                        autoMatchingAdapter.setHistoryRecords(historyRecords);
                    });
                }
                //刷新视图
                autoMatchingAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                historySearch.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
                        disableShowInput(historySearch);
                    }
                    return false;
                });
            }
        });

        cancelSearch.setOnClickListener(v->{
            disableShowInput(historySearch);
            finish();
        });

        //点击跳转
        autoMatchingAdapter.setOnItemClickListener((view, position) -> {
                TextView url = view.findViewById(R.id.Search_list_history_url);
                Intent intent = new Intent(AutoMatchingActivity.this, HomeActivity.class);
                intent.putExtra("history_url",url.getText().toString());
                startActivity(intent);
        });

        //点击非输入框部分隐藏键盘
        historyPage.setOnClickListener(v -> {
            if (v.getId() == R.id.history_page) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        showInput(historySearch);

        //显示出搜索内容
        content = historySearch.getText().toString();
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