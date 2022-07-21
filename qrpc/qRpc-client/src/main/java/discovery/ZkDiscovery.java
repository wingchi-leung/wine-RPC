package discovery;

import cn.hutool.core.collection.CollectionUtil;
import demo.rpc.common.registry.Discovery;
import demo.rpc.common.zookeeper.CuratorClient;
import org.omg.CORBA.SystemException;

import java.util.Collections;
import java.util.List;

import static demo.rpc.common.registry.RegistryConstant.ROOT_PATH;

public class ZkDiscovery implements Discovery {

    private final CuratorClient curatorClient;

    public ZkDiscovery(String discoveryAddr) {
        curatorClient=new CuratorClient(discoveryAddr);
    }

    /*
    根据服务名查找信息
     创建地址节点:/wine-rpc/demo.rpc.example.service.HelloService/address-
     */
    @Override
    public void discover(String serviceName) {
        String servicePath = ROOT_PATH+"/"+serviceName;
        try {
            List<String> addressList = curatorClient.getChildren(servicePath);
            if(CollectionUtil.isEmpty(addressList)){
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
