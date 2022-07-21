package demo.rpc.server.registry;

import demo.rpc.common.registry.Registry;
import demo.rpc.common.registry.URL;
import lombok.extern.slf4j.Slf4j;
import demo.rpc.common.zookeeper.CuratorClient;

import java.util.ArrayList;
import java.util.List;
import static demo.rpc.common.registry.RegistryConstant.ROOT_PATH;

/**
 * 服务注册类
 */
@Slf4j
public class zkRegistry implements Registry {

    private final CuratorClient curatorClient;
    private final List<String> pathList=new ArrayList<>();

    public zkRegistry(String registryAddr) {
        this.curatorClient = new CuratorClient(registryAddr,5000) ;
    }


    /**
     * /ROOT_PATH/${serviceName}/provider/ip:host
     * eg: /wine-rpc/demo.rpc.example.service.HelloService/address-
     * @param UrlLists url表
     */
    @Override
    public void register(List<URL> UrlLists ) throws Exception {
        System.out.println("注册");
//        创建根节点(持久化)
        String res0 = curatorClient.createPersistent(ROOT_PATH, null);
        log.debug("创建根节点:{}",res0);
        for(URL url : UrlLists){
            //创建服务节点(持久化)
            String servicePath = ROOT_PATH + "/" + url.getInterfaceName();
            String res = curatorClient.createPersistent(servicePath, null);
            log.debug("创建服务节点:{}",res);
            //创建address节点(临时)
            String addressPath = servicePath+"/address-";
            String address = url.getHost()+ ":"+ url.getPort();
            String path = curatorClient.createEphemeralNode(addressPath, address.getBytes());
            log.debug("创建地址节点:{}",path);
            pathList.add(path) ;
        }
    }



    @Override
    public void unRegister(URL url) {

    }

    @Override
    public void unRegisterAllMyService() {
        log.info("UnRegister all service");
        for(String path:pathList){
            try {
                curatorClient.deletePath(path);
            } catch (Exception e) {
                log.error("delete service path error "+e.getMessage());
            }
        }
    }



}
