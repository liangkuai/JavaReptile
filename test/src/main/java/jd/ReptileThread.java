package jd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangkuai on 2017/4/5.
 */

public class ReptileThread implements Runnable {

    private JDReptile jdReptile;

    public ReptileThread(JDReptile jdReptile) {
        this.jdReptile = jdReptile;
    }

    @Override
    public void run() {
        while (true) {
            String url = jdReptile.getUrl();

            if (url != null) {
                crawler(url);
            } else {
                // TODO 无需要爬取的 URL
            }
        }
    }

    public void crawler(String sUrl) {
        URL url;
        InputStream in = null;
        try {
            url = new URL(sUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            in = url.openStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(in));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            parseContent(sb.toString(), sUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void parseContent(String context, String url) {
        Pattern itemPattern = Pattern.compile("http://item\56jd\56com/.*?\56html"); // TODO https
        Matcher itemMatcher = itemPattern.matcher(url);
        if (itemMatcher.find() && itemMatcher.group(1) != null) {
//            JDBean commodity = new JDBean(context, url);
        }

        String hrefRegex = "href=\"//.*?\"";
        Pattern hrefPattern = Pattern.compile(hrefRegex);
        Matcher hrefMatcher = hrefPattern.matcher(context);
        while (hrefMatcher.find()) {
            String otherUrl = hrefMatcher.group().replaceAll("href=\"|http:|https:|\"", "");
            otherUrl = "http:" + otherUrl;    // TODO https

            // 未被爬到的 URL
            if (!jdReptile.containUrl(otherUrl)) {
                jdReptile.addUrl(otherUrl);
            }
        }
    }
}
