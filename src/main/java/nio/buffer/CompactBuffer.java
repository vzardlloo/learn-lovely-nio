package nio.buffer;


import java.nio.CharBuffer;

/**
 * 缓冲区的压缩操作
 */
public class CompactBuffer {


    static CharBuffer buffer = CharBuffer.allocate(20);

    public static void main(String[] args) {
        buffer.put('H').put('E').put('L').put('L').put('O').put(',').put('W').put('O').put('R').put('L').put('D');
        //翻转，准备释放buffer
        buffer.flip();
        buffer.position(3);
        System.out.println(">>=======压缩前========<<");
        System.out.println("position :: " + buffer.position());
        System.out.println("limit :: " + buffer.limit());
        //压缩,把position到limit之间的元素复制到buffer的开头，所以压缩后的buffer的position = limit(原) - position(原)
        //并且 limit 置为 capacity
        buffer.compact();
        System.out.println(">>=======压缩后========<<");
        System.out.println("position :: " + buffer.position());
        System.out.println("limit :: " + buffer.limit());
    }


}
