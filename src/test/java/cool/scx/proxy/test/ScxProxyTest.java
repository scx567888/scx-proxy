package cool.scx.proxy.test;

import cool.scx.logging.ScxLoggerFactory;
import cool.scx.proxy.ScxProxy;
import cool.scx.proxy.ScxProxyInterceptor;
import io.netty5.handler.codec.http.FullHttpRequest;
import io.netty5.handler.codec.http.FullHttpResponse;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.HostAndPort;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.httpproxy.*;
import org.testng.annotations.Test;

public class ScxProxyTest {

    static {
        ScxLoggerFactory.rootConfig().setLevel(System.Logger.Level.DEBUG);
    }

    public static void main(String[] args) {
        test1();
//        test2();
    }

    @Test
    public static void test1() {
        var scxProxy = new ScxProxy();
        scxProxy.setProxyInterceptor(new ScxProxyInterceptor() {
            @Override
            public FullHttpRequest handleProxyRequest(FullHttpRequest httpRequest) {
                System.out.println(httpRequest);
                return ScxProxyInterceptor.super.handleProxyRequest(httpRequest);
            }

            @Override
            public FullHttpResponse handleProxyResponse(FullHttpResponse httpResponse) {
                System.out.println(httpResponse);
                return ScxProxyInterceptor.super.handleProxyResponse(httpResponse);
            }
        });
        scxProxy.proxy(8080);
    }

    public static void test2() {
        var vertx = Vertx.vertx();
        var httpClient = vertx.createHttpClient(new HttpClientOptions()
                .setSsl(false)
                .setKeyCertOptions(new JksOptions()
                        .setPath("C:\\Users\\worker\\Downloads\\my_keystore.p12")
                        .setPassword("123456"))
        );

        var proxyClient = vertx.createHttpClient(new HttpClientOptions()
                .setSsl(false)
                .setKeyCertOptions(new JksOptions()
                        .setPath("C:\\Users\\worker\\Downloads\\my_keystore.p12")
                        .setPassword("123456"))
        );

//        Future<HttpClientRequest> request1 = httpClient.request(new RequestOptions()
//                        .setTimeout(5000)
//                        .setSsl(true)
//                .setAbsoluteURI("https://cn.bing.com/")
//                .setMethod(HttpMethod.GET));
//        request1.onSuccess(c -> {
//            c.end();
//            c.response().onSuccess(e -> {
//                e.body().onSuccess(b -> {
//                    var s = b.toString();
//                    System.out.println();
//                });
//            }).onFailure(e -> {
//                System.out.println();
//            });
//        }).onFailure(c -> {
//            System.out.println();
//        });
        var httpsClient = vertx.createHttpClient(new HttpClientOptions()
                .setSsl(true)
                .setKeyCertOptions(new JksOptions()
                        .setPath("C:\\Users\\worker\\Downloads\\my_keystore.p12")
                        .setPassword("123456"))
        );
        HttpServer httpServer = vertx.createHttpServer(new HttpServerOptions()
                .setSsl(false)
                .setKeyCertOptions(new JksOptions()
                        .setPath("C:\\Users\\worker\\Downloads\\my_keystore.p12")
                        .setPassword("123456")
                )
        );

        HttpProxy httpProxy = HttpProxy.reverseProxy(new ProxyOptions().setSupportWebSocket(true), httpClient);
//        HttpProxy origin = httpProxy.originRequestProvider((request, client) -> {
//            var authority = request.authority();
//            var server = SocketAddress.inetSocketAddress(authority.port(), authority.host());
//            if (request.isSSL()) {
//                return httpsClient.request(new RequestOptions().setServer(server)).onSuccess(c -> {
//                    System.out.println();
//                }).onFailure(e -> {
//                    System.out.println();
//                });
//            } else {
//                return httpClient.request(new RequestOptions().setServer(server)).onSuccess(c -> {
//                    c.response().onSuccess(r -> {
//                        System.out.println();
//                    }).onFailure(e -> {
//                        System.out.println();
//                    });
//
//                }).onFailure(e -> {
//                    System.out.println();
//                });
//            }
//        });
        httpProxy.originSelector(c->{
            HostAndPort authority = c.authority();
            var s= SocketAddress.inetSocketAddress(authority.port() == -1?80:authority.port(),authority.host());
            return Future.succeededFuture(s);
        });
        httpProxy.addInterceptor(new ProxyInterceptor() {
            @Override
            public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
                var request = context.request();
                String uri = request.absoluteURI();
                Body body = request.getBody();
                System.out.println(uri);
                return ProxyInterceptor.super.handleProxyRequest(context);
            }

            @Override
            public Future<Void> handleProxyResponse(ProxyContext context) {
                var response = context.response();
                Body body = response.getBody();
                System.out.println();
                return ProxyInterceptor.super.handleProxyResponse(context);
            }
        });
//        httpServer.requestHandler(request->{
//            ProxyRequest proxyRequest = ProxyRequest.reverseProxy(request);
//            HostAndPort authority = request.authority();
//
//            proxyClient.request(proxyRequest.getMethod(), authority.port(), authority.host(), proxyRequest.getURI())
//                    .compose(proxyRequest::send)
//                    .onSuccess(proxyResponse -> {
//                        // Send the proxy response
//                        proxyResponse.send();
//                    })
//                    .onFailure(err -> {
//                        // Release the request
//                        proxyRequest.release();
//
//                        // Send error
//                        request.response().setStatusCode(500)
//                                .send();
//                    });
//        }).webSocketHandler(c -> {
//            System.out.println();
//        });
        httpServer.requestHandler(httpProxy);
        httpServer.listen(17890).onSuccess(c -> {
            System.out.println("成功");
        }).onFailure(e -> {
            e.printStackTrace();
            System.out.println("失败");
        });

//        httpProxy.

    }

}
