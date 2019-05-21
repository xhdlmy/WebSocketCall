package com.xhd.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import com.xhd.base.R;

/**
 * Created by computer on 2018/7/6.
     * measure 过程 param.height 重写
     * scrollBy 过程
     * 回弹动画
     * NestedScrolling
 */

public class NestedScrollingDragView extends LinearLayout implements NestedScrollingParent {

    private static final java.lang.String TAG = "NestedScrolling";
    private View mHeaderView;
    private View mFooterView;
    private int mHeight;
    private static final int MAX_HEIGHT = 480;
    private static final int DURATION = 240;
    private View mChildView;
    private boolean mIsRunAnim; // 防止后续多次执行滑动

    public NestedScrollingDragView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        mHeaderView = new View(context);
        mHeaderView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        mFooterView = new View(context);
        mFooterView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NestedScrollingDragView);
        try {
            mHeight = (int) a.getDimension(R.styleable.NestedScrollingDragView_max_height, MAX_HEIGHT);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChildView = getChildAt(0);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, mHeight);
        addView(mHeaderView, 0, layoutParams);
        addView(mFooterView, getChildCount(), layoutParams);
        // 初始上移，至 mHeaderView 全部隐藏
        scrollBy(0, mHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 修改 RecyclerHeight 也是关键?
        ViewGroup.LayoutParams params = mChildView.getLayoutParams();
        params.height = getMeasuredHeight();

    }

    /**
     * 监测到有嵌套滑动，问下是否接受嵌套滑动
     * @param nestedScrollAxes 滑动方向 @see ViewCompat#SCROLL_AXIS_HORIZONTAL
     * @return true 接受嵌套滑动
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                && target instanceof RecyclerView
                && !mIsRunAnim) {
            return true;
        }
        return false;
    }

    /**
     * 回弹动画
     */
    private class ProgressAnimation extends Animation {
        // 预留
        private float startProgress = 0;
        private float endProgress = 1;

        private ProgressAnimation(){
            mIsRunAnim = true;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float progress = ((endProgress - startProgress) * interpolatedTime) + startProgress;
            scrollBy(0, (int) ((mHeight - getScrollY()) * progress));
            // 动画播放完毕，又可开启联动
            if (progress == 1) mIsRunAnim = false;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            setDuration(DURATION);
            setInterpolator(new AccelerateInterpolator());
        }
    }

    /**
     * nestedChild 未滑动之前告知其准备滑动的情况
     * 当我们滚动 nestedChild 时，nestedChild 进行实际的滚动前，会先调用 nestParent 该方法，
     * nestedParent 在该方法中可以把 nestedChild 想要滚动的距离消耗掉一部分或是全部消耗
     * @param dx nestedChild 想要变化的 dx
     * @param dy nestedChild 想要变化的 dy
     * @param consumed 回传告知 nestedChild 当前父View 想要消耗的距离
     *                 consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离 好让其做出相应的调整 （例如，消耗 y 方向的一半 consumed[1] = dy/2）
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // 如果该View之上还有父View都交给我来处理
        getParent().requestDisallowInterceptTouchEvent(true);
        // dy>0 往上滑动，dy<0 往下滑动
        boolean hiddenTop = dy > 0 && getScrollY() < mHeight && !ViewCompat.canScrollVertically(target, -1);
        boolean showTop = dy < 0 && !ViewCompat.canScrollVertically(target, -1);
        boolean hiddenBottom = dy < 0 && getScrollY() > mHeight && !ViewCompat.canScrollVertically(target, 1);
        boolean showBottom = dy > 0 && !ViewCompat.canScrollVertically(target, 1);
        if (hiddenTop || showTop || hiddenBottom || showBottom) {
            scrollBy(0, dy / 2);
            consumed[1] = dy;
        }
        // 限制错位问题
        if (dy > 0 && getScrollY() > mHeight && !ViewCompat.canScrollVertically(target, -1)) {
            scrollTo(0, mHeight);
        }
        if (dy < 0 && getScrollY() < mHeight && !ViewCompat.canScrollVertically(target, 1)) {
            scrollTo(0, mHeight);
        }
    }

    /**
     * 当 child 调用stopNestedScroll 时调用
     */
    @Override
    public void onStopNestedScroll(View child) {
        startAnimation(new ProgressAnimation());
    }

    /**
     * fling - 急冲、猛动（速度很快地滑动）
     * 在嵌套滑动的子View未 fling 之前报告准备 fling 的情况
     * @param velocityX 水平方向速度
     * @param velocityY 垂直方向速度
     * @return true 代表 nestedParent 消耗了该 fling
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        // 当RecyclerView在界面之内交给它自己惯性滑动
        if (getScrollY() == mHeight) {
            return false;
        }
        return true;
    }

    /**
     * 限制滑动 移动x轴不能超出最大范围
     */
    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        } else if (y > mHeight * 2) {
            y = mHeight * 2;
        }
        super.scrollTo(x, y);
    }

}

