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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.entity.HistoryList;
import cn.njupt.assignment.tou.entity.HistoryRecord;

/**
 * @author: sherman
 * @date: 2021/10/8
 * @description: 作为demo的adapter
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

    static class HistoryHeaderViewHolder extends RecyclerView.ViewHolder{

        private final TextView time;
        public HistoryHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.history_today_time);
        }
    }

    static class HistoryFooterViewHolder extends RecyclerView.ViewHolder{

        private final TextView deleteRecordOfToday;
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


    private List<HistoryRecord> historyRecords;

    private List<HistoryList> allRecords;

    Context context;    /*上下文*/

    private OnItemLongClickListener onItemLongClickListener;    /*配置长按事件*/

    private OnItemClickListener onItemClickListener;    /*配置点击事件*/

    private List<Integer> listOfDelete = new LinkedList<>();

    public HistoryAdapter(Context context){
        this.context = context;
    }

    public HistoryAdapter(Context context, List<HistoryList> allRecords){
        Log.i("DemoAdapter", "DemoAdapter: start");
        this.context = context;
        this.allRecords = allRecords;
    }

    public void setAllRecords(List<HistoryList> allRecords){
        this.allRecords = allRecords;
    }

    public void setHistoryRecords(List<HistoryRecord> historyRecords) {
        this.historyRecords = historyRecords;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setListOfDelete(List<Integer> listOfDelete) {
        this.listOfDelete = listOfDelete;
    }

    public List<Integer> getListOfDelete(){
        return this.listOfDelete;
    }

    public void init(){
        listOfDelete.clear();
        this.notifyDataSetChanged();
    }

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

    @Override
    protected HistoryHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_history_header, parent, false);
        return new HistoryHeaderViewHolder(itemView);
    }

    @Override
    protected HistoryFooterViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_history_footer, parent, false);
        return new HistoryFooterViewHolder(itemView);
    }

    @Override
    protected void onBindSectionHeaderViewHolder(HistoryHeaderViewHolder holder, int section) {
        //放时间进去即可
        holder.time.setText(allRecords.get(section).getTime());
    }

    @Override
    protected void onBindSectionFooterViewHolder(HistoryFooterViewHolder holder, int section) {
        //放删除按钮，删除按钮的值是历史是时间
        holder.deleteRecordOfToday.setText("删除"+allRecords.get(section).getTime()+"的记录");
    }

    @Override
    protected void onBindItemViewHolder(HistoryHolder holder, int section, int position) {
        Log.i("DemoAdapter", "onBindItemViewHolder: start");
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

    @Override
    protected int getSectionCount() {
        if (allRecords == null){
            return 0;
        }
        return allRecords.size();
    }

    @Override
    protected int getItemCountForSection(int section) {
        return allRecords.get(section).getListOfDay().size();
    }

    @Override
    protected boolean hasFooterInSection(int section) {
        return true;
    }
}
