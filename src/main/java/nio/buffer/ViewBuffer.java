package nio.buffer;


import nio.kit.PrintKit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

/**
 * 缓冲区视图,缓冲区的类型转换,要注意字节顺序排列问题
 */
public class ViewBuffer {

    //大端字节顺序(默认)
    static ByteBuffer buffer = ByteBuffer.allocate(20).order(ByteOrder.BIG_ENDIAN);
    //小端字节顺序
    static ByteBuffer buffer1 = ByteBuffer.allocate(20).order(ByteOrder.LITTLE_ENDIAN);

    public static void main(String[] args) {
        /**
         * 以'H'为例，'H'二进制表示为 '0000 0000 0100 1000' 强转为byte后截取低8位'0100 1000',
         * 大端顺序存储的时候,将其存储为'0100 1000',前面的0强转后存储为 '0000 0000'
         * 在转成char视图的时候,两个byte凑成一个char,变成'0000 0000 0100 1000',这正是
         * 大端顺序存储时'H'的存储形式,故而 '0000 0000 0100 1000' => 'H'
         */
        buffer.put((byte) 0).put((byte) 'H').put((byte) 0).put((byte) 'E').put((byte) 0)
                .put((byte) 'L').put((byte) 0).put((byte) 'L').put((byte) 0).put((byte) 'O');

        /**
         * 以'H'为例，'H'二进制表示为 '1000 0100 0000 0000' 强转为byte后截取低8位'0100 1000',
         * 小端顺序存储的时候,将其存储为 '1000 0100',后面的强转后存储为 '0000 0000'
         * 在转成char视图的时候,两个byte凑成一个char,变成'1000 0100 0000 0000',这正是
         * 小端顺序存储时'H'的存储形式,故而 '1000 0100 0000 0000' => 'H'
         */
        buffer1.put((byte) 'H').put((byte) 0).put((byte) 'E').put((byte) 0).put((byte) 'L')
                .put((byte) 0).put((byte) 'L').put((byte) 0).put((byte) 'O').put((byte) 0);

        buffer.flip();
        buffer1.flip();


        CharBuffer charBuffer = buffer.asCharBuffer();
        CharBuffer charBuffer1 = buffer1.asCharBuffer();

        PrintKit.printBuffer(charBuffer);
        PrintKit.printBuffer(charBuffer1);

    }


}
