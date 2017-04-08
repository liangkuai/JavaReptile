package jd;

/**
 * Created by liangkuai on 2017/4/5.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reptile of JD
 */
public class JDReptile {

    private Set<String> allUrl;
    private LinkedList<String> noCrawlerUrl;
    private ExecutorService threadPool;

    public static void main(String[] args) {
        JDReptile jdReptile = new JDReptile();
        jdReptile.parseHomePage("https://www.jd.com/");

        jdReptile.begin();
    }

    public JDReptile() {
        allUrl = new HashSet<>();
        noCrawlerUrl = new LinkedList<>();
        threadPool = Executors.newCachedThreadPool();
    }

    public void begin() {
        for (int i = 0; i < 10; i++) {
            threadPool.execute(new ReptileThread(this));
        }
    }

    /**
     * 解析首页
     * 缩小范围，尽量选出分类
     *
     * TODO 不做挑选，对所有链接进行爬取
     *
     * @param sUrl 根路径
     */
    public void parseHomePage(String sUrl) {
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
                sb.append("\\n");
            }

            String hrefRegex = "href=\".*?\"";   // 链接属性(152)
//            String hrefRegex = "href=\".*?\".*?>.*?<";   // 链接属性(152)
            Pattern hrefPattern = Pattern.compile(hrefRegex);
            Matcher hrefMatcher = hrefPattern.matcher(sb.toString());
            while (hrefMatcher.find()) {
                String typeRegex = "href=\"(http:)?//.*?\56jd\56com/?\"" +
                        "|href=\"//channel\56jd\56com/.*?.html\"";
                Pattern typePattern = Pattern.compile(typeRegex);
                Matcher typeMatcher = typePattern.matcher(hrefMatcher.group());
                while (typeMatcher.find()) {
                    String typeUrl = hrefMatcher.group().replaceAll("href=\"|http:|https:|\"", "");
                    typeUrl = "http:" + typeUrl;    // TODO https
                    System.out.println(typeUrl);

                    // 未被爬到的 URL
                    if (!containUrl(typeUrl)) {
                        addUrl(typeUrl);
                    }
                }
            }
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

    // TODO 同步?
    public boolean containUrl(String url) {
        return allUrl.contains(url);
    }

    /**
     * 同步
     *
     * TODO 考虑使用并发集合
     *
     * @param url 添加 URL 即未被爬到的 URL
     */
    public synchronized void addUrl(String url){
        allUrl.add(url);
        noCrawlerUrl.add(url);
    }

    public synchronized String getUrl() {
        if (noCrawlerUrl.isEmpty()) {
            return noCrawlerUrl.poll();
        } else
            return null;
    }

}
