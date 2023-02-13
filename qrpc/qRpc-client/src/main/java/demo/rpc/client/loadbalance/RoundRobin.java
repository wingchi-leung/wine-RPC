package demo.rpc.client.loadbalance;

import demo.rpc.common.protocol.RpcRequest;
import demo.rpc.common.registry.URL;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public class RoundRobin extends AbstractLoadBalance {
    private final LongAdder curIndex = new LongAdder();

    @Override
    protected URL doSelect(List<URL> candidateUrls, RpcRequest rpcRequest) {
        int idx = (int) (curIndex.longValue() % candidateUrls.size());
        curIndex.increment();
        return candidateUrls.get(idx);
    }
}
