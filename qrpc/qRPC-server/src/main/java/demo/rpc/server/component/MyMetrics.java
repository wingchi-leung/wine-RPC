package demo.rpc.server.component;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

public class MyMetrics {
    public static final Counter rpcRequests = Counter.build()
            .name("rpc_requests_total")
            .labelNames("method", "status")
            .help("Total number of RPC requests.")
            .register();
    public static final Histogram rpcRequestLatency = Histogram.build()
            .name("rpc_request_latency_seconds")
            .labelNames("method")
            .help("RPC request latency in seconds.")
            .register();
}
