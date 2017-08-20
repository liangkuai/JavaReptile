package test;

/**
 * Created by liangkuai on 2017/8/20.
 */
public class MessageUnit {

    public String message;

    public MessageUnit(String msg) {
        message = msg;
    }

    @Override
    public String toString() {
        return message;
    }
}
