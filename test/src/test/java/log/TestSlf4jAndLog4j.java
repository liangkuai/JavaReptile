package log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liangkuai on 2017/8/13.
 */
public class TestSlf4jAndLog4j {

    static private Logger logger = LoggerFactory.getLogger(TestSlf4jAndLog4j.class);

    public static void main(String[] args) {
        logger.info("Hello SLF4J");
    }
}
