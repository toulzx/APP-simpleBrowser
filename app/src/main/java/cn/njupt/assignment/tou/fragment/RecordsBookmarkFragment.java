package cn.njupt.assignment.tou.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.adapter.BookmarkAdapter;
import cn.njupt.assignment.tou.adapter.MoveToFolderAdapter;
import cn.njupt.assignment.tou.callback.ToBookmarkCallbackListener;
import cn.njupt.assignment.tou.callback.ToDialogRecordsCallbackListener;
import cn.njupt.assignment.tou.callback.ToHomeActivityCallbackListener;
import cn.njupt.assignment.tou.databinding.AlterBookmarkDialogBinding;
import cn.njupt.assignment.tou.databinding.BookmarkCheckedMoveToFolderBinding;
import cn.njupt.assignment.tou.databinding.BookmarkDeleteAffirmDialogBinding;
import cn.njupt.assignment.tou.databinding.BookmarkNewFolderDialogBinding;
import cn.njupt.assignment.tou.databinding.FragmentDialogRecordsBookmarkBinding;
import cn.njupt.assignment.tou.entity.Bookmark;
import cn.njupt.assignment.tou.viewmodel.BookmarkViewModel;

public class RecordsBookmarkFragment extends Fragment {

    private static final String TAG = RecordsBookmarkFragment.class.getSimpleName();
    private static final int IS_FOLDER = 1;
    private View mView;

    //记录书签的id，方便用id打开文件夹
    private static List<Integer> folderIdList = new ArrayList<>();
    private static int currentFolderIndex = 0; // 记录当前位于第几层
    private static int currentFolderId = -1; // 记录当前层的 id

    // 记录所打开的文件夹的文件名
    private static List<String> folderName = new ArrayList<>();

    //binding
    private FragmentDialogRecordsBookmarkBinding binding;
    private BookmarkViewModel bookmarkViewModel;

    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;

    //中间提示没有书签
    private LinearLayout no_bookmark;

    //基础界面的顶部搜索按键
    private LinearLayout search_layout;
    private EditText search_input;

    InputMethodManager mInputMethodManager;

    private boolean mIsEditMode = false;//判断是否在进行编辑操作，false代表没有进行编辑操作
    private boolean mIsCheckedAll = false;//判断是否选择了全部书签
    private boolean mIsOpenSearchModule = false;//判断是否打开搜索框，默认不打开，xml 对应控件设置为 gone
    private static List<Object> searchResult = new ArrayList<>();//判断是否处于搜索状态

    // callback
    public static ToDialogRecordsCallbackListener mToDialogRecordsCallbackListener;
    public static ToHomeActivityCallbackListener mToHomeActivityCallbackListener;
    public static void setToDialogRecordsCallbackListener(ToDialogRecordsCallbackListener listener) {
        mToDialogRecordsCallbackListener = listener;
    }
    public static void setToHomeActivityCallbackListener(ToHomeActivityCallbackListener listener) {
        mToHomeActivityCallbackListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentDialogRecordsBookmarkBinding.inflate(inflater, container, false);
        mView = binding.getRoot();

        initView();

        initData();

        initRecycleView();

        initLiveData();

        initListener();

        searchBookmark();

        return mView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Fragment 由于生命周期较长，需及时解绑
        binding = null;
    }



    /*----------------------------------------------------------------------------------------------------------------*/


    /**
     * 初始化控件
     * @return void
     * @date 2021/10/22 16:20
     */
    private void initView(){
        //控件初始化
        recyclerView = binding.bookmarkShow;
        //中间提示没有书签
        no_bookmark = binding.noBookmark;
        //顶部的搜索输入框
        search_layout = binding.searchLayout;
        search_input = binding.searchInput;

        mInputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 初始化相关数据
     * @return void
     * @date 2021/10/22 16:44
     */
    private void initData() {

        // 首次初始化书签的 id 列表 ids，存 -1
        if (folderIdList.size() == 0){
            folderIdList.add(-1);
        }

        // 首次初始化搜索状态列表，存 false
        if (searchResult.size() == 0){
            searchResult.add(false);
        }

        // 首次初始化 folder_name 列表，存 “书签”
        if(folderName.size() == 0){

            folderName.add("书签");
        }

    }


    /**
     * 初始化 recycleView 并绑定 Adapter
     * @return void
     * @date 2021/10/22 20:23
     */
    private void initRecycleView() {
        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BookmarkAdapter(requireContext());
        adapter.setItemClick((View view, Bookmark bookmarkBean)-> {
            int imageOrText = view.getId();
            if (imageOrText == R.id.one_bookmark_text){
                if(!mIsEditMode){
                    if (bookmarkBean.getIsFolder() == IS_FOLDER) {
                        RecordsBookmarkFragment.actionStart(requireContext(), bookmarkBean.getId(), bookmarkBean.getBname(), false);
                    } else {
                        if (mToHomeActivityCallbackListener != null) {
                            mToHomeActivityCallbackListener.loadUrl(bookmarkBean.getBurl());
                        }

                    }
                }
            }else if(imageOrText == R.id.one_bookmark_image){
                // 弹出操作菜单
                loadPopupMenu(view, bookmarkBean);
            }
        });

        recyclerView.setItemViewCacheSize(-1);//传入-1  就可解决
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化 LiveData
     * @return void
     * @date 2021/10/22 20:24
     */
    private void initLiveData() {
        LiveData<List<Bookmark>> listLiveData;
        //判断是否在进行搜索操作
        if (searchResult.get(currentFolderIndex) instanceof Boolean){
            listLiveData = bookmarkViewModel.getUpperBookmark(currentFolderId);
        }else{
            listLiveData = bookmarkViewModel.getBookmarkByInput(searchResult.get(currentFolderIndex).toString());
        }
        listLiveData.observe(getViewLifecycleOwner(), (List<Bookmark> bookmarkList)-> {
            adapter.setBookmarkList(bookmarkList);
            adapter.initMap();//初始化适配器中的map
            adapter.notifyDataSetChanged();
            if (bookmarkList.size() == 0){
                no_bookmark.setVisibility(View.VISIBLE);
            }else{
                no_bookmark.setVisibility(View.GONE);
            }
        });
    }


    /**
     * 初始化底部工具栏监听
     * @return void
     * @date 2021/10/22 20:26
     */
    private void initListener() {

        binding.setOperationclick((View v) -> {
            int bottom_id = v.getId();
            if (bottom_id == R.id.input_clear){//清空输入框
                if (search_input.getText().toString().trim().length() != 0) {
                    search_input.setText("");
                } else {//如果搜索框为空则关闭搜索模块和软键盘
                    setSearchModule(mIsOpenSearchModule);
                }
            }
        });

        /* callback */
        RecordsInDialogFragment.setToBookmarkCallbackListener(new ToBookmarkCallbackListener() {
            @Override
            public void onMenuButtonClick(View view) {
                if (mIsEditMode) {   // bookmark 编辑模式 => 左下按钮为“删除”键 => 执行删除操作
                    checkedDelete(adapter.getCheckedItems());
                } else {    // bookmark 常规模式 => 左下按钮为“更多”键 => 打开对应菜单栏
                    // View当前PopupMenu显示的相对View的位置
                    android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(getContext(), view);
                    // menu布局
                    if (currentFolderIndex == 0) {
                        popupMenu.getMenuInflater().inflate(R.menu.bookmark_menu, popupMenu.getMenu());
                    } else {
                        popupMenu.getMenuInflater().inflate(R.menu.bookmark_menu_in_folder, popupMenu.getMenu());
                    }
                    // menu的item点击事件
                    popupMenu.setOnMenuItemClickListener(item -> {
                        //点击逻辑
                        switch (item.getItemId()){
                            // 搜索
                            case R.id.bookmark_menu_search:
                                setSearchModule(mIsOpenSearchModule);
                                break;
                            // 新建文件夹
                            case R.id.bookmark_menu_new_folder:
                                newFolderDialog(requireContext());
                                break;
                            // 编辑模式
                            case R.id.bookmark_menu_edit:
                                loadEditOperation();
                                break;
                            default:
                                break;
                        }
                        return false;
                    });

                    // PopupMenu关闭事件
                    popupMenu.setOnDismissListener(menu -> {//关闭后的逻辑
                    });
                    popupMenu.show();
                }
            }
            @Override
            public void onBackButtonClick() {
                actionBack();
            }
            @Override
            public void onMoveButtonClick() {
                if (adapter.getCheckedItems().size() == 0) {
                    Toast.makeText(requireContext(), "未选中任何书签", Toast.LENGTH_SHORT).show();
                } else {
                    moveBookmark(requireContext());//弹出文件夹选择框并选择合适的文件夹
                }
            }

            @Override
            public void onCheckAllButtonClick(TextView checked_all, TextView checked_num) {
                if (!mIsCheckedAll) {
                    mIsCheckedAll = true;
                    checkedAll(checked_all, checked_num);//全选
                } else {
                    mIsCheckedAll = false;
                    checkedNone(checked_all, checked_num);//全不选
                }
            }

            @Override
            public void onCancelEditModeButtonClick() {
                setOriginModeUI();
            }
        });
    }

    /**
     * 搜索书签
     * @return void
     * @date 2021/10/22 20:48
     */
    public void searchBookmark(){
        search_input.setOnEditorActionListener((TextView v, int actionId, KeyEvent event)-> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                // -2代表执行搜索，跳转到搜索结果界面
                actionStart(requireContext(),-2,"搜索结果",v.getText().toString());
                // 强制隐藏键盘
                search_input.clearFocus();
                mInputMethodManager.hideSoftInputFromWindow(search_input.getWindowToken(), 0);
            }
            return false;
        });
    }


    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * 搜索模块的打开和关闭
     * @param isNowShown 当前搜索模块的状态：true:显示
     * @return void
     * @date 2021/10/23 18:53
     * @author tou
     */
    private void setSearchModule(boolean isNowShown) {
        if (isNowShown) {
            search_layout.setVisibility(View.GONE);
            search_input.clearFocus();
            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            mIsOpenSearchModule = false;
        } else {
            search_layout.setVisibility(View.VISIBLE);
            search_input.requestFocus();
            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            mIsOpenSearchModule = true;
        }
    }

    /**
     * 启动活动所要执行的操作
     * @param context:
     * @param id:
     * @param name:
     * @param search_result:
     * @return void
     * @date 2021/10/22 16:48
     */
    public static void actionStart(Context context, int id, String name, Object search_result){

        folderIdList.add(id);
        currentFolderIndex += 1;
        currentFolderId = id;
        folderName.add(name);
        searchResult.add(search_result);//启动活动时，存储该活动是否是搜索结果的展示

        if (mToDialogRecordsCallbackListener != null) {
            mToDialogRecordsCallbackListener.refreshFragment(currentFolderIndex, folderName.get(currentFolderIndex - 1));
        }

    }

    /**
     * 点击书签图标弹出菜单
     * @param view:
     * @param bookmarkBean:
     * @return void
     * @date 2021/10/22 20:06
     */
    public void loadPopupMenu(View view, Bookmark bookmarkBean){
        //创建弹出式菜单对象（最低版本11）
        PopupMenu popup = new PopupMenu(requireContext(), view);//第二个参数是绑定的那个view
        //获取菜单填充器
        MenuInflater inflater = popup.getMenuInflater();
        //填充菜单
        inflater.inflate(R.menu.bookmark_edit_menu, popup.getMenu());
        MenuItem copy_url_item = popup.getMenu().findItem(R.id.action_copy_url);
        copy_url_item.setVisible(bookmarkBean.getIsFolder() != IS_FOLDER);
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
                copyContent(bookmarkBean.getBurl());
                Toast.makeText(requireContext(), "复制成功", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        //显示(这一行代码不要忘记了)
        popup.show();
    }


    /**
     * 新增文件夹弹窗UI设置和点击事件监听
     * @param context:
     * @return void
     * @date 2021/10/22 20:27
     */
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

    /**
     * 编辑按钮
     * 编辑模式和基础模式相互切换
     * @return void
     * @date 2021/10/22 20:28
     */
    public void loadEditOperation(){
        if (mIsEditMode){
            setOriginModeUI();
        }else{
            if(adapter.getItemCount() == 0){
                Toast.makeText(requireContext(), "无书签可编辑", Toast.LENGTH_SHORT).show();
                return;
            }
            setEditModeUI();
        }
    }


    // 全选
    public void checkedAll(TextView checked_all, TextView checked_num){
        int num = adapter.getItemCount();
        Map<Integer, Boolean> map = new HashMap<>();
        for (int i = 0; i < num; i++) {
            map.put(i, true);
        }
        adapter.setMap(map);
        adapter.notifyDataSetChanged();
        checked_all.setText("全不选");
        checked_num.setText("已选择" + num + "项");
    }

    // 全不选
    public void checkedNone(TextView checked_all, TextView checked_num){
        Map<Integer, Boolean> map = new HashMap<>();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            map.put(i, false);
        }
        adapter.setMap(map);
        adapter.notifyDataSetChanged();
        checked_all.setText("全选");
        checked_num.setText("已选择0项");
    }


    /**
     * 返回上一个文件夹
     * @return void
     * @date 2021/10/22 20:45
     */
    public void actionBack(){

        String upperFolderName = folderName.remove(currentFolderIndex);
        folderIdList.remove(currentFolderIndex);
        searchResult.remove(currentFolderIndex);
        currentFolderIndex -= 1;
        if (currentFolderIndex >= 0){
            currentFolderId = folderIdList.get(currentFolderIndex);
        }else{
            currentFolderIndex = 0;
            currentFolderId = -1;
            folderName.clear();
            searchResult.clear();
            folderIdList.clear();
        }

        if (mToDialogRecordsCallbackListener != null) {
            mToDialogRecordsCallbackListener.refreshFragment(currentFolderIndex, upperFolderName);
        }

    }

    /**
     * 移动书签到某个文件夹
     * @param context:
     * @return void
     * @date 2021/10/22 20:47
     */
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
        List<Bookmark> checkedItems = adapter.getCheckedItems();//获取所要移动的书签
        List<Integer> checkedFolderIds = new ArrayList<>();//把所有已选项中的文件夹的id存起来
        List<Bookmark> temp = new ArrayList<>(checkedItems);
        int[] isFolders = {1};
        //按照不是搜索结果编辑的情况设计(当选择了文件夹时，不提供所选择的文件夹以及所属文件夹和其里面包含的全部文件夹)
        // （当只选择了网址时，不提供所属文件夹）
        // TODO: bug: 当有二级文件夹时会出报错，目前通过限制二级文件夹创建来屏蔽此问题
        for(int i = 0;i < temp.size();i++){
            if (checkedItems.get(i).getIsFolder() == 1){
                checkedFolderIds.add(checkedItems.get(i).getId());
                temp.addAll(bookmarkViewModel.getBookmarkOfSubfolder(checkedItems.get(i).getId(), isFolders));
            }
        }
        List<Bookmark> allFolder = bookmarkViewModel.getAllFolder(checkedFolderIds, currentFolderId);
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
                    Toast.makeText(requireContext(), "未选中任何文件夹", Toast.LENGTH_SHORT).show();
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
                    bookmarkViewModel.updateBookmark(currentFolderId,-checkedNum);
                    Toast.makeText(requireContext(), "移动成功", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    /*----------------------------------------------------------------------------------------------------------------*/

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
        alter_url_layout.setVisibility(bookmarkBean.getIsFolder() == IS_FOLDER ? View.GONE : View.VISIBLE);
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


    /**
     * 对所要删除的书签进行处理，得到最终所要删除的实体集
     * @param checkedItems:
     * @return void
     * @date 2021/10/22 20:13
     */
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


    /**
     * 复制内容到剪贴板
     * @param content:
     * @return void
     * @date 2021/10/22 20:15
     */
    public void copyContent(String content){
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }


    /**
     * 新增文件夹
     * @param name:
     * @param dialog:
     * @return void
     * @date 2021/10/22 20:27
     */
    public void addNewFolder(String name, AlertDialog dialog){
        //判断文件名是否已经存在
        int[] isFolders = {1};
        Integer isexit = bookmarkViewModel.isNewBookmarkNameExit(name, isFolders);
        if(isexit == 1){
            Toast.makeText(requireContext(), "文件名已经存在，请重新输入", Toast.LENGTH_SHORT).show();
        }else{
            Integer maxsort = bookmarkViewModel.getMaxSort(currentFolderId);
            bookmarkViewModel.insertBookmark(new Bookmark(name,"","",0,1, currentFolderId, maxsort == null?0:maxsort+1));
            bookmarkViewModel.updateBookmark(currentFolderId, 1);//更新书签数目
            Toast.makeText(requireContext(), "新增文件夹成功", Toast.LENGTH_SHORT).show();
            dialog.dismiss();//关闭弹窗
        }
    }

    /**
     * 显示基础状态下的 UI
     * @return void
     * @date 2021/10/22 20:29
     */
    public void setOriginModeUI(){
        adapter.isShowCheckBox(false);
        mIsEditMode = false;
        adapter.notifyDataSetChanged();
        mToDialogRecordsCallbackListener.resetBtnsText(false);
    }

    /**
     * 显示编辑模式下的 UI
     * @return void
     * @date 2021/10/22 20:29
     */
    public void setEditModeUI(){
        adapter.isShowCheckBox(true);
        adapter.initMap();
        mIsEditMode = true;
        adapter.setChecked_num(mToDialogRecordsCallbackListener.getCheckedNumTv());
        search_layout.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        // 通知修改按钮文字描述
        mToDialogRecordsCallbackListener.resetBtnsText(true);
    }


    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * 加载AlertDialog弹窗，并设置一些相关属性
     * @param context:
     * @param resource:
     * @param width:
     * @param height:
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @date 2021/10/22 20:09
     */
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


    /**
     * 修改操作，对用户所输入的数据进行判断，直至获得正确的数据
     * @param alterNameString:
     * @param alterUrlString:
     * @param bookmarkBean:
     * @param dialog:
     * @return void
     * @date 2021/10/22 20:10
     */
    public void handleAlterInput(String alterNameString, String alterUrlString, Bookmark bookmarkBean, AlertDialog dialog){
        if (bookmarkBean.getIsFolder() == IS_FOLDER && isNameNull(alterNameString)){//文件夹的情况
            int[] isFolders = {1};
            Integer isexit = bookmarkViewModel.isNewBookmarkNameExit(alterNameString, isFolders);
            if (isexit == 1){
                Toast.makeText(requireContext(), "名称已存在", Toast.LENGTH_SHORT).show();
            }else{
                alterBookmark(bookmarkBean, alterNameString, "");//进行数据修改操作
                dialog.dismiss();
            }
        }else if (bookmarkBean.getIsFolder() != IS_FOLDER && isNameNull(alterNameString) && !isUrlFaultOrNull(alterUrlString)){
            int[] isFolders = {0};
            Integer isNameExit = bookmarkViewModel.isNewBookmarkNameExit(alterNameString, isFolders);
            Integer isUrlExit = bookmarkViewModel.isNewUrlExit(alterUrlString);
            if (isNameExit == 1){
                Toast.makeText(requireContext(), "名称已存在", Toast.LENGTH_SHORT).show();
            }else if(isUrlExit == 1) {
                Toast.makeText(requireContext(), "网址已存在", Toast.LENGTH_SHORT).show();
            }else{
                alterBookmark(bookmarkBean, alterNameString, alterUrlString);
                dialog.dismiss();
            }
        }
    }


    /**
     * 弹窗确认删除
     * @param context:
     * @param needDelete:
     * @param num:
     * @param upper:
     * @return void
     * @date 2021/10/22 20:14
     */
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
                Toast.makeText(requireContext(), "删除成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /*
     * 判断所输入的名称是否为空
     * @param name:
     * @return boolean
     * @date 2021/10/22 20:11
     */
    public boolean isNameNull(String name){
        if(name.trim().length() == 0){
            Toast.makeText(requireContext(), "名称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 修改书签的操作(名称和网址)
     * @param bookmarkBean:
     * @param name:
     * @param url:
     * @return void
     * @date 2021/10/22 20:12
     */
    public void alterBookmark(Bookmark bookmarkBean, String name, String url){
        bookmarkBean.setBname(name);
        bookmarkBean.setBurl(url);
        bookmarkViewModel.alterBookmark(bookmarkBean);
        Toast.makeText(requireContext(), "修改成功", Toast.LENGTH_SHORT).show();
    }


    /**
     * 判断网址的格式是否正确，并且是否为空
     * @param url:
     * @return boolean
     * @date 2021/10/22 20:13
     */
    public boolean isUrlFaultOrNull(String url){
        if (url.trim().length() == 0){
            Toast.makeText(requireContext(), "网址不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }else{
//            String regex = "(ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\-\\.\\?\\,\\'\\/\\\\&%\\+\\$#_=]*)?";
            String regex = "(ht|f)tp(s?)://[0-9a-zA-Z]([-.w]*[0-9a-zA-Z])*(:(0-9)*)*(/?)([a-zA-Z0-9-.?,'/\\\\&%+$#_=]*)?";
            Pattern pat = Pattern.compile(regex);
            Matcher mat = pat.matcher(url.trim());
            boolean result = mat.matches();
            if (!result){
                Toast.makeText(requireContext(), "网址格式不正确", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

}