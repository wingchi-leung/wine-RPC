package wingchi.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@MapperScan("wingchi.payment.mapper")
@SpringBootApplication(scanBasePackages = {"demo.rpc", "wingchi.payment"})
@PropertySource(value={"classpath:application.yml","classpath:prometheus.properties"})
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

}
