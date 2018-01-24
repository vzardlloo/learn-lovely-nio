package nio.channel;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 非阻塞的accept方法
 */
public class ChannelAccept {

    private static final String GREETING = "Hello I must be going.\r\n";

    public static void main(String[] args) throws Exception {
        int port = 8000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        ByteBuffer buffer = ByteBuffer.wrap(GREETING.getBytes());
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //获取socket(端口的逻辑抽象),与物理端口绑定
        serverSocketChannel.socket().bind(new InetSocketAddress(port));

        serverSocketChannel.configureBlocking(false);
        while (true) {
            System.out.println("Waiting for connections...");
            //以非阻塞模式被调用，当没有传入连接在等待时，ServerSocketChannel.accept( )会立即返 回null。正是这种检查连接而不阻塞的能力实现了可伸缩性并降低了复杂性。
            SocketChannel sc = serverSocketChannel.accept();
            if (sc == null) {
                Thread.sleep(100);
            } else {
                System.out.println("Incoming connection from: " + sc.socket().getRemoteSocketAddress());
                buffer.rewind();
                sc.write(buffer);
                sc.close();
            }


        }
    }

}
