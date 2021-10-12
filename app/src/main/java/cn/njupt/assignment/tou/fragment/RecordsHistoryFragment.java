package cn.njupt.assignment.tou.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import cn.njupt.assignment.tou.adapter.HistoryAdapter;
import cn.njupt.assignment.tou.entity.HistoryList;
import cn.njupt.assignment.tou.entity.HistoryRecord;
import cn.njupt.assignment.tou.viewmodel.HistoryRecordViewModel;

public class RecordsHistoryFragment extends Fragment {

    private static final String TAG = RecordsHistoryFragment.class.getSimpleName();
    private View mView;

    RecyclerView recyclerView;
    HistoryRecordViewModel historyRecordViewModel;
    HistoryAdapter historyAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView  = inflater.inflate(R.layout.fragment_dialog_records_history, container, false);

        historyRecordViewModel = new ViewModelProvider(this).get(HistoryRecordViewModel.class);

        recyclerView = mView.findViewById(R.id.list_history_new);

        initData();

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

        return mView;
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
