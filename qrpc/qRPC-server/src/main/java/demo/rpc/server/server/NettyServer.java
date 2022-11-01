package demo.rpc.server.server;


import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RuntimeUtil;
import demo.rpc.common.netty.RpcMessageDecoder;
import demo.rpc.common.netty.RpcMessageEncoder;
import demo.rpc.common.registry.Registry;
import demo.rpc.common.registry.URL;
import demo.rpc.common.util.ServiceUtil;
import demo.rpc.server.nettyHandler.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import demo.rpc.server.registry.zkRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
public class NettyServer implements Server {

    private String serverAddr;
    private int serverPort;
    private zkRegistry zkRegistry;
    private List<URL> UrlLists = new ArrayList<>();

    public NettyServer(String serverAddr, String registryAddr) {
        log.info("registry address: {}", registryAddr);
        log.info("server address: {}", serverAddr);
        this.serverAddr = serverAddr;
        zkRegistry = new zkRegistry(registryAddr);
    }

    public NettyServer(){

    }

    public void setZkRegistry(String serverAddr, int serverPort, String registryString) {
        log.info("registry address: {}", registryString);
        log.info("server address: {}", serverAddr);
        this.serverAddr = serverAddr;
        this.serverPort=serverPort;
        zkRegistry = new zkRegistry(registryString);
    }

    protected void addService(String interfaceName, String version) {
        log.info("add demo.rpc.service,interface:{} ,version:{}", interfaceName, version);
        URL url = URL.buildServiceUrl(interfaceName, version,serverPort);
        UrlLists.add(url);
    }

    public void registerTest() throws Exception {

        if (zkRegistry == null) {
            log.error("no registry found!!");
        }
        zkRegistry.register(UrlLists);
    }


    /**
     * 启动netty服务器
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workderGroup = new NioEventLoopGroup();
        // LEARN Netty业务线程
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtil.getProcessorCount() * 2,
                ThreadUtil.newNamedThreadFactory("service-handler-group", false)
        );
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workderGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
//                            30秒没有读事件发生
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new RpcMessageEncoder());
                            pipeline.addLast(new RpcMessageDecoder());
                            pipeline.addLast(serviceHandlerGroup, new RpcServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(serverAddr, serverPort).sync();

            if (zkRegistry == null) {
                log.error("no registry found!!");
            }
            zkRegistry.register(UrlLists);

            log.info("Server started on port{}", serverPort);
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            if (ex instanceof InterruptedException) {
                log.error("server InterruptedException");
            } else {
                log.error("server error !! {}",ex.getMessage());
            }
        } finally {
            try {
                assert zkRegistry != null;
                zkRegistry.unRegisterAllMyService();
                bossGroup.shutdownGracefully();
                workderGroup.shutdownGracefully();
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }

        }
    }

    @Override
    public void stop() throws Exception {
    }

}

