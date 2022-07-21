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

    private final String interfaceName ;

    private final String version ;



    //TODO 端口号
    public static URL buildServiceUrl(String interfaceName,String version) {
        return URL.builder()
                .protocol("rpc")
                .host(NetUtil.getLocalhostStr())
                .port(8080)
                .interfaceName(interfaceName)
                .version(version)
                .build();
    }
}
