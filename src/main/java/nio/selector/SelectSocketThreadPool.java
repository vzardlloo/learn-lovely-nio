package nio.selector;


import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class SelectSocketThreadPool extends SelectSockets {

    private static final int MAX_THREADS = 5;
    private ThreadPool pool = new ThreadPool(MAX_THREADS);

    public static void main(String[] args) throws Exception {
        new SelectSocketThreadPool().go(args);
    }

    /**
     * 对于一个准备读出数据的通道的简单的数据处理方法
     *
     * @param key
     * @throws Exception
     */
    protected void readDataFromSocket(SelectionKey key) throws Exception {
        WorkThread worker = pool.getWorker();
        if (worker == null) {
            return;
        }
        worker.serviceChannel(key);
    }

    /**
     * 线程池
     */
    private class ThreadPool {
        List<WorkThread> idle = new LinkedList();

        ThreadPool(int poolsize) {
            for (int i = 0; i < poolsize; i++) {
                WorkThread thread = new WorkThread(this);
                thread.setName("Worker :: " + (i + 1));
                thread.start();

                idle.add(thread);
            }
        }

        WorkThread getWorker() {
            WorkThread worker = null;
            synchronized (idle) {
                if (idle.size() > 0) {
                    worker = idle.remove(0);
                }
            }
            return worker;
        }

        void returnWorker(WorkThread worker) {
            synchronized (idle) {
                idle.add(worker);
            }
        }


    }


    /**
     * 工作线程：
     */
    private class WorkThread extends Thread {
        private ByteBuffer buffer = ByteBuffer.allocate(1024);
        private ThreadPool pool;
        private SelectionKey key;

        WorkThread(ThreadPool pool) {
            this.pool = pool;
        }

        public synchronized void run() {
            System.out.println(this.getName() + " is ready");
            while (true) {
                try {
                    // Sleep and release object lock
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //中断线程
                    this.interrupt();
                }

                if (key == null) {
                    continue;
                }

                System.out.println(this.getName() + " has been awakened");
                try {


                } catch (Exception e) {

                }

            }
        }

        /**
         * 用来服务的通道
         *
         * @param key
         */
        synchronized void serviceChannel(SelectionKey key) {
            this.key = key;
            key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
            //唤醒这个线程
            this.notify();
        }

        /**
         * 排干通道
         *
         * @param key
         * @throws Exception
         */
        void drainChannel(SelectionKey key) throws Exception {
            SocketChannel channel = (SocketChannel) key.channel();
            int count;
            buffer.clear();
            while ((count = channel.read(buffer)) > 0) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                buffer.clear();
            }

            if (count < 0) {
                channel.close();
                return;
            }

            key.interestOps(key.interestOps() | SelectionKey.OP_READ);

            key.selector().wakeup();

        }

    }


}


