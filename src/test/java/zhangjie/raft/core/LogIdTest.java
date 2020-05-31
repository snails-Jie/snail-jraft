package zhangjie.raft.core;

import org.junit.Test;
import zhangjie.raft.core.entity.LogId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @Author zhangjie
 * @Date 2020/5/31 11:17
 **/
public class LogIdTest {


    @Test
    public void testCompareTo() {
        LogId logId = new LogId();
        assertEquals(0, logId.getIndex());
        assertEquals(0, logId.getTerm());

        assertTrue(new LogId(1, 0).compareTo(logId) > 0);
        assertTrue(new LogId(0, 1).compareTo(logId) > 0);

        logId = new LogId(1, 2);
        assertTrue(new LogId(0, 1).compareTo(logId) < 0);
        assertTrue(new LogId(0, 2).compareTo(logId) < 0);
        assertTrue(new LogId(3, 1).compareTo(logId) < 0);
        assertTrue(new LogId(1, 2).compareTo(logId) == 0);
    }

    @Test
    public void testChecksum() {
        LogId logId = new LogId();
        logId.setIndex(1);
        logId.setTerm(2);
        long c = logId.checksum();
        assertTrue(c != 0);
        assertEquals(c, logId.checksum());
    }
}
