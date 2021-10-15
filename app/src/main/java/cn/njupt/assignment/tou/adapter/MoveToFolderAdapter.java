package cn.njupt.assignment.tou.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.databinding.BookmarkCheckMoveToFolderItemBinding;
import cn.njupt.assignment.tou.entity.Bookmark;

public class MoveToFolderAdapter extends RecyclerView.Adapter<MoveToFolderAdapter.ViewHolder> {

    private List<Bookmark> bookmarkList;
    private BookmarkAdapter.ItemClick itemClick;

    public MoveToFolderAdapter(List<Bookmark> bookmarkList, BookmarkAdapter.ItemClick itemClick){
        this.bookmarkList=bookmarkList;
        this.itemClick=itemClick;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View view){
            super(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        BookmarkCheckMoveToFolderItemBinding binding = DataBindingUtil.getBinding(holder.itemView);
        Bookmark bookmarkBean = bookmarkList.get(position);
        binding.setBookmark(bookmarkBean);
        binding.setItemclick(itemClick);
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BookmarkCheckMoveToFolderItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.bookmark_check_move_to_folder_item, parent, false);
        View view = binding.getRoot();
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }
}
