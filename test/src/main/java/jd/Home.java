package jd;

/**
 * Created by liangkuai on 2017/4/10.
 */

public class Home {

    public static void main(String[] args) {
        JDReptile jdReptile = new JDReptile();
        jdReptile.parseHomePage("https://www.jd.com/");
        jdReptile.begin();
    }
}
