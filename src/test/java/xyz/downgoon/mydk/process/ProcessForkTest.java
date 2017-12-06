package xyz.downgoon.mydk.process;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.downgoon.mydk.util.OsUtils;

public class ProcessForkTest {

	private static final Logger LOG = LoggerFactory.getLogger(ProcessForkTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCat() throws Exception {

		if (OsUtils.isLinux() || OsUtils.isMac()) {
			new Cmd("ls").onSucc(m -> {
				LOG.info("ls output: {}", m);
				Assert.assertTrue(m.indexOf("README.md") != -1);
			}).exec();
		}

	}

}
