package cool.scx.proxy;

import io.netty5.channel.ChannelInitializer;
import io.netty5.channel.socket.SocketChannel;
import io.netty5.handler.codec.http.HttpContentCompressor;
import io.netty5.handler.codec.http.HttpObjectAggregator;
import io.netty5.handler.codec.http.HttpRequestDecoder;
import io.netty5.handler.codec.http.HttpResponseEncoder;

import static cool.scx.proxy.util.HandlerNames.*;

public class ScxProxyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ScxProxy scxProxy;

    public ScxProxyChannelInitializer(ScxProxy scxProxy) {
        this.scxProxy = scxProxy;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        //服务器端 Http 编解码器 处理解码 HttpRequest 和 编码 HttpResponse
        channel.pipeline().addLast(HTTP_REQUEST_DECODER, new HttpRequestDecoder());
        channel.pipeline().addLast(HTTP_RESPONSE_ENCODER, new HttpResponseEncoder());

        //聚合 Http 对象
        channel.pipeline().addLast(HTTP_OBJECT_AGGREGATOR, new HttpObjectAggregator<>(65535 * 10));

        //服务器端 压缩处理
        channel.pipeline().addLast(HTTP_CONTENT_COMPRESSOR, new HttpContentCompressor());

        //将请求转发给远程服务器
        channel.pipeline().addLast(SCX_HTTP_REQUEST_PROXY_HANDLER, new ScxHttpRequestProxyHandler(scxProxy));
        channel.pipeline().addLast(SCX_SOCKET_PROXY_HANDLER, new ScxSocketProxyHandler());
    }

}
