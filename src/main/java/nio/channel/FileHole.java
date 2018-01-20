package nio.channel;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 文件黑洞
 */
public class FileHole {

    public static void main(String[] args) throws IOException {

        File temp = File.createTempFile("holy", ".txt", new File("D:\\"));
        RandomAccessFile file = new RandomAccessFile(temp, "rw");
        FileChannel channel = file.getChannel();
        ByteBuffer buffer = ByteBuffer.allocateDirect(100);
        putData(0, buffer, channel);
        putData(50000, buffer, channel);
        putData(5000000, buffer, channel);
        System.out.println("Wrote temp file '" + temp.getPath()
                + "', size=" + channel.size());
        channel.close();
        file.close();

    }

    private static void putData(int positon, ByteBuffer buffer, FileChannel channel) throws IOException {
        String string = "*<-- location " + positon;
        buffer.clear();
        buffer.put(string.getBytes("US-ASCII"));
        buffer.flip();
        channel.position(positon);
        channel.write(buffer);
    }


}
