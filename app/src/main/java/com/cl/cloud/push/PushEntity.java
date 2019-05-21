package com.cl.cloud.push;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.HashMap;

public class PushEntity implements Parcelable {

	// 消息类型
	public enum MsgType {
		// 建立连接
		CONNECT(11),
		// 发起自动呼叫响应消息
		AUTO_CALL_REQ(21),
		// 自动呼叫推送消息
		AUTO_CALL_PUSH(22),
		// 发起短信发送响应消息
		AUTO_SEND_REQ(31),
		// 短信发送推送消息
		AUTO_SEND_PUSH(32),
		// 未定义的消息类型
		UN_DEFINE(-1);

		int index;

		MsgType(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public static MsgType getType(int index) {
			switch (index) {
			case 11:
				return CONNECT;
			case 21:
				return AUTO_CALL_REQ;
			case 22:
				return AUTO_CALL_PUSH;
			case 32:
				return AUTO_SEND_PUSH;
			default:
				return UN_DEFINE;
			}
		}

	}

	public int msgType;
	public int status;
	public String respTime;// response
	public HashMap<String, String> detail;

	public PushEntity() {}

	public PushEntity(int msgType, int status, String respTime, HashMap<String, String> detail) {
		this.msgType = msgType;
		this.status = status;
		this.respTime = respTime;
		this.detail = detail;
	}

	public MsgType getType() {
		return MsgType.getType(msgType);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.msgType);
		dest.writeInt(this.status);
		dest.writeString(this.respTime);
		dest.writeSerializable(this.detail);
	}

	protected PushEntity(Parcel in) {
		this.msgType = in.readInt();
		this.status = in.readInt();
		this.respTime = in.readString();
		this.detail = (HashMap<String, String>) in.readSerializable();
	}

	public static final Creator<PushEntity> CREATOR = new Creator<PushEntity>() {
		@Override
		public PushEntity createFromParcel(Parcel source) {
			return new PushEntity(source);
		}

		@Override
		public PushEntity[] newArray(int size) {
			return new PushEntity[size];
		}
	};
}
