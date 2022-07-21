package demo.rpc.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompressType {
    DUMMY((byte) 0 ,"dummy"),
    GZIP((byte)1,"gzip");
    private final byte value;
    private final String name ;

    /**
     * 通过值获取压缩类型枚举类
     * @param value
     * @return
     */
    public static CompressType fromValue(byte value){
        for(CompressType codecType:CompressType.values()){
            if(codecType.getValue()==value)
                return codecType;

        }
        return DUMMY;
    }

    /**
     * 根据名称获取枚举类
     * @param name
     * @return
     */
    public static CompressType fromName(String name){
        for(CompressType codecType:CompressType.values()){
            if(codecType.getName()==name)
                return codecType;

        }
        return DUMMY;
    }


}
