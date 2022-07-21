package demo.rpc.common.serialize.protostuff;

import demo.rpc.common.serialize.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

//TODO  schma cache? https://meua.github.io/2018/05/02/%E5%88%9D%E6%8E%A2Protostuff%E7%9A%84%E4%BD%BF%E7%94%A8/

public class ProtoStuffSerializer implements Serializer {
    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    public byte[] serialize(Object object) {
        Schema schema = RuntimeSchema.getSchema(object.getClass());
        byte []data;
        try{
             data = ProtostuffIOUtil.toByteArray(object, schema, BUFFER);
        }finally {
            BUFFER.clear() ;
        }
        return data ;


    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T t = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes,t,schema);

        return t;
    }
}
