package xyz.downgoon.mydk.example;


import java.io.File;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.downgoon.mydk.process.ForkFuture;
import xyz.downgoon.mydk.process.ForkTimeoutException;
import xyz.downgoon.mydk.process.ProcessFork;

/**
 * @title ProcessForkDemo
 * @description TODO 
 * @author liwei39
 * @date 2014-7-3
 * @version 1.0
 */
public class ProcessForkDemo {
    /*
     *  用main测试的原因：ffmpeg依赖外部环境，JUnit会导致自动化部署时单元测试失败.
     * */
    private static final Logger LOG = LoggerFactory.getLogger(ProcessForkDemo.class);

    public static void main(String[] args) throws Exception {
        //        demoReadLine();
        //        demoTimeoutTermination();
        //        demoNormalTermination();
        //        demoAwaitMulti();
        //        demoThumb();
//        demoMeta();
        //        demoMetaJSON();
//        demoMeta("E:\\software\\ffmpeg","E:\\BCSDownload","0087ef8b19a9bfcad143980f9ab4521111c14d");
//        demoThumb("E:\\software\\ffmpeg","E:\\BCSDownload","0087ef8b19a9bfcad143980f9ab4521111c14d","thumb");
        demoTranscode("E:\\software\\ffmpeg\\bin","E:\\BCSDownload","0087ef8b19a9bfcad143980f9ab4521111c14d","bq");
    }

    public static void demoTranscode(String ffmpegHome, String videoDir, String videoFile, String transcodedFile) throws Exception {
        ProcessFork processFork = new ProcessFork(new File(videoDir), "ffmpeg");
        ForkFuture future = processFork.fork(String.format("%s/ffmpeg -i \"%s\" -s 624*352 -b 250k -vcodec libx264 -acodec aac -ab 64k -strict experimental  \"%s.mp4\"", ffmpegHome,videoFile, transcodedFile));
        
        Thread.yield();//延迟一段时间再读取hasStdout()和hasStderr()

        int exitValue = future.awaitTerminated(10, TimeUnit.MINUTES);
        LOG.info("exitValue: {}", exitValue);
        if (future.hasStdout()) {
            LOG.info("stdout: {}", future.readFullyStdout());
        } else {
            LOG.info("stdout: {}", "EMPTY");
        }
        if (future.hasStderr()) {
            LOG.info("stderr: {}", future.readFullyStderr());
        } else {
            LOG.info("stderr: {}", "EMPTY");
        }
    }
    
    public static void demoReadLine() throws Exception {
        ProcessFork processFork = new ProcessFork(new File("D:\\data\\sample\\ad"), "ffmpeg");
        ForkFuture future = processFork.fork(
                "ffmpeg -y -i \"ad.flv\" -vcodec h264 -s 624x352 -r 25 -acodec copy -f mp4 \"ad_fork_3.mp4\"", true);

        LOG.info("isTerminated: {}", future.isTerminated());
        LOG.info("hasTerminated: {}", future.hasTerminated());

        Thread.yield();//延迟一段时间再读取hasStdout()和hasStderr()

        if (future.hasStdout()) {
            String out = null;
            while ((out = future.readLineStdout()) != null) {
                LOG.info("stdout:  {}", out);
            }
        }

        if (future.hasStderr()) {
            String err = null;
            while ((err = future.readLineStderr()) != null) {
                LOG.info("stderr:  {}", err);
            }
        }

    }

    public static void demoTimeoutTermination() throws Exception {
        ProcessFork processFork = new ProcessFork(new File("D:\\data\\sample\\ad"), "ffmpeg");
        ForkFuture future = processFork
                .fork("ffmpeg -y -i \"ad.flv\" -vcodec h264 -s 624x352 -r 25 -acodec copy -f mp4 \"ad_fork_4.mp4\"");

        int exitValue = future.awaitTerminated(5, TimeUnit.SECONDS);
        LOG.info("exitValue: {}", exitValue);
    }

    /** 转码 */
    public static void demoNormalTermination() throws Exception {
        ProcessFork processFork = new ProcessFork(new File("D:\\data\\sample\\ad"), "ffmpeg");
        ForkFuture future = processFork
                .fork("ffmpeg -y -i \"ad.flv\" -vcodec h264 -s 624x352 -r 25 -acodec copy -f mp4 \"ad_fork_4.mp4\"");

        Thread.yield();//延迟一段时间再读取hasStdout()和hasStderr()

        int exitValue = future.awaitTerminated(10, TimeUnit.MINUTES);
        LOG.info("exitValue: {}", exitValue);
        if (future.hasStdout()) {
            LOG.info("stdout: {}", future.readFullyStdout());
        } else {
            LOG.info("stdout: {}", "EMPTY");
        }
        if (future.hasStderr()) {
            LOG.info("stderr: {}", future.readFullyStderr());
        } else {
            LOG.info("stderr: {}", "EMPTY");
        }
    }

    public static void demoAwaitMulti() throws Exception {
        ProcessFork processFork = new ProcessFork(new File("D:\\data\\sample\\ad"), "ffmpeg");
        final ForkFuture future = processFork
                .fork("ffmpeg -y -i \"ad.flv\" -vcodec h264 -s 624x352 -r 25 -acodec copy -f mp4 \"ad_fork_4.mp4\"");

        Thread another = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    future.awaitTerminated(10, TimeUnit.MINUTES);
                } catch (ForkTimeoutException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        another.start();

        int exitValue = future.awaitTerminated(10, TimeUnit.MINUTES);
        LOG.info("exitValue: {}", exitValue);
        if (future.hasStdout()) {
            LOG.info("stdout: {}", future.readFullyStdout());
        } else {
            LOG.info("stdout: {}", "EMPTY");
        }
        if (future.hasStderr()) {
            LOG.info("stderr: {}", future.readFullyStderr());
        } else {
            LOG.info("stderr: {}", "EMPTY");
        }
    }

    /** 缩略图 */
    public static void demoThumb() throws Exception {
        ProcessFork processFork = new ProcessFork(new File("D:\\data\\sample\\ad"), "ffmpeg");
        ForkFuture future = processFork.fork("ffmpeg  -ss 00:00:02  -i ad_BQ.mp4 -vframes 1 -y fork_thumb_1.jpeg");

        int exitValue = future.awaitTerminated(10, TimeUnit.MINUTES);
        LOG.info("exitValue: {}", exitValue);
        if (future.hasStdout()) {
            LOG.info("stdout: {}", future.readFullyStdout());
        } else {
            LOG.info("stdout: {}", "EMPTY");
        }
        if (future.hasStderr()) {
            LOG.info("stderr: {}", future.readFullyStderr());
        } else {
            LOG.info("stderr: {}", "EMPTY");
        }
    }
    
    public static void demoThumb(String ffmpegHome, String videoDir, String videoFile, String thumbFile) throws Exception {
        ProcessFork processFork = new ProcessFork(new File(videoDir), "ffmpeg");
        ForkFuture future = processFork.fork(ffmpegHome+"/bin/"+"ffmpeg  -ss 00:00:02  -i "+videoFile+" -vframes 1 -y "+thumbFile+".jpeg");

        int exitValue = future.awaitTerminated(10, TimeUnit.MINUTES);
        LOG.info("exitValue: {}", exitValue);
        if (future.hasStdout()) {
            LOG.info("stdout: {}", future.readFullyStdout());
        } else {
            LOG.info("stdout: {}", "EMPTY");
        }
        if (future.hasStderr()) {
            LOG.info("stderr: {}", future.readFullyStderr());
        } else {
            LOG.info("stderr: {}", "EMPTY");
        }
    }

    public static void demoMeta(String ffmpegHome, String videoDir, String videoFile) throws Exception {//-loglevel error
        ProcessFork processFork = new ProcessFork(new File(videoDir), "ffmpeg");
        //"ffmpeg -loglevel error -i ad_BQ.mp4"
        ForkFuture future = processFork.fork(ffmpegHome+"/bin/"+"ffmpeg -i "+videoFile);

        int exitValue = future.awaitTerminated(10, TimeUnit.MINUTES);
        LOG.info("exitValue: {}", exitValue);
        if (future.hasStdout()) {
            LOG.info("stdout: {}", future.readFullyStdout());
        } else {
            LOG.info("stdout: {}", "EMPTY");
        }
        if (future.hasStderr()) {
            LOG.info("stderr: {}", future.readFullyStderr());
        } else {
            LOG.info("stderr: {}", "EMPTY");
        }
    }
    
    /** MetaData */
    public static void demoMeta() throws Exception {//-loglevel error
        ProcessFork processFork = new ProcessFork(new File("D:\\data\\sample\\ad"), "ffmpeg");
        //"ffmpeg -loglevel error -i ad_BQ.mp4"
        ForkFuture future = processFork.fork("ffmpeg -i ad_BQ.mp4");

        int exitValue = future.awaitTerminated(10, TimeUnit.MINUTES);
        LOG.info("exitValue: {}", exitValue);
        if (future.hasStdout()) {
            LOG.info("stdout: {}", future.readFullyStdout());
        } else {
            LOG.info("stdout: {}", "EMPTY");
        }
        if (future.hasStderr()) {
            LOG.info("stderr: {}", future.readFullyStderr());
        } else {
            LOG.info("stderr: {}", "EMPTY");
        }
    }

    /** MetaData JSON Format */
    public static void demoMetaJSON() throws Exception {
        ProcessFork processFork = new ProcessFork(new File("D:\\data\\sample\\ad"), "ffmpeg");
        //"ffprobe -print_format json -show_format -show_streams ad_BQ.mp4"
        ForkFuture future = processFork
                .fork("ffprobe -loglevel error -print_format json -show_format -show_streams ad_BQ.mp4");

        int exitValue = future.awaitTerminated(10, TimeUnit.MINUTES);
        LOG.info("exitValue: {}", exitValue);
        if (future.hasStdout()) {
            LOG.info("stdout: {}", future.readFullyStdout());
        } else {
            LOG.info("stdout: {}", "EMPTY");
        }
        if (future.hasStderr()) {
            LOG.info("stderr: {}", future.readFullyStderr());
        } else {
            LOG.info("stderr: {}", "EMPTY");
        }
    }
}
