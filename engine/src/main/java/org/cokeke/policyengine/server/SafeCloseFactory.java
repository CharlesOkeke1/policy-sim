package org.cokeke.policyengine.server;

import java.util.List;

import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;
import org.java_websocket.server.DefaultWebSocketServerFactory;

// Fix for Java-WebSocket 1.6.0: closeConnection() reads close fields without the lock flushAndClose() writes them under.
public final class SafeCloseFactory extends DefaultWebSocketServerFactory {
  private static final class SafeImpl extends WebSocketImpl {
    SafeImpl(WebSocketListener l, Draft d) { super(l, d); }
    SafeImpl(WebSocketListener l, List<Draft> d) { super(l, d); }
    @Override public synchronized void closeConnection() { super.closeConnection(); }
  }
  @Override public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d) { return new SafeImpl(a, d); }
  @Override public WebSocketImpl createWebSocket(WebSocketAdapter a, List<Draft> d) { return new SafeImpl(a, d); }
}