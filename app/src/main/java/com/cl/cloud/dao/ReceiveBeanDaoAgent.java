package com.cl.cloud.dao;

import com.cl.cloud.app.App;
import com.cl.cloud.push.PushEntity;
import com.cl.cloud.util.SpUtils;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;

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

    public List<ReceiveBean> queryUserBeans(){
        List<ReceiveBean> receiveBeans = queryAll();
        ReceiveBean receiveBean = receiveBeans.get(0);
        PushEntity pushEntity = receiveBean.getPushEntity();
        pushEntity.getType();

        String userName = SpUtils.getInstances().getUserName();
        QueryBuilder<ReceiveBean> qb = sDao.queryBuilder().where(
                ReceiveBeanDao.Properties.UserName.eq(userName));
        if(qb.list() == null || qb.list().size() == 0) return null;
        return qb.list();
    }

    public List<ReceiveBean> queryAutoCallBeans(){
        List<ReceiveBean> list = new ArrayList<>();
        List<ReceiveBean> receiveBeans = queryUserBeans();
        for (ReceiveBean receiveBean : receiveBeans) {
            PushEntity pushEntity = receiveBean.getPushEntity();
            if(PushEntity.MsgType.AUTO_CALL_PUSH.equals(pushEntity.getType())){
                list.add(receiveBean);
            }
        }
        return list;
    }

    public List<ReceiveBean> querySendSmsBeans(){
        List<ReceiveBean> list = new ArrayList<>();
        List<ReceiveBean> receiveBeans = queryUserBeans();
        for (ReceiveBean receiveBean : receiveBeans) {
            PushEntity pushEntity = receiveBean.getPushEntity();
            if(PushEntity.MsgType.AUTO_SEND_PUSH.equals(pushEntity.getType())){
                list.add(receiveBean);
            }
        }
        return list;
    }

}
