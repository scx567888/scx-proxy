package cool.scx.proxy.test;

import cool.scx.util.$;
import cool.scx.util.cycle_iterable.CycleIterable;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class sss {
    public static void main(String[] args) {
        var s= new NioEventLoopGroup(Integer.MAX_VALUE,Executors.newVirtualThreadPerTaskExecutor());

        for (int i = 0; i < 99; i++) {
            s.submit(()->{
                try {
                    Thread.sleep(999);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("123");
            });    
        }
        $.sleep(99999);
    }
}
