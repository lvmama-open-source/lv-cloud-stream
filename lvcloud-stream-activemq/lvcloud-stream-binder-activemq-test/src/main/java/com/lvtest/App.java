package com.lvtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.router.ExpressionEvaluatingRouter;
import org.springframework.messaging.MessageChannel;

@SpringBootApplication
@EnableBinding
public class App {
	
	public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
	
	@Autowired
    private BinderAwareChannelResolver resolver;
	
	@Bean(name = "routerChannel")
    public MessageChannel routerChannel() {
        return new DirectChannel();
    }
	
    @Bean
    @ServiceActivator(inputChannel = "routerChannel")
    public ExpressionEvaluatingRouter router() {
//                ExpressionEvaluatingRouter router = new ExpressionEvaluatingRouter(new SpelExpressionParser().parseExpression("payload.id"));
    	Expression expression = new SpelExpressionParser().parseExpression("headers[destination]");
        ExpressionEvaluatingRouter router = new ExpressionEvaluatingRouter(expression);
        //作用于通过spel表达式没有获取到对应的通道信息
        router.setDefaultOutputChannelName("default-output");
        router.setChannelResolver(resolver);
        return router;
    }
	
}
