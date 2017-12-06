package xyz.downgoon.mydk.process.underlying;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.downgoon.mydk.process.PumperListener;

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
    public void onReadBegin(String pumperName) {
        LOG.info("{} BEGIN ...", pumperName);
    }

    @Override
    public void onReadLine(String pumperName, String lineText, int lineNumber) {
        LOG.info("{} READ LINE#{} : {}", pumperName, lineNumber, lineText);
    }

    @Override
    public void onReadEnd(String pumperName, int totalLines) {
        LOG.info("{} END LINE {}", pumperName, totalLines);
    }

    @Override
    public void onReadException(String pumperName, IOException ioe) {
        LOG.warn("{} IOException: ", pumperName, ioe);
    }

}
