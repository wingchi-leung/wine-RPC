package demo.rpc.common.zookeeper;

import demo.rpc.common.constant.RegistryConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.List;

import static demo.rpc.common.constant.RegistryConstant.ROOT_PATH;

@Slf4j
public class CuratorClient {
    private CuratorFramework client;

    public CuratorClient(String connectString, int sessionTimeOut, int connectionTimeOut, String namespace) {
        client= CuratorFrameworkFactory.builder().connectString(connectString)
                .connectionTimeoutMs(connectionTimeOut).sessionTimeoutMs(sessionTimeOut)
                .retryPolicy(new ExponentialBackoffRetry(1000,10))
                .build();
        client.start() ;
    }
    public CuratorClient(String connectString,int timeOut){
        this(connectString,timeOut,timeOut, RegistryConstant.ZK_NAMESPACE) ;
    }
    public CuratorClient(String connectString){
        this(connectString, RegistryConstant.ZK_SESSION_TIMEOUT, RegistryConstant.ZK_CONNECTION_TIMEOUT, RegistryConstant.ZK_NAMESPACE) ;
    }



    public CuratorFramework getClient() {
        return client;
    }

    public String createEphemeralNode(String path, byte[] data) throws Exception {
        return createNodeWithMode(path,data,CreateMode.EPHEMERAL);
    }


    public String createNodeWithMode(String path,byte[] data,CreateMode createMode) throws Exception {
        String res=null;
        try{
            if(data!=null){
                res = client.create().creatingParentsIfNeeded()
                        .withMode(createMode)
                        .forPath(path,data);
            }else {
                res = client.create().creatingParentsIfNeeded()
                        .withMode(createMode)
                        .forPath(path);
            }

        }
        catch (KeeperException.NodeExistsException e) {
            log.warn("ZNode " + path + " already exists.");
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return res;
    }

    public String createPersistent(String path, byte[] data) throws Exception {
        return createNodeWithMode(path, data, CreateMode.PERSISTENT);
    }

    public void addListener(String path, CuratorCacheListener listener){
        String fullPath = buildPath(path);
        CuratorCache curatorCache = CuratorCache.build(client,fullPath) ;
        curatorCache.listenable().addListener(listener) ;
        curatorCache.start();
    }

    private String buildPath(String path) {
        if(path.startsWith(ROOT_PATH)) return path ;
        if(path.startsWith("/")) return ROOT_PATH + path ;
        return ROOT_PATH+"/"+path;
    }


    public byte[] getData(String path) throws Exception {
        return client.getData().forPath(path);
    }


    public void deletePath(String path) throws Exception {
        client.delete().forPath(path);
    }

    public List<String> getChildren(String path) throws  Exception{
        return client.getChildren().forPath(path);
    }


    public void close(){
        client.close();
    }





}
