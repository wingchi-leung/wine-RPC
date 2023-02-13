package demo.rpc.client.dto;


public interface RpcResult {
    boolean isSuccess();

    Object getData();
}
