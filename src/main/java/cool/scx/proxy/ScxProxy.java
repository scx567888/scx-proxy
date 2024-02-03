package cool.scx.proxy;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.NetServer;

public class ScxProxy {
    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        
        var netServer = vertx.createNetServer();
        
        netServer.connectHandler(c->{
            System.out.println(c);
        });

        netServer.listen(9999);

        var httpServer = vertx.createHttpServer();
        
        httpServer.connectionHandler(c->{
            System.out.println(c);
        });
        
        httpServer.requestHandler(c->{
            System.out.println(c); 
        });
        
        httpServer.webSocketHandler(c->{
            System.out.println();
        });
        
        httpServer.listen(8888);
        System.out.println();
    }
}
