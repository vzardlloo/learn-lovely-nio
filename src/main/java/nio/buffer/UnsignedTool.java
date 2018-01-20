package nio.buffer;

import java.nio.ByteBuffer;

/**
 * 存取无符号数据
 */
public class UnsignedTool {

    static ByteBuffer buffer = ByteBuffer.allocate(10);

    //---------------------------------------------------------------
    //存取short
    public static short getUnsignedShort(ByteBuffer buffer) {
        return ((short) (buffer.get() & 0xff));
    }

    public static void putUnsignedShort(ByteBuffer buffer, int value) {
        buffer.put((byte) (value & 0xff));
    }

    public static short getunsignedShort(ByteBuffer buffer, int position) {
        return ((short) (buffer.get(position) & (short) 0xff));
    }

    public static void putUnsignedShort(ByteBuffer bb, int position, int value) {
        bb.put(position, (byte) (value & 0xff));
    }

    //----------------------------------------------------------------------------

    //存取int
    public static int getUnsignedInt(ByteBuffer bb) {
        return (bb.getShort() & 0xffff);
    }

    public static void putUnsignedInt(ByteBuffer bb, int value) {
        bb.putShort((short) (value & 0xffff));
    }


    public static int getUnsignedInt(ByteBuffer bb, int position) {
        return (bb.getShort(position) & 0xffff);
    }

    public static void putUnsignedInt(ByteBuffer bb, int position, int value) {
        bb.putShort(position, (short) (value & 0xffff));
    }

    //--------------------------------------------------------------------------

    //存取long
    public static long getUnsignedLong(ByteBuffer bb) {
        return ((long) bb.getInt() & 0xffffffffL);
    }

    public static void putUnsignedLong(ByteBuffer bb, long value) {
        bb.putInt((int) (value & 0xffffffffL));
    }

    public static long getUnsignedLong(ByteBuffer bb, int position) {
        return ((long) bb.getInt(position) & 0xffffffffL);
    }

    public static void putUnsignedLong(ByteBuffer bb, int position, long value) {
        bb.putInt(position, (int) (value & 0xffffffffL));
    }

    public static void main(String[] args) {

        System.out.println(">>======存取short========<<");
        UnsignedTool.putUnsignedShort(buffer, 8);
        buffer.flip();
        System.out.println(UnsignedTool.getUnsignedShort(buffer));
        buffer.clear();

        System.out.println(">>===存取short,使用游标===<<");
        UnsignedTool.putUnsignedShort(buffer, 4, 5);
        System.out.println(UnsignedTool.getunsignedShort(buffer, 4));
        buffer.clear();

        System.out.println(">>======存取int==========<<");
        UnsignedTool.putUnsignedInt(buffer, 6);
        buffer.flip();
        System.out.println(UnsignedTool.getUnsignedInt(buffer));
        buffer.clear();

        System.out.println(">>===存取int,使用游标=====<<");
        UnsignedTool.putUnsignedInt(buffer, 4, 5);
        System.out.println(UnsignedTool.getUnsignedInt(buffer, 4));
        buffer.clear();

        System.out.println(">>======存取long=========<<");
        UnsignedTool.putUnsignedLong(buffer, 7324324L);
        buffer.flip();
        System.out.println(UnsignedTool.getUnsignedInt(buffer));
        buffer.clear();

        System.out.println(">>==存取long,使用游标====<<");
        UnsignedTool.putUnsignedLong(buffer, 5, 882342L);
        System.out.println(UnsignedTool.getUnsignedInt(buffer, 5));
        buffer.clear();


    }


}
