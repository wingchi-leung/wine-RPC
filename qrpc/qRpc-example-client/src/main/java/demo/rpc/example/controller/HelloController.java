package demo.rpc.example.controller;

import demo.rpc.common.annotation.RpcAutowire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.CalculatorService;
import service.HelloService;
@Slf4j
@RestController
public class HelloController {

    @RpcAutowire(version="1.0")
    private HelloService helloService;

    @RpcAutowire(version="1.0")
    private CalculatorService calculatorService;

    @GetMapping("/add")
    public String add(@RequestParam("a") int a, @RequestParam("b") int b) {
        int res = calculatorService.add(a, b);
        return String.valueOf(res);
    }

    @GetMapping("/hello")
    public String hello() {
        log.info("进来hello方法了,helloService=");
//        TODO  动态代理，为什么只是打印方法都会去invoker方法中？
//        System.out.println(helloService);
        return  helloService.Hello().toString();
    }
}
