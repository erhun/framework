package com.erhun.framework.basic.utils.uuid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gorilla
 */
public class ObjectId implements java.io.Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectId.class);

    private static AtomicInteger nextInc = new AtomicInteger((new Random()).nextInt());

    private static final long serialVersionUID = 1L;

    private static final int genMachine;
    private final int time;
    private final int machine;
    private final int inc;
    private boolean newId;

    static {

        try {
            int machinePiece;
            try {
                StringBuilder sb = new StringBuilder();
                Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
                while (e.hasMoreElements()) {
                    NetworkInterface ni = e.nextElement();
                    sb.append(ni.toString());
                }
                machinePiece = sb.toString().hashCode() << 16;
            } catch (Throwable e) {
                LOGGER.warn(e.getMessage(), e);
                machinePiece = (new Random().nextInt()) << 16;
            }
            LOGGER.info("machine piece post: " + Integer.toHexString(machinePiece));
            final int processPiece;
            int processId = new Random().nextInt();
            try {
                processId = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
            } catch (Throwable t) {
            }

            ClassLoader loader = ObjectId.class.getClassLoader();
            int loaderId = loader != null ? System.identityHashCode(loader) : 0;

            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toHexString(processId));
            sb.append(Integer.toHexString(loaderId));
            processPiece = sb.toString().hashCode() & 0xFFFF;
            LOGGER.info("process piece: " + Integer.toHexString(processPiece));

            genMachine = machinePiece | processPiece;
            LOGGER.info("machine : " + Integer.toHexString(genMachine));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public ObjectId() {
        time = (int) (System.currentTimeMillis() / 1000);
        machine = genMachine;
        inc = nextInc.getAndIncrement();
        newId = true;
    }

    public static String id() {
        return get().toHexString();
    }

    public static ObjectId get() {
        return new ObjectId();
    }

    public String toHexString() {
        final StringBuilder buf = new StringBuilder(24);
        for (final byte b : toByteArray()) {
            buf.append(String.format("%02x", b & 0xff));
        }
        return buf.toString();
    }

    public byte[] toByteArray() {
        byte b[] = new byte[12];
        ByteBuffer bb = ByteBuffer.wrap(b);
        bb.putInt(time);
        bb.putInt(machine);
        bb.putInt(inc);
        return b;
    }

    private int compareUnsigned(int i, int j) {
        long li = 0xFFFFFFFFL;
        li = i & li;
        long lj = 0xFFFFFFFFL;
        lj = j & lj;
        long diff = li - lj;
        if (diff < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        if (diff > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) diff;
    }

    public int getTimestamp() {
        return time;
    }

    public Date getDate() {
        return new Date(time * 1000L);
    }

    public static int getCurrentCounter() {
        return nextInc.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjectId that = (ObjectId) o;
        return equal(serialVersionUID, ObjectId.serialVersionUID) &&
                equal(time, that.time) &&
                equal(machine, that.machine) &&
                equal(inc, that.inc) &&
                equal(newId, that.newId) &&
                equal(machine, that.machine);
    }

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{serialVersionUID, time, machine, inc, newId,
                nextInc, machine});
    }

}
