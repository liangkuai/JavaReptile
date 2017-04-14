package jd;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by liangkuai on 2017/4/13.
 */
public class HttpToolTest {
    @Test
    public void getPageContentFromUrl() throws Exception {
        String content = HttpTool.getPageContentFromUrl("xxx");
        System.out.println(content);
    }

    @Ignore
    public void getPageContentOfLineFromUrl() throws Exception {
    }

}