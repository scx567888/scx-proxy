package cool.scx.proxy;

import cool.scx.proxy.util.WindowsProxyHelper;

public class ScxProxy {

    /**
     * 用来向远程服务器发送请求
     */
    final ScxProxyClient proxyClient;

    /**
     * 用来接受请求
     */
    final ScxProxyServer proxyServer;

    /**
     * 拦截器
     */
    ScxProxyInterceptor proxyInterceptor;

    /**
     * 旧的代理状态 用于停机时恢复
     */
    WindowsProxyHelper.ProxyInfo oldProxyInfo;

    public ScxProxy() {
        this.proxyClient = new ScxProxyClient(this);
        this.proxyServer = new ScxProxyServer(this);
    }

    public void setProxyInterceptor(ScxProxyInterceptor proxyInterceptor) {
        this.proxyInterceptor = proxyInterceptor;
    }

    public void start(int port) {
        this.oldProxyInfo = WindowsProxyHelper.getProxyInfoOrNull();

        WindowsProxyHelper.setProxyServer(port);
        WindowsProxyHelper.clearProxyOverride();
        WindowsProxyHelper.enableProxy();

        proxyServer.start(port);

        Runtime.getRuntime().addShutdownHook(Thread.ofPlatform().unstarted(this::shutdownHook));
    }

    private void shutdownHook() {
        proxyServer.stop();
        //恢复原来的代理设置
        WindowsProxyHelper.setProxy(this.oldProxyInfo);
    }

}
