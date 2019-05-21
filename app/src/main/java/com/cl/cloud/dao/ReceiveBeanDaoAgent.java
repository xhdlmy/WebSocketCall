package com.cl.cloud.dao;

import com.cl.cloud.app.App;
import com.cl.cloud.push.PushEntity;
import com.cl.cloud.util.SpUtils;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReceiveBeanDaoAgent {

    private static ReceiveBeanDao sDao;

    private ReceiveBeanDaoAgent(){
        sDao = App.getDaoSession().getReceiveBeanDao();
    }

    private static ReceiveBeanDaoAgent instance;

    public static ReceiveBeanDaoAgent getInstance() {
        if(instance == null){
            synchronized (ReceiveBeanDaoAgent.class) {
                if(instance == null){
                    instance = new ReceiveBeanDaoAgent();
                }
            }
        }
        return instance;
    }

    public long insert(ReceiveBean bean){
        return sDao.insert(bean);
    }

    public void delete(ReceiveBean bean){
        sDao.delete(bean);
    }

    public void clear() {
        if(queryAll() != null && queryAll().size() != 0){
            sDao.deleteAll();
        }
    }

    public void modify(ReceiveBean bean){
        sDao.update(bean);
    }

    public List<ReceiveBean> queryAll(){
        return sDao.loadAll();
    }

    /**
     * 1 按时间顺序降序排列
     * 2 符合该用户的名单
     * 3 只获取该获取的数量
     * 4 根据 Type 获取不同类型数据
     */
    public List<ReceiveBean> queryReciveBeans(int page, int perPageCount, PushEntity.MsgType msgType){
        String userName = SpUtils.getInstances().getUserName();
        QueryBuilder<ReceiveBean> qb = sDao.queryBuilder()
                .orderDesc(ReceiveBeanDao.Properties.CreateTime)
                .where(ReceiveBeanDao.Properties.UserName.eq(userName))
                .where(ReceiveBeanDao.Properties.MsgType.eq(msgType))
                .limit(page * perPageCount);
        if(qb.list() == null || qb.list().size() == 0) return new ArrayList<>();
        List<ReceiveBean> list = qb.list();
        int startIndex = (page - 1) * perPageCount - 1;
        int endIndex = page * perPageCount - 1;
        return list.subList(startIndex, endIndex);
    }

    public List<ReceiveBean> queryAutoCallBeans(int page, int perPageCount){
        return queryReciveBeans(page, perPageCount, PushEntity.MsgType.AUTO_CALL_PUSH);
    }

    public List<ReceiveBean> querySendSmsBeans(int page, int perPageCount){
        return queryReciveBeans(page, perPageCount, PushEntity.MsgType.AUTO_SEND_PUSH);
    }

}
