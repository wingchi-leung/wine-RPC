package wingchi.account;

import demo.rpc.server.server.NettyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@MapperScan("wingchi.account.mapper")
@SpringBootApplication(scanBasePackages = {"wingchi.account","demo.rpc.common","demo.rpc.server"})
@PropertySource(value={"classpath:application-zookeeper.properties","classpath:application.yml"})
public class AccountApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(AccountApplication.class, args);
        NettyServer nettyServer = ctx.getBean("nettyServer", NettyServer.class);
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(nettyServer);
        executorService.shutdown();
    }

}
