package cn.njupt.assignment.tou.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author: sherman
 * @date: 2021/9/21
 * @description: 历史记录的实体类
 */
@Entity(tableName = "historyRecord")
public class HistoryRecord extends BaseObservable implements Parcelable,Comparable<HistoryRecord> {
    @PrimaryKey(autoGenerate = true) // 设置主键
    @ColumnInfo(name = "id") // 定义对应的数据库的字段名称
    private int id;// 主键

    @ColumnInfo(name = "hname")
    private String hname;// 网站标题

    @ColumnInfo(name = "hurl")
    private String hurl;// 网站链接

    @ColumnInfo(name = "hicon")
    private String hicon;// 网站图标

    @ColumnInfo(name = "hdate")
    private String hdate;// 访问网站的时间


    public HistoryRecord(String hname, String hurl, String hicon, String hdate) {
        this.hname = hname;
        this.hurl = hurl;
        this.hicon = hicon;
        this.hdate = hdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Bindable
    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    @Bindable
    public String getHurl() {
        return hurl;
    }

    public void setHurl(String hurl) {
        this.hurl = hurl;
    }

    @Bindable
    public String getHicon() {
        return hicon;
    }

    public void setHicon(String hicon) {
        this.hicon = hicon;
    }

    @Bindable
    public String getHdate() {
        return hdate;
    }

    public void setHdate(String hdate) {
        this.hdate = hdate;
    }

    protected HistoryRecord(Parcel in) {
        this.id = in.readInt();
        this.hname = in.readString();
        this.hurl = in.readString();
        this.hicon = in.readString();
        this.hdate = in.readString();
    }

    /**
     * @description 负责反序列化
     * @param
     * @return
     * @author sherman
     * @time 2021/9/19 20:58
     */
    public static final Creator<HistoryRecord> CREATOR = new Creator<HistoryRecord>() {
        /**
         * @description 从序列化对象中，获取原始的对象
         * @param
         * @return
         * @author sherman
         * @time 2021/9/19 21:01
         */
        @Override
        public HistoryRecord createFromParcel(Parcel source) {
            return new HistoryRecord(source);
        }

        /**
         * @description 创建指定长度的原始对象数组
         * @param
         * @return
         * @author sherman
         * @time 2021/9/19 21:01
         */
        @Override
        public HistoryRecord[] newArray(int size) {
            return new HistoryRecord[size];
        }
    };

    /**
     * @description 返回的是内容的描述信息，只针对一些特殊的需要描述信息的对象,需要返回1,其他情况返回0就可以
     * @param
     * @return
     * @author sherman
     * @time 2021/9/19 21:06
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @description 序列化
     * @param dest
     * @param flags
     * @return void
     * @author sherman
     * @time 2021/9/19 21:07
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.hname);
        dest.writeString(this.hurl);
        dest.writeString(this.hicon);
        dest.writeString(this.hdate);
    }

    @Override
    public int compareTo(HistoryRecord h) {
        return h.getId()-this.id;
    }

    @Override
    public String toString() {
        return "HistoryRecord{" +
                "id=" + id +
                ", hname='" + hname + '\'' +
                ", hurl='" + hurl + '\'' +
                ", hicon='" + hicon + '\'' +
                ", hdate='" + hdate + '\'' +
                '}';
    }
}
