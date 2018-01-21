package nio.channel;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ExampleForFileChannel {


    public static void main(String[] args) throws IOException {

        File temp = File.createTempFile("file", ".txt", new File("D:\\"));

        RandomAccessFile randomAccessFile = new RandomAccessFile(temp, "rw");
        ByteBuffer buffer = ByteBuffer.allocateDirect(100);
        FileChannel fileChannel = randomAccessFile.getChannel();
        //填充文件
        for (int i = 0; i < 1000; i++) {
            putData(i, buffer, fileChannel);
        }

        //文件的位置
        System.out.println("file position : " + fileChannel.position());
        //文件大小
        System.out.println("file size : " + fileChannel.size());
        //重新设置大小
        fileChannel.truncate(800);
        System.out.println("file new size : " + fileChannel.size());
        //强制将全部待定的修改都应用到磁盘的文件
        fileChannel.force(true);

        randomAccessFile.close();
        fileChannel.close();

    }

    private static void putData(int positon, ByteBuffer buffer, FileChannel channel) throws IOException {
        String string = "*<-- location ";
        buffer.clear();
        buffer.put(string.getBytes("US-ASCII"));
        buffer.flip();
        channel.position(positon);
        channel.write(buffer);
    }


}
