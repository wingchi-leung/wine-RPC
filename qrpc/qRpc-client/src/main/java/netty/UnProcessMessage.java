package netty;

import cn.hutool.json.JSONUtil;
import demo.rpc.common.protocol.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnProcessMessage {
    public static final Map<Long, CompletableFuture<RpcResponse<?>>> FUTURE_MAP  =
            new ConcurrentHashMap<>();
    public static void put(long RequestId, CompletableFuture<RpcResponse<?>> future){
        FUTURE_MAP.put(RequestId,future) ;
    }

    public static void complete(RpcResponse<?> rpcResponse){
        CompletableFuture<RpcResponse<?>> future = FUTURE_MAP.remove(rpcResponse.getRequestId());
        if(future!=null){
            future.complete(rpcResponse) ;
        }else{
            //LEARN 这是啥意思？
            throw new IllegalArgumentException("future is null.rpcResponse="+ JSONUtil.toJsonStr(rpcResponse));
        }
    }
}
