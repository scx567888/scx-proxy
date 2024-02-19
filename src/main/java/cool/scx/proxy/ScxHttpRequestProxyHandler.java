package cool.scx.proxy;

import cool.scx.proxy.util.ScxProxyHelper;
import io.netty5.bootstrap.Bootstrap;
import io.netty5.buffer.Buffer;
import io.netty5.channel.Channel;
import io.netty5.channel.ChannelHandler;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.channel.ChannelInitializer;
import io.netty5.channel.socket.nio.NioSocketChannel;
import io.netty5.handler.codec.http.*;

import static cool.scx.proxy.util.HandlerNames.*;

public class ScxHttpRequestProxyHandler implements ChannelHandler {

    private final ScxProxy scxProxy;

    public ScxHttpRequestProxyHandler(ScxProxy scxProxy) {
        this.scxProxy = scxProxy;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest httpRequest) {

            //检查是否为 CONNECT 请求 用于 http
            if (httpRequest.method() == HttpMethod.CONNECT) {
                var connectResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, ctx.bufferAllocator().allocate(0));
                ctx.writeAndFlush(connectResponse);
                return;
            }

            //调用自定义的拦截器
            if (scxProxy.proxyInterceptor != null) {
                httpRequest = scxProxy.proxyInterceptor.handleProxyRequest(httpRequest);
            }

            try {
                //
                var hostAndPort = ScxProxyHelper.getHostAndPort(httpRequest);

                //转发请求
                sendToServer(hostAndPort, ctx, httpRequest);
            }catch (Exception e){
             e.printStackTrace();   
            }
          

        }else{
            //处理不了就转发给下一个
            ChannelHandler.super.channelRead(ctx, msg);    
        }
        
    }

    public void sendToServer(ScxProxyHelper.HostAndPort remoteAddress, ChannelHandlerContext clientCtx, HttpRequest fullHttpRequest) {
        var bootstrap = new Bootstrap();
        bootstrap.group(clientCtx.channel().executor())
                .channelFactory(NioSocketChannel::new)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {

                        //客户端 Http 编解码器 处理解码 HttpResponse 和 编码 HttpRequest
                        channel.pipeline().addLast(HTTP_REQUEST_ENCODER, new HttpRequestEncoder());
                        channel.pipeline().addLast(HTTP_RESPONSE_DECODER, new HttpResponseDecoder());

                        //聚合 Http 对象
                        channel.pipeline().addLast(HTTP_OBJECT_AGGREGATOR, new HttpObjectAggregator<>(10 * 65535));

                        //客户端 解压缩处理
                        channel.pipeline().addLast(HTTP_CONTENT_DECOMPRESSOR, new HttpContentDecompressor());

                        //客户端响应对象处理
                        channel.pipeline().addLast(new ScxHttpResponseProxyHandler(scxProxy, clientCtx));
                    }
                });

        var connect = bootstrap.connect(remoteAddress.toInetSocketAddress());

        connect.addListener(channelFuture -> {
            if (channelFuture.isSuccess()) {
                Channel now = channelFuture.getNow();
                now.writeAndFlush(fullHttpRequest);
            } else {
                clientCtx.channel().close();
                channelFuture.cause().printStackTrace();
            }
        });
    }

}
