package demo.rpc.example.serviceImpl;


import demo.rpc.common.annotation.RpcService;
import demo.rpc.commonapi.service.CalculatorService;

@RpcService(value = CalculatorService.class,version = "1.0")
public class CalculatorServiceImpl implements CalculatorService {

    @Override
    public int add(int a, int b) {
        return a+b;
    }

    @Override
    public int multiply(int a, int b) {
        return a*b;
    }

    @Override
    public int divide(int a, int b) {
        return a/b;
    }


}