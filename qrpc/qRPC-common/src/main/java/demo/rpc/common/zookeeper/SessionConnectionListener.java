package demo.rpc.common.zookeeper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.nio.charset.StandardCharsets;

@Slf4j
public class SessionConnectionListener implements ConnectionStateListener {
    private String path;
    private String data ;

    public SessionConnectionListener(String ptah, String data) {
        this.path = ptah;
        this.data = data;
    }


    @SneakyThrows
    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        if(connectionState==ConnectionState.LOST){
            log.error("zk Session 超时！");
        }while(true){
            try{
                if (curatorFramework.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                    try{
                        curatorFramework.create().creatingParentsIfNeeded()
                                .withMode(CreateMode.EPHEMERAL)
                                .forPath(path, data.getBytes(StandardCharsets.UTF_8));
                        log.info("Zk 重连成功！");
                    }catch (KeeperException.NodeExistsException ex){
                        log.warn("重连...节点{}已经存在!",path);
                    }
                    break;
                }
            }catch (InterruptedException e){
                break;
            }catch (Exception e){
                throw e;
            }
        }
    }
}
