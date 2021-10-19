package cn.njupt.assignment.tou.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import cn.njupt.assignment.tou.entity.Bookmark;
import cn.njupt.assignment.tou.repository.BookmarkRepository;

public class BookmarkViewModel extends AndroidViewModel {private BookmarkRepository bookmarkRepository;

    public BookmarkViewModel(@NonNull Application application) {
        super(application);
        bookmarkRepository = new BookmarkRepository(application);
    }

    public LiveData<List<Bookmark>> getBookmarkAll(){
        return bookmarkRepository.getBookmarkall();
    }

    //获取当前层书签
    public LiveData<List<Bookmark>> getUpperBookmark(int id) {
        return bookmarkRepository.getUpperBookmark(id);
    }

    //判断文件名是否已经存在
    public Integer isNewBookmarkNameExit(String name, int[] isFolders){
        return bookmarkRepository.isNewBookmarkNameExit(name, isFolders);
    }

    //判断网址是否已经存在
    public Integer isNewUrlExit(String url){
        return  bookmarkRepository.isNewUrlExit(url);
    }

    //获取upper与文件夹id相同的书签的sort的最大值
    public Integer getMaxSort(int id){
        return bookmarkRepository.getMaxSort(id);
    }

    //获取所有文件夹
    public List<Bookmark> getAllFolder(List<Integer> id, int upper){
        return bookmarkRepository.getAllFolder(id, upper);
    }

    //根据id拿到子书签中的所有文件夹的id值
    public List<Bookmark> getBookmarkOfSubfolder(int id, int[] isFolders){
        return bookmarkRepository.getBookmarkOfSubfolder(id, isFolders);
    }

    //根据输入的字段进行模糊查询
    public LiveData<List<Bookmark>> getBookmarkByInput(String input){
        return bookmarkRepository.getBookmarkByinput(input);
    }

    public void deleteBookmarks(List<Bookmark> needDelete){
        bookmarkRepository.deleteBookmarks(needDelete);
    }

    //根据URL删除数据
    public Bookmark getBookmarkBasisUrl(String url){
        return bookmarkRepository.getBookmarkBasisUrl(url);
    }

    public void insertBookmark(Bookmark... bookmarkBeans){
        bookmarkRepository.insertBookmark(bookmarkBeans);
    }

    public void updateBookmark(int id, int num, Bookmark... bookmarkBeans){
        bookmarkRepository.updateBookmark(id, num, bookmarkBeans);
    }

    //当移动书签到别的文件夹时，更新所选择文件夹的upper
    public void updateUpperOfBookmark(int id, List<Integer> checkedIds, Bookmark... bookmarkBeans){
        bookmarkRepository.updateUpperOfBookmark(id, checkedIds, bookmarkBeans);
    }

    //修改书签
    public void alterBookmark(Bookmark... bookmarkBeans){
        bookmarkRepository.alterBookmark(bookmarkBeans);
    }
}
