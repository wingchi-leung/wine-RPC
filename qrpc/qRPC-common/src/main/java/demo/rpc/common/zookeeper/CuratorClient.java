package demo.rpc.common.zookeeper;

import demo.rpc.common.registry.RegistryConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.List;
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


    public void addConnectionStateListener(ConnectionStateListener connectionStateListener) {
        client.getConnectionStateListenable().addListener(connectionStateListener);
    }


    public byte[] getData(String path) throws Exception {
        return client.getData().forPath(path);
    }

    public void updatePathData(String path, byte[] data) throws Exception {
        client.setData().forPath(path, data);
    }

    public void deletePath(String path) throws Exception {
        client.delete().forPath(path);
    }

    public List<String> getChildren(String path) throws  Exception{

        return client.getChildren().forPath(path);
    }

    public void watchTreeNode(String path, TreeCacheListener listener){
        TreeCache treeCache =new TreeCache(client,path) ;
        treeCache.getListenable().addListener(listener);
    }

    public void watchPathChildrenNode(String path, PathChildrenCacheListener listener) throws Exception {
        PathChildrenCache pathChildrenCache =new PathChildrenCache(client,path,true) ;
        pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE) ;
        pathChildrenCache.getListenable().addListener(listener);
    }

    public void close(){
        client.close();
    }





}
