package cool.scx.proxy;

import io.netty5.handler.codec.http.FullHttpRequest;
import io.netty5.handler.codec.http.FullHttpResponse;

/**
 * 拦截器
 */
public interface ScxProxyInterceptor {

    default FullHttpRequest handleProxyRequest(FullHttpRequest httpRequest) {
        return httpRequest;
    }

    default FullHttpResponse handleProxyResponse(FullHttpResponse httpResponse) {
        return httpResponse;
    }

}
