package nio.buffer;


import nio.kit.PrintKit;

import java.nio.CharBuffer;

public class CopyBuffer {

    static CharBuffer buffer = CharBuffer.allocate(20);

    public static void main(String[] args) {
        buffer.put('H').put('E').put('L').put('L').put('O').put(',').put('W').put('O').put('R').put('L').put('D');
        buffer.position(2).limit(8);
        //复制后的缓存区的position等值均与母体一致
        CharBuffer bufferCopy1 = buffer.duplicate();
        System.out.println("两者的position是否相等 :: " + (buffer.position() == bufferCopy1.position()));
        System.out.println("两者的limit是否相等 :: " + (buffer.limit() == bufferCopy1.limit()));
        System.out.println("");

        //可以独立做各种游标操作不互相影响
        buffer.position(3).limit(9);
        PrintKit.printBuffer(buffer);
        bufferCopy1.position(1).limit(7);
        PrintKit.printBuffer(bufferCopy1);

        System.out.println("");

        //两者共享一份数据元素
        buffer.put('@');
        System.out.println(bufferCopy1.get(3));

        System.out.println("");

        //分割方式复制,复制剩余元素,不共享数据元素
        CharBuffer bufferCopy2 = buffer.slice();
        System.out.println("capacity :: " + bufferCopy2.capacity());
        System.out.println("limit :: " + bufferCopy2.limit());
        System.out.println("position :: " + bufferCopy2.position());
    }

}
