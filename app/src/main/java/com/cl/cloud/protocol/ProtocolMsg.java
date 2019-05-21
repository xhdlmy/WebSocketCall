package com.cl.cloud.protocol;

public interface ProtocolMsg {

	// 协议封包
	ByteWrapper wrap();
	// 协议解包
	void unWrap(ByteWrapper wrapper);

	int getLength();

	AbstractMsg.Command getCommandId();

}
