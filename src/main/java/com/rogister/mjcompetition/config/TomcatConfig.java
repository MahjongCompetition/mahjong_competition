package com.rogister.mjcompetition.config;

import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {
    
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        
        tomcat.addConnectorCustomizers(connector -> {
            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
            
            // 设置连接超时
            protocol.setConnectionTimeout(20000);
            
            // 设置最大连接数
            protocol.setMaxConnections(8192);
            
            // 设置最大线程数
            protocol.setMaxThreads(200);
            
            // 设置最小备用线程数
            protocol.setMinSpareThreads(10);
            
            // 设置接受计数
            protocol.setAcceptCount(100);
            
            // 设置压缩
            protocol.setCompression("on");
            protocol.setCompressionMinSize(2048);
            protocol.setCompressibleMimeType("text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json");
        });
        
        return tomcat;
    }
}
