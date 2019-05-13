package xyz.downgoon.mydk.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class OrderedHashTest {

    @Test
    public void testBeforeAfter() {
        OrderedHash<String, String> orderedHash = new NonThreadSafeOrderedHash<>();
        orderedHash.add("T1#S1", "Action#0");
        orderedHash.add("T2#S1", "Action#1");
        orderedHash.add("T1#S2", "Action#2").add("T2#S2", "Action#3");

        Assert.assertEquals(4, orderedHash.size());
        Assert.assertEquals("Action#0", orderedHash.getHead());
        Assert.assertEquals("Action#3", orderedHash.getTail());

        Assert.assertEquals(2, orderedHash.getIndex("T1#S2"));
        Assert.assertEquals("Action#2", orderedHash.getValue("T1#S2"));

        // normal case
        AtomicBoolean isHead = new AtomicBoolean();
        AtomicBoolean isTail = new AtomicBoolean();
        Assert.assertEquals("Action#1", orderedHash.getBefore("T1#S2", isHead));
        Assert.assertEquals("Action#3", orderedHash.getAfter("T1#S2", isTail));

        // not found case
        Assert.assertNull(orderedHash.getBefore("not-found", isHead));
        Assert.assertNull(orderedHash.getAfter("not-found", isTail));
        Assert.assertNull(orderedHash.getValue("not-found"));
        Assert.assertEquals(-1, orderedHash.getIndex("not-found"));
        Assert.assertEquals(-1, orderedHash.indexOf("not-found"));

        // boundary case
        Assert.assertNull(orderedHash.getBefore("T1#S1", isHead));
        Assert.assertTrue(isHead.get());
        Assert.assertNull(orderedHash.getAfter("T2#S2", isTail));
        Assert.assertTrue(isTail.get());

    }

    @Test
    public void testEmpltyCase() {
        OrderedHash<String, String> orderedHash = new NonThreadSafeOrderedHash<>();

        Assert.assertEquals(0, orderedHash.size());
        Assert.assertNull(orderedHash.getHead());
        Assert.assertNull(orderedHash.getTail());

        Assert.assertEquals(-1, orderedHash.getIndex("not-found"));
        Assert.assertNull(orderedHash.getValue("not-found"));

        AtomicBoolean isHead = new AtomicBoolean();
        AtomicBoolean isTail = new AtomicBoolean();
        Assert.assertNull(orderedHash.getBefore("not-found", isHead));
        Assert.assertNull(orderedHash.getAfter("not-found", isTail));

    }
}
