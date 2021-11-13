package cn.njupt.assignment.tou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.databinding.LayoutBookmarkItemBinding;
import cn.njupt.assignment.tou.entity.Bookmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private List<Bookmark> bookmarkList;
    private Context context;
    private ItemClick itemClick;
    private boolean isShowCheckBox = false;
    // 存储勾选框状态的map集合
    private Map<Integer, Boolean> map = new HashMap<>();
    private TextView checked_num;
    private CheckBox checkBox;

    public BookmarkAdapter(Context context) {
        this.context = context;
    }

    public void setBookmarkList(List<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public void isShowCheckBox(boolean isShowCheckBox) {
        this.isShowCheckBox = isShowCheckBox;
    }

    public void setMap(Map<Integer, Boolean> map) {
        this.map = map;
    }

    //获取map
    public Map<Integer, Boolean> getMap() {
        return map;
    }

    //初始化map集合,默认为不选中
    public void initMap() {
        for (int i = 0; i < bookmarkList.size(); i++) {
            map.put(i, false);
        }
    }

    public void setChecked_num(TextView checked_num) {
        this.checked_num = checked_num;
    }

    public interface ItemClick{
        void onClicked(View view, Bookmark bookmark);
    }

    //获取布局中的一些组件实例用于数据展示
    static class ViewHolder extends RecyclerView.ViewHolder {

        private View view;

        public ViewHolder(View view){
            super(view);

            this.view = view;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
//        holder.setIsRecyclable(false); // 为了条目不复用
        LayoutBookmarkItemBinding binding = DataBindingUtil.getBinding(holder.itemView);
        Bookmark bookmarkBean = bookmarkList.get(position);
        binding.setBookmark(bookmarkBean);
        binding.setItemclick(itemClick);
//        holder.itemView.setTag(position);//设置Tag
       showCheckBox(binding, position, bookmarkBean);//显示复选框以进行编辑操作
        binding.executePendingBindings();
    }

    //是否显示复选框
    public void showCheckBox(LayoutBookmarkItemBinding binding, int position, Bookmark bookmark){
        checkBox = binding.checkedItem;
        if (isShowCheckBox == true){
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    map.put(position, isChecked);
                    int new_true_num = trueNum(map);
                    String been_checked_num = "已选择" + new_true_num + "项";
                    checked_num.setText(been_checked_num);
                }
            });
            // 设置CheckBox的状态
            if (map.get(position) == null) {
                map.put(position, false);
            }
            checkBox.setChecked(map.get(position));
        }else{
            checkBox.setVisibility(View.GONE);
            map.clear();//清除所有键值对
        }
    }

    //计算map中true的数量
    public int trueNum(Map<Integer, Boolean> map){
        int ture_num = 0;
        for(int i = 0; i < map.size(); i++){
            if (map.get(i) !=null){
                if (map.get(i) == true){
                    ture_num += 1;
                }
            }
        }
        return ture_num;
    }

    //获取所选项的id值
    public List<Bookmark> getCheckedItems(){
        List<Bookmark> checkedItems = new ArrayList<>();
        for(int i = 0; i < map.size(); i++){
            if (map.get(i) !=null){
                if (map.get(i) == true){
                    checkedItems.add(bookmarkList.get(i));
                }
            }
        }
        return checkedItems;
    }

    //点击item选中CheckBox
    public void setSelectItem(int position) {
        //对当前状态取反
        if (map.get(position)) {
            map.put(position, false);
        } else {
            map.put(position, true);
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        if(bookmarkList != null){
            return bookmarkList.size();
        }else{
            return 0;
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LayoutBookmarkItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_bookmark_item, parent, false);
        View view = binding.getRoot();
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }



}
