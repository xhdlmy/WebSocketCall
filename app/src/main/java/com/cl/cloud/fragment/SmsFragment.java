package com.cl.cloud.fragment;

import com.cl.cloud.R;
import com.cl.cloud.dao.ReceiveBean;
import com.xhd.base.fragment.BaseFragment;

import java.util.List;

/**
 * Created by work2 on 2019/5/20.
 */

public class SmsFragment extends RecyclerFragment {

    @Override
    protected List<ReceiveBean> getReceiveData(int page, int per) {
        return mDaoAgent.querySendSmsBeans(page, per);
    }
}
