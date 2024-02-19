package cool.scx.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;

public class ScxHttpResponseProxyHandler extends ChannelInboundHandlerAdapter {

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
        }
        super.channelRead(ctx, msg);
    }

}
