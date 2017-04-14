package jd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    /**
     * 爬虫线程体
     *
     * 1. 从未爬取过的 url 队列中获取一个 url，
     * 如果 该url 是商品地址，即形如 "http(https)://item.jd.com/xxx.html",
     * 先爬取该 url 页面，进行商品信息解析。
     *
     * 2. 接着进一步爬取 url
     */
    @Override
    public void run() {
        while (true) {
            String urlStr = jdReptile.getUrl();
            if (urlStr != null) {
                Pattern itemPattern = Pattern.compile(
                        "(http|https)://item\56jd\56com/(.+?)\56html/?");
                Matcher itemMatcher = itemPattern.matcher(urlStr);
                if (itemMatcher.find()) {
                    System.out.println(urlStr);
                    new Item().parseItemPage(urlStr);
                }
                crawler(urlStr);
            } else {
                // TODO 无未爬取的 URL
            }
        }
    }

    /**
     * 进一步爬取 url
     *
     * @param urlStr url 字符串
     */
    public void crawler(String urlStr) {
        String pageContent = HttpTool.getPageContentFromUrl(urlStr);
        Document doc = Jsoup.parse(pageContent);
        Elements links = doc.select("a[href]");
        String moreUrlStr = null;
        for (Element link : links) {
            if ((moreUrlStr = link.attr("abs:href")) != null && !moreUrlStr.isEmpty()) {
                jdReptile.addUrl(moreUrlStr);
            }
        }
    }

    public void parseContent(String content, String urlStr) {
        if (content != null) {
            String aRegex = "<a(.*?)href=\"//(.*?)\56jd\56com/(.*?)\"(.*?)>(.*?)</a>";
            Pattern aPattern = Pattern.compile(aRegex);
            Matcher aMatcher = aPattern.matcher(content);
            while (aMatcher.find()) {
                String hrefRegex = "href=\"//(.*?).jd.com/(.*?)\"";
                Pattern hrefPattern = Pattern.compile(hrefRegex);
                Matcher hrefMatcher = hrefPattern.matcher(aMatcher.group());
                if (hrefMatcher.find()) {
                    String otherUrl = hrefMatcher.group().replaceAll("href=\"|http:|https:|\"", "");
                    otherUrl = "http:" + otherUrl;
                }
            }
        }
    }
}
