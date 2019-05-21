package com.xhd.base.adapter;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.xhd.base.R;
import com.xhd.base.adapter.anim.AlphaInAnimation;
import com.xhd.base.adapter.anim.BaseAnimation;
import com.xhd.base.adapter.anim.ScaleInAnimation;
import com.xhd.base.adapter.anim.SlideInBottomAnimation;
import com.xhd.base.adapter.anim.SlideInLeftAnimation;
import com.xhd.base.adapter.anim.SlideInRightAnimation;
import com.xhd.base.adapter.loadmore.LoadMoreItem;
import com.xhd.base.adapter.loadmore.SimpleLoadMoreItem;
import com.xhd.base.util.LogUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * Adapter模式：数据与显示分离
 * BaseViewHolder:负责View
 * BaseRecyclerAdapter<T>:负责绑定数据

    loadmore 一般写法：

        success:

             List<xxxBean> list = xxxResp.getResults();
             if(list != null && list.size() != 0) {
                 mPager++;
                 mAdapter.addDatas(results);
                 mAdapter.loadComplete();
             }else{
                mAdapter.loadEnd();
             }

        failed:

            mAdapter.loadFail();

 */


public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {

    public static final String TAG = BaseRecyclerAdapter.class.getSimpleName();

    protected Context mContext;

    protected int mLayoutId;
    protected List<T> mDatas;

    /**
     * MultipleType 的值不能为 该三种中的任何一个
     */
    public static final int DATA_VIEW = 0; // super.getItemViewType(position) == 0, 一定为0
    public static final int EMPTY_VIEW = -1;
    public static final int LOADING_VIEW = -2;
    public static final int HEADER_VIEW = -3;
    public static final int FOOTER_VIEW = -4;

    // header footer
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    private boolean mEmptyHeaderEnable = true;
    private boolean mEmptyFooterEnable = true;

    // empty view
    private @LayoutRes
    int mEmptyResId = R.layout.item_recycler_empty;

    // loadmore view
    private LoadMoreItem mLoadMoreItem = new SimpleLoadMoreItem();
    private OnLoadMoreListener mLoadMoreListener;
    private boolean mLoadMoreEnable = true; // 默认开启

    // onclick event
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    // multiple item
    private IMultiTypeSupport<T> mMultiTypeSupport;

    // animation
    public static final int ALPHAIN = 1;
    public static final int SCALEIN = 2;
    public static final int SLIDEIN_BOTTOM = 3;
    public static final int SLIDEIN_LEFT = 4;
    public static final int SLIDEIN_RIGHT = 5;

    @IntDef({ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {
    }

    private BaseAnimation mCustomAnimation; // 自定义动画
    private BaseAnimation mSelectedAmimation = new AlphaInAnimation(); // 提供现有可选择动画
    private Interpolator mInterpolator = new LinearInterpolator();
    public static final int ANIM_DURATION = 300;
    private int mDuration = ANIM_DURATION;
    private boolean mIsAnimationEnable = true; // 默认开启动画
    private boolean mIsAnimationOnlyFirst = false; // 默认每一次加载都开启动画

    /**
     * 构造方法
     * @param context
     * @param layoutId data layout
     * @param datas
     */
    public BaseRecyclerAdapter(@NonNull Context context, @LayoutRes int layoutId, List<T> datas) {
        mContext = context;
        mLayoutId = layoutId;
        mDatas = datas;
        if (datas == null) mDatas = new ArrayList<>();
        mHeaderLayout = new LinearLayout(mContext);
        mFooterLayout = new LinearLayout(mContext);
    }

    @Override
    public int getItemCount() {
        int count;
        if (getEmptyCount() == 1) {
            count = 1;
            if (mEmptyHeaderEnable && getHeaderCount() != 0) count++;
            if (mEmptyFooterEnable && getFooterCount() != 0) count++;
        } else {
            count = getHeaderCount() + mDatas.size() + getFooterCount() + getLoadMoreCount();
        }
        return count;
    }

    public int getDataCount(){
        return mDatas.size();
    }

    // 0 or 1
    public int getHeaderCount() {
        if (mHeaderLayout == null || mHeaderLayout.getChildCount() == 0) return 0;
        return 1;
    }

    // 0 or 1
    public int getFooterCount() {
        if (mFooterLayout == null || mFooterLayout.getChildCount() == 0) return 0;
        return 1;
    }

    // 0 or 1
    public int getEmptyCount(){
        if (mDatas != null && mDatas.size() != 0) return 0;
        if (mEmptyResId == 0) return 0;
        return 1;
    }

    // 0 or 1
    public int getLoadMoreCount() {
        if (mDatas == null || mDatas.size() == 0) return 0;
        if (mLoadMoreListener == null) return 0;
        if (!mLoadMoreEnable) return 0; // 加载关闭，则不显示此 LoadMoreItem
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        // EmptyView
        if (getEmptyCount() == 1) {
            boolean header = mEmptyHeaderEnable && getHeaderCount() != 0;
            switch (position) {
                case 0:
                    if (header) {
                        return HEADER_VIEW;
                    } else {
                        return EMPTY_VIEW;
                    }
                case 1:
                    if (header) {
                        return EMPTY_VIEW;
                    } else {
                        return FOOTER_VIEW;
                    }
                case 2:
                    return FOOTER_VIEW;
                default:
                    return EMPTY_VIEW;
            }
        }
        // DataView
        int headerNum = getHeaderCount();
        if (position < headerNum) {
            return HEADER_VIEW;
        } else {
            int adjPosition = position - headerNum;
            int dataSize = mDatas.size();
            if (adjPosition < dataSize) {
                return getMultiItemViewType(adjPosition);
            } else {
                adjPosition = adjPosition - dataSize;
                int footerNum = getFooterCount();
                if (adjPosition < footerNum) {
                    return FOOTER_VIEW;
                } else {
                    return LOADING_VIEW;
                }
            }
        }
    }

    protected int getMultiItemViewType(int position) {
        if (mMultiTypeSupport != null) {
            int itemViewType = mMultiTypeSupport.getItemViewType(position, mDatas.get(position));
            if(itemViewType == DATA_VIEW
                    || itemViewType == EMPTY_VIEW
                    || itemViewType == LOADING_VIEW
                    || itemViewType == HEADER_VIEW
                    || itemViewType == FOOTER_VIEW){
                throw new RuntimeException("multiple type support can't use the same type value(int)");
            }
            return itemViewType;
        }
        return DATA_VIEW; // super.getItemViewType(position) == 0
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.i(TAG, "onCreateViewHolder");
        BaseViewHolder viewHolder = null;
        switch (viewType) {
            case HEADER_VIEW:
                viewHolder = BaseViewHolder.create(mHeaderLayout);
                break;
            case FOOTER_VIEW:
                viewHolder = BaseViewHolder.create(mFooterLayout);
                break;
            case EMPTY_VIEW:
                viewHolder = BaseViewHolder.create(mContext, mEmptyResId, parent);
                break;
            case LOADING_VIEW:
                viewHolder = BaseViewHolder.create(mContext, mLoadMoreItem.getLayoutId(), parent);
                // 加载更多失败后，再次加载
                viewHolder.getView(mLoadMoreItem.getLoadFailViewId()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mLoadMoreItem.getStatus() == LoadMoreItem.STATUS_FAIL) {
                            mLoadMoreItem.setStatus(LoadMoreItem.STATUS_DEFAULT);
                            notifyItemChanged(getLoadMorePosition()); // onBindViewHolder 会自动执行 startLoadMore();
                        }
                    }
                });
                break;
            default:
                // 根据是否有 Multi 支持来判断
                if(mMultiTypeSupport != null) {
                    viewHolder = BaseViewHolder.create(mContext, mMultiTypeSupport.getLayoutId(viewType), parent);
                }else{
                    viewHolder = BaseViewHolder.create(mContext, mLayoutId, parent);
                }
                bindOnClickListener(viewHolder);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        LogUtils.i(TAG, "onBindViewHolder");
        final BaseViewHolder viewHolder = (BaseViewHolder) holder;
        // 自动加载更多
        if (position == getItemCount() - 1){
            startLoadMore();
        }

        int viewType = viewHolder.getItemViewType();
        switch (viewType) {
            case HEADER_VIEW:
                LogUtils.i(TAG, "onBindHeaderView");
                break;
            case EMPTY_VIEW:
                break;
            case FOOTER_VIEW:
                break;
            case LOADING_VIEW:
                mLoadMoreItem.convert(viewHolder);
                break;
            default:
                int adjustPos = position - getHeaderCount();
                LogUtils.i(TAG, "onBindDataView : " );
                onBindData(viewHolder, mDatas.get(adjustPos), adjustPos);
        }
    }

    protected abstract void onBindData(final BaseViewHolder holder, T data, final int position);

     /*========================== 以下：header footer ===========================*/

    public LinearLayout getHeaderLayout() {
        return mHeaderLayout;
    }

    public LinearLayout getFooterLayout() {
        return mFooterLayout;
    }

    // 0 or -1
    private int getHeaderViewPosition() {
        // Return to header view notify position
        if (getEmptyCount() == 1) {
            if (mEmptyHeaderEnable) return 0;
            return -1;
        } else {
            return 0;
        }
    }

    // lastPos or -1
    private int getFooterViewPosition() {
        // Return to footer view notify position
        if (getEmptyCount() == 1) {
            int position = 1;
            if (mEmptyHeaderEnable && getHeaderCount() != 0) position++;
            if (mEmptyFooterEnable) return position;
            return -1;
        } else {
            return getHeaderCount() + mDatas.size();
        }
    }

    public BaseRecyclerAdapter<T> addHeaderView(View header) {
        return addHeaderView(header, -1);
    }

    public BaseRecyclerAdapter<T> addHeaderView(View header, int index) {
        return addHeaderView(header, index, LinearLayout.VERTICAL);
    }

    public BaseRecyclerAdapter<T> addHeaderView(View header, int index, int orientation) {
        if (orientation == LinearLayout.VERTICAL) {
            mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
            mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        } else {
            mHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
            mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
        }
        final int childCount = mHeaderLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mHeaderLayout.addView(header, index);
        // 只有一开始的时候才需要加上 mHeaderLayout
        if (mHeaderLayout.getChildCount() == 1) {
            int position = getHeaderViewPosition();
            if (position != -1) notifyItemInserted(position);
        }
        return this;
    }

    public BaseRecyclerAdapter<T> setHeaderView(View header) {
        return setHeaderView(header, 0, LinearLayout.VERTICAL);
    }

    public BaseRecyclerAdapter<T> setHeaderView(View header, int index) {
        return setHeaderView(header, index, LinearLayout.VERTICAL);
    }

    public BaseRecyclerAdapter<T> setHeaderView(View header, int index, int orientation) {
        if (mHeaderLayout == null || mHeaderLayout.getChildCount() <= index) {
            return addHeaderView(header, index, orientation);
        } else {
            mHeaderLayout.removeViewAt(index);
            mHeaderLayout.addView(header, index);
            return this;
        }
    }

    public BaseRecyclerAdapter<T> addFooterView(View footer) {
        return addFooterView(footer, -1, LinearLayout.VERTICAL);
    }

    public BaseRecyclerAdapter<T> addFooterView(View footer, int index) {
        return addFooterView(footer, index, LinearLayout.VERTICAL);
    }

    public BaseRecyclerAdapter<T> addFooterView(View footer, int index, int orientation) {
        if (orientation == LinearLayout.VERTICAL) {
            mFooterLayout.setOrientation(LinearLayout.VERTICAL);
            mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        } else {
            mFooterLayout.setOrientation(LinearLayout.HORIZONTAL);
            mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
        }
        final int childCount = mFooterLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mFooterLayout.addView(footer, index);
        // 只有一开始的时候才需要加上 mFooterLayout
        if (mFooterLayout.getChildCount() == 1) {
            int position = getFooterViewPosition();
            if (position != -1) notifyItemInserted(position);
        }
        return this;
    }

    public BaseRecyclerAdapter<T> setFooterView(View header) {
        return setFooterView(header, 0, LinearLayout.VERTICAL);
    }

    public BaseRecyclerAdapter<T> setFooterView(View header, int index) {
        return setFooterView(header, index, LinearLayout.VERTICAL);
    }

    public BaseRecyclerAdapter<T> setFooterView(View header, int index, int orientation) {
        if (mFooterLayout == null || mFooterLayout.getChildCount() <= index) {
            return addFooterView(header, index, orientation);
        } else {
            mFooterLayout.removeViewAt(index);
            mFooterLayout.addView(header, index);
            return this;
        }
    }

    public void removeHeaderView(View header) {
        if (getHeaderCount() == 0) return;
        mHeaderLayout.removeView(header);
        if (mHeaderLayout.getChildCount() == 0) {
            int position = getHeaderViewPosition();
            // mHeaderLayout 全部删除了
            if (position != -1) notifyItemRemoved(position);
        }
    }

    public void removeFooterView(View footer) {
        if (getFooterCount() == 0) return;
        mFooterLayout.removeView(footer);
        if (mFooterLayout.getChildCount() == 0) {
            int position = getFooterViewPosition();
            // mFooterLayout 全部删除了
            if (position != -1) notifyItemRemoved(position);
        }
    }

    public void removeAllHeaderView() {
        if (getHeaderCount() == 0) return;

        mHeaderLayout.removeAllViews();
        int position = getHeaderViewPosition();
        if (position != -1) notifyItemRemoved(position);
    }

    public void removeAllFooterView() {
        if (getFooterCount() == 0) return;

        mFooterLayout.removeAllViews();
        int position = getFooterViewPosition();
        if (position != -1) notifyItemRemoved(position);
    }

    /**
     * 供外界调用
     */
    public BaseRecyclerAdapter<T> setHeaderFooterEmptyEnable(boolean emptyHeaderEnable, boolean emptyFooterEnable) {
        mEmptyHeaderEnable = emptyHeaderEnable;
        mEmptyFooterEnable = emptyFooterEnable;
        return this;
    }

    /*=========================== 以下：loadmore =======================*/

    /**
     * 供外界调用
     * @param loadMoreItem 替换 loadmoreView
     */
    public BaseRecyclerAdapter<T> setLoadMoreView(LoadMoreItem loadMoreItem){
        this.mLoadMoreItem = loadMoreItem;
        return this;
    }

    /**
     * 供外界调用
     * @param enable 根据第一次加载是否就加载完毕来决定是否需要加载更多
     */
    public BaseRecyclerAdapter<T> setLoadMoreEnable(boolean enable){
        this.mLoadMoreEnable = enable;
        return this;
    }

    public BaseRecyclerAdapter<T> setOnLoadMoreListener(OnLoadMoreListener listener){
        this.mLoadMoreListener = listener;
        return this;
    }

    public int getLoadMorePosition(){
        return getItemCount() - 1;
    }

    // 触发：onBindViewHolder
    private void startLoadMore() {
        if (getLoadMoreCount() == 0) return;
        if (mLoadMoreItem.getStatus() != LoadMoreItem.STATUS_DEFAULT) return;
        mLoadMoreItem.setStatus(LoadMoreItem.STATUS_LOADING);
        Log.i(TAG, "mLoadMoreListener.onLoadMore");
        mLoadMoreListener.onLoadMore();
    }

    // onLoadMore结束后：有新增数据
    public void loadComplete() {
        if (getLoadMoreCount() == 0) return;
        Log.i(TAG, "loadComplete");
        mLoadMoreItem.setStatus(LoadMoreItem.STATUS_DEFAULT);
        notifyItemChanged(getLoadMorePosition());
    }

    // onLoadMore结束后：无更新数据
    public void loadEnd() {
        if (getLoadMoreCount() == 0) return;
        Log.i(TAG, "loadEnd");
        mLoadMoreItem.setStatus(LoadMoreItem.STATUS_END);
        notifyItemChanged(getLoadMorePosition());
    }

    // onLoadMore结束后：加载失败
    public void loadFail() {
        if (getLoadMoreCount() == 0) return;
        Log.i(TAG, "loadFail");
        mLoadMoreItem.setStatus(LoadMoreItem.STATUS_FAIL);
        notifyItemChanged(getLoadMorePosition());
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    /*=========================== 以下：empty layout =======================*/

    /**
     * 供外界调用
     * @param resId 替换 emptyView
     */
    public BaseRecyclerAdapter<T> setEmptyView(@LayoutRes int resId){
        this.mEmptyResId = resId;
        return this;
    }

    /*=========================== 以下：multiple type =======================*/

    /**
     * 供外界调用
     * @param support 更新 dataView tips:定义不同 Type 时不要与 DATA_VIEW EMPTY_VIEW LOADING_VIEW 一样
     */
    public BaseRecyclerAdapter<T> setMultiTypeView(IMultiTypeSupport<T> support){
        this.mMultiTypeSupport = support;
        return this;
    }


   /*=========================== 以下：mDatas update notifyChanged =======================*/

    public void setDatas(List<T> datas){
        mDatas = datas;
        if(mDatas == null) mDatas = new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void addDatas(List<T> datas){
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void addData(T data){
        mDatas.add(data);
        notifyItemInserted(mDatas.size()-1);
    }

    public void addData(int position, T data){
        mDatas.add(position, data);
        notifyItemInserted(position);
    }

    public void setData(int position, T data){
        mDatas.set(position, data);
        notifyItemChanged(position);
    }

    public void removeData(int position){
        mDatas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, mDatas.size());
    }

    public void moveTop(int position){
        // 交换顺序(position之前的元素统统往后一位)
        T t = mDatas.get(position);
        for (int i = position - 1; i >= 0; i--){
            T ti = mDatas.get(i);
            mDatas.set(i + 1, ti);
        }
        mDatas.set(0, t);
        notifyDataSetChanged();
    }

    /*=========================== 以下：item click event =======================*/

    public BaseRecyclerAdapter<T> setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener = listener;
        return this;
    }

    public BaseRecyclerAdapter<T> setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mOnItemLongClickListener = listener;
        return this;
    }

    private void bindOnClickListener(final BaseViewHolder viewHolder) {
        if (viewHolder == null || viewHolder.getConvertView() == null) return;

        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null) mOnItemClickListener.onItemClick(v, viewHolder.getLayoutPosition() - getHeaderCount());
            }
        });
        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnItemLongClickListener != null) mOnItemLongClickListener.onItemLongClick(v, viewHolder.getLayoutPosition()  - getHeaderCount());
                return true;
            }
        });
    }

    public interface OnItemClickListener {

        void onItemClick(View v, int position);

    }

    public interface OnItemLongClickListener {

        void onItemLongClick(View v, int position);

    }
    
    /*=========================== 以下：animation =======================*/

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type != EMPTY_VIEW && type != LOADING_VIEW) {
            addAnimation(holder);
        }
    }

    // add animation when you want to show
    private void addAnimation(RecyclerView.ViewHolder holder) {
        if (mIsAnimationEnable) {
            if (!mIsAnimationOnlyFirst) {
                BaseAnimation animation = mSelectedAmimation;
                if (mCustomAnimation != null) animation = mCustomAnimation;
                for (Animator anim : animation.getAnimators(holder.itemView)) {
                    startAnim(anim);
                }
            }
        }
    }

    // set anim to start when open loading
    protected void startAnim(Animator anim) {
        anim.setDuration(mDuration).start();
        anim.setInterpolator(mInterpolator);
    }

    /**
     * 供外界调用
     * @param animationType 选择现有提供的动画
     */
    public BaseRecyclerAdapter<T> setSelectedAnimation(@AnimationType int animationType) {
        switch (animationType) {
            case ALPHAIN:
                mSelectedAmimation = new AlphaInAnimation();
                break;
            case SCALEIN:
                mSelectedAmimation = new ScaleInAnimation();
                break;
            case SLIDEIN_BOTTOM:
                mSelectedAmimation = new SlideInBottomAnimation();
                break;
            case SLIDEIN_LEFT:
                mSelectedAmimation = new SlideInLeftAnimation();
                break;
            case SLIDEIN_RIGHT:
                mSelectedAmimation = new SlideInRightAnimation();
                break;
            default:
                break;
        }
        return this;
    }

    /**
     * 供外界调用
     * @param animation 选择自定义的动画
     */
    public BaseRecyclerAdapter<T> setCustomAnimation(BaseAnimation animation) {
        this.mCustomAnimation = animation;
        return this;
    }

    public BaseRecyclerAdapter<T> setAnimationEnable(boolean isAnimationEnable){
        this.mIsAnimationEnable = isAnimationEnable;
        return this;
    }

    /**
     * 供外界调用
     * @param isAnimationOnlyFirst 是否只是第一次显示时才加载动画
     */
    public BaseRecyclerAdapter<T> setAnimationOnlyFirst(boolean isAnimationOnlyFirst) {
        this.mIsAnimationOnlyFirst = isAnimationOnlyFirst;
        return this;
    }
        
}

