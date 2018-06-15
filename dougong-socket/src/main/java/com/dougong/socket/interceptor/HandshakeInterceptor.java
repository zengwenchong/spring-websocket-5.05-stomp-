package com.dougong.socket.interceptor;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;  
  
/** 
 * stomp握手拦截器 
 * @author tomZ 
 * @date 2016年11月4日 
 * @desc TODO 
 */  
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {  
    private static final Logger logger = LoggerFactory.getLogger( HandshakeInterceptor.class);  
      
    @Override  
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,  
            Map<String, Object> attributes) throws Exception {  
    	logger.info("===============before  handshake=============");  
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            String name = httpServletRequest.getParameter("name");
            //HttpSession session = servletRequest.getServletRequest().getSession(false);
            HttpSession session = httpServletRequest.getSession(false);
            if (session != null) {
                //使用userName区分WebSocketHandler，以便定向发送消息
                String userid = (String) session.getAttribute("userid");//用户信息
                if (userid == null) {
                	userid = "default-system"+session.getId();
                }
                attributes.put("userid",userid);//用户userid
            }
            
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);  
    }  
      
    @Override  
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,  
            Exception ex) {
    	System.out.println(wsHandler.getClass());
        logger.info("===============after handshake=============");  
        super.afterHandshake(request, response, wsHandler, ex);  
    }  
}  