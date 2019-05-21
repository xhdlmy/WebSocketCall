package com.cl.cloud.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cl.cloud.R;

/**
 * Created by computer on 2018/1/17.
 * MainActivity 底部 Tab ：关联具体 fragment 与 Tab 对应。
 */

public class MainBottomTab {
    
    public int drawableResId;
    public int textStringId;
    public Class fragmentCls;

    public MainBottomTab(int drawableResId, int textStringId, Class fragmentCls) {
        this.drawableResId = drawableResId;
        this.textStringId = textStringId;
        this.fragmentCls = fragmentCls;
    }

    public View getBottomTab(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.tab_indicator, null);
        ImageView iv = view.findViewById(R.id.iv_indicator);
        TextView tv = view.findViewById(R.id.tv_indicator);
        iv.setImageResource(drawableResId);
        tv.setText(textStringId);
        return view;
    }

}

