package cool.scx.proxy;

import io.netty5.channel.ChannelHandler;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.http.FullHttpResponse;

public class ScxHttpResponseProxyHandler implements ChannelHandler {

    private final ScxProxy scxProxy;
    private final ChannelHandlerContext clientCtx;

    public ScxHttpResponseProxyHandler(ScxProxy scxProxy, ChannelHandlerContext clientCtx) {
        this.scxProxy = scxProxy;
        this.clientCtx = clientCtx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpResponse httpResponse) {
            //处理 http 请求
            if (scxProxy.proxyInterceptor != null) {
                httpResponse = scxProxy.proxyInterceptor.handleProxyResponse(httpResponse);
            }
            clientCtx.channel().writeAndFlush(httpResponse);
        } else {
            ChannelHandler.super.channelRead(ctx, msg);
        }
    }

}
