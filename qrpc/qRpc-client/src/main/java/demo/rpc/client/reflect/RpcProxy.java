package demo.rpc.client.reflect;

import demo.rpc.client.dto.RpcResult;
import demo.rpc.common.protocol.RpcRequest;
import demo.rpc.common.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class RpcProxy<T> implements InvocationHandler {
    private Class<T> proxy;
    private String version;

    public RpcProxy(Class<T> proxy, String version) {
        this.proxy = proxy;
        this.version = version;
    }

    /**
     * 1.构造RPC协议对象
     * 2.发起RPC远程调用
     * 3.等待RPC调用执行结果
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("获取到Method的Class:{}", method.getDeclaringClass());
        RpcRequest request = RpcRequest.builder().interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .version(version)
                .params(args)
                .paramsTypes(method.getParameterTypes()).build();
        NettyInvoker invoker = new NettyInvoker();

        RpcResult rpcResult = invoker.invoke(request);
        // getData方法实际上是调用Completable的get方法,此方法阻塞。
        Object data = ((RpcResponse<?>) rpcResult.getData()).getData();
        log.info("get RpcResponse Data = {}", data);
        return data;
    }

    /**
     * 根据服务类型和版本获取代理类
     *
     * @param interfaceClass 接口名
     * @param version        版本号
     * @param <T>            服务类型
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T createServiceProxy(Class<T> interfaceClass, String version) {
        T t = (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxy<T>(interfaceClass, version));
        return t;
    }

}
