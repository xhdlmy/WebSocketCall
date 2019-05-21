package com.xhd.base.app;

import android.app.Activity;

import java.util.Stack;

public class ActivityStackManager {

    private static final String TAG = ActivityStackManager.class.getSimpleName();
    private static ActivityStackManager instance;

    public static ActivityStackManager getInstance() {
        if(instance == null) instance = new ActivityStackManager();
        return instance;
    }

    // search-查找基于1-base的栈位置 push-推入栈 pop-弹出栈 peek-查看栈顶元素
    private Stack<Activity> mStack;

    private ActivityStackManager() {
        mStack = new Stack<Activity>();
    }

    public void addActivity(Activity activity) {
        mStack.push(activity);
    }

    public void removeActivity(Activity activity) {
        mStack.remove(activity);
        activity.finish();
    }

    public Activity getTopActivity() {
        if (mStack.empty()) return null;
        return mStack.peek();
    }

    public boolean isTopActivity(Activity activity) {
        if (mStack.empty()) return false;
        return activity == mStack.peek();
    }

    public int getActSize() {
        return mStack.size();
    }

    public boolean isLastActivity(){
        return getActSize() == 1;
    }

    public void finishAllActivity() {
        if(mStack.size() <= 0) return;
        for (int i = 0; i < mStack.size(); i++){
            Activity peek = mStack.peek();
            peek.finish();
        }
    }

}


