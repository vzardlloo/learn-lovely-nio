package nio.channel;


import nio.kit.PrintKit;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 演示基本的channel I/O ,生成随机字符串写入文件，再把文件内容读入内存
 */
public class ExampleChannel {

    private static final String DEMOGRAPHIC = "D:\\blahblah.txt";

    public static void main(String[] args) throws Exception {

        int reps = 10;
        if (args.length > 0) {
            reps = Integer.parseInt(args[0]);
        }
        //创建一个文件输出流
        FileOutputStream fos = new FileOutputStream(DEMOGRAPHIC);
        //从文件流获取通道
        GatheringByteChannel gatherChannel = fos.getChannel();
        //获取一个reps大小的缓冲区数组
        ByteBuffer[] bs = byteBufferArray(reps);
        while (gatherChannel.write(bs) > 0) {

        }

        System.out.println("Mindshare paradigms synergized to: " + DEMOGRAPHIC);
        System.out.println("");

        //创建一个文件输入流
        FileInputStream fis = new FileInputStream(DEMOGRAPHIC);
        //从文件流获取通道
        FileChannel fileChannel = fis.getChannel();
        //获取一个reps大小的缓冲区数组
        ByteBuffer[] bb = new ByteBuffer[reps];
        for (int i = 0; i < bb.length; i++) {
            bb[i] = ByteBuffer.allocate(1024);
        }
        //读到内存里并打印
        for (int i = 0; i < bb.length; i++) {
            while (fileChannel.read(bb[i]) != -1) {
                bb[i].flip();
                PrintKit.printBuffer(bb[i]);
            }

        }
    }

    private static String newline = System.getProperty("line.separator");

    /**
     * 获取一个指定容量的缓冲区数组
     *
     * @param howMany
     * @return
     * @throws Exception
     */
    private static ByteBuffer[] byteBufferArray(int howMany) throws Exception {
        List list = new LinkedList();
        for (int i = 0; i < howMany; i++) {
            list.add(pickRandom(col1, ""));
            list.add(pickRandom(col2, ""));
            list.add(pickRandom(col3, newline));
        }
        ByteBuffer[] buffers = new ByteBuffer[list.size()];
        list.toArray(buffers);
        return buffers;
    }

    private static Random rand = new Random();

    /**
     * 获取一个由随机字符串填满的缓冲区
     *
     * @param strings
     * @param suffix
     * @return
     * @throws Exception
     */
    private static ByteBuffer pickRandom(String[] strings, String suffix) throws Exception {
        String string = strings[rand.nextInt(strings.length)];
        int total = string.length() + suffix.length();
        ByteBuffer buffer = ByteBuffer.allocate(total);
        buffer.put(string.getBytes("US-ASCII"));
        buffer.put(suffix.getBytes("US-ASCII"));
        buffer.flip();
        return buffer;
    }

    private static String[] col1 = {
            "Aggregate", "Enable", "Leverage",
            "Facilitate", "Synergize", "Repurpose",
            "Strategize", "Reinvent", "Harness"
    };

    private static String[] col2 = {
            "cross-platform", "best-of-breed", "frictionless",
            "ubiquitous", "extensible", "compelling",
            "mission-critical", "collaborative", "integrated"
    };

    private static String[] col3 = {
            "methodologies", "infomediaries", "platforms",
            "schemas", "mindshare", "paradigms",
            "functionalities", "web services", "infrastructures"
    };


}
