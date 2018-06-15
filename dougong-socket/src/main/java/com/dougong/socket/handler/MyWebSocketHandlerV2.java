package com.dougong.socket.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

//@Component
public class MyWebSocketHandlerV2 extends TextWebSocketHandler {
	
	private static Logger logger = LoggerFactory.getLogger(MyWebSocketHandlerV2.class);
	
	private static final Map<String, WebSocketSession> users;// 这个会出现性能问题，最好用Map来存储，key用userid

	static {
		users = new HashMap<String, WebSocketSession>();
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		super.handleTextMessage(session, message);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> map = session.getAttributes();
		if (map != null && map.size() > 0) {
			String userid = (String) map.get("userid");
			users.put(userid, session);
		}
		System.out.println("connect to the websocket success......当前数量:" + users.size());
		// 这块会实现自己业务，比如，当用户登录后，会把离线消息推送给用户
		// TextMessage returnMessage = new TextMessage("你将收到的离线");
		// session.sendMessage(returnMessage);
		super.afterConnectionEstablished(session);// 必须调用父类不然会报错
	}
	
	 /**
     * 关闭连接时触发
     * 用户退出后的处理，不如退出之后，要将用户信息从websocket的session中remove掉，这样用户就处于离线状态了，也不会占用系统资源
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.debug("websocket connection closed......");
        String username= (String) session.getAttributes().get("WEBSOCKET_USERNAME");
        String userid= (String) session.getAttributes().get("userid");
        System.out.println("用户"+username+"已退出！");
        users.remove(userid);
        System.out.println("剩余在线用户"+users.size());
        super.afterConnectionClosed(session, closeStatus);
    }
    
    /**
     * js调用websocket.send时候，会调用该方法
     */
    @Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    	//将消息进行转化，因为是消息是json数据，可能里面包含了发送给某个人的信息，所以需要用json相关的工具类处理之后再封装成TextMessage，
	    //我这儿并没有做处理，消息的封装格式一般有{from:xxxx,to:xxxxx,msg:xxxxx}，来自哪里，发送给谁，什么消息等等	    
	    //TextMessage msg = (TextMessage)message.getPayload();
	    //给所有用户群发消息
    	//this.sendMessageToUsers(returnMessage,session);
	    //给指定用户群发消息
	    //sendMessageToUser(userId,msg);
    	//session.sendMessage(message);
        super.handleMessage(session, message);
    }
    

    //后台错误信息处理方法
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
        	String userid= (String) session.getAttributes().get("userid");
        	users.remove(userid);
        	session.close();
        }
        logger.debug("websocket connection closed......");
        super.handleTransportError(session, exception);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    /**
     * 给某个用户发送消息
     *
     * @param userName
     * @param message
     */
    public void sendMessageToUser(String userName, TextMessage message) {
        for (Map.Entry<String , WebSocketSession> entry : users.entrySet()) {
            if (entry.getValue().getAttributes().get("WEBSOCKET_USERNAME").equals(userName)) {
                try {
                    if (entry.getValue().isOpen()) {
                    	entry.getValue().sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 给所有在线用户发送消息
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
        for (Map.Entry<String , WebSocketSession> entry : users.entrySet()) {
            try {
        		 if (entry.getValue().isOpen()) {
        			 entry.getValue().sendMessage(message);
                 }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 给所有在线用户发送消息
     * 
     * @param message
     * @throws IOException
     */
    public void broadcast(final TextMessage message) throws IOException {
        Iterator<Entry<String, WebSocketSession>> it = users.entrySet().iterator();
 
        // 多线程群发
        while (it.hasNext()) {
            final Entry<String, WebSocketSession> entry = it.next();
            if (entry.getValue().isOpen()) {
                // entry.getValue().sendMessage(message);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            if (entry.getValue().isOpen()) {
                                entry.getValue().sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
 
        }
    }
}
