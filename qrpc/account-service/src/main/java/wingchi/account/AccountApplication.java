package wingchi.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@MapperScan("wingchi.account.mapper")
@PropertySource(value={"classpath:application-zookeeper.properties"})
@SpringBootApplication(scanBasePackages = {"demo.rpc.server","demo.rpc.common","wingchi.account"})
public class AccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }

}
