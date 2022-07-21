package demo.rpc.common.compress;

public interface Compressor {
    /**
     * 压缩
     * @param bytes 要压缩的数组
     * @return 压缩后的数组
     */
    byte[] compress(byte[] bytes);

    /**
     * 解压缩
     * @param bytes
     * @return
     */
    byte[] decompress(byte[] bytes);
}
