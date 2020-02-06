package com.garyodernichts.jpaexec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class ByteHelper {
    public static String toBitString(final byte[] b) {
        final char[] bits = new char[8 * b.length];
        for(int i = 0; i < b.length; i++) {
            final byte byteval = b[i];
            int bytei = i << 3;
            int mask = 0x1;
            for(int j = 7; j >= 0; j--) {
                final int bitval = byteval & mask;
                if(bitval == 0) {
                    bits[bytei + j] = '0';
                } else {
                    bits[bytei + j] = '1';
                }
                mask <<= 1;
            }
        }
        return String.valueOf(bits);
    }

    public static byte[] makeWORD(short val) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putShort(val);
        return bb.array();
    }

    public static byte[] makeDWORD(int val) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(val);
        return bb.array();
    }

    public static int readDWORD(byte[] val) {
        return ByteBuffer.wrap(val).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
}
