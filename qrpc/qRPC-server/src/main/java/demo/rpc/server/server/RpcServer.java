package demo.rpc.server.server;

import cn.hutool.core.map.MapUtil;
import demo.rpc.common.annotation.RpcService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

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
@Component
public class RpcServer extends NettyServer implements InitializingBean, DisposableBean, ApplicationContextAware {

    @Value("${zookeeper.port}")
    int zkPort;

    @Value("${server.port}")
    int serverPort;

    @Value("${server.address}")
    String serverAddress;

    @Value("${zookeeper.address}")
    String zkAddress;


    public RpcServer(){
        super();
    }

    /**
     * 容器启动时,扫描容器内被@RpcService标注的服务。
     * @param ctx 容器上下文
     * @throws BeansException 异常
     */
    @SneakyThrows
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        setZkRegistry(serverAddress,serverPort,zkAddress+":"+zkPort);
        Map<String, Object> serviceMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtil.isEmpty(serviceMap)) {
            log.warn("no service found!");
            return;
        }
        //https://blog.csdn.net/ypp91zr/article/details/103730870
        for (Object service : serviceMap.values()) {
            Class<?>  target = AopProxyUtils.ultimateTargetClass(service);
            RpcService rpcService = target.getAnnotation(RpcService.class);
            log.info("接口名称 interface={}",rpcService.value().getName());
            String interfaceName = rpcService.value().getName();
            String version = rpcService.version();
            addService(interfaceName, version);
            //缓存到本地。
            RpcServiceCache.addServiceToLocalCache(version, service);
        }

    }
    @Override
    public void destroy() throws Exception {
        super.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.start();
    }
}
