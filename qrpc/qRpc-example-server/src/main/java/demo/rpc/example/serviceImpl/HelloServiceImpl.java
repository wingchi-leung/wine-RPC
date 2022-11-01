package demo.rpc.example.serviceImpl;


import service.HelloService;
import demo.rpc.common.annotation.RpcService;

@RpcService(value = HelloService.class,version = "1.0")
public class HelloServiceImpl implements HelloService {

    @Override
    public String  Hello() {
        return "hello,I am so happy so i am going to cry!!";
    }
}
