package demo.rpc.common.registry;

import java.util.List;

public interface Discovery {
    List<URL> discover(URL url);

    List<URL> reset(URL url);

    void addToLocalCache(URL url) ;

    void removeFromLocalCache(URL url) ;
}
