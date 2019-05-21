package com.cl.cloud.fragment;

import com.cl.cloud.dao.ReceiveBean;

import java.util.List;

/**
 * Created by work2 on 2019/5/20.
 */

public class PhoneFragment extends RecyclerFragment {

    @Override
    protected List<ReceiveBean> getReceiveData(int page, int per) {
        return mDaoAgent.queryAutoCallBeans(page, per);
    }
}
