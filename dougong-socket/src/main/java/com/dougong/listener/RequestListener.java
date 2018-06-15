package com.dougong.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextListener;

/**
 * @author zengwc
 * @email 
 * Company:杭州枓栱科技信息技术有限公司
 * CreateDate 2017年11月7日 下午2:51:54 
 * Description:该监听器解决socket握手拦截器session为空
 * @since JDK 1.8
 * @version 
 * @see
 */
//@WebListener
public class RequestListener extends RequestContextListener {
    
    public void requestInitialized(ServletRequestEvent sre)  { 
    	super.requestInitialized(sre);
    	//将所有request请求都携带上httpSession
    	HttpServletRequest request = (HttpServletRequest)sre.getServletRequest();
    	request.getSession();
    }

    public void requestDestroyed(ServletRequestEvent arg0)  { 
    	super.requestDestroyed(arg0);
    }
}