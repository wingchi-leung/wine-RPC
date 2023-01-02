package demo.rpc.example;

import demo.rpc.client.RpcClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "demo.rpc")
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
         RpcClient rpcClient = (RpcClient)applicationContext.getBean("rpcClient");
    }
}
