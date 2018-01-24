package nio.channel;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Random;

/**
 * 管道的使用 buffer(in thread_1) -->WritableByteBuffer-->pipe连接-->ReadableByteBuffer -->buffer(in thread_2)
 */
public class PipeTest {

    public static void main(String[] args) throws Exception {
        //返回一个将向给定的输出流写入数据的通道
        WritableByteChannel out = Channels.newChannel(System.out);

        ReadableByteChannel workChannel = startWorker(10);
        ByteBuffer buffer = ByteBuffer.allocate(100);
        //从Channel中读取数据到buffer中
        while (workChannel.read(buffer) >= 0) {
            buffer.flip();
            //写到out通道里,随后到System.out中
            out.write(buffer);
            buffer.clear();
        }

    }

    private static ReadableByteChannel startWorker(int reps) throws Exception {
        //在线程间传递数据,连接不同线程中的两个通道
        Pipe pipe = Pipe.open();
        Worker worker = new Worker(pipe.sink(), reps);
        //work线程中把buffer数据写入到WritableByteChannel中
        worker.start();
        //pipe连接WritableByteChannel和ReadableByteChannel,然后把ReadableByteChannel返回到当前线程
        return pipe.source();
    }

    /**
     * 新开一个线程,将buffer中的内容的写入通道
     */
    private static class Worker extends Thread {
        WritableByteChannel writableByteChannel;
        private int reps;

        Worker(WritableByteChannel channel, int reps) {
            this.writableByteChannel = channel;
            this.reps = reps;
        }

        @Override
        public void run() {
            ByteBuffer buffer = ByteBuffer.allocate(100);
            try {
                for (int i = 0; i < this.reps; i++) {
                    doSomeWork(buffer);
                    //读入通道
                    while (writableByteChannel.write(buffer) > 0) {

                    }
                }
                this.writableByteChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 随机字符串数组
         */
        private String[] products = {
                "No good deed goes unpunished",
                "To be, or what?",
                "No matter where you go, there you are",
                "Just say \"Yo\"",
                "My karma ran over my dogma"
        };

        private Random random = new Random();

        /**
         * put一个随机字符串进缓存区
         *
         * @param buffer
         */
        private void doSomeWork(ByteBuffer buffer) {
            int product = random.nextInt(products.length);
            buffer.clear();
            buffer.put(products[product].getBytes());
            buffer.put("\r\n".getBytes());
            buffer.flip();
        }

    }


}
