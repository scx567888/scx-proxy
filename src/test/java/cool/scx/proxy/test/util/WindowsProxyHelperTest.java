package cool.scx.proxy.test.util;

import cool.scx.proxy.util.WindowsProxyHelper;
import org.testng.annotations.Test;

public class WindowsProxyHelperTest {

    public static void main(String[] args) {
        test1();
    }

    @Test
    public static void test1() {
        //获取旧的代理设置
        var oldProxyInfo = WindowsProxyHelper.getProxyInfo();

        //设置自己的 代理
        WindowsProxyHelper.setProxyServer(8080);
        WindowsProxyHelper.setProxyOverride("192.*");
        WindowsProxyHelper.enableProxy();
        WindowsProxyHelper.clearProxyOverride();
        WindowsProxyHelper.clearProxyServer();
        WindowsProxyHelper.disableProxy();

        //结束时 还原为 原来的代理设置
        WindowsProxyHelper.setProxy(oldProxyInfo);
    }

}
