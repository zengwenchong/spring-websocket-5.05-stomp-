package com.dougong.socket.config;

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
	 * @param stompEndpointRegistry
	 * 
	 * 
	 *            连接的端点，客户端建立连接时需要连接这里配置的端点
	 * 
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 为java stomp client提供链接
		// 在网页上可以通过"/applicationName/hello"来和服务器的WebSocket连接
		registry.addEndpoint("/gs-guide-websocket").setAllowedOrigins("*")
				/*.setHandshakeHandler(new MyHandshakeHandler())*/
				.addInterceptors(new HandshakeInterceptor())
				.withSockJS();

		// 为js客户端提供链接
		//registry.addEndpoint("/hello").setAllowedOrigins("*")/*.setHandshakeHandler(new MyHandshakeHandler())
		//		.addInterceptors(new MyHandshakeInterceptor())*/.withSockJS();
		// 在网页上可以通过"/applicationName/hello"来和服务器的WebSocket连接
		// registry.addEndpoint("/gs-guide-websocket")
		// .addInterceptors(new
		// SpringWebSocketHandlerInterceptor()).setAllowedOrigins("*").withSockJS();
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		registration.addDecoratorFactory(this.decoratorFactory);
		registration.setMessageSizeLimit(75536).setSendBufferSizeLimit(75536).setSendTimeLimit(75536);
//		super.configureWebSocketTransport(registration);
	}

	/**
	 * 输入通道参数设置
	 */
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
//		// super.configureClientInboundChannel(registration);
//		registration.setInterceptors(new MySubscriptionInterceptor ());
//		// 线程信息
//		registration.taskExecutor().corePoolSize(4).maxPoolSize(8).keepAliveSeconds(60);
//		super.configureClientInboundChannel(registration);
	}

	/**
	 * 输出通道参数配置
	 */
	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		// super.configureClientOutboundChannel(registration);
		// 线程信息
		//registration.taskExecutor().corePoolSize(4).maxPoolSize(8);
//		super.configureClientOutboundChannel(registration);
	}

	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		//messageConverters.add(new ByteArrayMessageConverter());
		return true;
	}
	
}