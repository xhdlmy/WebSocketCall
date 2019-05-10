package com.cl.cloud.websocket;

import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * 监听 WebSocket 连接状态改变时的回调
 */
public abstract class WsStatusListener {

  public void onOpen(WebSocket webSocket, Response response) {
  }

  public void onMessage(String msg) {
  }

  public void onMessage(ByteString bytes) {
  }

  public void onClosing(int code, String reason) {
  }

  public void onClosed(int code, String reason) {
  }

  public void onFailure(Throwable t, Response response) {
  }

}
