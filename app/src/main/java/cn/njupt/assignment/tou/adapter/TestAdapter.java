package cn.njupt.assignment.tou.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.activity.HomeActivity;
import cn.njupt.assignment.tou.activity.TestActivity;
import cn.njupt.assignment.tou.entity.HistoryRecord;


/**
 * @author: sherman
 * @date: 2021/10/22
 * @description: 测试适配器
 */
public class TestAdapter extends BaseAdapter implements Filterable {

    private List<HistoryRecord> data;

    private List<HistoryRecord> historyRecords;

    private Context context;

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


    public TestAdapter(Context context){
        this.context = context;
    }

    public TestAdapter(@NonNull Context context, List<HistoryRecord> records) {
        this.context = context;
        historyRecords = records;
    }

    public void setHistoryRecords(List<HistoryRecord> historyRecords){
        this.historyRecords = historyRecords;
    }

    public void clearHistoryRecords(){
        this.historyRecords.clear();
    }

    @Override
    public int getCount() {
        if (historyRecords == null){
            return 0;
        }else{
            return historyRecords.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (historyRecords == null) {
            return null;
        } else {
            return historyRecords.get(position);
        }
    }

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
            view = View.inflate(context,R.layout.auto_matching_list_unit,null);
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

        Glide.with(viewHolder.image.getContext()).load(record.getHicon()).into(viewHolder.image);
        viewHolder.name.setText(record.getHname());
        viewHolder.url.setText(record.getHurl());
        viewHolder.id.setText(String.valueOf(record.getId()));
        viewHolder.layout.setOnClickListener(v -> {
            TextView url = view.findViewById(R.id.Search_list_history_url);
            Intent intent = new Intent(TestAdapter.this.context, HomeActivity.class);
            intent.putExtra("history_url",url.getText().toString());
            TestAdapter.this.context.startActivity(intent);
        });

        return view;
    }

    static class ViewHolder{
        ImageView image;
        TextView name;
        TextView url;
        TextView id;
        RelativeLayout layout;

    }

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

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (List<HistoryRecord>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
