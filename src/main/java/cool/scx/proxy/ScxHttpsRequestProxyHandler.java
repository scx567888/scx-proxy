package cool.scx.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ScxHttpsRequestProxyHandler extends ChannelInboundHandlerAdapter {

    private final ScxProxy scxProxy;

    public ScxHttpsRequestProxyHandler(ScxProxy scxProxy) {
        this.scxProxy = scxProxy;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

}
