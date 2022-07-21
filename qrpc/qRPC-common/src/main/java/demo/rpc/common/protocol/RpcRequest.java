package demo.rpc.common.protocol;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest {
    /**
     * 接口名
     */
    private String interfaceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数列表
     */
    private Object[] params;
    /**
     * 参数类型
     */
    private Class<?>[] paramsTypes;
    /**
     * 版本号
     */
    private String version ;
    public String getRpcServiceForCache(){
        if(StrUtil.isBlank(version)){
            return interfaceName;
        }
        return interfaceName + "_" + version;
    }





}
