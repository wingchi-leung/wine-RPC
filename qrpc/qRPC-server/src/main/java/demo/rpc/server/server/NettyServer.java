package demo.rpc.server.server;


import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RuntimeUtil;
import demo.rpc.common.netty.RpcMessageDecoder;
import demo.rpc.common.netty.RpcMessageEncoder;
import demo.rpc.server.nettyHandler.RpcServerHandler;
import demo.rpc.server.registry.ZkRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Slf4j
@NoArgsConstructor
@Component
public class NettyServer implements Server, Runnable {

    @Value("${netty.port}")
    int serverPort;

    @Value("${server.address}")
    String serverAddress;


    @Autowired
    private ZkRegistry zkRegistry;


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
            ChannelFuture future = bootstrap.bind(serverAddress, serverPort).sync();
            log.info("Server started on port：{}", serverPort);
            future.channel().closeFuture().sync();
            System.out.println("netty 服务器启动成功!");
        } catch (Exception ex) {
            if (ex instanceof InterruptedException) {
                log.error("server InterruptedException");
            } else {
                log.error("server error !! {}", ex.getMessage());
            }
        } finally {
            try {
                unregister();
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

    @Override
    public void run() {
        try {
            start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void unregister() {
        zkRegistry.unRegisterAllMyService();
    }

}

