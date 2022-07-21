package demo.rpc.common.zookeeper;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class RpcServiceInfo implements Serializable {
    private String serviceName ;
    private String version ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcServiceInfo that = (RpcServiceInfo) o;
        return Objects.equals(serviceName, that.serviceName) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, version);
    }
    public String toJson(){
//        JsonU
        return null ;
    }

}
