package xyz.downgoon.mydk.util;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtilsTest {

	private static final Logger LOG = LoggerFactory.getLogger(FileUtilsTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testClasspathFile() {
		File file1 = FileUtils.classpathFile("cp-file1.properties");
		// XXX/target/test-classes/cp-file1.properties
		LOG.info("cp-file1: {}", file1.getAbsolutePath());

		Assert.assertNotNull(file1);
		Assert.assertTrue(file1.exists());

		File file2 = FileUtils.classpathFile("cp-dir/cp-file2.txt");
		// XXX/target/test-classes/cp-dir/cp-file2.txt
		LOG.info("cp-file2: {}", file2.getAbsolutePath());

		Assert.assertNotNull(file2);
		Assert.assertTrue(file2.exists());

		File file3 = FileUtils.classpathFile("cp-404.dat");
		Assert.assertNull(file3);

	}

	@Test
	public void testHasClasspathFile() {
		Assert.assertTrue(FileUtils.hasClasspathFile("cp-file1.properties"));
		Assert.assertTrue(FileUtils.hasClasspathFile("cp-dir/cp-file2.txt"));
		Assert.assertFalse(FileUtils.hasClasspathFile("cp-404.dat"));
	}

	@Test
	public void testClasspathStream() {
		Assert.assertNotNull(FileUtils.classpathStream("cp-file1.properties"));
		Assert.assertNotNull(FileUtils.classpathStream("cp-dir/cp-file2.txt"));
		Assert.assertNull(FileUtils.classpathStream("cp-404.dat"));
	}

}
