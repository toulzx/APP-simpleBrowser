package cn.njupt.assignment.tou.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.activity.HomeActivity;
import cn.njupt.assignment.tou.helper.WebViewPageHelper;
import cn.njupt.assignment.tou.utils.ToastUtil;


/* 需要一个 Adapter 来将数据和视图(item.xml)绑定起来 */
public class PageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = PageAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<Bitmap> mBitmapList;

    public PageAdapter(Context context, ArrayList<Bitmap> bitmapList) {
        this.mContext = context;
        this.mBitmapList = bitmapList;
        Log.i(TAG, "[abc]PageAdapter: bitmap =  "+ (bitmapList.size() == 0 ? "null" : bitmapList.get(0)));
    }

    /* 创建 ViewHolder，传入整个 itemView */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_page_item, parent, false);
        return new PageHolder(itemView);
    }


    /* 数据与 ViewHolder 绑定 */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PageHolder pageHolder = (PageHolder) holder;
        pageHolder.mPage.setImageBitmap(mBitmapList.get(position));
        Log.i(TAG, "[abc]onBindViewHolder:  bitmap = "+mBitmapList.get(position));
        if (position == WebViewPageHelper.getCurrentWebViewIndex()) {
            pageHolder.mDelete.setImageDrawable(null);
        }
        pageHolder.mPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HomeActivity.class);
                WebView webView = WebViewPageHelper.getWebViewPageList().get(holder.getBindingAdapterPosition()).getWebView();
                WebViewPageHelper.setCurrentWebView(webView, holder.getBindingAdapterPosition());
                mContext.startActivity(intent);
            }
        });
        pageHolder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getBindingAdapterPosition() == WebViewPageHelper.getCurrentWebViewIndex()) {
                    ToastUtil.shortToast(mContext, "正在此页面");
                } else {
                    ToastUtil.shortToast(mContext, "删除成功");
                    WebViewPageHelper.deletePage(holder.getBindingAdapterPosition());
                    mBitmapList.remove(holder.getBindingAdapterPosition());
                    notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mBitmapList.size();
    }




    /* ViewHolder 的主要作用就是将 XML 中的控件以变量的形式保存起来，方便我们后面数据绑定 */
    public class PageHolder extends RecyclerView.ViewHolder {

        public ImageView mPage;
        public ImageView mDelete;

        public PageHolder(@NonNull View itemView) {
            super(itemView);

            mPage = itemView.findViewById(R.id.page_item_imageView);
            mDelete = itemView.findViewById(R.id.delete_page);

        }

    }

}
