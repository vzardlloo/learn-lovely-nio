package nio.channel;


import com.sun.org.apache.xpath.internal.SourceTree;
import nio.kit.PrintKit;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Random;

public class ExampleForFileLock {
    //int类型的字节数
    private static final int SIZEOF_INT = 4;
    //锁定区域的开始
    private static final int INDEX_START = 0;
    //int的数量
    private static final int INDEX_COUNT = 10;
    //锁定区域的字节数大小
    private static final int INDEX_SIZE = INDEX_COUNT * SIZEOF_INT;

    private static ByteBuffer byteBuffer = ByteBuffer.allocate(INDEX_SIZE);
    private static IntBuffer indexBuffer = byteBuffer.asIntBuffer();
    private Random random = new Random();

    public static void main(String[] args) {

        try {
            boolean writer = false;
            String filename;
            if (args.length != 2) {
                System.out.println("Usage: [ -r | -w ] filename");
                return;
            }
            writer = args[0].equals("-w");
            filename = args[1];
            File file = new File(filename);
            if (file.exists() && file.isFile() && writer) {
                file.delete();
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(filename, (writer) ? "rw" : "r");

            FileChannel fileChannel = randomAccessFile.getChannel();
            ExampleForFileLock effl = new ExampleForFileLock();
            if (writer) {
                effl.doUpdates(fileChannel);
            } else {
                effl.doQuery(fileChannel);
            }


        } catch (IOException | OverlappingFileLockException e) {
            e.printStackTrace();
        }


    }

    //------------------------------------------------------------------

    void doQuery(FileChannel fileChannel) {
        try {
            //共享锁
            FileLock lock = fileChannel.lock(INDEX_START, INDEX_SIZE, true);
            while (true) {
                println("trying for shared lock...");
                int resp = random.nextInt(60) + 20;
                for (int i = 0; i < resp; i++) {
                    int n = random.nextInt(INDEX_COUNT);
                    int position = INDEX_START + (n * SIZEOF_INT);
                    byteBuffer.clear();
                    //从通道读取到缓冲区
                    fileChannel.read(byteBuffer, position);
                    //byteBuffer.flip();
                    int value = indexBuffer.get(n);
                    println("Index entry " + n + "=" + value);
                    Thread.sleep(100);
                }
                println("<sleeping>");
                Thread.sleep(random.nextInt(3000) + 500);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------------------------------------
    void doUpdates(FileChannel fileChannel) {
        try {
            while (true) {
                println("try for exclusive lock...");
                //独占锁
                FileLock lock = fileChannel.lock(INDEX_START, INDEX_SIZE, false);
                updateIndex(fileChannel);
                lock.release();
                println("<sleeping>");
                Thread.sleep(random.nextInt(2000) + 500);
            }
        } catch (IOException | InterruptedException e) {
            e.getStackTrace();
        }
    }

    //--------------------------------------------------------------------------
    private int idxval = 1;
    private int offset = 0;

    private void updateIndex(FileChannel fileChannel) {
        try {
            indexBuffer.clear();
            for (int i = 0; i < INDEX_COUNT; i++) {
                idxval++;
                println("Update index " + i + "=" + idxval);
                indexBuffer.put(idxval);
                Thread.sleep(500);
            }
            //从缓冲区写入通道,position标记文件位置
            fileChannel.write(byteBuffer, INDEX_START + (offset++) * INDEX_SIZE);
            fileChannel.force(true);
            byteBuffer.clear();


        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }


    //-------------------------------------------------------------------
//    private int lastLineLen = 0;

    private void println(String msg) {
        System.out.println("\r ");
        System.out.println(msg);
//        for (int i = 0; i < lastLineLen; i++) {
//            System.out.println(" ");
//        }
        System.out.println("\r");
        System.out.flush();
//        lastLineLen = msg.length();
    }


}
