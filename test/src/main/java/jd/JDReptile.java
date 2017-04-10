package jd;

/**
 * Created by liangkuai on 2017/4/5.
 */

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
     * @param urlStr 根路径
     */
    public void parseHomePage(String urlStr) {
        InputStream inputStream = null;
        try {
            URL homeUrl = new URL(urlStr);
            URLConnection urlConnection = homeUrl.openConnection();
            urlConnection.addRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
            inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder homePageStr = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                homePageStr.append(line);
            }
//            System.out.println(homePageStr);

            String hrefRegex = "href=\"(.*?)\"";   // 链接属性(152)
//            String hrefRegex = "href=\".*?\".*?>.*?<";   // 链接属性(152)
            Pattern hrefPattern = Pattern.compile(hrefRegex);
            Matcher hrefMatcher = hrefPattern.matcher(homePageStr.toString());
            while (hrefMatcher.find()) {
                String typeRegex = "href=\"(http:)?//(.*?)\56jd\56com/?\"" +
                        "|href=\"//channel\56jd\56com/(.*?)\56html\"";
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
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getPageContentFromUrl(String urlStr) {
        HttpEntity entity = null;
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet request = new HttpGet(urlStr);
            request.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
            CloseableHttpResponse response = httpclient.execute(request);

            entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"));
            StringBuilder contentOfUrl = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                contentOfUrl.append(line);
            }
            return contentOfUrl.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                e.printStackTrace();
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
     * @param urlStr 添加 URL 即未被爬到的 URL
     */
    public synchronized void addUrl(String urlStr){
        allUrl.add(urlStr);
        noCrawlerUrl.add(urlStr);
    }

    public synchronized String getUrl() {
        if (!noCrawlerUrl.isEmpty()) {
            return noCrawlerUrl.poll();
        } else
            return null;
    }

}
