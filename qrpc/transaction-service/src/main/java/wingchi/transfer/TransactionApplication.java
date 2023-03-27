package wingchi.transfer;

import demo.rpc.server.server.RpcServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@MapperScan("wingchi.transfer.mapper")
@SpringBootApplication(scanBasePackages = {"wingchi.transfer","demo.rpc.common","demo.rpc.server"})
@PropertySource(value={"classpath:application-zookeeper.properties","classpath:application.yml"})
public class TransactionApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(TransactionApplication.class, args);
        RpcServer rpcServer = ctx.getBean("RpcServer", RpcServer.class);
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(rpcServer);
        executorService.shutdown();
    }
}
