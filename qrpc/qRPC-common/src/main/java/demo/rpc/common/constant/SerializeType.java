package demo.rpc.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SerializeType {
    PROTOSTUFF((byte) 1,"protostuff");
    private final byte value;
    private final String name ;
    public static SerializeType fromValue(byte value){
        for(SerializeType serialize : SerializeType.values()){
            if (serialize.getValue()==value) {
                return serialize;
            }
        }
        return null ;
    }
    public static SerializeType fromName(String name){
        for(SerializeType serialize : SerializeType.values()){
            if (serialize.getName().equals(name)) {
                return serialize;
            }
        }
        return null ;
    }
}
