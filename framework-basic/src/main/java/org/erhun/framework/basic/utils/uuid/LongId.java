package org.erhun.framework.basic.utils.uuid;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gorilla
 */
public class LongId implements java.io.Serializable {


    private static AtomicInteger nextInc = new AtomicInteger((new Random()).nextInt());

    public static final Long id(){
        return System.currentTimeMillis() + nextInc.incrementAndGet();
    }
}
