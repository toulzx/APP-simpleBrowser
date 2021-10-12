package cn.njupt.assignment.tou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.entity.HistoryRecord;

/**
 * @author: sherman
 * @date: 2021/10/10
 * @description: 网址自动匹配的适配器
 */
public class AutoMatchingAdapter extends RecyclerView.Adapter<AutoMatchingAdapter.HistorySearchHolder> {


    static class HistorySearchHolder extends RecyclerView.ViewHolder{
        //显示item部分
        private final ImageView image;
        private final TextView name;
        private final TextView url;
        private final TextView id;

        public HistorySearchHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.Search_list_history_image);
            name = itemView.findViewById(R.id.Search_list_history_name);
            url = itemView.findViewById(R.id.Search_list_history_url);
            id = itemView.findViewById(R.id.Search_list_history_id);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view , int position);
    }

    Context context;

    List<HistoryRecord> historyRecords = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public AutoMatchingAdapter(Context context){
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener(){
        return this.onItemClickListener;
    }

    public void setHistoryRecords(List<HistoryRecord> lists){
        this.historyRecords = lists;
    }

    public void clearHistoryRecords(){
        this.historyRecords.clear();
    }



    @NonNull
    @Override
    public HistorySearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //定义一个构造
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //创建卡片  参数——————  布局，组件组，是否是根节点
        View itemView;
        itemView = layoutInflater.inflate(R.layout.auto_matching_list_unit, parent, false);
        //返回组件卡片
        return new HistorySearchHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorySearchHolder holder, int position) {
        HistoryRecord record = historyRecords.get(position);
        Glide.with(holder.image.getContext()).load(record.getHicon()).into(holder.image);
        holder.name.setText(record.getHname());
        holder.url.setText(record.getHurl());
        holder.id.setText(String.valueOf(record.getId()));

        //点击事件
        if (onItemClickListener != null){
            holder.itemView.setOnClickListener(v ->{
                onItemClickListener.onItemClick(holder.itemView,position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return historyRecords.size();
    }


}
