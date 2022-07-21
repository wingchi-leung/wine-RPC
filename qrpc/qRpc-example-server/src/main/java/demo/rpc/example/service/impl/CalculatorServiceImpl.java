package demo.rpc.example.service.impl;


import demo.rpc.common.annotation.RpcService;
import demo.rpc.example.service.CalculatorService;

@RpcService(value = CalculatorService.class,version = "1.0")
public class CalculatorServiceImpl implements CalculatorService {

    @Override
    public int add(int a, int b) {
        return a+b;
    }
}