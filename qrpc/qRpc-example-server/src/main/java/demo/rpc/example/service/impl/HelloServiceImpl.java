package demo.rpc.example.service.impl;


import demo.rpc.example.service.HelloService;
import demo.rpc.common.annotation.RpcService;

@RpcService(value = HelloService.class,version = "1.0")
public class HelloServiceImpl implements HelloService {

    @Override
    public String  Hello() {
        return "hello";
    }
}
