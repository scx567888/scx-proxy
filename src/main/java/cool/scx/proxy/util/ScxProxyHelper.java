package cool.scx.proxy.util;

import io.netty5.handler.codec.http.HttpRequest;

import java.net.InetSocketAddress;

import static cool.scx.util.StringUtils.startsWithIgnoreCase;

public class ScxProxyHelper {

    /**
     * 获取代理请求
     *
     * @param httpRequest http请求
     */
    public static HostAndPort getHostAndPort(HttpRequest httpRequest) {
        var hostValue = httpRequest.headers().get("host");

        var arr = hostValue.toString().split(":");

        var host = arr[0];
        var port = 80;
        if (arr.length > 1) {
            port = Integer.parseInt(arr[1]);
        } else if (startsWithIgnoreCase(httpRequest.uri(), "https")) {
            port = 443;
        }
        return new HostAndPort(host, port);
    }

    public record HostAndPort(String host, int port) {

        public InetSocketAddress toInetSocketAddress() {
            return new InetSocketAddress(host, port);
        }
        
    }

}
