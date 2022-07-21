import demo.rpc.common.zookeeper.CuratorClient;


class ZkTest {
    String connectString="ZK1:2181" ;
    String port = "2281" ;
//    @Test
    public  void ZkRegistry() throws Exception {
        CuratorClient curatorClient =new CuratorClient(connectString) ;
        String res = curatorClient.createEphemeralNode("/dl", "hello!".getBytes());
        byte[] data = curatorClient.getData("/dl");
        System.out.println((data.toString()));
        curatorClient.close();
    }

}
