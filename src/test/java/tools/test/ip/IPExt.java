package tools.test.ip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class IPExt {

    public static void main(String[] args) {
        IPExt.load("D:/Downloads/ip/mydata4vipday2.datx");

        String[] arr = IPExt.find("1.100.130.193");
        System.out.println(Arrays.toString(arr));
        // System.out.println(Arrays.toString(IPExt.find("118.28.8.8")));
        // System.out.println(Arrays.toString(IPExt.find("255.255.255.255")));
    }

    public static int            prefixLength    = 256 * 256 * 4;      // 前262144个字符是作为前缀

    public static boolean        enableFileWatch = false;

    /** 整个数据的长度 */
    private static int           length;
    private static int[]         index           = new int[65536];
    private static ByteBuffer    dataBuffer;
    private static ByteBuffer    indexBuffer;
    private static Long          lastModifyTime  = 0L;
    private static File          ipFile;
    private static ReentrantLock lock            = new ReentrantLock();

    public static void load(String filename) {
        ipFile = new File(filename);
        load();
        if (enableFileWatch) {
            watch();
        }
    }

    public static void load(String filename, boolean strict) throws Exception {
        ipFile = new File(filename);
        if (strict) {
            int contentLength = Long.valueOf(ipFile.length()).intValue();
            if (contentLength < 512 * 1024) {
                throw new Exception("ip data file error.");
            }
        }
        load();
        if (enableFileWatch) {
            watch();
        }
    }

    public static String[] find(String ip) {
        String[] ips = ip.split("\\.");
        int prefixId = (Integer.valueOf(ips[0]) * 256 + Integer.valueOf(ips[1]));
        long ipLong = ip2long(ip);

        int key = index[prefixId];
        int startIdx = key * 9 + prefixLength;

        int max_comp_len = length - prefixLength - 4;// 有效的数据长度 减去前缀以及最前面四个字节表示文件数据量
        long tmpInt;
        long index_offset = -1;
        int index_length = -1;
        byte b = 0;
        for (; startIdx < max_comp_len; startIdx += 9) {
            int v = indexBuffer.getInt(startIdx);
            tmpInt = int2long(v);
            if (tmpInt >= ipLong) {
                index_offset = bytesToLong(b, indexBuffer.get(startIdx + 6), indexBuffer.get(startIdx + 5),
                                           indexBuffer.get(startIdx + 4));
                index_length = (0xFF & indexBuffer.get(startIdx + 7) << 8) + (0xFF & indexBuffer.get(startIdx + 8));
                break;
            }
        }

        byte[] areaBytes;

        lock.lock();
        try {
            dataBuffer.position(length + (int) index_offset - prefixLength);
            areaBytes = new byte[index_length];
            dataBuffer.get(areaBytes, 0, index_length);
        } finally {
            lock.unlock();
        }

        return new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1);
    }

    private static void watch() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                long time = ipFile.lastModified();
                if (time > lastModifyTime) {
                    load();
                }
            }
        }, 1000L, 5000L, TimeUnit.MILLISECONDS);
    }

    private static void load() {
        lastModifyTime = ipFile.lastModified();
        lock.lock();
        try {
            dataBuffer = ByteBuffer.wrap(getBytesByFile(ipFile));
            dataBuffer.position(0);
            length = dataBuffer.getInt(); // indexLength
            byte[] indexBytes = new byte[length];
            dataBuffer.get(indexBytes, 0, length - 4);
            indexBuffer = ByteBuffer.wrap(indexBytes);
            indexBuffer.order(ByteOrder.LITTLE_ENDIAN);

            for (int i = 0; i < 256; i++) {
                for (int j = 0; j < 256; j++) {
                    int v = indexBuffer.getInt();
                    index[i * 256 + j] = v;
                }
            }
            indexBuffer.order(ByteOrder.BIG_ENDIAN);
        } finally {
            lock.unlock();
        }
    }

    private static byte[] getBytesByFile(File file) {
        FileInputStream fin = null;
        byte[] bs = new byte[new Long(file.length()).intValue()];
        try {
            fin = new FileInputStream(file);
            int readBytesLength = 0;
            int i;
            while ((i = fin.available()) > 0) {
                fin.read(bs, readBytesLength, i);
                readBytesLength += i;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bs;
    }

    private static long bytesToLong(byte a, byte b, byte c, byte d) {
        return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
    }

    private static int str2Ip(String ip) {
        String[] ss = ip.split("\\.");
        int a, b, c, d;
        a = Integer.parseInt(ss[0]);
        b = Integer.parseInt(ss[1]);
        c = Integer.parseInt(ss[2]);
        d = Integer.parseInt(ss[3]);
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    private static long ip2long(String ip) {
        return int2long(str2Ip(ip));
    }

    private static long int2long(int i) {
        long l = i & 0x7fffffffL;
        if (i < 0) {
            l |= 0x080000000L;
        }
        return l;
    }
}
