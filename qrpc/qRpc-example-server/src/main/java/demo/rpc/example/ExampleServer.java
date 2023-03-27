package demo.rpc.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;


@SpringBootApplication(scanBasePackages = {"demo.rpc.common", "demo.rpc.example","demo.rpc.server"})
@PropertySource(value = {"classpath:application-zookeeper.properties"})
public class ExampleServer {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ExampleServer.class);
    }
}
