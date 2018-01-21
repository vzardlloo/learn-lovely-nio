package nio.channel;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * RFC 868 协议的时间服务器客户端
 * http://www.ietf.org/rfc/rfc0868.txt
 *
 * @author vzard
 */
public class TimeClient {

    private static final int DEFAULT_TIME_PORT = 37;
    private static final long DIFF_1990 = 2208988800L;
    protected int port = DEFAULT_TIME_PORT;
    //远程主机列表
    protected List<InetSocketAddress> remotehosts;
    //DatagramChannel 是无连接的。每个数据报（datagram）都是一个自包含的实体，拥有它自己 的目的地址及不依赖其他数据报的数据净荷
    protected DatagramChannel datagramChannel;

    public TimeClient(String[] args) throws Exception {
        if (args.length == 0) {
            throw new Exception("Usage: [-p port] host...");
        }
        parseArgs(args);
        this.datagramChannel = DatagramChannel.open();

    }

    public static void main(String[] args) throws Exception {
        TimeClient timeClient = new TimeClient(args);
        timeClient.sentRequests();
        timeClient.getReplies();
    }

    /**
     * 发送时间请求给所有的时间服务器
     *
     * @throws Exception
     */
    protected void sentRequests() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        Iterator<InetSocketAddress> it = remotehosts.iterator();
        while (it.hasNext()) {
            InetSocketAddress inetSocketAddress = it.next();
            System.out.println("Request time from : " + inetSocketAddress.getHostName() + " : " + inetSocketAddress.getPort());
            //空的数据包
            buffer.clear().flip();
            //发送一个空的数据包给服务器
            datagramChannel.send(buffer, inetSocketAddress);
        }
    }


    public void getReplies() throws Exception {
        //创建一个可以存放long类型数据的缓冲区,原因是一个无符号的32位时间值可以通过long类型来截取
        ByteBuffer longBuffer = ByteBuffer.allocate(8);
        //按大端顺序排列
        longBuffer.order(ByteOrder.BIG_ENDIAN);
        //用0充满整个缓冲区
        longBuffer.putLong(0, 0);
        //截取低32位
        longBuffer.position(4);
        ByteBuffer buffer = longBuffer.slice();

        //预期获得的应答数量
        int expect = remotehosts.size();
        //已经收到的应答数量
        int replied = 0;
        System.out.println("");
        System.out.println("Waiting for replies ...");
        while (true) {
            InetSocketAddress inetSocketAddress = receivePacket(datagramChannel, buffer);
            buffer.flip();
            replied++;
            printTime(longBuffer.getLong(0), inetSocketAddress);
            if (expect == replied) {
                System.out.println("All packages received...");
                break;
            }

            System.out.println("Received " + replied + " of " + expect + "expect");
        }
    }

    /**
     * 接受一个服务器返回的包含32位时间值的数据包
     *
     * @param channel
     * @param buffer
     * @return
     * @throws Exception
     */
    protected InetSocketAddress receivePacket(DatagramChannel channel, ByteBuffer buffer) throws Exception {
        buffer.clear();
        //接受一个无符号的32位的,大端顺序排列的值,即以32位表示的时间
        return ((InetSocketAddress) channel.receive(buffer));
    }

    protected void printTime(long remote1990, InetSocketAddress socketAddress) {
        //1970.1.1 到现在的秒数
        long local = System.currentTimeMillis() / 1000;

        long remote = remote1990 - DIFF_1990;

        Date remoteDate = new Date(remote * 1000);

        Date localDate = new Date(local * 1000);

        long skew = remote - local;

        System.out.println("Reply from " + socketAddress.getHostName() + ": " + socketAddress.getPort());
        System.out.println("there: " + remoteDate);
        System.out.println("here: " + localDate);
        System.out.print("skew: ");
        if (skew == 0) {
            System.out.println("none");
        } else if (skew > 0) {
            System.out.println(skew + " seconds ahead");
        } else {
            System.out.println((-skew) + " second behind");
        }

    }

    /**
     * 解析参数
     *
     * @param args
     */
    protected void parseArgs(String[] args) {
        remotehosts = new LinkedList();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            //发送客户端的请求到指定的端口
            if (arg.equals("-p")) {
                i++;
                this.port = Integer.parseInt(args[i]);
                continue;
            }
            InetSocketAddress socketAddress = new InetSocketAddress(arg, port);
            if (socketAddress.getAddress() == null) {
                System.out.println("Can't resolve address:" + arg);
                continue;
            }
            //添加到远程主机列表
            remotehosts.add(socketAddress);
        }
    }

}
