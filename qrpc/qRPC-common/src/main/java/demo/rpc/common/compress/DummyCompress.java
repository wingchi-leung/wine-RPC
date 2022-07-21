package demo.rpc.common.compress;

public class DummyCompress implements Compressor{
    @Override
    public byte[] compress(byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        return bytes;
    }
}
