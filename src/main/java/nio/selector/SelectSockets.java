package nio.selector;


import nio.kit.PrintKit;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class SelectSockets {

    public static int PORT_NUMBER = 1234;

    public static void main(String[] args) throws Exception {
        new SelectSockets().go(args);
    }

    public void go(String[] args) throws Exception {
        int port = PORT_NUMBER;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        System.out.println("Listening on port : " + port);

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //根据通道来获取对应的socket
        ServerSocket serverSocket = serverSocketChannel.socket();
        //创建一个selector
        Selector selector = Selector.open();

        serverSocket.bind(new InetSocketAddress(port));
        //必须要把通道置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //注册到选择器上
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(100);

        while (true) {
            int n = selector.selectNow();
            System.out.println(n);
            if (n == 0) {
                continue;
            }
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            //
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel channel = server.accept();

                    registerChannel(selector, channel, SelectionKey.OP_READ);

                    sayHello(channel);

                }
                if (key.isReadable()) {

                    readDataFromSocket(key);
                }
                it.remove();
            }
        }
    }

    //-------------------------------------------------------------------------

    /**
     * 将一个通道注册到一个选择器
     *
     * @param selector
     * @param channel
     * @param ops
     * @throws Exception
     */
    protected void registerChannel(Selector selector, SocketChannel channel, int ops) throws Exception {
        if (channel == null) {
            return;
        }
        channel.configureBlocking(false);
        channel.register(selector, ops);

    }

    //-------------------------------------------------------------------------

    //对所有通道使用一个字节缓冲区，一个线程为所有的通道服务，没有并发问题
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    /**
     * 对于一个准备读出数据的通道的简单的数据处理方法
     *
     * @param key
     * @throws Exception
     */
    protected void readDataFromSocket(SelectionKey key) throws Exception {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int count;
        buffer.clear();
        while ((count = socketChannel.read(buffer)) > 0) {
            buffer.flip();
            PrintKit.printBuffer(buffer);
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
            buffer.clear();
        }
        if (count < 0) {
            socketChannel.close();
        }

    }

    /**
     * 把一个字符串写进通道
     *
     * @param channel
     * @throws Exception
     */
    private void sayHello(SocketChannel channel) throws Exception {
        buffer.clear();
        buffer.put("Hi there!\r\n".getBytes());
        buffer.flip();

        channel.write(buffer);
    }


}
