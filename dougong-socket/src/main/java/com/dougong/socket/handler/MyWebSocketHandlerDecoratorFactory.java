package com.dougong.socket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
@Component
public class MyWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory{
	
	private MyWebSocketHandler myWebSocketHandler;
	
	public MyWebSocketHandlerDecoratorFactory(){
		
	}
	
	@Override
	public WebSocketHandler decorate(WebSocketHandler handler) {
		if(this.myWebSocketHandler == null)
			this.myWebSocketHandler = new MyWebSocketHandler(handler);
		
		return this.myWebSocketHandler;
	}

	public MyWebSocketHandler getMyWebSocketHandler() {
		return myWebSocketHandler;
	}
}
