package cool.scx.proxy;

import cool.scx.proxy.util.WindowsProxyHelper;
import io.netty5.bootstrap.ServerBootstrap;
import io.netty5.channel.EventLoopGroup;
import io.netty5.channel.MultithreadEventLoopGroup;
import io.netty5.channel.SingleThreadEventLoop;
import io.netty5.channel.nio.NioHandler;
import io.netty5.channel.socket.nio.NioServerSocketChannel;
import io.netty5.handler.logging.LogLevel;
import io.netty5.handler.logging.LoggingHandler;

public class ScxProxy {

    /**
     * 拦截器
     */
    ScxProxyInterceptor proxyInterceptor;
    
    /**
     * 旧的代理状态 用于停机时恢复
     */
    WindowsProxyHelper.ProxyInfo oldProxyInfo;
    
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;

    public void setProxyInterceptor(ScxProxyInterceptor proxyInterceptor) {
        this.proxyInterceptor = proxyInterceptor;
    }

    public void proxy(int port) {
        this.oldProxyInfo = WindowsProxyHelper.getProxyInfoOrNull();

        WindowsProxyHelper.setProxyServer(port);
        WindowsProxyHelper.clearProxyOverride();
        WindowsProxyHelper.enableProxy();

        start(port);

        Runtime.getRuntime().addShutdownHook(Thread.ofPlatform().unstarted(this::shutdownHook));
    }

    private void shutdownHook() {
        stop();
        //恢复原来的代理设置
        WindowsProxyHelper.setProxy(this.oldProxyInfo);
    }

    protected void start(int port) {

        this.bossGroup = new MultithreadEventLoopGroup(1,NioHandler.newFactory());
        this.workerGroup = new MultithreadEventLoopGroup(NioHandler.newFactory());

        this.serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channelFactory(NioServerSocketChannel::new)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ScxProxyChannelInitializer(this));

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

    protected void stop() {
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
