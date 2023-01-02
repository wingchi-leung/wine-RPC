package demo.rpc.client.loadbalance;

import cn.hutool.core.collection.CollectionUtil;
import demo.rpc.common.protocol.RpcRequest;
import demo.rpc.common.registry.URL;

import java.util.List;

public abstract  class AbstractLoadBalance implements LoadBalance{

    @Override
    public URL select(List<URL> urlList, RpcRequest request) {
        if(CollectionUtil.isEmpty(urlList))
            return null;
        if(urlList.size()==1){
            return urlList.get(0);
        }
        return doSelect(urlList,request);
    }

    protected abstract URL doSelect(List<URL> candidateUrls,RpcRequest rpcRequest);
}
