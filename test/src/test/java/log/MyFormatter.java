package log;

import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by liangkuai on 2017/4/13.
 */

/**
 * 格式化日志
 */
public class MyFormatter extends Formatter {

    /**
     * 格式化日志
     *
     * @param record 日志请求体
     * @return 格式化日志
     */
    @Override
    public String format(LogRecord record) {
        JSONObject logContentJson = new JSONObject(true);

        SimpleDateFormat data = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        logContentJson.put("class", record.getSourceClassName());
        logContentJson.put("method", record.getSourceMethodName());
        logContentJson.put("level", record.getLevel().getName());
        logContentJson.put("time", data.format(new Date(record.getMillis())));
        logContentJson.put("message", record.getMessage());

        return logContentJson.toJSONString() + "\n";
    }
}
