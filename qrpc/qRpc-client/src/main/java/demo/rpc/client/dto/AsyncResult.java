package demo.rpc.client.dto;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class AsyncResult implements RpcResult {
    private final CompletableFuture<?> future;

    public AsyncResult(CompletableFuture<?> future) {
        this.future = future;
    }

    @Override
    public boolean isSuccess() {
        return !future.isCompletedExceptionally();
    }

    @Override
    public Object getData() {
        try {
            return future.get();
        } catch (Exception e) {
            log.error("Result get Data error.", e);
        }
        return null;
    }
}
