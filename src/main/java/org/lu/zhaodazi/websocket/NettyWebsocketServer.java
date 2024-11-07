package org.lu.zhaodazi.websocket;
import io.netty.channel.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.logging.LogLevel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
public class NettyWebsocketServer {
    public static final int WEB_SOCKET_PORT = 8090;
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors());

    public static final ConcurrentHashMap<String,Channel> allChannels = new ConcurrentHashMap<>();
    public static final NettyWebsocketServerInputHandler NETTY_WEBSOCKET_SERVER_INPUT_HANDLER = new NettyWebsocketServerInputHandler(allChannels);

    @PostConstruct
    public void start() throws InterruptedException {
        run();
    }

    @PreDestroy
    public void destroy() {
        Future<?> future = bossGroup.shutdownGracefully();
        Future<?> future1 = workerGroup.shutdownGracefully();
        future.syncUninterruptibly();
        future1.syncUninterruptibly();
        log.info("关闭 WebSocket 服务器成功");
    }

    public void run() throws InterruptedException {
        // 创建服务器启动引导对象
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO)) // 为 bossGroup 添加日志处理器
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 添加 IdleStateHandler，30秒内如果没有收到客户端的心跳，就关闭连接
                        pipeline.addLast(new IdleStateHandler(300, 0, 0));
                        // 添加 HTTP 编解码器，因为 WebSocket 是基于 HTTP 协议的
                        pipeline.addLast(new HttpServerCodec());
                        // 添加 ChunkedWriteHandler，用于大数据的分块传输
                        pipeline.addLast(new ChunkedWriteHandler());
                        // 添加 HttpObjectAggregator，将 HTTP 消息的多个部分合成一条完整的 HTTP 消息
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        // 添加自定义的 HttpHeadersHandler，用于保存用户 IP
                        pipeline.addLast(new HttpHeadersHandler());
                        // 添加 WebSocketServerProtocolHandler，用于处理 WebSocket 握手及帧的处理
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        // 添加自定义的 WebSocket 处理器，用于处理业务逻辑
                        pipeline.addLast(NETTY_WEBSOCKET_SERVER_INPUT_HANDLER);
                        //加入channelGroup
                        allChannels.put(socketChannel.id().asShortText(),socketChannel);
                    }
                });
        // 绑定端口，启动服务器
        serverBootstrap.bind(WEB_SOCKET_PORT).sync();
        log.info("WebSocket 服务器启动成功，监听端口: {}", WEB_SOCKET_PORT);
    }
}
