package com.cl.cloud.protocol;

import com.cl.cloud.app.App;
import com.cl.cloud.app.Constant;

public class ConnectMsg extends AbstractMsg {

	private int version = Constant.PROTOCOL_VERSION;
	private int appVersion = App.sVersionCode;
	private String channelCode = Constant.CHANNEL;
	private String userName;
	private String userPwd;
	private byte os = Constant.OS_APP;
	private String timeStamp;
	private byte[] sign;

	public ConnectMsg(String userName, String userPwd) {
		this.userName = userName;
		this.userPwd = userPwd;
		timeStamp = Long.toString(System.currentTimeMillis());
		sign = Md5Encrypt.get16BitMD5Byte((channelCode + timeStamp + Constant.PROTOCOL_KEY).getBytes());
	}

	@Override
	public void wrap(ByteWrapper wrapper) {
		wrapper.writeInt(version, 4);
		wrapper.writeInt(appVersion, 4);
		wrapper.writeByte(Command.CONNECT.getIndex());
		wrapper.writeString(channelCode, 16);
		wrapper.writeByte(os);
		wrapper.readBytes(34);//将位置向前移动34个字节，作为保留字节
		wrapper.writeString(timeStamp, 16);
		wrapper.writeBytes(sign, 16);
		wrapper.writeString(userName, 32);
		wrapper.writeString(userPwd, 32);
	}

	@Override
	public void unWrap(ByteWrapper wrapper) {}

	@Override
	public int getLength() {
		return Constant.CONNECT_LEN;
	}

	@Override
	public Command getCommandId() {
		return Command.CONNECT;
	}

}
