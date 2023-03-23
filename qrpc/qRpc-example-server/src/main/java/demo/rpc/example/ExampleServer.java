package demo.rpc.example;

import demo.rpc.example.serviceImpl.CalculatorServiceImpl;
import demo.rpc.example.serviceImpl.HelloServiceImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import service.CalculatorService;
import service.HelloService;


@SpringBootApplication(scanBasePackages = {"demo.rpc.server","demo.rpc.common","demo.rpc.example"})
@PropertySource(value={"classpath:application-zookeeper.properties"})
public class ExampleServer {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ExampleServer.class);
        HelloService helloService = new HelloServiceImpl();
        CalculatorService calculatorService = new CalculatorServiceImpl();
    }
}
