package nio.buffer;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class GetAndPutIntoBuffer {

    //创建一个字符缓冲区，容量为15
    static ByteBuffer buffer = ByteBuffer.allocate(15);

    //创建一个字符缓冲流
    static CharBuffer charBuffer = CharBuffer.allocate(100);
    //strings的游标
    static int index = 0;

    static String[] strings = {
            "A random string value",
            "The product of an infinite number of monkeys",
            "Hey hey we're the Monkees",
            "Opening act for the Monkees: Jimi Hendrix",
            "'Scuse me while I kiss this fly",
            "Help Me!  Help Me!",
    };

    public static void main(String[] args) {
        //put 5 个字符进入缓冲区
        //默认的put(),get()都是在position指向的位置进行
        buffer.put((byte) 'H').put((byte) 'E').put((byte) 'L').put((byte) 'L').put((byte) 'O');
        // capacity,limit,position,mark是buffer的基本属性,大部分操作要依赖于它们
        //容量
        System.out.println("capacity :: " + buffer.capacity());
        //上界
        System.out.println("limit :: " + buffer.limit());
        //位置
        System.out.println("position :: " + buffer.position());
        //标记，默认为undefined,标记后等于position的值
        buffer.mark();


        //把缓存区从读入状态改成写出状态,实际上就是把position重置为0,limit置为原本position的值
        buffer.flip();
        System.out.println(">>=========使用flip()后=========<<");
        System.out.println("limit :: " + buffer.limit());
        System.out.println("position :: " + buffer.position());


        System.out.println(">>=========读取buffer元素========<<");
        System.out.println("第 0 个字符 :: " + (char) buffer.get(0));
        System.out.println("第 1 个字符 :: " + (char) buffer.get(1));


        System.out.println("");
        //先填充
        while (fillBuffer(charBuffer)) {
            //翻转,转成读取状态
            charBuffer.flip();
            //读取缓冲区
            drainBuffer(charBuffer);
            //清空缓存区
            charBuffer.clear();
        }

    }


    /**
     * 填充缓冲区
     *
     * @param buffer
     * @return
     */
    static boolean fillBuffer(CharBuffer buffer) {
        //index大于strings.length 结束
        if (index >= strings.length) {
            return false;
        }
        String str = strings[index++];
        for (int i = 0; i < str.length(); i++) {
            buffer.put(str.charAt(i));
        }
//        System.out.println("position :: "+buffer.position());
//        System.out.println("limit :: "+buffer.limit());
        return true;
    }

    /**
     * 释放缓冲区
     *
     * @param buffer
     */
    static void drainBuffer(CharBuffer buffer) {
        //remaining 记录position到limit剩余的元素
        while (buffer.hasRemaining()) {
            System.out.print(buffer.get());
        }
        System.out.println("");
    }

}
