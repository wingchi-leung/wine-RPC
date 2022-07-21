package reflect;

import demo.rpc.common.protocol.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理对象
 * @param <T>
 * @param <P>
 */

@Slf4j
public class RpcProxy<T,P> implements InvocationHandler {
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
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest.builder().interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .version(version)
                .params(args)
                .paramsTypes(method.getParameterTypes()).build();

        return null;
    }
}
