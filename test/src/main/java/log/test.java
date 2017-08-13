package log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liangkuai on 2017/8/13.
 */
public class test {

    static public Logger logger = LoggerFactory.getLogger(test.class);

    public static void main(String[] args) {
        logger.info("Hello SLF4J");
    }
}
