package com.cl.cloud.protocol;

public abstract class AbstractMsg implements ProtocolMsg {

	public enum Command {
		// 1 登录连接
		CONNECT((byte) 1),
		// 2 自动呼叫
		AUTO_CALL((byte) 2),
		// -1 未知命令
		UNKNOW((byte) -1);

		private byte index;

		Command(byte index) {
			this.index = index;
		}

		public byte getIndex() {
			return index;
		}

		public static AbstractMsg.Command getType(byte index) {
			switch (index) {
				case 1:
					return CONNECT;
				case 2:
					return AUTO_CALL;
				default:
					return UNKNOW;
			}
		}
	}

	@Override
	public ByteWrapper wrap() {
		byte[] bytes = new byte[getLength()];
		ByteWrapper wrapper = new ByteWrapper(bytes);
		wrap(wrapper);
		return wrapper;
	}

	public abstract void wrap(ByteWrapper wrapper);

}
