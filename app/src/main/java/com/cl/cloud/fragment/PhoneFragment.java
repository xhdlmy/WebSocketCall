package com.cl.cloud.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cl.cloud.R;
import com.cl.cloud.app.App;
import com.cl.cloud.app.Constant;
import com.cl.cloud.dao.ReceiveBean;
import com.cl.cloud.dao.ReceiveBeanDaoAgent;
import com.cl.cloud.push.PushEntity;
import com.cl.cloud.util.TelePhonyHelper;
import com.xhd.base.adapter.BaseRecyclerAdapter;
import com.xhd.base.adapter.BaseViewHolder;
import com.xhd.base.fragment.BaseFragment;
import com.xhd.base.util.NetworkUtils;

import java.util.List;

/**
 * Created by work2 on 2019/5/20.
 */

public class PhoneFragment extends BaseFragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private BaseRecyclerAdapter<ReceiveBean> mAdapter;
    private List<ReceiveBean> mList;

    private ReceiveBeanDaoAgent mDaoAgent = ReceiveBeanDaoAgent.getInstance();

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_phone;
    }

    @Override
    protected void initView() {
        mSwipeRefreshLayout = mView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if(NetworkUtils.isNetworkConnected(App.getAppContext())){
                mSwipeRefreshLayout.setRefreshing(true);
                // TODO refreshData
            }else{
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mRecyclerView = mView.findViewById(R.id.recycler_view);
    }

    @Override
    public void initData() {
        mList = mDaoAgent.queryAutoCallBeans();
        mAdapter = new BaseRecyclerAdapter<ReceiveBean>(mActivity, R.layout.item_recycler_auto_phone, mList) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindData(BaseViewHolder holder, ReceiveBean data, int position) {
                TextView tvId = holder.getView(R.id.tv_id);
                TextView tvPhone = holder.getView(R.id.tv_phone);
                TextView tvName = holder.getView(R.id.tv_name);
                TextView tvTime = holder.getView(R.id.tv_time);
                tvId.setText("Id " + data.getId());
                PushEntity entity = data.getPushEntity();
                final String phone = entity.detail.get(Constant.KEY_RECEIVER);
                tvPhone.setText(mActivity.getString(R.string.phone) + " " + phone);
                tvName.setText(mActivity.getString(R.string.name) + " " + entity.detail.get(Constant.KEY_NAME));
                tvTime.setText(mActivity.getString(R.string.time) + " " + entity.respTime);
                // onClick
                View convertView = holder.getConvertView();
                convertView.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setTitle(mActivity.getString(R.string.sure_call) + phone);
                    builder.setCancelable(true);
                    builder.setPositiveButton(mActivity.getString(R.string.sure), (dialog, which) -> {
                        // 自动拨号
                        TelePhonyHelper.dealCall(entity);
                        dialog.dismiss();
                    });
                    builder.setNegativeButton(mActivity.getString(R.string.cancel), (dialog, which) -> {
                        dialog.dismiss();
                    });
                    builder.create().show();            
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }
}
