package com.cl.cloud.dao;

import com.cl.cloud.app.App;
import com.cl.cloud.push.PushEntity;
import com.google.gson.Gson;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.converter.PropertyConverter;
import org.greenrobot.greendao.annotation.Generated;
import com.cl.cloud.push.PushEntity.MsgType;

/**
 * Created by work2 on 2019/5/20.
 */

@Entity
public class ReceiveBean {

    @Id(autoincrement = true)
    Long id;
    String userName;
    long createTime;
    @Convert(converter = PushEntityConverter.class, columnType = String.class)
    PushEntity pushEntity;
    @Convert(converter = PushTypeConverter.class, columnType = Integer.class)
    PushEntity.MsgType msgType;

    @Generated(hash = 359812667)
    public ReceiveBean(Long id, String userName, long createTime, PushEntity pushEntity,
            PushEntity.MsgType msgType) {
        this.id = id;
        this.userName = userName;
        this.createTime = createTime;
        this.pushEntity = pushEntity;
        this.msgType = msgType;
    }

    @Keep
    public ReceiveBean(String userName, long createTime, PushEntity pushEntity, PushEntity.MsgType msgType) {
        this.userName = userName;
        this.createTime = createTime;
        this.pushEntity = pushEntity;
        this.msgType = msgType;
    }

    @Generated(hash = 61988218)
    public ReceiveBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public PushEntity getPushEntity() {
        return this.pushEntity;
    }

    public void setPushEntity(PushEntity pushEntity) {
        this.pushEntity = pushEntity;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public PushEntity.MsgType getMsgType() {
        return this.msgType;
    }

    public void setMsgType(PushEntity.MsgType msgType) {
        this.msgType = msgType;
    }

    @Keep
    public static class PushEntityConverter implements PropertyConverter<PushEntity, String> {

        @Keep
        private Gson mGson = App.getGson();

        @Override
        public PushEntity convertToEntityProperty(String databaseValue) {
            return mGson.fromJson(databaseValue, PushEntity.class);
        }

        @Override
        public String convertToDatabaseValue(PushEntity entityProperty) {
            return mGson.toJson(entityProperty);
        }
    }

    @Keep
    public static class PushTypeConverter implements PropertyConverter<PushEntity.MsgType, Integer> {

        @Override
        public PushEntity.MsgType convertToEntityProperty(Integer databaseValue) {
            return PushEntity.MsgType.getType(databaseValue);
        }

        @Override
        public Integer convertToDatabaseValue(PushEntity.MsgType typeProperty) {
            return typeProperty.getIndex();
        }
    }

}
