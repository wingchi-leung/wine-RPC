package demo.rpc.server.registry;

import demo.rpc.common.registry.Registry;
import demo.rpc.common.registry.URL;
import demo.rpc.common.zookeeper.CuratorClient;
import demo.rpc.common.zookeeper.SessionConnectionListener;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static demo.rpc.common.constant.RegistryConstant.PROVIDERS;
import static demo.rpc.common.constant.RegistryConstant.ROOT_PATH;

/**
 * 服务注册类
 */
@Slf4j
@NoArgsConstructor
@Component
public class ZkRegistry implements Registry {

    private CuratorClient curatorClient;
    private List<String> pathList = new ArrayList<>();

    public void initZkRegistry(String registryAddr) {
        this.curatorClient = new CuratorClient(registryAddr, 5000);
    }


    /**
     * eg: /wine-rpc/demo.rpc.example.service.HelloService/providers
     * @param UrlLists url表
     */
    @Override
    public void register(List<URL> UrlLists) throws Exception {
//        创建根节点(持久化)
        String res0 = curatorClient.createPersistent(ROOT_PATH, null);
        log.info("创建根节点:{}", res0);
        for (URL url : UrlLists) {
            //创建服务节点(持久化)
            String servicePath = ROOT_PATH + "/" + url.getInterfaceName();
            log.info("注册服务节点..{}", servicePath);
            String res = curatorClient.createPersistent(servicePath, null);
            log.debug("创建服务节点:{}", res);
            //创建address节点(临时)
            String addressPath = servicePath + "/" + PROVIDERS;
            String address = url.getHost() + ":" + url.getPort();
            String path = curatorClient.createEphemeralNode(addressPath, address.getBytes(StandardCharsets.UTF_8));
            curatorClient.getClient()
                    .getConnectionStateListenable()
                    .addListener(new SessionConnectionListener(addressPath, address));
            log.info("创建地址节点:{}", path);
            pathList.add(path);
        }
    }


    @Override
    public void unRegister(URL url) {

    }

    @Override
    public void unRegisterAllMyService() {
        log.info("UnRegister all service");
        for (String path : pathList) {
            try {
                curatorClient.deletePath(path);
            } catch (Exception e) {
                log.error("delete service path error " + e.getMessage());
            }
        }
    }

}
