package log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by liangkuai on 2017/4/13.
 */

/**
 * 不使用配置文件，封装 Logger
 */
public class MyLogger {

    /**
     * 自定义 Logger
     * 为 Logger 添加 FileHandler
     *
     * @param name 类名
     * @return 自定义 Logger
     */
    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);

        // Set level
        logger.setLevel(Level.WARNING);

        // TODO Set filter

        /*
        Set handler

        1. 指定目录: /test_jd.log
        2. 单个日志文件容量: 10M
        3. 日志文件数量: 10
        4. 日志追加: true
         */
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("../test_jd.log", 10000000, 10, true);
            fileHandler.setEncoding("UTF-8");
            fileHandler.setLevel(Level.WARNING);
            fileHandler.setFormatter(new MyFormatter());
            logger.addHandler(fileHandler);
        } catch (UnsupportedEncodingException e) {
            System.out.println("[ERROR] 不支持字符集");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[ERROR] 打开文件失败");
            e.printStackTrace();
        }

        return logger;
    }
}
