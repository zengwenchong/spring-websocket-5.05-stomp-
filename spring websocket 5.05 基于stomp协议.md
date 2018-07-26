### spring websocket 5.05 基于stomp协议

​	WebSocket协议定义了两种类型的消息，文本和二进制，但其内容未定义。 为客户端和服务器定义了一种协商子协议的机制 - 即更高级别的消息传递协议，用于在WebSocket之上定义每种消息可以发送哪种消息，每种消息的格式和内容是什么等等 上。 子协议的使用是可选的，但是客户端和服务器需要就定义消息内容的一些协议达成一致。

​	STOMP是一种简单的面向文本的消息传递协议，最初是为脚本语言（如Ruby，Python和Perl）创建的，用于连接企业消息代理。 它旨在解决常用消息传递模式的最小子集。 STOMP可用于任何可靠的双向流媒体网络协议，如TCP和WebSocket。 尽管STOMP是一种面向文本的协议，但消息负载可以是文本或二进制。

STOMP是一个基于帧的协议，其帧在HTTP上建模。 STOMP框架的结构：

[![attachments-2018-07-1UPbnQXg5b3b4fbebeb80.png](https://ask.gupaoedu.com/image/show/attachments-2018-07-1UPbnQXg5b3b4fbebeb80.png)](https://ask.gupaoedu.com/image/show/attachments-2018-07-1UPbnQXg5b3b4fbebeb80.png)

​	客户可以使用SEND或SUBSCRIBE命令发送或订阅消息以及描述消息的内容和由谁来接收消息的“目标”头。这使得一个简单的发布 - 订阅机制可以用来通过代理发送消息到其他连接的客户端，或者发送消息到服务器来请求执行一些工作。
	在使用Spring的STOMP支持时，Spring WebSocket应用程序充当客户端的STOMP代理。消息被路由到@Controller消息处理方法或一个简单的内存代理，用于跟踪订阅并向订阅用户广播消息。您还可以将Spring配置为与专用的STOMP代理（例如RabbitMQ，ActiveMQ等）一起使用，以用于消息的实际广播。在这种情况下，Spring维护与代理的TCP连接，将消息转发给它，并将消息从它传递到连接的WebSocket客户端。因此，Spring Web应用程序可以依靠统一的基于HTTP的安全性，通用验证以及熟悉的编程模型消息处理工作。

​	这里是客户订阅接收股票报价的例子，服务器可以定期发送例如通过计划任务通过SimpMessagingTemplate将消息发送给代理：

[![attachments-2018-07-6FxzNmcF5b3b4fd684419.png](https://ask.gupaoedu.com/image/show/attachments-2018-07-6FxzNmcF5b3b4fd684419.png)](https://ask.gupaoedu.com/image/show/attachments-2018-07-6FxzNmcF5b3b4fd684419.png)

​	以下是客户端发送交易请求的示例，服务器可以通过@MessageMapping方法处理，稍后在执行后向客户端广播交易确认消息和详细信息：

[![attachments-2018-07-EVicNX4s5b3b4fe956a8f.png](https://ask.gupaoedu.com/image/show/attachments-2018-07-EVicNX4s5b3b4fe956a8f.png)](https://ask.gupaoedu.com/image/show/attachments-2018-07-EVicNX4s5b3b4fe956a8f.png)

​	目的地的含义在STOMP规范中有意不透明。 它可以是任何字符串，完全取决于STOMP服务器来定义它们支持的目的地的语义和语法。 然而，对于目的地是类似路径的字符串，其中“/ topic / ..”意味着发布 - 订阅（一对多）和“/队列/”意味着点对点（一对一 ）消息交换。

STOMP服务器可以使用MESSAGE命令向所有用户广播消息。 以下是向订阅客户端发送股票报价的服务器示例：

[![attachments-2018-07-71aLC1Lm5b3b4ffaa7be4.png](https://ask.gupaoedu.com/image/show/attachments-2018-07-71aLC1Lm5b3b4ffaa7be4.png)](https://ask.gupaoedu.com/image/show/attachments-2018-07-71aLC1Lm5b3b4ffaa7be4.png)

​	知道服务器不能发送未经请求的消息很重要。 所有来自服务器的消息都必须响应特定的客户端订阅，并且服务器消息的“subscription-id”头必须与客户端订阅的“id”头相匹配。
以上概述旨在提供对STOMP协议的最基本的了解。 建议全面查看协议规范。

​	使用STOMP作为子协议使Spring Framework和Spring Security能够提供更丰富的编程模型，而不是使用原始WebSockets。 关于HTTP与原始TCP的关系以及Spring MVC和其他Web框架如何提供丰富的功能都可以做到这一点。 以下是一些好处：
	不需要发明自定义消息协议和消息格式。
	STOMP客户端可用，包括Spring框架中的Java客户端。
	消息代理（如RabbitMQ，ActiveMQ等）可以用于（可选）管理订阅和广播消息。
	应用程序逻辑可以组织在任何数量的@ Controller中，并且根据STOMP目标报头路由到他们的消息，以及用	给定连接的单个WebSocketHandler处理原始WebSocket消息。

​	使用Spring Security来保护基于STOMP目标和消息类型的消息。

​	spring-messaging和spring-websocket模块提供了对WebSocket支持的STOMP。 一旦你有这些依赖关系，你可以通过WebSocket和SockJS Fallback公开一个STOMP端点，如下所示：

### WebSocketConfig配置：

```java
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;

import org.springframework.messaging.converter.MessageConverter;

import org.springframework.messaging.simp.config.ChannelRegistration;

import org.springframework.messaging.simp.config.MessageBrokerRegistry;

import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.dougong.socket.handler.MyWebSocketHandlerDecoratorFactory;

import com.dougong.socket.interceptor.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

@Autowired
private MyWebSocketHandlerDecoratorFactory decoratorFactory;

/**
 * applicationDestinationPrefixes应用前缀，所有请求的消息将会路由到@MessageMapping的controller上，
 * enableStompBrokerRelay是代理前缀，而返回的消息将会路由到代理上，所有订阅该代理的将收到响应的消息。
 * 
 */
@Override
public void configureMessageBroker(MessageBrokerRegistry config) {
    // 应用程序以/app为前缀，代理目的地以/topic、/user为前缀
    config.enableSimpleBroker("/topic", "/user");
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/user/"); 
    // config.setApplicationDestinationPrefixes("/app");
    // config.setUserDestinationPrefix("/user");
    // config.enableSimpleBroker("/topic", "/queue");
    // registry.enableStompBrokerRelay("/topic", "/queue")
    // 下面这配置为默认配置，如有变动修改配置启用就可以了
    // .setRelayHost("127.0.0.1") //activeMq服务器地址
    // .setRelayPort(61613)//activemq 服务器服务端口
    // .setClientLogin("guest") //登陆账户
    // .setClientPasscode("guest") // ;
}

/**
 * 将"/hello"路径注册为STOMP端点，这个路径与发送和接收消息的目的路径有所不同，这是一个端点，客户端在订阅或发布消息到目的地址前，要连接该端点，
 * 即用户发送请求url="/applicationName/hello"与STOMP server进行连接。之后再转发到订阅url；
 * PS：端点的作用——客户端在订阅或发布消息到目的地址前，要连接该端点。
 * 
 * @param stompEndpointRegistry     连接的端点，客户端建立连接时需要连接这里配置的端点
 * 
 */
@Override

public void registerStompEndpoints(StompEndpointRegistry registry) {
    // 为java stomp client提供链接
    // 在网页上可以通过"/applicationName/hello"来和服务器的WebSocket连接
    registry.addEndpoint("/gs-guide-websocket").setAllowedOrigins("*")
    /.setHandshakeHandler(new MyHandshakeHandler())/
    .addInterceptors(new HandshakeInterceptor())
    .withSockJS();
    // 为js客户端提供链接
    //registry.addEndpoint("/hello").setAllowedOrigins("")/.setHandshakeHandler(new MyHandshakeHandler())
    //.addInterceptors(new MyHandshakeInterceptor())*/.withSockJS();
    // 在网页上可以通过"/applicationName/hello"来和服务器的WebSocket连接
    // registry.addEndpoint("/gs-guide-websocket")
    // .addInterceptors(new
    // SpringWebSocketHandlerInterceptor()).setAllowedOrigins("*").withSockJS();
}

@Override
public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    registration.addDecoratorFactory(this.decoratorFactory);
    registration.setMessageSizeLimit(75536).setSendBufferSizeLimit(75536)
                             .setSendTimeLimit(75536);
    //super.configureWebSocketTransport(registration);
}

/**
 * 输入通道参数设置
 */
@Override
public void configureClientInboundChannel(ChannelRegistration registration) {
	//// super.configureClientInboundChannel(registration);
	//registration.setInterceptors(new MySubscriptionInterceptor ());
	//// 线程信息
	//registration.taskExecutor().corePoolSize(4).maxPoolSize(8).keepAliveSeconds(60);
	//super.configureClientInboundChannel(registration);

}

/**
 * 输出通道参数配置
 */
@Override
public void configureClientOutboundChannel(ChannelRegistration registration) {

	// super.configureClientOutboundChannel(registration);
	// 线程信息
	//registration.taskExecutor().corePoolSize(4).maxPoolSize(8);
	//super.configureClientOutboundChannel(registration);
}

@Override
public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
	//messageConverters.add(new ByteArrayMessageConverter());
	return true;
}

}

```



这里需要注意configureWebSocketTransport方法中的registration.addDecoratorFactory(this.decoratorFactory);

[![attachments-2018-07-B01kRxrO5b3b5034d5f60.png](https://ask.gupaoedu.com/image/show/attachments-2018-07-B01kRxrO5b3b5034d5f60.png)](https://ask.gupaoedu.com/image/show/attachments-2018-07-B01kRxrO5b3b5034d5f60.png)

decoratorFactory是自定义的一个类实现啦WebSocketHandlerDecoratorFactory接口，主要作用是用来处理自定义的WebSocketHandler，自定义的WebSocketHandler可以监控用户下线或上线等。

其中还有registerStompEndpoints方法中添加了一个stomp握手拦截器 

[![attachments-2018-07-2DdNigen5b3b509f87483.png](https://ask.gupaoedu.com/image/show/attachments-2018-07-2DdNigen5b3b509f87483.png)](https://ask.gupaoedu.com/image/show/attachments-2018-07-2DdNigen5b3b509f87483.png)



下面是decoratorFactory、自定义的WebSocketHandler和stomp握手拦截器的类:

### MyWebSocketHandlerDecoratorFactory

```java
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
        if(this.myWebSocketHandler == null){
            this.myWebSocketHandler = new MyWebSocketHandler(handler);    
        }
        return this.myWebSocketHandler;
    }

    public MyWebSocketHandler getMyWebSocketHandler() {
        return myWebSocketHandler;
    }
}

```



## MyWebSocketHandler

```java
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.web.socket.*;

import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.io.IOException;

import java.util.HashMap;

import java.util.Iterator;

import java.util.Map;

import java.util.Map.Entry;

public class MyWebSocketHandler extends WebSocketHandlerDecorator{

    public MyWebSocketHandler(WebSocketHandler delegate) {

    super(delegate);

    }

	private static Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);

    private static final Map<String , WebSocketSession> users;//这个会出现性能问题，最好用Map来存储，key用userid
    
    static {
        users = new HashMap<String , WebSocketSession>();
    }
 	/**
     * 连接成功时候，会触发页面上onopen方法
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        // TODO Auto-generated method stub
        Map<String, Object> map = session.getAttributes();
        if(map != null && map.size() > 0){
        	String userid = (String) map.get("userid");
        	users.put(userid,session);
        }
        System.out.println("connect to the websocket success......当前数量:"+users.size());
        //这块会实现自己业务，比如，当用户登录后，会把离线消息推送给用户
        //TextMessage returnMessage = new TextMessage("你将收到的离线");
        //session.sendMessage(returnMessage);
        super.afterConnectionEstablished(session);//必须调用父类不然会报错
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

```



### HandshakeInterceptor

```java
/** 
 * stomp握手拦截器 
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
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler                         wsHandler,   Exception ex) {
   		System.out.println(wsHandler.getClass());
        logger.info("===============after handshake=============");  
        super.afterHandshake(request, response, wsHandler, ex);  
    }  
}  

```



启动后访问：http://localhost:8888/index/socket

前端用的是：[LayIM](http://www.baidu.com/link?url=FnNt_7K_8ucs3bg0HpPbAp-6t2rcYuMHI03BUYbDJlTiTPJ9JPo8E_9n8quuzuyR)+stomp.min.js

 