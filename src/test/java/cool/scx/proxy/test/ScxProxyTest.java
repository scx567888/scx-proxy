package cool.scx.proxy.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;

public class ScxProxyTest {

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        var serverBootstrap = new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {

                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof ByteBuf byteBuf)
                                {
                                    
                                    System.out.println(byteBuf.toString(StandardCharsets.UTF_8));
                                }
//                                super.channelRead(ctx, msg);
                            }
                        });
                    }
                });
        
        serverBootstrap.bind(8080);
    }

}
