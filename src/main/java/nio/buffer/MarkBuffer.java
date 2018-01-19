package nio.buffer;


import java.nio.CharBuffer;

/**
 * mark/reset的使用
 */
public class MarkBuffer {

    static CharBuffer buffer = CharBuffer.allocate(20);

    public static void main(String[] args) {

        buffer.put('H').put('E').put('L').put('L').put('O').put(',').put('W').put('O').put('R').put('L').put('D');
        //将会把执行mark()时的position赋值给mark
        buffer.position(2).mark().position(9);
        System.out.println(">>=======reset()前=======<<");
        System.out.println("position :: " + buffer.position());
        //将会把mark的值赋值给position
        buffer.reset();
        System.out.println(">>=======reset()后=======<<");
        System.out.println("position :: " + buffer.position());

    }


}
