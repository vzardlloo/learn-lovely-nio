package nio.channel;


import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * connect()的异步使用
 */
public class ConnectAsync {

    public static void main(String[] args) throws Exception {
        if (1 <= args.length && args.length < 2) {
            System.out.println("Usage: [host port]");
            return;
        }
        String host = "localhost";
        int port = 8000;

        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        //远程主机及端口的逻辑抽象
        InetSocketAddress address = new InetSocketAddress(host, port);
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        System.out.println("initiating connection...");
        socketChannel.connect(address);
        //正在建立连接时finishConnect()会返回false,异步执行不会等待连接完成，而会去doSomething
        while (!socketChannel.finishConnect()) {
            doSomeThing();
        }
        System.out.println("connection established...");
        socketChannel.close();


    }

    private static void doSomeThing() {
        System.out.println("doing something...");
    }

}
