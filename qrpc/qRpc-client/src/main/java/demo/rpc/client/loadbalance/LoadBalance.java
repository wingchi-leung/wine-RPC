package demo.rpc.client.loadbalance;

import demo.rpc.common.protocol.RpcRequest;
import demo.rpc.common.registry.URL;

import java.util.List;

public interface LoadBalance {
    URL select(List<URL> urlList, RpcRequest request);
}
