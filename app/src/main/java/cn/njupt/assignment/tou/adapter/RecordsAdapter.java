package cn.njupt.assignment.tou.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import cn.njupt.assignment.tou.fragment.RecordsBookmarkFragment;
import cn.njupt.assignment.tou.fragment.RecordsHistoryFragment;

public class RecordsAdapter extends FragmentStateAdapter {

    private static final String TAG = RecordsAdapter.class.getSimpleName();

    // index in viewPager2
    public static final int HISTORY_PAGE_INDEX = 0;
    public static final int BOOKMARK_PAGE_INDEX = 1;

    //用于存储更新 fragment 的特定标识(id)，初始值为任意不极端的数字即可
    public static final Long HISTORY_PAGE_DEFAULT_VALUE = 222L;
    public static final Long BOOKMARK_PAGE_DEFAULT_VALUE = 111L;
    private List<Long> FragmentIdList = Arrays.asList(HISTORY_PAGE_DEFAULT_VALUE, BOOKMARK_PAGE_DEFAULT_VALUE);

    //得用hashset防重，用于存储adapter内的顺序
    private HashSet<Long> creatIds = new HashSet<>();

    public RecordsAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    /**
     * 设置不同的 id 从而使 viewPager2 刷新 fragment
     * 这里通过累加实现
     * @return void
     * @date 2021/10/23 15:24
     * @author tou
     */
    public void update() {
        FragmentIdList.set(BOOKMARK_PAGE_INDEX, FragmentIdList.get(BOOKMARK_PAGE_INDEX) + 1);
        notifyDataSetChanged();
    }


    /**
     * Provide a new Fragment associated with the specified position.
     * <p>
     * The adapter will be responsible for the Fragment lifecycle:
     * <ul>
     *     <li>The Fragment will be used to display an item.</li>
     *     <li>The Fragment will be destroyed when it gets too far from the viewport, and its state
     *     will be saved. When the item is close to the viewport again, a new Fragment will be
     *     requested, and a previously saved state will be used to initialize it.
     * </ul>
     *
     * @param position
     * @see ViewPager2#setOffscreenPageLimit
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (getItemViewType(position)) {
            case 222:
                fragment = new RecordsHistoryFragment();
                break;
            default:
                fragment = new RecordsBookmarkFragment();
                break;
        }
        creatIds.add(FragmentIdList.get(position));//创建的时候将未添加的fragment添加进来，每次刷新都会调用这里，其次调用containsItem
        return fragment;
    }


    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return FragmentIdList.size();
    }


    /**
     * Default implementation works for collections that don't add, move, remove items.
     * <p>
     * TODO(b/122670460): add lint rule
     * When overriding, also override {@link #containsItem(long)}.
     * <p>
     * If the item is not a part of the collection, return {@link RecyclerView#NO_ID}.
     *
     * @param position Adapter position
     * @return stable item id {@link RecyclerView.Adapter#hasStableIds()}
     */
    @Override
    public long getItemId(int position) {
        return FragmentIdList.get(position);
    }

    /**
     * Default implementation works for collections that don't add, move, remove items.
     * <p>
     * TODO(b/122670460): add lint rule
     * When overriding, also override {@link #getItemId(int)}
     *
     * @param itemId
     */
    @Override
    public boolean containsItem(long itemId) {
        return creatIds.contains(itemId);
    }

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     *
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>. Type codes need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {
        return FragmentIdList.get(position).intValue();
    }
}
