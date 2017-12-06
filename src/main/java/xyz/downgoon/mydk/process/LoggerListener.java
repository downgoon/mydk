package xyz.downgoon.mydk.process;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @title StreamGobblerLogger
 * @description TODO 
 * @author liwei39
 * @date 2014-7-3
 * @version 1.0
 */
public class LoggerListener implements PumperListener {

    private static final Logger LOG = LoggerFactory.getLogger(LoggerListener.class);

    @Override
    public void onReadBegin(String gobblerName) {
        LOG.info("{} BEGIN ...", gobblerName);
    }

    @Override
    public void onReadLine(String gobblerName, String lineText, int lineNumber) {
        LOG.info("{} READ LINE#{} : {}", gobblerName, lineNumber, lineText);
    }

    @Override
    public void onReadEnd(String gobblerName, int totalLines) {
        LOG.info("{} END LINE {}", gobblerName, totalLines);
    }

    @Override
    public void onReadException(String gobblerName, IOException ioe) {
        LOG.warn("{} IOException: ", gobblerName, ioe);
    }

}
