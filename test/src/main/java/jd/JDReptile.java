package jd;

/**
 * Created by liangkuai on 2017/4/5.
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reptile of JD
 */
public class JDReptile {

    private Set<String> allUrl;
    private Queue<String> noCrawlerUrl;
    private ExecutorService threadPool;

    /**
     * TODO allUrl 考虑: TreeSet
     */
    public JDReptile() {
        this.allUrl = new HashSet<>();
        this.noCrawlerUrl = new LinkedBlockingQueue<>();
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void begin() {
        for (int i = 0; i < 10; i++) {
            threadPool.execute(new ReptileThread(this));
        }
    }

    /**
     * 解析首页
     *
     * @param urlStr 根路径
     */
    public void parseHomePage(String urlStr) {
        String homePageContent = HttpTool.getPageContentFromUrl(urlStr);
        if (homePageContent == null) {
            return;
        }

        Document doc = Jsoup.parse(homePageContent);
        Elements links = doc.select("a[href]");
        String moreUrlStr;
        for (Element link : links) {
            if ((moreUrlStr = link.attr("abs:href")) != null
                    && !moreUrlStr.isEmpty()) {
                this.addUrl(moreUrlStr);
            }
        }
    }

    // TODO 同步?
    public boolean containUrl(String url) {
        return allUrl.contains(url);
    }

    /**
     * 爬取到 URL
     * 1. 添加到爬取过的 URL 集合中
     * 2. 添加到未爬取过的 URL 队列中
     *
     * @param urlStr 添加 URL 即未被爬到的 URL
     */
    public void addUrl(String urlStr) {
        synchronized (this) {
            this.allUrl.add(urlStr);
        }

        // TODO LinkedBlockingQueue 未设置容量上限，暂未考虑阻塞队列满的情况
        if (!this.noCrawlerUrl.offer(urlStr)) {

            // fixme 阻塞队列满，插入元素失败处理
        }
    }

    /**
     * 从未爬取过的 URL 队列中获取队列头
     *
     * @return 未爬取过的 URL 队列的第一个 URL
     */
    public String getUrl() {
        return noCrawlerUrl.poll();
    }


    /**
     * 爬取首页，解析
     * 缩小范围，尽量选出分类
     *
     * @param urlStr 首页 URL
     */
    public void parseHomePageByJava(String urlStr) {
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

            String hrefRegex = "href=\"(.*?)\"";
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

}
