package cool.scx.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import static cool.scx.proxy.util.HandlerNames.*;

public class ScxProxyServer {

    private final ScxProxy scxProxy;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;

    public ScxProxyServer(ScxProxy scxProxy) {
        this.scxProxy = scxProxy;
    }

    public void start(int port) {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();

        this.serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channelFactory(NioServerSocketChannel::new)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {

                        //服务器端 Http 编解码器 处理解码 HttpRequest 和 编码 HttpResponse
                        channel.pipeline().addLast(HTTP_REQUEST_DECODER, new HttpRequestDecoder());
                        channel.pipeline().addLast(HTTP_RESPONSE_ENCODER, new HttpResponseEncoder());

                        //聚合 Http 对象
                        channel.pipeline().addLast(HTTP_OBJECT_AGGREGATOR, new HttpObjectAggregator(65535 * 10));

                        //服务器端 压缩处理
                        channel.pipeline().addLast(HTTP_CONTENT_COMPRESSOR, new HttpContentCompressor());

                        //将请求转发给远程服务器
                        channel.pipeline().addLast(SCX_HTTP_PROXY_HANDLER, new ScxHttpProxyHandler(scxProxy));
                        channel.pipeline().addLast(SCX_SOCKET_PROXY_HANDLER, new ScxSocketProxyHandler());

                    }
                });

        var bind = serverBootstrap.bind(port);
        bind.addListener(channelFuture -> {
            if (channelFuture.isSuccess()) {
                System.out.println("启动成功!!! ");
            } else {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
    }

    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (serverBootstrap != null) {
            //todo 关闭服务器 ?
        }
    }

}
