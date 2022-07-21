package demo.rpc.common.serialize;

public interface Serializer {

    /**
     * 序列化
     * @param object 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object object) ;


    /**
     * 反序列化
     * @param bytes 待反序列化的字节数组
     * @param clazz 反序列化后的类
     * @param <T> 类型
     * @return 反序列化后的对象。
     */
    <T> T deSerialize(byte[] bytes,Class<T> clazz);
}
