//package cn.voicecomm.ai.voicesagex.console.application.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
///**
// * @author jiwh
// * @date 2023/4/19 9:28
// */
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//  /**
//   * URI: -> gateway: /administration/web-websocket/websocket -> websocket: /websocket
//   *
//   * @param registry reg
//   */
//  @Override
//  public void registerStompEndpoints(StompEndpointRegistry registry) {
//    registry
//        .addEndpoint("/websocket")
//        .setAllowedOriginPatterns("*")
//        .withSockJS();
//  }
//
//  @Override
//  public void configureMessageBroker(MessageBrokerRegistry registry) {
//    registry.enableSimpleBroker("/user");
//    registry.setApplicationDestinationPrefixes("/app");
//    registry.setUserDestinationPrefix("/user");
//  }
//
//}
