package cn.njupt.assignment.tou.dao;

import cn.njupt.assignment.tou.entity.Bookmark;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface BookmarkDao {

    /**
     * 查询所有
     *
     * @return
     */
    @Query("SELECT * FROM bookmark order by sort desc")
    LiveData<List<Bookmark>> getBookmarkAll();

    //根据upper打开相应的文件夹,查询与文件夹id值一致的upper
    @Query("SELECT * FROM bookmark where upper = :id order by sort desc")
    LiveData<List<Bookmark>> getUpperBookmark(int id);

    //根据id拿到子书签中的文件夹或者网址
    @Query("SELECT * FROM bookmark where upper = :id and isFolder in (:isFolders)")
    List<Bookmark> getBookmarkOfSubfolder(int id, int[] isFolders);

    //获取所有文件夹
    @Query("SELECT * FROM bookmark where isFolder = 1 and id not in (:id) and id <> :upper")
    List<Bookmark> getAllFolder(List<Integer> id, int upper);

    //判断书签名是否已经存在
    @Query("SELECT CASE WHEN count(bname) > 0 THEN 1 else 0 END FROM bookmark where bname = :name and isFolder in (:isFolders)")
    Integer isNewBookmarkNameExit(String name, int[] isFolders);

    //判断网址是否已经存在
    @Query("SELECT CASE WHEN count(burl) > 0 THEN 1 else 0 END FROM bookmark where burl = :url and isFolder = 0")
    Integer isNewUrlExit(String url);

    //获取upper与文件夹id相同的书签的sort的最大值
    @Query("SELECT CASE WHEN max(sort) is null THEN -1 ELSE max(sort) END FROM bookmark where upper = :id")
    Integer getMaxSort(int id);

    //根据指定字段模糊查询，根据输入的搜索内容搜索网址的名称name和网址url
    @Query("SELECT * FROM bookmark WHERE bname like '%'||:input||'%' or burl like '%'||:input||'%' order by id desc")
    LiveData<List<Bookmark>> loadBookmarkByinput(String input);

    //项数据库添加数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookmark(Bookmark... bookmarkBeans);

    //修改数据，根据id重新设置文件夹的书签数目
    @Query("update bookmark set bnum = bnum + :num where isFolder = 1 and id = :id")
    void updateBookmark(Integer id, int num);

    //当移动书签到别的文件夹时，更新所选择书签的upper
    @Query("update bookmark set upper = :id where id in (:checkedIds)")
    void updateUpperOfBookmark(int id, List<Integer> checkedIds);

    //修改书签
    @Update()
    void alterBookmark(Bookmark... bookmarkBeans);

    // 删除数据
    @Delete()
    void deleteBookmarks(Bookmark... bookmarkBeans);

    //根据url来删除网址书签
    @Query("select * from bookmark where burl = :url")
    Bookmark getBookmarkBasisUrl(String url);
}
