package cn.njupt.assignment.tou.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.activity.HomeActivity;
import cn.njupt.assignment.tou.adapter.HistoryAdapter;
import cn.njupt.assignment.tou.callback.RecordsHistoryCallBackListener;
import cn.njupt.assignment.tou.entity.HistoryList;
import cn.njupt.assignment.tou.entity.HistoryRecord;
import cn.njupt.assignment.tou.utils.ToastUtil;
import cn.njupt.assignment.tou.viewmodel.HistoryRecordViewModel;

public class RecordsHistoryFragment extends Fragment {

    private static final String TAG = RecordsHistoryFragment.class.getSimpleName();
    private View mView;

    private RecyclerView recyclerView;
    private HistoryRecordViewModel historyRecordViewModel;
    private HistoryAdapter historyAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView  = inflater.inflate(R.layout.fragment_dialog_records_history, container, false);
        }

        // bind and initialize
        historyRecordViewModel = new ViewModelProvider(this).get(HistoryRecordViewModel.class);
        recyclerView = mView.findViewById(R.id.list_history_new);

        initData();
        recyclerView.setNestedScrollingEnabled(false);

        //添加RecyclerView管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        Log.i(TAG, "onCreateView: requireContext() = "+requireContext());

        //添加适配器
        Log.i(TAG, sortForList(historyRecordViewModel.getAll()).get(0).getTime());
        historyAdapter = new HistoryAdapter(getContext(),sortForList(historyRecordViewModel.getAll()));
        recyclerView.setAdapter(historyAdapter);

        //实时更新recycler view的状态并展示出来
        historyRecordViewModel.getHistoryRecordAll().observe(getViewLifecycleOwner(), historyRecords -> {
            Collections.sort(historyRecords);
            historyAdapter.setHistoryRecords(historyRecords);
            historyAdapter.setAllRecords(sortForList(historyRecords));

        //刷新视图
        historyAdapter.notifyDataSetChanged();
        });

        //长按事件
        historyAdapter.setOnItemLongClickListener((view, position) -> {
            onLongClickShow(view,position);
        });

        //点击事件
        onClickShow();

        // 回调函数，当点击 ParentFragment 中的 Button 时，触发清除历史记录操作
        RecordsInDialogFragment.setHistoryCallBackListener(new RecordsHistoryCallBackListener() {
            @Override
            public void onHistoryButtonClick(View view) {
                Log.i(TAG, "onButtonClick: 我不好我不好我不好");
                // View当前PopupMenu显示的相对View的位置
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                // menu布局
                popupMenu.getMenuInflater().inflate(R.menu.history_edit, popupMenu.getMenu());
                // menu的item点击事件
                popupMenu.setOnMenuItemClickListener(item -> {
                    //点击逻辑
                    switch (item.getItemId()){
                        case R.id.remove_today_history:
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();
                            String time = simpleDateFormat.format(date);
                            historyRecordViewModel.deleteTodayHistory(time);
                            break;
                        case R.id.remove_all_history:
                            historyRecordViewModel.deleteAll();
                            break;
                        default:
                            break;
                    }
                    return false;
                });

                // PopupMenu关闭事件
                popupMenu.setOnDismissListener(menu -> {
                    //关闭后的逻辑
                    /*Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT).show();*/
                });
                popupMenu.show();
            }
        });

        return mView;
    }


    /**
     * @description 点击清除历史记录按键触发事件
     * @param view
     * @param position
     * @return
     * @author sherman
     * @time 2021/10/12 16:25
     */
    public void onLongClickShow(View view, int position){
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.history_delete, popupMenu.getMenu());

        //弹出式菜单的菜单项点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            historyAdapter.notifyItemRemoved(position);
            TextView textView;
            switch (item.getItemId()) {
                case R.id.removeItem:
                    historyAdapter.notifyItemRemoved(position);
                    textView = view.findViewById(R.id.list_history_id);
                    historyRecordViewModel.deleteOneHistoryRecord(
                            Integer.parseInt(textView.getText().toString())
                    );
                    break;
                case R.id.historyCopyItem:
                    historyAdapter.notifyDataSetChanged();
                    ClipboardManager clip = (ClipboardManager) getActivity().getSystemService((Context.CLIPBOARD_SERVICE));
                    textView = view.findViewById(R.id.list_history_url);
                    ClipData clipData = ClipData.newPlainText(null, textView.getText().toString());
                    clip.setPrimaryClip(clipData);
                    ToastUtil.shortToast(getContext(),"网址已复制到粘贴板");
                    break;
                default:
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    /**
     * 单击item触发事件
     */
    private void onClickShow(){
        historyAdapter.setOnItemClickListener((view, section, position) -> {
            TextView url = view.findViewById(R.id.list_history_url);
            Intent intent = new Intent(getContext(), HomeActivity.class);
            intent.putExtra("history_url",url.getText().toString());
            startActivity(intent);
        });
    }

    private void clickHistoryEdit(View view) {
        System.out.println("========================");
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.history_edit, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            //点击逻辑
            switch (item.getItemId()){
                case R.id.remove_today_history:
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String time = simpleDateFormat.format(date);
                    historyRecordViewModel.deleteTodayHistory(time);
                    break;
                case R.id.remove_all_history:
                    historyRecordViewModel.deleteAll();
                    break;
                default:
                    break;
            }
            return false;
        });

        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(menu -> {
            //关闭后的逻辑
            /*Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT).show();*/
        });
        popupMenu.show();
    }


    /**
     * 将拿到的数据进行排序，排好序后进行分组，再对分组进行排序，最后返回一个有序的分组
     * @param beans
     * @return
     */
    private List<HistoryList> sortForList(List<HistoryRecord> beans){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String today = simpleDateFormat.format(date);

        List<HistoryList> groups = new LinkedList<>();      /*排好序的分组*/
        if (beans != null && beans.size()!=0) {
            HistoryList recordOfGroup = new HistoryList();    /*某一天的分组*/
            String time = beans.get(0).getHdate();      /*某一天的历史时间*/
            //对每个排好序的记录进行分组
            for (HistoryRecord h : beans) {
                if (time.equals(h.getHdate())){
                    //时间同一天，则放入同一组中
                    recordOfGroup.getListOfDay().add(h);
                }else {
                    if (!time.equals("")){
                        //时间不同，则将上一组存入groups中
                        if (time.equals(today)){
                            //加上今天的标识
                            recordOfGroup.setTimeToday(time);
                            groups.add(recordOfGroup);
                        }else {
                            recordOfGroup.setTimeAndSort(time);
                            groups.add(recordOfGroup);
                        }
                    }
                    //新建一组来存另一天的历史记录
                    recordOfGroup = new HistoryList();
                    //将该历史记录添入新的分组
                    time = h.getHdate();
                    recordOfGroup.getListOfDay().add(h);
                }
            }
            if (!time.equals("")){
                //时间不同，则将上一组存入groups中
                System.out.println("<<<<<<"+time);
                if (time.equals(today)){
                    //加上今天的标识
                    recordOfGroup.setTimeToday(time);
                }else {
                    recordOfGroup.setTimeAndSort(time);
                }
                groups.add(recordOfGroup);
            }
        }
        //将分组排序好
        Collections.sort(groups);
        return groups;
    }




    private void initData() {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();

            historyRecordViewModel.insertHistoryRecord("哔哩哔哩 (゜-゜)つロ 干杯~-bilibili官方","https://www.bilibili.com/","https://www.bilibili.com/favicon.ico",simpleDateFormat.parse("2021-07-18"));
            historyRecordViewModel.insertHistoryRecord("墨刀 - 百度","https://www.baidu.com/s?wd=%E5%A2%A8%E5%88%80","https://www.baidu.com/favicon.ico",simpleDateFormat.parse("2021-07-18"));
            historyRecordViewModel.insertHistoryRecord("墨刀-是一款在线原型设计与远程协作平台","https://modao.cc/","https://modao.cc/favicon.ico",simpleDateFormat.parse("2021-07-18"));
            historyRecordViewModel.insertHistoryRecord("Github","https://github.com/","https://github.com/favicon.ico",simpleDateFormat.parse("2021-07-18"));

            historyRecordViewModel.insertHistoryRecord("Github","https://github.com/","https://github.com/favicon.ico",simpleDateFormat.parse("2021-07-19"));
            historyRecordViewModel.insertHistoryRecord("墨刀 - 远程办公好帮手 在线产品原型设计与协作平台","https://modao.cc/embed/auth_box?type=signup","https://modao.cc/favicon.ico",simpleDateFormat.parse("2021-07-19"));
            historyRecordViewModel.insertHistoryRecord("哔哩哔哩 (゜-゜)つロ 干杯~-bilibili官方","https://www.bilibili.com/","https://www.bilibili.com/favicon.ico",simpleDateFormat.parse("2021-07-19"));
            historyRecordViewModel.insertHistoryRecord("墨刀 - 远程办公好帮手 在线产品原型设计与协作平台","https://modao.cc/embed/auth_box?type=signup","https://modao.cc/favicon.ico",simpleDateFormat.parse("2021-07-19"));

            historyRecordViewModel.insertHistoryRecord("墨刀-是一款在线原型设计与远程协作平台","https://modao.cc/","https://modao.cc/favicon.ico",simpleDateFormat.parse("2021-07-20"));
            historyRecordViewModel.insertHistoryRecord("墨刀 - 远程办公好帮手 在线产品原型设计与协作平台","https://modao.cc/embed/auth_box?type=signup","https://modao.cc/favicon.ico",simpleDateFormat.parse("2021-07-20"));
            historyRecordViewModel.insertHistoryRecord("哔哩哔哩 (゜-゜)つロ 干杯~-bilibili官方","https://www.bilibili.com/","https://www.bilibili.com/favicon.ico",simpleDateFormat.parse("2021-07-20"));
            historyRecordViewModel.insertHistoryRecord("墨刀 - 远程办公好帮手 在线产品原型设计与协作平台","https://modao.cc/embed/auth_box?type=signup","https://modao.cc/favicon.ico",simpleDateFormat.parse("2021-07-20"));

            historyRecordViewModel.insertHistoryRecord("墨刀-是一款在线原型设计与远程协作平台","https://modao.cc/","https://modao.cc/favicon.ico",simpleDateFormat.parse("2021-07-21"));
            historyRecordViewModel.insertHistoryRecord("Github","https://github.com/","https://github.com/favicon.ico",simpleDateFormat.parse("2021-07-21"));
            historyRecordViewModel.insertHistoryRecord("墨刀-是一款在线原型设计与远程协作平台","https://modao.cc/","https://modao.cc/favicon.ico",simpleDateFormat.parse("2021-07-21"));
            historyRecordViewModel.insertHistoryRecord("哔哩哔哩 (゜-゜)つロ 干杯~-bilibili官方","https://www.bilibili.com/","https://www.bilibili.com/favicon.ico",simpleDateFormat.parse("2021-07-21"));
            historyRecordViewModel.insertHistoryRecord("墨刀-是一款在线原型设计与远程协作平台","https://modao.cc/","https://modao.cc/favicon.ico",simpleDateFormat.parse("2021-07-21"));

            historyRecordViewModel.insertHistoryRecord("墨刀 - 百度","https://www.baidu.com/s?wd=%E5%A2%A8%E5%88%80","https://www.baidu.com/favicon.ico",simpleDateFormat.parse(simpleDateFormat.format(date)));
            historyRecordViewModel.insertHistoryRecord("墨刀-是一款在线原型设计与远程协作平台","https://modao.cc/","https://modao.cc/favicon.ico",simpleDateFormat.parse(simpleDateFormat.format(date)));
            historyRecordViewModel.insertHistoryRecord("Github","https://github.com/","https://github.com/favicon.ico",simpleDateFormat.parse(simpleDateFormat.format(date)));

        }catch (ParseException e){
            System.out.println("插入测试数据失败");
        }
    }


}
