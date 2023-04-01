package demo.rpc.client.discovery;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import demo.rpc.common.registry.Discovery;
import demo.rpc.common.registry.URL;
import demo.rpc.common.zookeeper.CuratorClient;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static demo.rpc.common.constant.RegistryConstant.PROVIDERS;
import static demo.rpc.common.constant.RegistryConstant.ROOT_PATH;


@Slf4j

public class ZkDiscovery implements Discovery {

    private final String discoverAddress =  "127.0.0.1:2181" ;

    private static volatile ZkDiscovery zkDiscovery= null ;

    private final CuratorClient curatorClient;

    /**
     * 已拉取的服务的本地缓存 {serviceName:[URL]}
     */
    private final Map<String, Set<String>> urlCache = new ConcurrentHashMap<>();

    private ZkDiscovery() {
        curatorClient = new CuratorClient(discoverAddress);
    }


    public static ZkDiscovery getInstance(){
        if(zkDiscovery==null){
            synchronized (ZkDiscovery.class){
                if(zkDiscovery==null){
                    zkDiscovery=new ZkDiscovery();
                }
            }
        }
        return zkDiscovery;
    }
    /*
    根据服务名查找信息
     创建地址节点:/wine-rpc/demo.rpc.example.service.HelloService/providers
     */
    @Override
    public List<URL> discover(URL url) {
        String serviceName = url.getInterfaceName();
        List<URL> urls = null;
        if (urlCache.containsKey(serviceName)) {
            urls = urlCache.get(serviceName).stream().map(URL::valueOf).collect(Collectors.toList());
        }else{
            urls = reset(url);
        }
        log.info("Zk discover url : {}",urls) ;
        return urls;
    }

    /**
     * 重置：去注册中心拉去服务，并缓存到本地
     * /wine-rpc/demo.rpc.example.service.HelloService/providers
     *
     * @param url
     * @return
     */
    @Override
    public List<URL> reset(URL url) {
        String serviceName = url.getInterfaceName();
        urlCache.remove(serviceName) ;
        String servicePath = ROOT_PATH + "/" + serviceName + "/" + PROVIDERS;
        List<String> addressList = new ArrayList<>();
        try {
            String s = new String(curatorClient.getData(servicePath), StandardCharsets.UTF_8);
            // TODO  url编码 取出格式：ip:address 实际上应该取出url
            addressList.add(s) ;
            if (CollectionUtil.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        List<URL> urls = new ArrayList<>();
        for (String providerInfo : addressList) {
            String[] split = providerInfo.split(":");
            URL url1 = URL.builder()
                    .host(split[0])
                    .port(Integer.parseInt(split[1]))
                    .interfaceName(serviceName)
                    .version(url.getVersion())
                    .build();
            urls.add(url1);
            //TODO Zk Watch
//            watch(url1);
            addToLocalCache(url1);
        }
        return urls;
    }

    private void watch(URL url) {
        String path = toServicePath(url);
        curatorClient.addListener(path,(type,oldData,data)->{
            log.info("watch event. type={},oldData={},data={}",type,oldData,data) ;

        });
    }
    private String toServicePath(URL url){
        String serviceName = url.getInterfaceName();

        return  ROOT_PATH + "/" + serviceName + "/" + PROVIDERS;
    }

    /**
     * 将服务地址缓存到本地
     *
     * @param url
     */
    @Override
    public void addToLocalCache(URL url) {
        String serviceName = url.getInterfaceName();
        if (!urlCache.containsKey(serviceName)) {
            urlCache.put(serviceName, new ConcurrentHashSet<>());
        }
        urlCache.get(serviceName).add(url.toAddrString(url));
    }

    @Override
    public void removeFromLocalCache(URL url) {
        String serviceName = url.getInterfaceName();
        if (urlCache.containsKey(serviceName)) {
            urlCache.get(serviceName).remove(url.toAddrString(url));
        }
    }

}
