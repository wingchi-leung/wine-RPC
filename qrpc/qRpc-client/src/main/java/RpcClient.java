import demo.rpc.common.annotation.RpcAutowire;
import discovery.ZkDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import reflect.RpcProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

@Slf4j
public class RpcClient implements ApplicationContextAware, DisposableBean {


    private ZkDiscovery zkDiscovery;

    public RpcClient(ZkDiscovery zkDiscovery) {
        this.zkDiscovery = zkDiscovery;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        String[] beanNames = ctx.getBeanDefinitionNames();
        for(String beanName :beanNames){
            Object bean  = ctx.getBean(beanName) ;
            Field[] declaredFields = bean.getClass().getDeclaredFields();
            try{
                for(Field field:declaredFields){
                    RpcAutowire rpcAutowire = field.getAnnotation(RpcAutowire.class);
                    if(rpcAutowire!=null){
                        String version = rpcAutowire.version();
                        field.setAccessible(true);
                        //将被RpcAutowire注解标注的字段值设置成代理对象
                        log.debug("获取的类别是:{}",field.getType());
                        field.set(bean,createService(field.getType(),version));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据服务类型和版本获取代理类
     * @param interfaceClass 接口名
     * @param version 版本号
     * @param <T> 服务类型
     * @param <P>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T,P> T createService(Class<T> interfaceClass, String version) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxy<T,P>(interfaceClass,version));
    }

    @Override
    public void destroy() throws Exception {

    }


}
