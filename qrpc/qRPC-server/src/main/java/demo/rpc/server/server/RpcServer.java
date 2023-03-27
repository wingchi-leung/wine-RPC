package demo.rpc.server.server;

import cn.hutool.core.map.MapUtil;
import demo.rpc.common.annotation.RpcService;
import demo.rpc.common.registry.URL;
import demo.rpc.server.registry.ZkRegistry;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 接口名：demo.rpc.example.service.CalculatorService
 * 版本 ：1.0
 * --————————————————————————————————————————————————————————————
 * 接口名：demo.rpc.example.service.HelloService
 * 版本 ：1.0
 */

/**
 * Server的几大组成:
 * <p>1. Netty服务器</p>
 * <p>2. 服务(注解） Spring</p>
 * <p>3. ZK注册中心</p>
 * <p>4. RPC协议：编码,解码,心跳,粘包拆包...</p>
 */
@Slf4j
@NoArgsConstructor
@Component
public class RpcServer implements ApplicationContextAware {

    @Value("${zookeeper.port}")
    int zkPort;

    @Value("${netty.port}")
    int serverPort;

    @Value("${zookeeper.address}")
    String zkAddress;

    private final List<URL> urlList = new ArrayList<>();

    @Autowired
    private ZkRegistry zkRegistry;


    /**
     * 容器启动时,扫描容器内被@RpcService标注的服务。
     * @param ctx 容器上下文
     * @throws BeansException 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        log.info("zkRegistry String: {}", zkAddress + ":" + zkPort);
        zkRegistry.initZkRegistry(zkAddress + ":" + zkPort);
        Map<String, Object> serviceMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtil.isEmpty(serviceMap)) {
            log.warn("no service found!");
            return;
        }
        //https://blog.csdn.net/ypp91zr/article/details/103730870
        for (Object service : serviceMap.values()) {
            Class<?> target = AopProxyUtils.ultimateTargetClass(service);
            RpcService rpcService = target.getAnnotation(RpcService.class);
            log.info("接口名称 interface={}", rpcService.value().getName());
            String interfaceName = rpcService.value().getName();
            String version = rpcService.version();
            addService(interfaceName, version);
            //缓存到本地。
            RpcServiceCache.addServiceToLocalCache(version, target, service);
        }
        register();
    }

    public void register() {
        try {
            zkRegistry.register(urlList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public void registerTest() throws Exception {
        if (zkRegistry == null) {
            log.error("no registry found!!");
        }
        zkRegistry.register(urlList);
    }

    protected void addService(String interfaceName, String version) {
        log.info("add demo.rpc.service,interface:{} ,version:{}", interfaceName, version);
        URL url = URL.buildServiceUrl(interfaceName, version, serverPort);
        urlList.add(url);
    }
}
