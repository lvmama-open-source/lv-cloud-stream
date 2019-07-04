package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.cloud.stream.reactive.FluxSender;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.router.ExpressionEvaluatingRouter;
import org.springframework.messaging.MessageChannel;

import reactor.core.publisher.Flux;

/**
 * Created by dengcheng on 2018/5/24.
 */
@SpringBootApplication
@EnableBinding
public class App{



//   public interface Sink {
//
//        String INPUT1 = "input1";
//        String INPUT2 = "input2";
//
//
//        @Input(INPUT1)
//        SubscribableChannel input1();
//
//
//        @Input(INPUT2)
//        SubscribableChannel input2();
//
//    }

    @Autowired
    private BinderAwareChannelResolver resolver;

    @Bean(name = "sourceChannel")
    public MessageChannel localChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "sourceChannel")
    public ExpressionEvaluatingRouter router() {
//                ExpressionEvaluatingRouter router = new ExpressionEvaluatingRouter(new SpelExpressionParser().parseExpression("payload.id"));
    	Expression expression = new SpelExpressionParser().parseExpression("headers[dest]");
        ExpressionEvaluatingRouter router = new ExpressionEvaluatingRouter(expression);
        //作用于通过spel表达式没有获取到对应的通道信息
        router.setDefaultOutputChannelName("default-output");
        router.setChannelResolver(resolver);
        return router;
    }
    
//    @EnableBinding(Processor.class)
//    @EnableAutoConfiguration
//    public static class UppercaseTransformer {
//
//      @StreamListener
//      public void receive(@Input(Processor.ORDER1) Flux<String> input,
//         @Output(Processor.OUTPUT) FluxSender output) {
//         output.send(input.map(s -> s.toUpperCase()));
//      }
//    }


//
//    @EnableBinding(
//            Sink.class)
//    static class TestSink {
//        private final Log logger = LogFactory.getLog(getClass());
//
//        @StreamListener(Sink.ORDER)
//        public void receive(String data) {
//            logger.info("begin .... Data received from customer-1..." + data);
////            try {
////                Thread.sleep(20000);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//            logger.info("Data received from customer-1..." + data);
//        }
//
//
//    }


    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }


}
