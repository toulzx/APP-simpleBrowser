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
import cn.njupt.assignment.tou.databinding.LayoutBookmarkEditDialogBinding;
import cn.njupt.assignment.tou.databinding.LayoutBookmarkCheckedMoveToFolderBinding;
import cn.njupt.assignment.tou.databinding.LayoutBookmarkDeleteAffirmDialogBinding;
import cn.njupt.assignment.tou.databinding.FragmentDialogRecordsBookmarkBinding;
import cn.njupt.assignment.tou.databinding.LayoutBookmarkNewFolderDialogBinding;
import cn.njupt.assignment.tou.entity.Bookmark;
import cn.njupt.assignment.tou.viewmodel.BookmarkViewModel;

public class RecordsBookmarkFragment extends Fragment {

    private static final String TAG = RecordsBookmarkFragment.class.getSimpleName();
    private static final int IS_FOLDER = 1;
    private View mView;

    //???????????????id????????????id???????????????
    private static List<Integer> folderIdList = new ArrayList<>();
    private static int currentFolderIndex = 0; // ???????????????????????????
    private static int currentFolderId = -1; // ?????????????????? id

    // ???????????????????????????????????????
    private static List<String> folderName = new ArrayList<>();

    //binding
    private FragmentDialogRecordsBookmarkBinding binding;
    private BookmarkViewModel bookmarkViewModel;

    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;

    //????????????????????????
    private LinearLayout no_bookmark;

    //?????????????????????????????????
    private LinearLayout search_layout;
    private EditText search_input;

    InputMethodManager mInputMethodManager;

    private boolean mIsEditMode = false;//????????????????????????????????????false??????????????????????????????
    private boolean mIsCheckedAll = false;//?????????????????????????????????
    private boolean mIsOpenSearchModule = false;//????????????????????????????????????????????????xml ????????????????????? gone
    private static List<Object> searchResult = new ArrayList<>();//??????????????????????????????

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
        // Fragment ??????????????????????????????????????????
        binding = null;
    }



    /*----------------------------------------------------------------------------------------------------------------*/


    /**
     * ???????????????
     * @return void
     * @date 2021/10/22 16:20
     */
    private void initView(){
        //???????????????
        recyclerView = binding.bookmarkShow;
        //????????????????????????
        no_bookmark = binding.noBookmark;
        //????????????????????????
        search_layout = binding.searchLayout;
        search_input = binding.searchInput;

        mInputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * ?????????????????????
     * @return void
     * @date 2021/10/22 16:44
     */
    private void initData() {

        // ???????????????????????? id ?????? ids?????? -1
        if (folderIdList.size() == 0){
            folderIdList.add(-1);
        }

        // ??????????????????????????????????????? false
        if (searchResult.size() == 0){
            searchResult.add(false);
        }

        // ??????????????? folder_name ???????????? ????????????
        if(folderName.size() == 0){

            folderName.add("??????");
        }

    }


    /**
     * ????????? recycleView ????????? Adapter
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
                // ??????????????????
                loadPopupMenu(view, bookmarkBean);
            }
        });

        recyclerView.setItemViewCacheSize(-1);//??????-1  ????????????
        recyclerView.setAdapter(adapter);
    }

    /**
     * ????????? LiveData
     * @return void
     * @date 2021/10/22 20:24
     */
    private void initLiveData() {
        LiveData<List<Bookmark>> listLiveData;
        //?????????????????????????????????
        if (searchResult.get(currentFolderIndex) instanceof Boolean){
            listLiveData = bookmarkViewModel.getUpperBookmark(currentFolderId);
        }else{
            listLiveData = bookmarkViewModel.getBookmarkByInput(searchResult.get(currentFolderIndex).toString());
        }
        listLiveData.observe(getViewLifecycleOwner(), (List<Bookmark> bookmarkList)-> {
            adapter.setBookmarkList(bookmarkList);
            adapter.initMap();//????????????????????????map
            adapter.notifyDataSetChanged();
            if (bookmarkList.size() == 0){
                no_bookmark.setVisibility(View.VISIBLE);
            }else{
                no_bookmark.setVisibility(View.GONE);
            }
        });
    }


    /**
     * ??????????????????????????????
     * @return void
     * @date 2021/10/22 20:26
     */
    private void initListener() {

        binding.setOperationclick((View v) -> {
            int bottom_id = v.getId();
            if (bottom_id == R.id.input_clear){//???????????????
                if (search_input.getText().toString().trim().length() != 0) {
                    search_input.setText("");
                } else {//??????????????????????????????????????????????????????
                    setSearchModule(mIsOpenSearchModule);
                }
            }
        });

        /* callback */
        RecordsInDialogFragment.setToBookmarkCallbackListener(new ToBookmarkCallbackListener() {
            @Override
            public void onMenuButtonClick(View view) {
                if (mIsEditMode) {   // bookmark ???????????? => ?????????????????????????????? => ??????????????????
                    checkedDelete(adapter.getCheckedItems());
                } else {    // bookmark ???????????? => ?????????????????????????????? => ?????????????????????
                    // View??????PopupMenu???????????????View?????????
                    android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(getContext(), view);
                    // menu??????
                    if (currentFolderIndex == 0) {
                        popupMenu.getMenuInflater().inflate(R.menu.bookmark_menu, popupMenu.getMenu());
                    } else {
                        popupMenu.getMenuInflater().inflate(R.menu.bookmark_menu_in_folder, popupMenu.getMenu());
                    }
                    // menu???item????????????
                    popupMenu.setOnMenuItemClickListener(item -> {
                        //????????????
                        switch (item.getItemId()){
                            // ??????
                            case R.id.bookmark_menu_search:
                                setSearchModule(mIsOpenSearchModule);
                                break;
                            // ???????????????
                            case R.id.bookmark_menu_new_folder:
                                newFolderDialog(requireContext());
                                break;
                            // ????????????
                            case R.id.bookmark_menu_edit:
                                loadEditOperation();
                                break;
                            default:
                                break;
                        }
                        return false;
                    });

                    // PopupMenu????????????
                    popupMenu.setOnDismissListener(menu -> {//??????????????????
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
                    Toast.makeText(requireContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
                } else {
                    moveBookmark(requireContext());//???????????????????????????????????????????????????
                }
            }

            @Override
            public void onCheckAllButtonClick(TextView checked_all, TextView checked_num) {
                if (!mIsCheckedAll) {
                    mIsCheckedAll = true;
                    checkedAll(checked_all, checked_num);//??????
                } else {
                    mIsCheckedAll = false;
                    checkedNone(checked_all, checked_num);//?????????
                }
            }

            @Override
            public void onCancelEditModeButtonClick() {
                setOriginModeUI();
            }
        });
    }

    /**
     * ????????????
     * @return void
     * @date 2021/10/22 20:48
     */
    public void searchBookmark(){
        search_input.setOnEditorActionListener((TextView v, int actionId, KeyEvent event)-> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                // -2????????????????????????????????????????????????
                actionStart(requireContext(),-2,"????????????",v.getText().toString());
                // ??????????????????
                search_input.clearFocus();
                mInputMethodManager.hideSoftInputFromWindow(search_input.getWindowToken(), 0);
            }
            return false;
        });
    }


    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * ??????????????????????????????
     * @param isNowShown ??????????????????????????????true:??????
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
     * ?????????????????????????????????
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
        searchResult.add(search_result);//???????????????????????????????????????????????????????????????

        if (mToDialogRecordsCallbackListener != null) {
            mToDialogRecordsCallbackListener.refreshFragment(currentFolderIndex, folderName.get(currentFolderIndex - 1));
        }

    }

    /**
     * ??????????????????????????????
     * @param view:
     * @param bookmarkBean:
     * @return void
     * @date 2021/10/22 20:06
     */
    public void loadPopupMenu(View view, Bookmark bookmarkBean){
        //??????????????????????????????????????????11???
        PopupMenu popup = new PopupMenu(requireContext(), view);//?????????????????????????????????view
        //?????????????????????
        MenuInflater inflater = popup.getMenuInflater();
        //????????????
        inflater.inflate(R.menu.bookmark_edit_menu, popup.getMenu());
        MenuItem copy_url_item = popup.getMenu().findItem(R.id.action_copy_url);
        copy_url_item.setVisible(bookmarkBean.getIsFolder() != IS_FOLDER);
        //??????????????????????????????
        popup.setOnMenuItemClickListener((MenuItem item)-> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_alter){
                loadAlterDialog(requireContext(), bookmarkBean);
            }else if (itemId == R.id.action_delete){//????????????
                List<Bookmark> checkedItems = new ArrayList<>();
                checkedItems.add(bookmarkBean);
                checkedDelete(checkedItems);//??????
            }else if (itemId == R.id.action_copy_url){
                copyContent(bookmarkBean.getBurl());
                Toast.makeText(requireContext(), "????????????", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        //??????(??????????????????????????????)
        popup.show();
    }


    /**
     * ?????????????????????UI???????????????????????????
     * @param context:
     * @return void
     * @date 2021/10/22 20:27
     */
    public void newFolderDialog(Context context){
        Map<String, Object> dialogMap = loadAlertDialog(context, R.layout.layout_bookmark_new_folder_dialog, 900, LinearLayout.LayoutParams.WRAP_CONTENT);
        View dialogView = (View) dialogMap.get("dialogView");
        AlertDialog dialog = (AlertDialog) dialogMap.get("dialog");
        //??????????????????
        LayoutBookmarkNewFolderDialogBinding dialogBinding = LayoutBookmarkNewFolderDialogBinding.bind(dialogView);//??????databinding
        EditText editText = dialogBinding.newFolderName;
        dialogBinding.setNewfolderclick((View v)-> {
            int cancelOrAffirm = v.getId();
            if (cancelOrAffirm == R.id.add_cancel){
                dialog.dismiss();
            }else if (cancelOrAffirm == R.id.add_affirm){
                String name = editText.getText().toString();
                if(isNameNull(name)){
                    addNewFolder(name, dialog);//???????????????
                }
            }
        });
    }

    /**
     * ????????????
     * ???????????????????????????????????????
     * @return void
     * @date 2021/10/22 20:28
     */
    public void loadEditOperation(){
        if (mIsEditMode){
            setOriginModeUI();
        }else{
            if(adapter.getItemCount() == 0){
                Toast.makeText(requireContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            setEditModeUI();
        }
    }


    // ??????
    public void checkedAll(TextView checked_all, TextView checked_num){
        int num = adapter.getItemCount();
        Map<Integer, Boolean> map = new HashMap<>();
        for (int i = 0; i < num; i++) {
            map.put(i, true);
        }
        adapter.setMap(map);
        adapter.notifyDataSetChanged();
        checked_all.setText("?????????");
        checked_num.setText("?????????" + num + "???");
    }

    // ?????????
    public void checkedNone(TextView checked_all, TextView checked_num){
        Map<Integer, Boolean> map = new HashMap<>();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            map.put(i, false);
        }
        adapter.setMap(map);
        adapter.notifyDataSetChanged();
        checked_all.setText("??????");
        checked_num.setText("?????????0???");
    }


    /**
     * ????????????????????????
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
     * ??????????????????????????????
     * @param context:
     * @return void
     * @date 2021/10/22 20:47
     */
    public void moveBookmark(Context context){
        Map<String, Object> dialogMap = loadAlertDialog(context, R.layout.layout_bookmark_checked_move_to_folder, 800, 1000);
        View dialogView = (View) dialogMap.get("dialogView");
        AlertDialog dialog = (AlertDialog) dialogMap.get("dialog");
        //???????????????????????????
        final int[] movetoId = {-1};//-1??????????????????????????????
        //??????????????????
        LayoutBookmarkCheckedMoveToFolderBinding dialogBinding = LayoutBookmarkCheckedMoveToFolderBinding.bind(dialogView);//??????databinding
        TextView checked_move_to_folder_text = dialogBinding.checkedMoveToFolderText;
        RecyclerView recyclerView = dialogBinding.moveToFolderShow;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //????????????????????????????????????
        List<Bookmark> checkedItems = adapter.getCheckedItems();//???????????????????????????
        List<Integer> checkedFolderIds = new ArrayList<>();//????????????????????????????????????id?????????
        List<Bookmark> temp = new ArrayList<>(checkedItems);
        int[] isFolders = {1};
        //?????????????????????????????????????????????(??????????????????????????????????????????????????????????????????????????????????????????????????????????????????)
        // ?????????????????????????????????????????????????????????
        // TODO: bug: ????????????????????????????????????????????????????????????????????????????????????????????????
        for(int i = 0;i < temp.size();i++){
            if (checkedItems.get(i).getIsFolder() == 1){
                checkedFolderIds.add(checkedItems.get(i).getId());
                temp.addAll(bookmarkViewModel.getBookmarkOfSubfolder(checkedItems.get(i).getId(), isFolders));
            }
        }
        List<Bookmark> allFolder = bookmarkViewModel.getAllFolder(checkedFolderIds, currentFolderId);
        if (allFolder.size() == 0){
            checked_move_to_folder_text.setText("????????????????????????");
            LinearLayout move_affirm = dialogBinding.moveAffirm;
            View view = dialogBinding.boundary;
            move_affirm.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }
        MoveToFolderAdapter moveToFolderAdapter = new MoveToFolderAdapter(allFolder, (View view, Bookmark bookmarkBean)-> {
            movetoId[0] = bookmarkBean.getId();
            String selectedText = "????????????"+bookmarkBean.getBname();
            checked_move_to_folder_text.setText(selectedText);
        });
        recyclerView.setAdapter(moveToFolderAdapter);
        dialogBinding.setMovetofolderclick((View v)-> {
            int cancelOrAffirm = v.getId();
            if (cancelOrAffirm == R.id.move_cancel){
                dialog.dismiss();
            }else if (cancelOrAffirm == R.id.move_affirm){
                if (movetoId[0] == -1){
                    Toast.makeText(requireContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
                }else {
                    int checkedNum = checkedItems.size();
                    //?????????????????????????????????????????????
                    bookmarkViewModel.updateBookmark(movetoId[0], checkedNum);
                    //??????????????????????????????sort???upper
                    int checkedFolserSubMaxSort = bookmarkViewModel.getMaxSort(movetoId[0]);
                    int max = checkedFolserSubMaxSort + checkedNum;
                    for(Bookmark bookmarkBean:checkedItems){
                        bookmarkBean.setSort(max);
                        bookmarkBean.setUpper(movetoId[0]);
                        bookmarkViewModel.alterBookmark(bookmarkBean);
                        max -= 1;
                    }
                    //???????????????????????????????????????????????????(??????)
                    bookmarkViewModel.updateBookmark(currentFolderId,-checkedNum);
                    Toast.makeText(requireContext(), "????????????", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    public void loadAlterDialog(Context context, Bookmark bookmarkBean){
        Map<String, Object> dialogMap = loadAlertDialog(context, R.layout.layout_bookmark_edit_dialog, 900, LinearLayout.LayoutParams.WRAP_CONTENT);
        View dialogView = (View) dialogMap.get("dialogView");
        AlertDialog dialog = (AlertDialog) dialogMap.get("dialog");
        //??????databinding????????????????????????
        LayoutBookmarkEditDialogBinding dialogBinding = LayoutBookmarkEditDialogBinding.bind(dialogView);
        EditText alterName = dialogBinding.alterName;
        EditText alterUrl = dialogBinding.alterUrl;
        LinearLayout alter_url_layout = dialogBinding.alterUrlLayout;
        alterName.setText(bookmarkBean.getBname());
        alter_url_layout.setVisibility(bookmarkBean.getIsFolder() == IS_FOLDER ? View.GONE : View.VISIBLE);
        alterUrl.setText(bookmarkBean.getBurl());
        //??????????????????
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
     * ???????????????????????????????????????????????????????????????????????????
     * @param checkedItems:
     * @return void
     * @date 2021/10/22 20:13
     */
    public void checkedDelete(List<Bookmark> checkedItems){
        if (checkedItems.size() == 0){
            Toast.makeText(requireContext(), "??????????????????", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Bookmark> needDelete = new ArrayList<>(checkedItems);
        int upper = checkedItems.get(0).getUpper();
        int[] isFolders = {0, 1};
        for (int i = 0;i < needDelete.size(); i++){//????????????????????????????????????????????????
            if (needDelete.get(i).getIsFolder() == 1){
                needDelete.addAll(bookmarkViewModel.getBookmarkOfSubfolder(needDelete.get(i).getId(), isFolders));
            }
        }
        deleteAffirm(requireContext(), needDelete, checkedItems.size(), upper);
    }


    /**
     * ????????????????????????
     * @param content:
     * @return void
     * @date 2021/10/22 20:15
     */
    public void copyContent(String content){
        //???????????????????????????
        ClipboardManager cm = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // ?????????????????????ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // ???ClipData?????????????????????????????????
        cm.setPrimaryClip(mClipData);
    }


    /**
     * ???????????????
     * @param name:
     * @param dialog:
     * @return void
     * @date 2021/10/22 20:27
     */
    public void addNewFolder(String name, AlertDialog dialog){
        //?????????????????????????????????
        int[] isFolders = {1};
        Integer isexit = bookmarkViewModel.isNewBookmarkNameExit(name, isFolders);
        if(isexit == 1){
            Toast.makeText(requireContext(), "???????????????????????????????????????", Toast.LENGTH_SHORT).show();
        }else{
            Integer maxsort = bookmarkViewModel.getMaxSort(currentFolderId);
            bookmarkViewModel.insertBookmark(new Bookmark(name,"","",0,1, currentFolderId, maxsort == null?0:maxsort+1));
            bookmarkViewModel.updateBookmark(currentFolderId, 1);//??????????????????
            Toast.makeText(requireContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
            dialog.dismiss();//????????????
        }
    }

    /**
     * ???????????????????????? UI
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
     * ???????????????????????? UI
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
        // ??????????????????????????????
        mToDialogRecordsCallbackListener.resetBtnsText(true);
    }


    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * ??????AlertDialog????????????????????????????????????
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
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//????????????EditText????????????????????????
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
     * ????????????????????????????????????????????????????????????????????????????????????
     * @param alterNameString:
     * @param alterUrlString:
     * @param bookmarkBean:
     * @param dialog:
     * @return void
     * @date 2021/10/22 20:10
     */
    public void handleAlterInput(String alterNameString, String alterUrlString, Bookmark bookmarkBean, AlertDialog dialog){
        if (bookmarkBean.getIsFolder() == IS_FOLDER && isNameNull(alterNameString)){//??????????????????
            int[] isFolders = {1};
            Integer isexit = bookmarkViewModel.isNewBookmarkNameExit(alterNameString, isFolders);
            if (isexit == 1){
                Toast.makeText(requireContext(), "???????????????", Toast.LENGTH_SHORT).show();
            }else{
                alterBookmark(bookmarkBean, alterNameString, "");//????????????????????????
                dialog.dismiss();
            }
        }else if (bookmarkBean.getIsFolder() != IS_FOLDER && isNameNull(alterNameString) && !isUrlFaultOrNull(alterUrlString)){
            int[] isFolders = {0};
            Integer isNameExit = bookmarkViewModel.isNewBookmarkNameExit(alterNameString, isFolders);
            Integer isUrlExit = bookmarkViewModel.isNewUrlExit(alterUrlString);
            if (isNameExit == 1){
                Toast.makeText(requireContext(), "???????????????", Toast.LENGTH_SHORT).show();
            }else if(isUrlExit == 1) {
                Toast.makeText(requireContext(), "???????????????", Toast.LENGTH_SHORT).show();
            }else{
                alterBookmark(bookmarkBean, alterNameString, alterUrlString);
                dialog.dismiss();
            }
        }
    }


    /**
     * ??????????????????
     * @param context:
     * @param needDelete:
     * @param num:
     * @param upper:
     * @return void
     * @date 2021/10/22 20:14
     */
    public void deleteAffirm(Context context, List<Bookmark> needDelete, int num, int upper){
        Map<String, Object> dialogMap = loadAlertDialog(context, R.layout.layout_bookmark_delete_affirm_dialog, 800, LinearLayout.LayoutParams.WRAP_CONTENT);
        View dialogView = (View) dialogMap.get("dialogView");
        AlertDialog dialog = (AlertDialog) dialogMap.get("dialog");
        //??????????????????
        LayoutBookmarkDeleteAffirmDialogBinding dialogBinding = LayoutBookmarkDeleteAffirmDialogBinding.bind(dialogView);//??????databinding
        TextView textView = dialogBinding.deleteNum;
        String totalNum = "?????????"+needDelete.size();
        textView.setText(totalNum);
        dialogBinding.setDeleteclick((View v)-> {
            int cancelOrAffirm = v.getId();
            if (cancelOrAffirm == R.id.delete_cancel){
                dialog.dismiss();
            }else if (cancelOrAffirm == R.id.delete_affirm){
                //??????????????????
                bookmarkViewModel.deleteBookmarks(needDelete);
                //??????????????????id???????????????????????????????????????????????????????????????????????????????????????
                bookmarkViewModel.updateBookmark(upper, -num);
                dialog.dismiss();
                Toast.makeText(requireContext(), "????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /*
     * ????????????????????????????????????
     * @param name:
     * @return boolean
     * @date 2021/10/22 20:11
     */
    public boolean isNameNull(String name){
        if(name.trim().length() == 0){
            Toast.makeText(requireContext(), "??????????????????", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * ?????????????????????(???????????????)
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
        Toast.makeText(requireContext(), "????????????", Toast.LENGTH_SHORT).show();
    }


    /**
     * ??????????????????????????????????????????????????????
     * @param url:
     * @return boolean
     * @date 2021/10/22 20:13
     */
    public boolean isUrlFaultOrNull(String url){
        if (url.trim().length() == 0){
            Toast.makeText(requireContext(), "??????????????????", Toast.LENGTH_SHORT).show();
            return true;
        }else{
//            String regex = "(ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\-\\.\\?\\,\\'\\/\\\\&%\\+\\$#_=]*)?";
            String regex = "(ht|f)tp(s?)://[0-9a-zA-Z]([-.w]*[0-9a-zA-Z])*(:(0-9)*)*(/?)([a-zA-Z0-9-.?,'/\\\\&%+$#_=]*)?";
            Pattern pat = Pattern.compile(regex);
            Matcher mat = pat.matcher(url.trim());
            boolean result = mat.matches();
            if (!result){
                Toast.makeText(requireContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

}