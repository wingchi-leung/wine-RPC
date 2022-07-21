package netty;

import cn.hutool.core.util.StrUtil;
import demo.rpc.common.netty.RpcMessageDecoder;
import demo.rpc.common.netty.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class NettyClient {
    private final Bootstrap bootstrap;
    private static NettyClient instance = null;
    private static Map<SocketAddress, Channel> channelMap = new ConcurrentHashMap<>();

    public static NettyClient getInstance() {
        if (instance == null) {
            synchronized (NettyClient.class) {
                if (instance == null) {
                    instance = new NettyClient();
                }
            }
        }
        return instance;
    }

    private NettyClient() {
        bootstrap = new Bootstrap()
                .channel(NioSocketChannel.class)
                .group(new NioEventLoopGroup())
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        /*
                        进站顺序：head-->IdleState-->RpcDecoder-->clientHandler-->tail
                        出站顺序：tail-->encoder-->idleState-->head
                         */
                        //进出站
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcMessageEncoder()); //出站
                        pipeline.addLast(new RpcMessageDecoder()); //进站
                        pipeline.addLast(new NettyClientHandler()); //进站
                    }
                });
    }

    public Channel getChannel(SocketAddress address) {
        Channel channel = channelMap.get(address);
        if (channel == null || !channel.isActive()) {
            channel = connect(address);
            channelMap.put(address, channel);
        }
        return channel;
    }

    private Channel connect(SocketAddress address) {
        try {
            log.info("Try to connect server [{}]", address);
            CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
            ChannelFuture connectFuture = bootstrap.connect(address);
            connectFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    completableFuture.complete(future.channel());
                } else {
                    throw new IllegalArgumentException(StrUtil.format("connect fail,address :", address));
                }
            });
            return completableFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("connect fail" + address + " message :" + e.getMessage());
        }
    }
}

