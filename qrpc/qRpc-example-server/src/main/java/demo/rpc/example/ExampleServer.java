package demo.rpc.example;

import demo.rpc.example.service.CalculatorService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import demo.rpc.example.service.HelloService;
import demo.rpc.server.server.*;
import demo.rpc.example.service.impl.CalculatorServiceImpl;
import demo.rpc.example.service.impl.HelloServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
@SpringBootApplication(scanBasePackages = "demo.rpc")

public class ExampleServer {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ExampleServer.class);
        RpcServer bean = (RpcServer) applicationContext.getBean("rpcServer") ;
        HelloService helloService = new HelloServiceImpl();
        CalculatorService calculatorService = new CalculatorServiceImpl();
    }
}
