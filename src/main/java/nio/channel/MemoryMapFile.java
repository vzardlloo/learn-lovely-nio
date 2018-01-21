package nio.channel;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 内存映射文件(实现一个简单的http服务)
 */
public class MemoryMapFile {

    private static final String OUTPUT_FILE = "Http.out";
    private static final String LINE_SEP = "\r\n";
    private static final String SERVER_ID = "Server: vzard";
    private static final String HTTP_HDR = "HTTP/1.0 200 OK" + LINE_SEP + SERVER_ID + LINE_SEP;
    private static final String HTTP_404_HDR = "HTTP/1.0 404 Not Found" + LINE_SEP + SERVER_ID + LINE_SEP;
    private static final String MSG_404 = "Could not open file: ";

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: filename");
            return;
        }

        String filename = args[0];
        ByteBuffer header = ByteBuffer.wrap(bytes(HTTP_HDR));
        ByteBuffer dynhdrs = ByteBuffer.allocate(128);
        ByteBuffer[] gather = {header, dynhdrs, null};
        String contentType = "unknown/unknown";
        long contentLength = -1;
        try {
            FileInputStream fis = new FileInputStream(filename);
            FileChannel fileChannel = fis.getChannel();
            //内存映射
            MappedByteBuffer filedata = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            gather[2] = filedata;
            contentLength = fileChannel.size();
            //根据 URL 的指定 "file" 部分尝试确定对象的内容类型
            contentType = URLConnection.guessContentTypeFromName(filename);
        } catch (IOException e) {
            //file could not be opened,report problem
            ByteBuffer buffer = ByteBuffer.allocate(128);
            String msg = MSG_404 + e + LINE_SEP;
            buffer.put(bytes(msg));
            buffer.flip();
            //Use the http error response
            gather[0] = ByteBuffer.wrap(bytes(HTTP_404_HDR));
            gather[2] = buffer;
            contentLength = msg.length();
            contentType = "text/plain";
        }

        StringBuffer response = new StringBuffer();
        response.append("Content-Length: " + contentLength);
        response.append(LINE_SEP);
        response.append("Content-Type: ").append(contentType);
        response.append(LINE_SEP).append(LINE_SEP);
        dynhdrs.put(bytes(response.toString()));
        dynhdrs.flip();
        FileOutputStream fos = new FileOutputStream(OUTPUT_FILE);
        FileChannel out = fos.getChannel();
        //all the buffers have been prepared; write item out
        while (out.write(gather) > 0) {

        }
        out.close();
        System.out.println("Success to output written to " + OUTPUT_FILE);
    }


    // Convert a string to its constituent bytes
    // from the ASCII character se
    private static byte[] bytes(String string) throws Exception {
        return (string.getBytes("US-ASCII"));
    }


}
