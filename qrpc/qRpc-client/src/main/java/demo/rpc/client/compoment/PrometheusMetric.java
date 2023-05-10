package demo.rpc.client.compoment;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Data
public class PrometheusMetric {
    private  MeterRegistry registry;
    private  Counter rpcRetryCounter;

    @Autowired
    public PrometheusMetric(MeterRegistry registry){
        this.registry=registry;
    }

    @PostConstruct
    private void init(){
        rpcRetryCounter= registry.counter("rpc_retry_counter","retry","rpc");

    }
}
