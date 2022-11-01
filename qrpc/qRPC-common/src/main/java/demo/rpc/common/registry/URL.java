package demo.rpc.common.registry;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.NetUtil;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 节点： /q-rpc/${serviceName}/providers
 * 服务元数据组成的URL,是放在临时节点的数据内容
 * <p> qrpc://host:port?interface=${serviceName}&version=${version} </p>
 */
@Getter
@Builder
public class URL {
    /**
     * 协议
     */
    private final String protocol;
    /**
     * 主机
     */
    private final String host;
    /**
     * 端口
     */
    private final int port;
    /**
     * 接口名称
     */
    private final String interfaceName ;
    /**
     * 版本号
     */
    private final String version ;



    //TODO 端口号
    public static URL buildServiceUrl(String interfaceName,String version,int port ) {
        return URL.builder()
                .protocol("rpc")
                .host(NetUtil.getLocalhostStr())
                .port(port)
                .interfaceName(interfaceName)
                .version(version)
                .build();
    }

    public  String toAddrString(URL url){
        return url.getHost()+":"+url.getPort();
    }

    public static URL valueOf(String url) {
        String[] split = url.split(":");
        return URL.builder().host(split[0]).port(Integer.parseInt(split[1])).build();

    }
}
