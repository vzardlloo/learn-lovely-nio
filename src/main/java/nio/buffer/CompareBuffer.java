package nio.buffer;


import java.nio.CharBuffer;

/**
 * 比较两个缓冲区
 */
public class CompareBuffer {

    static CharBuffer buffer1 = CharBuffer.allocate(20);
    static CharBuffer buffer2 = CharBuffer.allocate(15);

    public static void main(String[] args) {
        buffer1.put('H').put('E').put('L').put('L').put('O').put(',').put('W').put('O').put('R').put('L').put('D');
        buffer2.put("V").put('Z').put('A').put('R').put('D').put('L').put('L').put('O').put('O');

        buffer1.position(2);
        buffer1.limit(5);

        buffer2.position(5);
        buffer2.limit(8);

        /**
         * 判断两个缓冲区是否相等的充要条件是：
         * 1.对象类型相等
         * 2.剩余的元素和顺序相同(剩余元素为position到limit之间的元素,包含position不含limit)
         */
        System.out.println(buffer1.equals(buffer2));
        System.out.println(buffer1.compareTo(buffer2));
    }

}
