package xyz.downgoon.mydk.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AntPathMatcherTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMatch() throws URISyntaxException {
		AntPathMatcher matcher = new AntPathMatcher();

        String requestPath = new URI("http://example.com/user/list.jsp?location=bj").getPath();
        System.out.println("request path: " + requestPath);

        String patternPath = "/user/list.jsp";
        boolean isMatch = matcher.match(patternPath, requestPath);

        System.out.println("isMatch: " + isMatch);
        Assert.assertEquals(true, isMatch);
        
        
        Assert.assertEquals(true, matcher.match("/user/*", requestPath));
        Assert.assertEquals(true, matcher.match("/user/**", requestPath));
        Assert.assertEquals(false, matcher.match("**", requestPath));
        Assert.assertEquals(true, matcher.match("/**", requestPath));
        Assert.assertEquals(true, matcher.match("/**/list.jsp", requestPath));
        Assert.assertEquals(true, matcher.match("/**/lis?.jsp", requestPath));
	}

}
