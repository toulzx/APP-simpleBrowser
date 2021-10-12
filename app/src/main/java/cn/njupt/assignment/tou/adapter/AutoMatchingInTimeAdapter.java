package cn.njupt.assignment.tou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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

    private List<HistoryRecord> historyRecords;

    private final Context context;

    public void setHistoryRecordBeans(List<HistoryRecord> historyRecords){
        this.historyRecords = historyRecords;
    }

    public AutoMatchingInTimeAdapter(Context context){
        this.context = context;
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

    public AutoMatchingInTimeAdapter(@NonNull Context context, List<HistoryRecord> list) {
        this.context = context;
        historyRecords = list;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        HistoryRecord record = historyRecords.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.history_list_unit,parent);

        ImageView image = view.findViewById(R.id.list_history_image);
        TextView name = view.findViewById(R.id.list_history_name);
        TextView url = view.findViewById(R.id.list_history_url);
        TextView id = view.findViewById(R.id.list_history_id);
        TextView time = view.findViewById(R.id.list_history_time);

        Glide.with(image.getContext()).load(record.getHicon()).into(image);
        name.setText(record.getHname());
        url.setText(record.getHurl());
        id.setText(String.valueOf(record.getId()));
        time.setText(record.getHdate());

        return itemView;
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
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (List<HistoryRecord>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
