package demo.rpc.common.registry;

import java.util.List;
import java.util.Map;

public interface Registry {

    void register(List<URL> list ) throws Exception;

    /**
     * 取消服务
     * @param url
     */
    void unRegister(URL url) ;


    /**
     * 取消本机所有服务
     */
    void unRegisterAllMyService() ;
}
