package xyz.downgoon.mydk.util;

import junit.framework.Assert;

import org.junit.Test;

public class RetryTemplateTest {

    @Test
    public void testEcho() {
        UnreliableService us = new UnreliableService(1);
        RetryTemplate retryTemplate = new RetryTemplate(us,"US", 1,2,4);
        
        Exception exception = null;
        try {
            retryTemplate.retry("echo", "hello hill");
        } catch (Exception e) {
            exception = e;
        }
        Assert.assertNull(exception);
    }
    
    @Test
    public void testSilence() {
        UnreliableService us = new UnreliableService(1);
        RetryTemplate retryTemplate = new RetryTemplate(us,"US", 1,2,4);
        
        Exception exception = null;
        try {
            retryTemplate.retry("silence", "hello hill");
        } catch (Exception e) {
            exception = e;
        }
        Assert.assertNull(exception);
    }
    
    static class UnreliableService {
        private int unreliableCount = 1;

        public UnreliableService(int unreliableCount) {
            super();
            this.unreliableCount = unreliableCount;
        }
        
        private int echoCount = 0;
        
        public String echo(String arg) {
            echoCount ++;
            if (echoCount <= unreliableCount) {
                throw new IllegalStateException("unreliabe call "+echoCount);
            }
            return arg;
        } 
        
        private int silenceCount = 0;
        
        public void silence(String arg) {
            silenceCount ++;
            if (silenceCount <= unreliableCount) {
                throw new IllegalStateException("unreliabe call "+silenceCount);
            }
        } 
    }

}

