package demo.rpc.server.server;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端的服务缓存
 */
@Slf4j
public class RpcServiceCache {
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    public static void addServiceToLocalCache(String version, Object service ){
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length==0){
            throw new RuntimeException("rpc service didn't implement any interface!, service ："+service.getClass().getName());
        }

        String RpcServiceName=interfaces[0].getCanonicalName();
        if(!StrUtil.isBlank(version)){
            RpcServiceName = RpcServiceName.concat("_" + version);
        }
        log.info("缓存。。。。RpcServiceName cache: {}",RpcServiceName);
        serviceMap.putIfAbsent(RpcServiceName,service);
        log.info(StrUtil.format("add Service. rpcServiceName={},class={}",RpcServiceName,service.getClass()));
    }

    public static  Object getService(String RpcServiceName){
        Object service = serviceMap.get(RpcServiceName);
        if(service==null){
            throw new RuntimeException("service not found! serviceName="+RpcServiceName);
        }
        return service;
    }
}
