package demo.rpc.server.server;

import demo.rpc.common.annotation.RpcService;
import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
// LEARN ctxRPC的实现方法?

/**
 * 接口名：demo.rpc.example.service.CalculatorService
 * 版本 ：1.0
--————————————————————————————————————————————————————————————
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
public class RpcServer extends NettyServer implements InitializingBean, DisposableBean, ApplicationContextAware  {
    static String registryAddr="192.168.57.100:2181" ;
    static String serverAddr="127.0.0.1:8080" ;


    public RpcServer(String serverAddr, String registryAddr) {
        super(serverAddr, registryAddr);
    }

    public  RpcServer(){
        super(serverAddr, registryAddr);
    }
    /**
     * 容器启动时,扫描容器内被@RpcService标注的服务。
     * @param ctx 容器上下文
     * @throws BeansException 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceMap = ctx.getBeansWithAnnotation(RpcService.class);
        if(MapUtil.isEmpty(serviceMap)){
            log.debug("no service find!") ;
            return;
        }
        for (Object service : serviceMap.values())
        {
            RpcService rpcService = service.getClass().getAnnotation(RpcService.class);
            String interfaceName = rpcService.value().getName();
//            System.out.println("接口名："+interfaceName);
            String version = rpcService.version();
//            System.out.println("版本 ："+version);
            super.addService(interfaceName, version);
        }

    }


    @Override
    public void destroy() throws Exception {
//        super.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        super.start();
        registerTest();
    }


}
