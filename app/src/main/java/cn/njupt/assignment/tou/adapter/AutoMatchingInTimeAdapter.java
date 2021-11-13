package cn.njupt.assignment.tou.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.entity.HistoryRecord;

/**
 * @author: sherman
 * @date: 2021/10/10
 * @description: 实时自动匹配适配器
 */
public class AutoMatchingInTimeAdapter extends BaseAdapter implements Filterable {

    private List<HistoryRecord> data;

    //历史记录
    private List<HistoryRecord> historyRecords;

    //上下文
    private Context context;

    //设置item点击监听器，在HomeActivity中进行回调
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view , int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener(){
        return this.onItemClickListener;
    }


    public AutoMatchingInTimeAdapter(Context context){
        this.context = context;
    }

    public AutoMatchingInTimeAdapter(@NonNull Context context, List<HistoryRecord> records) {
        this.context = context;
        historyRecords = records;
    }

    public void setHistoryRecords(List<HistoryRecord> historyRecords){
        this.historyRecords = historyRecords;
    }

    //清除历史记录
    public void clearHistoryRecords(){
        if (this.historyRecords!=null&&this.historyRecords.size()>0)
            this.historyRecords.clear();
        this.notifyDataSetChanged();
    }

    //获取item数量
    @Override
    public int getCount() {
        if (historyRecords == null){
            return 0;
        }else{
            return historyRecords.size();
        }
    }

    //获取指定位置的item
    @Override
    public Object getItem(int position) {
        if (historyRecords == null) {
            return null;
        } else {
            return historyRecords.get(position);
        }
    }

    //获取item的id
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryRecord record = historyRecords.get(position);
        ViewHolder viewHolder;
        View view;

        if (convertView == null){
            view = View.inflate(context,R.layout.layout_auto_matching_list_unit,null);
            //用viewHolder存储控件信息
            viewHolder = new ViewHolder();
            viewHolder.image = view.findViewById(R.id.Search_list_history_image);
            viewHolder.name = view.findViewById(R.id.Search_list_history_name);
            viewHolder.url = view.findViewById(R.id.Search_list_history_url);
            viewHolder.id = view.findViewById(R.id.Search_list_history_id);
            viewHolder.layout = view.findViewById(R.id.Search_list_history_item);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        //绑定数据
        Glide.with(viewHolder.image.getContext()).load(record.getHicon()).into(viewHolder.image);
        viewHolder.name.setText(record.getHname());
        viewHolder.url.setText(record.getHurl());
        viewHolder.id.setText(String.valueOf(record.getId()));

        return view;
    }

    static class ViewHolder{
        ImageView image;
        TextView name;
        TextView url;
        TextView id;
        RelativeLayout layout;

    }

    //过滤器
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<HistoryRecord> lists = new ArrayList<>();
                if(constraint != null){
                    for (HistoryRecord h: historyRecords) {
                        lists.add(h);
                    }
                }
                results.count = lists.size();
                results.values = lists;
                return results;
            }

            //设置过滤器返回的结果
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (List<HistoryRecord>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
