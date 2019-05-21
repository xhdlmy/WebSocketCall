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

/**
 * Created by work2 on 2019/5/20.
 */

@Entity
public class ReceiveBean {

    @Id(autoincrement = true)
    Long id;
    String userName;
    @Convert(converter = PushEntityConverter.class, columnType = String.class)
    PushEntity pushEntity;

    @Generated(hash = 1293362969)
    public ReceiveBean(Long id, String userName, PushEntity pushEntity) {
        this.id = id;
        this.userName = userName;
        this.pushEntity = pushEntity;
    }

    @Keep
    public ReceiveBean(String userName, PushEntity pushEntity) {
        this.userName = userName;
        this.pushEntity = pushEntity;
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

}
