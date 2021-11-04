package cn.njupt.assignment.tou.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.truizlop.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Date;
import java.util.List;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.entity.HistoryList;
import cn.njupt.assignment.tou.entity.HistoryRecord;
import cn.njupt.assignment.tou.viewmodel.HistoryRecordViewModel;

/**
 * @author: sherman
 * @date: 2021/10/8
 * @description: 历史记录的adapter
 */
public class HistoryAdapter extends SectionedRecyclerViewAdapter<
        HistoryAdapter.HistoryHeaderViewHolder,
        HistoryAdapter.HistoryHolder,
        HistoryAdapter.HistoryFooterViewHolder> {


    static class HistoryHolder extends RecyclerView.ViewHolder{
        //显示item部分
        private final ImageView image;
        private final TextView name;
        private final TextView url;
        private final TextView id;
        private final TextView time;

        private HistoryHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.list_history_image);
            name = itemView.findViewById(R.id.list_history_name);
            url = itemView.findViewById(R.id.list_history_url);
            id = itemView.findViewById(R.id.list_history_id);
            time = itemView.findViewById(R.id.list_history_time);
        }
    }

    //头部
    static class HistoryHeaderViewHolder extends RecyclerView.ViewHolder{

        private final TextView time;
        public HistoryHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.history_today_time);
        }
    }

    //底部
    static class HistoryFooterViewHolder extends RecyclerView.ViewHolder{

        private final TextView deleteRecordOfToday;
        private HistoryRecordViewModel historyRecordViewModel;
        public HistoryFooterViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteRecordOfToday = itemView.findViewById(R.id.history_today_delete);
        }
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view , int position);
    }

    public interface OnItemClickListener{
        void onItemClick(View view , int section , int position);
    }

    public interface OnFooterClickListener{
        void onItemClick(View view , int section ,String datetime);
    }


    private List<HistoryRecord> historyRecords;

    private List<HistoryList> allRecords;

    Context context;    /*上下文*/

    private OnItemLongClickListener onItemLongClickListener;    /*配置长按事件*/

    private OnItemClickListener onItemClickListener;    /*配置item点击事件*/

    private OnFooterClickListener onFooterClickListener; /*配置底部点击事件*/

    public HistoryAdapter(Context context){
        this.context = context;
    }

    public HistoryAdapter(Context context, List<HistoryList> allRecords){
        Log.i("DemoAdapter", "DemoAdapter: start");
        this.context = context;
        this.allRecords = allRecords;
    }

    //注入所有历史记录
    public void setAllRecords(List<HistoryList> allRecords){
        this.allRecords = allRecords;
    }

    //设置历史记录
    public void setHistoryRecords(List<HistoryRecord> historyRecords) {
        this.historyRecords = historyRecords;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnFooterClickListener(OnFooterClickListener onFooterClickListener){
        this.onFooterClickListener = onFooterClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }


    //创建itemViewHolder
    @NonNull
    @Override
    protected HistoryHolder onCreateItemViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("DemoAdapter", "onCreateItemViewHolder: start");
        //定义一个构造
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //创建卡片  参数——————  布局，组件组，是否是根节点
        View itemView = layoutInflater.inflate(R.layout.layout_history_list_unit, parent, false);
        //返回组件卡片
        return new HistoryHolder(itemView);
    }

    //创建头部viewHolder
    @Override
    protected HistoryHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_history_header, parent, false);
        return new HistoryHeaderViewHolder(itemView);
    }

    //创建底部viewHolder
    @Override
    protected HistoryFooterViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_history_footer, parent, false);
        return new HistoryFooterViewHolder(itemView);
    }

    //绑定头部viewHolder数据
    @Override
    protected void onBindSectionHeaderViewHolder(HistoryHeaderViewHolder holder, int section) {
        //放时间进去即可
        holder.time.setText(allRecords.get(section).getTime());
    }

    //绑定底部viewHolder
    @Override
    protected void onBindSectionFooterViewHolder(HistoryFooterViewHolder holder, int section) {
        //放删除按钮，删除按钮的值是历史是时间
        holder.deleteRecordOfToday.setText("删除"+allRecords.get(section).getTime()+"的记录");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = allRecords.get(section).getTime();
        String datetime;
        if (date.startsWith("今天")){
            Date today = new Date();
            datetime = simpleDateFormat.format(today);
        }else{
            datetime = date;
        }
        //点击事件
        if (onFooterClickListener != null) {
            holder.deleteRecordOfToday.setOnClickListener(v -> {
                onFooterClickListener.onItemClick(holder.itemView, section, datetime);
            });
        }
    }

    //绑定item的数据
    @Override
    protected void onBindItemViewHolder(HistoryHolder holder, int section, int position) {
        HistoryList beans = allRecords.get(section);
        HistoryRecord bean = beans.getListOfDay().get(position);

        //放每天的历史记录进section
        Glide.with(holder.image.getContext()).load(bean.getHicon()).into(holder.image);
        holder.name.setText(bean.getHname());
        holder.url.setText(bean.getHurl());
        holder.id.setText(String.valueOf(bean.getId()));
        holder.time.setText(bean.getHdate());

        //点击事件
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                onItemClickListener.onItemClick(holder.itemView, section, position);
            });
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        //长按事件
        if(onItemLongClickListener!=null) {
            holder.itemView.setOnLongClickListener(v -> {
                onItemLongClickListener.onItemLongClick(holder.itemView,position);
                return false;
            });
        }
    }

    //获取section数量
    @Override
    protected int getSectionCount() {
        if (allRecords == null){
            return 0;
        }
        return allRecords.size();
    }

    //获取每个section中item的数量
    @Override
    protected int getItemCountForSection(int section) {
        return allRecords.get(section).getListOfDay().size();
    }

    //设置开启底部栏
    @Override
    protected boolean hasFooterInSection(int section) {
        return true;
    }
}
