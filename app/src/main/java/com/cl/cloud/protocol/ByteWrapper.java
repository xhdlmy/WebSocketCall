package com.cl.cloud.protocol;

import java.io.UnsupportedEncodingException;

/**
 * a wrapper that based on byte array, which provides some useful methods
 */
public class ByteWrapper {

	public enum EncodeType {
		ASC(0), WRITE_CARD(3), GB(15), UCS2(8), BINARY(4);
		private final int index;

		private EncodeType(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public static EncodeType getType(int index) {
			switch (index) {
			case 0:
				return ASC;
			case 3:
				return WRITE_CARD;
			case 15:
				return GB;
			case 8:
				return UCS2;
			case 4:
				return BINARY;
			default:
				return ASC;
			}
		}
	}

	private byte[] buf;
	/**
	 * 位置指针
	 */
	private int position;
	/**
	 * 限制长度
	 */
	private int limit;

	/**
	 * 
	 * 构造一个ByteWrapper
	 * 
	 * @param bufferSize
	 *            ByteWrapper的大小
	 */
	public ByteWrapper(int bufferSize) {
		if (bufferSize <= 0) {
			throw new IllegalArgumentException(
					"bufferSize must be a positive integer: " + bufferSize);
		}
		buf = new byte[bufferSize];
		position = 0;
		limit = bufferSize;
	}

	/**
	 * 
	 * 构造一个ByteWrapper
	 * 
	 * @param buffer
	 *            构造ByteWrapper的字节数组
	 */
	public ByteWrapper(byte[] buffer) {
		if (buffer == null || buffer.length < 1) {
			throw new IllegalArgumentException("buffer should not be empty");
		}
		this.buf = buffer;
		position = 0;
		limit = buffer.length;
	}

	/**
	 * @return
	 * @description 从buffer 中读取 1个字节
	 * @param
	 */
	public byte readByte() {
		checkSpace(1);
		byte b = buf[position];
		position++;
		return b;
	}

	/**
	 * @param length
	 *            读取的长度
	 * @return
	 * @description 从buffer 中读取 length长度的字节
	 * @param
	 */
	public byte[] readBytes(int length) {
		checkSpace(length);
		byte[] bytes = new byte[length];
		System.arraycopy(buf, position, bytes, 0, length);
		position += length;
		return bytes;
	}

	/**
	 * 
	 * @param length
	 *            读取的长度
	 * @return
	 * @description 从buffer 中读取 length长度的字节 并转换为Int
	 * @param
	 */
	public int readInt(int length) {
		if (length < 1 || length > 4) {
			throw new IllegalArgumentException(
					"out of integer's byte length(1-4): " + length);
		}
		return (int) readLong(length);
	}

	/**
	 * 
	 * @param length
	 *            读取的长度
	 * @return
	 * @description 从buffer 中读取 length长度的字节 并转换为long
	 * @param
	 */
	public long readLong(int length) {
		if (length < 1 || length > 8) {
			throw new IllegalArgumentException(
					"out of long's byte length(1-8): " + length);
		}
		checkSpace(length);
		long r = 0L;
		for (int i = 0; i < length; i++) {
			if (i > 0) {
				r = r << 8;
			}
			r |= 0xFF & buf[position];
			position++;
		}
		return r;
	}

	public long[] readFixed96Long() {
		return new long[] { readLong(4), readLong(4), readLong(4) };
	}

	/**
	 * @param length
	 *            读取的长度
	 * @return
	 * @description 从buffer 中读取 length长度的字节 并转换为String,使用系统默认的编码
	 * @param
	 */
	public String readString(int length) {
		checkSpace(length);
		String str = new String(buf, position, length).trim();
		position += length;
		return str;
	}

	/**
	 * 
	 * @param length
	 *            读取的长度
	 * @param charset
	 *            编码
	 * @return
	 * @throws UnsupportedEncodingException
	 * @description 从buffer 中读取 length长度的字节 并转换为String,指定编码
	 * @param
	 */
	public String readString(int length, String charset)
			throws UnsupportedEncodingException {
		checkSpace(length);
		String str = new String(buf, position, length, charset);
		position += length;
		return str;
	}

	public void writeByte(byte b) {
		checkSpace(1);
		buf[position] = b;
		position++;
	}

	/**
	 * @param bytes
	 * @description 向buffer数组的position位置后追加bytes 字节数组
	 * @param
	 */
	public void writeBytes(byte[] bytes) {
		if (bytes == null || bytes.length < 1) {
			return;
		}
		checkSpace(bytes.length);
		System.arraycopy(bytes, 0, buf, position, bytes.length);
		position += bytes.length;
	}

	public void writeBytes(byte[] bytes, int length) {
		if (bytes == null || bytes.length < 1) {
			return;
		}
		if (length < bytes.length) {
			throw new IllegalArgumentException(
					"length can't be small than bytes's length! " + length);
		}
		checkSpace(bytes.length);
		System.arraycopy(bytes, 0, buf, position, bytes.length);
		position += length;
	}

	/**
	 * @param value
	 * @description 向buffer中写入Int数据，占1位
	 * @param
	 */
	public void writeFixed8(int value) {
		writeInt(value, 1);
	}

	/**
	 * 
	 * @param value
	 * @description 向buffer中写入short数据，占2位
	 * @param
	 */
	public void writeFixed16(short value) {
		writeInt(value, 2);
	}

	/**
	 * 
	 * @param value
	 * @param length
	 * @description 向buffer中写入Int数据
	 * @param
	 */
	public void writeInt(int value, int length) {
		if (length < 1 || length > 4) {
			throw new IllegalArgumentException(
					"out of integer's byte length(1-4): " + length);
		}

		writeLong(value, length);
	}
	
	
	public void writeDouble(double value, int length) {
		long tempValue = Double.doubleToLongBits(value);
		writeLong(tempValue, length);
	}
	
	/**
	 * 小端法 
	 * @param value
	 * @param length
	 */
	public void writeDouble4LittleEndian(double value, int length) {
		long tempValue = Double.doubleToLongBits(value);
		writeLong4LittleEndian(tempValue, length);
	}
	
	/**
	 * 
	 * @param value
	 *            值
	 * @param length
	 *            数据长度
	 * @description 向buffer中写入long数据
	 * @param
	 */
	public void writeLong(long value, int length) {
		if (length < 1 || length > 8) {
			throw new IllegalArgumentException(
					"out of long's byte length(1-8): " + length);
		}
		checkSpace(length);
		for (int i = 0; i < length; i++) {
			// 高位补零(大端法)
			buf[position] = (byte) ((value >> (length - i - 1) * 8) & 0xFF);
//			buf[position] = (byte) ((value >> i * 8) & 0xFF);
			position++;
		}
	}

	/**
	 * 
	 * @param value
	 * @param length
	 * @description 向buffer中写入Int数据
	 * @param
	 */
	public void writeInt4LittleEndian(int value, int length) {
		if (length < 1 || length > 4) {
			throw new IllegalArgumentException(
					"out of integer's byte length(1-4): " + length);
		}
		writeLong4LittleEndian(value, length);
	}

	/**
	 * 
	 * @param value
	 *            值
	 * @param length
	 *            数据长度
	 * @description 向buffer中写入long数据
	 * @param
	 */
	public void writeLong4LittleEndian(long value, int length) {
		if (length < 1 || length > 8) {
			throw new IllegalArgumentException(
					"out of long's byte length(1-8): " + length);
		}
		checkSpace(length);
		for (int i = 0; i < length; i++) {
			// 低位补零（小端法）
			buf[position] = (byte) ((value >> i * 8) & 0xFF);
			position++;
		}
	}

	/**
	 * @param sequenceNum
	 * @description 写入一个长整形的序列
	 * @param
	 */
	public void writeFixed96Long(long[] sequenceNum) {
		writeLong(sequenceNum[0], 4);
		writeLong(sequenceNum[1], 4);
		writeLong(sequenceNum[2], 4);
	}

	/**
	 * 
	 * @param str
	 * @description 写入一个字符串
	 * @param
	 */
	public void writeString(String str) {
		if (str != null && str.length() > 0) {
			byte[] bytes;
			try {
				bytes = str.getBytes("US-ASCII");
				checkSpace(bytes.length);
				System.arraycopy(bytes, 0, buf, position, bytes.length);
				position += bytes.length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * <p>
	 * Write string as US-ASCII bytes
	 * </p>
	 * <p>
	 * If length larger than string bytes's length, will be repeat to fill with
	 * '\0' until reach the length
	 * </p>
	 * 
	 * @param str
	 *            String for writing
	 * @param length
	 *            Writing length
	 * @exception RuntimeException
	 *                If length less than string bytes's length
	 */
	public void writeString(String str, int length) {
		checkSpace(length);
		if (str == null) {
			for (int i = 0; i < length; i++) {
				buf[position] = '\0';
				position++;
			}
			return;
		}

		try {
			byte[] bytes = str.getBytes("US-ASCII");
			if (bytes.length > length) {
				System.arraycopy(bytes, 0, buf, position, length);
				position += length;
			} else {
				System.arraycopy(bytes, 0, buf, position, bytes.length);
				position += bytes.length;
				for (int i = 0; i < length - bytes.length; i++) {
					buf[position] = '\0';
					position++;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * Write string as UTF-8 bytes
	 * </p>
	 * <p>
	 * If length larger than string bytes's length, will be repeat to fill with
	 * '\0' until reach the length
	 * </p>
	 * 
	 * @param str
	 *            String for writing
	 * @param length
	 *            Writing length
	 * @exception RuntimeException
	 *                If length less than string bytes's length
	 */
	public void writeStringUTF8(String str, int length) {
		checkSpace(length);
		if (str == null) {
			for (int i = 0; i < length; i++) {
				buf[position] = '\0';
				position++;
			}
			return;
		}

		try {
			byte[] bytes = str.getBytes("UTF-8");
			if (bytes.length > length) {
				System.arraycopy(bytes, 0, buf, position, length);
				position += length;
			} else {
				System.arraycopy(bytes, 0, buf, position, bytes.length);
				position += bytes.length;
				for (int i = 0; i < length - bytes.length; i++) {
					buf[position] = '\0';
					position++;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param str
	 * @param length
	 * @param charset
	 * @throws UnsupportedEncodingException
	 */
	public void writeString(String str, int length, String charset)
			throws UnsupportedEncodingException {
		checkSpace(length);
		if (str == null) {
			for (int i = 0; i < length; i++) {
				buf[position] = '\0';
				position++;
			}
			return;
		}

		byte[] bytes = str.getBytes(charset);
		if (bytes.length > length) {
			System.arraycopy(bytes, 0, buf, position, length);
			position += length;
		} else {
			System.arraycopy(bytes, 0, buf, position, bytes.length);
			position += bytes.length;
			for (int i = 0; i < length - bytes.length; i++) {
				buf[position] = '\0';
				position++;
			}
		}
	}

	/**
	 * Write string length
	 * 
	 * @param destTerminalID
	 * @param length
	 * @return
	 */
	public void writeStringArray(String[] destTerminalID, int length) {
		for (int j = 0; j < destTerminalID.length; j++) {
			writeString(destTerminalID[j], length);
		}
	}

	/**
	 * @param length
	 * @description 越界检查
	 * @param
	 */
	private void checkSpace(int length) {
		if (length < 0) {
			throw new IllegalArgumentException(
					"length must be a positive integer: " + length);
		}

		if (position + length > limit) {
			throw new RuntimeException("the buffer limit is: " + limit
					+ ", but expected to: " + (position + length));
		}
	}

	public static int getStringSize(String content, EncodeType encode) {
		try {
			switch (encode) {
			case ASC:
				return content.getBytes("UTF-8").length;
			case UCS2:
				return content.getBytes("UTF-16BE").length;
			case GB:
				return content.getBytes("GBK").length;
			default:
				return 0;
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("encode not supported.", e);
		}
	}

	/**
	 * 
	 * @param content
	 *            字符串内容
	 * @param encode
	 *            编码类型
	 * @return
	 * @description String 编码
	 * @param
	 */
	public static byte[] encodeString(String content, EncodeType encode) {
		try {
			switch (encode) {
			case UCS2:
				return content.getBytes("UTF-16BE");
			case GB:
				return content.getBytes("GBK");
			default:
				return content.getBytes("UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("encode not supported.", e);
		}
	}

	/**
	 * get the original byte array
	 * 
	 * @return the array
	 */
	public byte[] array() {
		return buf;
	}

	/**
	 * get the left space
	 * 
	 * @return byte length
	 */
	public int getLeftSpace() {
		return limit - position;
	}

	public int getPosition() {
		return position;
	}

	public byte[] subByteWrap(int start, int length) {
		if (start < 0 || length < 1) {
			throw new IllegalArgumentException(
					"length or start must be a positive integer: " + length);
		}

		if (length > buf.length) {
			throw new RuntimeException("the buffer length is: " + buf.length
					+ ", but expected to: " + length);
		}
		byte[] subBytes = new byte[length];
		System.arraycopy(buf, start, subBytes, 0, length);
		return subBytes;
	}

	public int size() {
		return limit;
	}
}
