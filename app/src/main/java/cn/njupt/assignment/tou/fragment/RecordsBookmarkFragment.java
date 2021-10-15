package cn.njupt.assignment.tou.fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.activity.HomeActivity;
import cn.njupt.assignment.tou.adapter.BookmarkAdapter;
import cn.njupt.assignment.tou.adapter.MoveToFolderAdapter;
import cn.njupt.assignment.tou.databinding.ActivityBookmarkBinding;
import cn.njupt.assignment.tou.databinding.AlterBookmarkDialogBinding;
import cn.njupt.assignment.tou.databinding.BookmarkCheckedMoveToFolderBinding;
import cn.njupt.assignment.tou.databinding.BookmarkDeleteAffirmDialogBinding;
import cn.njupt.assignment.tou.databinding.BookmarkNewFolderDialogBinding;
import cn.njupt.assignment.tou.entity.Bookmark;
import cn.njupt.assignment.tou.utils.ToastUtil;
import cn.njupt.assignment.tou.viewmodel.BookmarkViewModel;
import cn.njupt.assignment.tou.callback.RecordsBookmarkCallbackListener;

public class RecordsBookmarkFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = RecordsBookmarkFragment.class.getSimpleName();

    private View mView;

    //记录书签的id，方便用id打开文件夹
    private static List<Integer> ids = new ArrayList<>();
    private static int present = 0;//记录当前位于第几层
    private static int now_id = -1;
    private static List<String> folderName = new ArrayList<>();//记录所打开的文件夹的文件名

    //binding
    private ActivityBookmarkBinding binding;
    private BookmarkViewModel bookmarkViewModel;
    private RecyclerView recyclerView;//recyclerview控件
    private BookmarkAdapter bookmarkAdapter;

    //顶部工具栏
    private LinearLayout base_top;
    private LinearLayout checked_top;

    private TextView present_name;
    private TextView upper_name;

    private TextView Tv_checked_all;
    private TextView checked_num;

    //中间提示没有书签
    private LinearLayout no_bookmark;

    //底部工具栏
    private LinearLayout base_bottom;
    private LinearLayout checked_bottom;

    //基础界面的顶部搜索按键
    private LinearLayout search_layout;
    private EditText search_input;
    private ImageView back_upper;
    
    private LinearLayout Ll_new_folder;
    private LinearLayout Ll_edit;

    private boolean isEdit = false;//判断是否在进行编辑操作，false代表没有进行编辑操作
    private boolean isCheckedAll = false;//判断是否选择了全部书签
    private boolean isOpenSearch = true;//判断是否打开搜索框
    private static List<Object> searchResult = new ArrayList<>();//判断是否处于搜索状态

    private TextView Tv_checked_cancel;
    private LinearLayout Ll_checked_delete;
    private LinearLayout Ll_search;
    private ImageView Iv_input_cleear;
    private ImageView Iv_back_upper;
    private LinearLayout Ll_checked_move;


    @Override
    public void onCreate(@Nullable Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.activity_bookmark, container, false);
        }

        // 回调函数
        RecordsInDialogFragment.setBookmarkCallBackListener(new RecordsBookmarkCallbackListener() {
            @Override
            public void onBookmarkButtonClick(View view) {
                Log.i(TAG, "onButtonClick: 你好你好你好");
                ToastUtil.shortToast(requireContext(), "别点，我怕痒~");
            }
        });


        if (ids.size() == 0){//首次初始化列表ids，存-1
            ids.add(-1);
        }
        if (searchResult.size() == 0){
            searchResult.add(false);
        }

        initView();//初始化控件

        if(folderName.size() == 0){//初始化folder_name列表
            folderName.add("书签");
        }else if(!(searchResult.get(present) instanceof Boolean)){
            present_name.setText(folderName.get(present));
        }else if(folderName.size() >= 2){
            //用folder_name这个列表存储打开的文件夹的名称
            present_name.setText(folderName.get(present));//设置顶部左侧的名称
            upper_name.setText("◂"+folderName.get(present-1));
        }
        if (now_id == -1){
            back_upper.setVisibility(View.GONE);
        }else{
            back_upper.setVisibility(View.VISIBLE);
        }
        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookmarkAdapter = new BookmarkAdapter(requireContext());
        bookmarkAdapter.setItemClick((View view, Bookmark bookmarkBean)-> {
            int imageOrText = view.getId();
            if (imageOrText == R.id.one_bookmark_text){
                if(!isEdit){
                    if (bookmarkBean.getIsFolder() == 1) {//1代表点击的是文件夹
                        actionStart(requireContext(), bookmarkBean.getId(), bookmarkBean.getBname(), false);
                    } else {
                        //这里到时候实现点击跳转网页
                        Intent intent = new Intent(requireContext(), HomeActivity.class);
                        intent.putExtra("bookmark_url", bookmarkBean.getBurl());
                        startActivity(intent);
                    }
                }
            }else if(imageOrText == R.id.one_bookmark_image){
                loadPopupMenu(view, bookmarkBean);//弹出操作菜单
            }
        });
        recyclerView.setItemViewCacheSize(-1);//传入-1  就可解决
        recyclerView.setAdapter(bookmarkAdapter);
        LiveData<List<Bookmark>> listLiveData;
        //判断是否在进行搜索操作
        if (searchResult.get(present) instanceof Boolean){
            base_bottom.setVisibility(View.VISIBLE);
            listLiveData = bookmarkViewModel.getUpperBookmark(now_id);
        }else{
            base_bottom.setVisibility(View.GONE);
            listLiveData = bookmarkViewModel.getBookmarkByInput(searchResult.get(present).toString());
        }
        listLiveData.observe(getViewLifecycleOwner(), (List<Bookmark> bookmarkList)-> {
            bookmarkAdapter.setBookmarkList(bookmarkList);
            bookmarkAdapter.initMap();//初始化适配器中的map
            bookmarkAdapter.notifyDataSetChanged();
            if (bookmarkList.size() == 0){
                no_bookmark.setVisibility(View.VISIBLE);
            }else{
                no_bookmark.setVisibility(View.GONE);
            }
        });

        searchBookmark();

        return mView;
    }

    //启动活动所要执行的操作
    public static void actionStart(Context context, int id, String name, Object search_result){
        Intent intent = new Intent(context, HomeActivity.class);
        ids.add(id);
        present += 1;
        now_id = id;
        folderName.add(name);
        searchResult.add(search_result);//启动活动时，存储该活动是否是搜索结果的展示
        context.startActivity(intent);
    }

    public void onBackPressed(){
        //super.onBackPressed();
        ids.remove(present);
        folderName.remove(present);
        searchResult.remove(present);
        present -= 1;
        if (present >= 0){
            now_id= ids.get(present);
        }else{
            present = 0;
            now_id = -1;
            folderName.clear();
            searchResult.clear();
            ids.clear();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //初始化控件
    private void initView(){


        recyclerView = mView.findViewById(R.id.bookmark_show);
        base_top = mView.findViewById(R.id.base_top);
        present_name = mView.findViewById(R.id.present_name);
        upper_name = mView.findViewById(R.id.upper_name);
        checked_top = mView.findViewById(R.id.checked_top);
        checked_num = mView.findViewById(R.id.checked_num);
        back_upper = mView.findViewById(R.id.back_upper);
        no_bookmark = mView.findViewById(R.id.no_bookmark);
        search_layout = mView.findViewById(R.id.search_layout);
        search_input = mView.findViewById(R.id.search_input);
        base_bottom = mView.findViewById(R.id.base_bottom);
        checked_bottom = mView.findViewById(R.id.checked_bottom);
        
        
        
        Ll_new_folder = mView.findViewById(R.id.new_folder);
        Ll_edit = mView.findViewById(R.id.edit);
        Tv_checked_all = mView.findViewById(R.id.checked_all);
        Tv_checked_cancel = mView.findViewById(R.id.checked_cancel);
        Ll_checked_delete = mView.findViewById(R.id.checked_delete);
        Ll_search = mView.findViewById(R.id.search);
        Iv_input_cleear = mView.findViewById(R.id.input_cleear);
        Iv_back_upper = mView.findViewById(R.id.back_upper);
        Ll_checked_move = mView.findViewById(R.id.checked_move);

        Ll_new_folder.setOnClickListener(this);
        Ll_edit.setOnClickListener(this);
        Tv_checked_all.setOnClickListener(this);
        Tv_checked_cancel.setOnClickListener(this);
        Ll_checked_delete.setOnClickListener(this);
        Ll_search.setOnClickListener(this);
        Iv_input_cleear.setOnClickListener(this);
        Iv_back_upper.setOnClickListener(this);
        Ll_checked_move.setOnClickListener(this);
    }
    

    //初始化底部工具栏监听
    @Override
    public void onClick(View v) {
        int bottom_id = v.getId();
        if (bottom_id == R.id.new_folder){//添加新文件夹
            newFolderDialog(requireContext());
        }else if (bottom_id == R.id.edit){//编辑
            loadEditOperation();//加载编辑的界面
        }else if (bottom_id == R.id.checked_all){//全选
            if (!isCheckedAll) {
                isCheckedAll = true;
                checkedAll();//全选
            } else {
                isCheckedAll = false;
                checkedNone();//全不选
            }
        }else if(bottom_id == R.id.checked_cancel || bottom_id == R.id.checked_close){//取消，关闭
            showBaseTopBottom();
        }else if (bottom_id == R.id.checked_delete){//删除
            checkedDelete(bookmarkAdapter.getCheckedItems());
        }else if(bottom_id == R.id.search){//搜索
            if (isOpenSearch) {
                search_layout.setVisibility(View.GONE);
                isOpenSearch = false;
            } else {
                search_layout.setVisibility(View.VISIBLE);
                isOpenSearch = true;
            }
        }else if (bottom_id == R.id.input_cleear){//清空输入框
            if (search_input.getText().toString().trim().length() != 0) {
                search_input.setText("");
            }
        }else if(bottom_id == R.id.back_upper) {
            onBackPressed();//返回上一级
        }else if (bottom_id == R.id.checked_move){//转移至
            if (bookmarkAdapter.getCheckedItems().size() == 0) {
                ToastUtil.shortToast(requireContext(),"未选中任何书签");
            } else {
                moveBookmark(requireContext());//弹出文件夹选择框并选择合适的文件夹
            }
        }
    }

    //搜索
    public void searchBookmark(){
        search_input.setOnEditorActionListener((TextView v, int actionId, KeyEvent event)-> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                //-2代表执行搜索，跳转到搜索结果界面
                actionStart(requireContext(),-2,"搜索结果",v.getText().toString());
            }
            return false;
        });
    }

    //点击底部编辑按钮，切换成编辑界面
    public void loadEditOperation(){
        if (isEdit){
            showBaseTopBottom();
        }else{//当前并未处于编辑状态
            if(bookmarkAdapter.getItemCount() == 0){
                ToastUtil.shortToast(requireContext() ,"无书签可编辑");
                return;
            }
            showEditTopBottom();
        }
    }

    //显示编辑顶部工具栏和底部工具栏
    public void showEditTopBottom(){
        bookmarkAdapter.isShowCheckBox(true);
        bookmarkAdapter.initMap();
        isEdit = true;
        bookmarkAdapter.setChecked_num(checked_num);
        base_top.setVisibility(View.GONE);
        search_layout.setVisibility(View.GONE);
        checked_top.setVisibility(View.VISIBLE);
        base_bottom.setVisibility(View.GONE);
        checked_bottom.setVisibility(View.VISIBLE);
        Tv_checked_all.setText("全选");
        checked_num.setText("已选择0项");
        bookmarkAdapter.notifyDataSetChanged();
    }

    //显示基础状态下的顶部工具栏和底部工具栏
    public void showBaseTopBottom(){
        bookmarkAdapter.isShowCheckBox(false);
        isEdit = false;
        base_top.setVisibility(View.VISIBLE);
        search_layout.setVisibility(View.VISIBLE);
        checked_top.setVisibility(View.GONE);
        base_bottom.setVisibility(View.VISIBLE);
        checked_bottom.setVisibility(View.GONE);
        bookmarkAdapter.notifyDataSetChanged();
    }

    //对所要删除的书签进行处理，得到最终所要删除的实体集
    public void checkedDelete(List<Bookmark> checkedItems){
        if (checkedItems.size() == 0){
            Toast.makeText(requireContext(), "无内容可删除", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Bookmark> needDelete = new ArrayList<>(checkedItems);
        int upper = checkedItems.get(0).getUpper();
        int[] isFolders = {0, 1};
        for (int i = 0;i < needDelete.size(); i++){//拿到要删除文件夹中的全部书签实体
            if (needDelete.get(i).getIsFolder() == 1){
                needDelete.addAll(bookmarkViewModel.getBookmarkOfSubfolder(needDelete.get(i).getId(), isFolders));
            }
        }
        deleteAffirm(requireContext(), needDelete, checkedItems.size(), upper);
    }

    //弹窗确认删除
    public void deleteAffirm(Context context, List<Bookmark> needDelete, int num, int upper){
        Map<String, Object> dialogMap = loadAlertDialog(context, R.layout.bookmark_delete_affirm_dialog, 800, LinearLayout.LayoutParams.WRAP_CONTENT);
        View dialogView = (View) dialogMap.get("dialogView");
        AlertDialog dialog = (AlertDialog) dialogMap.get("dialog");
        //点击事件监听
        BookmarkDeleteAffirmDialogBinding dialogBinding = BookmarkDeleteAffirmDialogBinding.bind(dialogView);//绑定databinding
        TextView textView = dialogBinding.deleteNum;
        String totalNum = "共计："+needDelete.size();
        textView.setText(totalNum);
        dialogBinding.setDeleteclick((View v)-> {
            int cancelOrAffirm = v.getId();
            if (cancelOrAffirm == R.id.delete_cancel){
                dialog.dismiss();
            }else if (cancelOrAffirm == R.id.delete_affirm){
                //执行删除操作
                bookmarkViewModel.deleteBookmarks(needDelete);
                //根据当前层的id更新上一层文件夹所拥有的标签数目（注意是减去，记得加负号）
                bookmarkViewModel.updateBookmark(upper, -num);
                dialog.dismiss();
                ToastUtil.shortToast(requireContext(),"删除成功");
            }
        });
    }

    //全选
    public void checkedAll(){
        int num = bookmarkAdapter.getItemCount();
        Map<Integer, Boolean> map = new HashMap<>();
        for (int i = 0; i < num; i++) {
            map.put(i, true);
        }
        bookmarkAdapter.setMap(map);
        bookmarkAdapter.notifyDataSetChanged();
        Tv_checked_all.setText("全不选");
        String selectedNum = "已选择" + num + "项";
        checked_num.setText(selectedNum);
    }

    //全不选
    public void checkedNone(){
        Map<Integer, Boolean> map = new HashMap<>();
        for (int i = 0; i < bookmarkAdapter.getItemCount(); i++) {
            map.put(i, false);
        }
        bookmarkAdapter.setMap(map);
        bookmarkAdapter.notifyDataSetChanged();
        Tv_checked_all.setText("全选");
        checked_num.setText("已选择0项");
    }

    //点击书签图标弹出菜单
    public void loadPopupMenu(View view, Bookmark bookmarkBean){
        //创建弹出式菜单对象（最低版本11）
        PopupMenu popup = new PopupMenu(requireContext(), view);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.bookmark_edit_menu, popup.getMenu());
        MenuItem copy_url_item = popup.getMenu().findItem(R.id.action_copy_url);
        copy_url_item.setVisible(bookmarkBean.getIsFolder() != 1);
        //绑定菜单项的点击事件
        popup.setOnMenuItemClickListener((MenuItem item)-> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_alter){
                loadAlterDialog(requireContext(), bookmarkBean);
            }else if (itemId == R.id.action_delete){//删除操作
                List<Bookmark> checkedItems = new ArrayList<>();
                checkedItems.add(bookmarkBean);
                checkedDelete(checkedItems);//删除
            }else if (itemId == R.id.action_copy_url){
                copyContent(requireContext(),bookmarkBean.getBurl());
                ToastUtil.shortToast(requireContext(),"复制成功");
            }
            return false;
        });
        //显示(这一行代码不要忘记了)
        popup.show();
    }

    //复制内容到剪贴板
    public void copyContent(Context context,String s){
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", s);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

    //弹出修改的操作框
    public void loadAlterDialog(Context context, Bookmark bookmarkBean){
        Map<String, Object> dialogMap = loadAlertDialog(context, R.layout.alter_bookmark_dialog, 900, LinearLayout.LayoutParams.WRAP_CONTENT);
        View dialogView = (View) dialogMap.get("dialogView");
        AlertDialog dialog = (AlertDialog) dialogMap.get("dialog");
        //绑定databinding，获取输入的数据
        AlterBookmarkDialogBinding dialogBinding = AlterBookmarkDialogBinding.bind(dialogView);
        EditText alterName = dialogBinding.alterName;
        EditText alterUrl = dialogBinding.alterUrl;
        LinearLayout alter_url_layout = dialogBinding.alterUrlLayout;
        alterName.setText(bookmarkBean.getBname());
        alter_url_layout.setVisibility(bookmarkBean.getIsFolder() == 1 ? View.GONE : View.VISIBLE);
        alterUrl.setText(bookmarkBean.getBurl());
        //点击事件监听
        dialogBinding.setAlterclick((View v)-> {
            int cancelOrAffirm = v.getId();
            if (cancelOrAffirm == R.id.alter_cancel){
                dialog.dismiss();
            }else if (cancelOrAffirm == R.id.alter_affirm){
                String alterNameString = alterName.getText().toString();
                String alterUrlString = alterUrl.getText().toString();
                handleAlterInput(alterNameString, alterUrlString, bookmarkBean, dialog);
            }
        });
    }

    //修改操作，对用户所输入的数据进行判断，直至获得正确的数据
    public void handleAlterInput(String alterNameString, String alterUrlString, Bookmark bookmarkBean, AlertDialog dialog){
        if (bookmarkBean.getIsFolder() == 1 && isNameNull(alterNameString)){//文件夹的情况
            int[] isFolders = {1};
            Integer isexit = bookmarkViewModel.isNewBookmarkNameExit(alterNameString, isFolders);
            if (isexit == 1){
                ToastUtil.shortToast(requireContext(),"名称已存在");
            }else{
                alterBookmark(bookmarkBean, alterNameString, "");//进行数据修改操作
                dialog.dismiss();
            }
        }else if (bookmarkBean.getIsFolder() == 0 && isNameNull(alterNameString) && !isUrlFaultOrNull(alterUrlString)){
            int[] isFolders = {0};
            Integer isNameExit = bookmarkViewModel.isNewBookmarkNameExit(alterNameString, isFolders);
            Integer isUrlExit = bookmarkViewModel.isNewUrlExit(alterUrlString);
            if (isNameExit == 1){
                ToastUtil.shortToast(requireContext(),"名称已存在");
            }else if(isUrlExit == 1) {
                ToastUtil.shortToast(requireContext(),"网址已存在");
            }else{
                alterBookmark(bookmarkBean, alterNameString, alterUrlString);
                dialog.dismiss();
            }
        }
    }

    //修改书签的操作(名称和网址)
    public void alterBookmark(Bookmark bookmarkBean, String name, String url){
        bookmarkBean.setBname(name);
        bookmarkBean.setBurl(url);
        bookmarkViewModel.alterBookmark(bookmarkBean);
        ToastUtil.shortToast(requireContext(),"修改成功");
    }

    //判断所输入的名称是否为空
    public boolean isNameNull(String name){
        if(name.trim().length() == 0){
            ToastUtil.shortToast(requireContext(),"名称不能为空");
            return false;
        }
        return true;
    }

    //判断网址的格式是否正确，并且是否为空
    public boolean isUrlFaultOrNull(String url){
        if (url.trim().length() == 0){
            ToastUtil.shortToast(requireContext(),"网址不能为空");
            return true;
        }else{
//            String regex = "(ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\-\\.\\?\\,\\'\\/\\\\&%\\+\\$#_=]*)?";
            String regex = "(ht|f)tp(s?)://[0-9a-zA-Z]([-.w]*[0-9a-zA-Z])*(:(0-9)*)*(/?)([a-zA-Z0-9-.?,'/\\\\&%+$#_=]*)?";
            Pattern pat = Pattern.compile(regex);
            Matcher mat = pat.matcher(url.trim());
            boolean result = mat.matches();
            if (!result){
                ToastUtil.shortToast(requireContext(),"网址格式不正确");
                return true;
            }
        }
        return false;
    }

    //新增文件夹
    public void addNewFolder(String name, AlertDialog dialog){
        //判断文件名是否已经存在
        int[] isFolders = {1};
        Integer isexit = bookmarkViewModel.isNewBookmarkNameExit(name, isFolders);
        if(isexit == 1){
            ToastUtil.shortToast(requireContext(),"文件名已经存在，请重新输入");
        }else{
            Integer maxsort = bookmarkViewModel.getMaxSort(now_id);
            bookmarkViewModel.insertBookmark(new Bookmark(name,"","",0,1, now_id, maxsort == null?0:maxsort+1));
            bookmarkViewModel.updateBookmark(now_id, 1);//更新书签数目
            ToastUtil.shortToast(requireContext(),"新增文件夹成功");
            dialog.dismiss();//关闭弹窗
        }
    }

    //新增文件夹弹窗UI设置和点击事件监听
    public void newFolderDialog(Context context){
        Map<String, Object> dialogMap = loadAlertDialog(context, R.layout.bookmark_new_folder_dialog, 900, LinearLayout.LayoutParams.WRAP_CONTENT);
        View dialogView = (View) dialogMap.get("dialogView");
        AlertDialog dialog = (AlertDialog) dialogMap.get("dialog");
        //点击事件监听
        BookmarkNewFolderDialogBinding dialogBinding = BookmarkNewFolderDialogBinding.bind(dialogView);//绑定databinding
        EditText editText = dialogBinding.newFolderName;
        dialogBinding.setNewfolderclick((View v)-> {
            int cancelOrAffirm = v.getId();
            if (cancelOrAffirm == R.id.add_cancel){
                dialog.dismiss();
            }else if (cancelOrAffirm == R.id.add_affirm){
                String name = editText.getText().toString();
                if(isNameNull(name)){
                    addNewFolder(name, dialog);//添加文件夹
                }
            }
        });
    }

    //加载AlertDialog弹窗，并设置一些相关属性
    public Map<String,Object> loadAlertDialog(Context context, int resource, int width, int height){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        View dialogView = View.inflate(context, resource, null);
        dialog.setView(dialogView);
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//解决点击EditText不显示键盘的问题
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(width, height);
        window.setDimAmount(0.4f);
        Map<String, Object> dialogMap = new HashMap<>();
        dialogMap.put("dialog", dialog);
        dialogMap.put("dialogView", dialogView);
        return dialogMap;
    }

    //移动书签到某个文件夹
    public void moveBookmark(Context context){
        Map<String, Object> dialogMap = loadAlertDialog(context, R.layout.bookmark_checked_move_to_folder, 800, 1000);
        View dialogView = (View) dialogMap.get("dialogView");
        AlertDialog dialog = (AlertDialog) dialogMap.get("dialog");
        //存储所选择的文件夹
        final int[] movetoId = {-1};//-1代表未选中任何文件夹
        //点击事件监听
        BookmarkCheckedMoveToFolderBinding dialogBinding = BookmarkCheckedMoveToFolderBinding.bind(dialogView);//绑定databinding
        TextView checked_move_to_folder_text = dialogBinding.checkedMoveToFolderText;
        RecyclerView recyclerView = dialogBinding.moveToFolderShow;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //以下部分是对数据进行处理
        List<Bookmark> checkedItems = bookmarkAdapter.getCheckedItems();//获取所要移动的书签
        List<Integer> checkedFolderIds = new ArrayList<>();//把所有已选项中的文件夹的id存起来
        List<Bookmark> temp = new ArrayList<>(checkedItems);
        int[] isFolders = {1};
        //按照不是搜索结果编辑的情况设计(当选择了文件夹时，不提供所选择的文件夹以及所属文件夹和其里面包含的全部文件夹)
        // （当只选择了网址时，不提供所属文件夹）
        for(int i = 0;i < temp.size();i++){
            if (checkedItems.get(i).getIsFolder() == 1){
                checkedFolderIds.add(checkedItems.get(i).getId());
                temp.addAll(bookmarkViewModel.getBookmarkOfSubfolder(checkedItems.get(i).getId(), isFolders));
            }
        }
        List<Bookmark> allFolder = bookmarkViewModel.getAllFolder(checkedFolderIds, now_id);
        if (allFolder.size() == 0){
            checked_move_to_folder_text.setText("无文件夹可供选择");
            LinearLayout move_affirm = dialogBinding.moveAffirm;
            View view = dialogBinding.boundary;
            move_affirm.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }
        MoveToFolderAdapter moveToFolderAdapter = new MoveToFolderAdapter(allFolder, (View view, Bookmark bookmarkBean)-> {
            movetoId[0] = bookmarkBean.getId();
            String selectedText = "已选择："+bookmarkBean.getBname();
            checked_move_to_folder_text.setText(selectedText);
        });
        recyclerView.setAdapter(moveToFolderAdapter);
        dialogBinding.setMovetofolderclick((View v)-> {
            int cancelOrAffirm = v.getId();
            if (cancelOrAffirm == R.id.move_cancel){
                dialog.dismiss();
            }else if (cancelOrAffirm == R.id.move_affirm){
                if (movetoId[0] == -1){
                    ToastUtil.shortToast(requireContext(),"未选中任何文件夹");
                }else {
                    int checkedNum = checkedItems.size();
                    //更新所要移动到的文件夹的标签数
                    bookmarkViewModel.updateBookmark(movetoId[0], checkedNum);
                    //更新所选项中每一个的sort和upper
                    int checkedFolserSubMaxSort = bookmarkViewModel.getMaxSort(movetoId[0]);
                    int max = checkedFolserSubMaxSort + checkedNum;
                    for(Bookmark bookmarkBean:checkedItems){
                        bookmarkBean.setSort(max);
                        bookmarkBean.setUpper(movetoId[0]);
                        bookmarkViewModel.alterBookmark(bookmarkBean);
                        max -= 1;
                    }
                    //更新所选书签的上一层文件夹的标签数(负数)
                    bookmarkViewModel.updateBookmark(now_id,-checkedNum);
                    ToastUtil.shortToast(requireContext(),"移动成功");
                    dialog.dismiss();
                }
            }
        });
    }


}
