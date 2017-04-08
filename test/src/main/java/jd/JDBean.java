package jd;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangkuai on 2017/4/8.
 */

public class JDBean {

    private String url;         // url
    private String number;      // 编号
    private String image;       // 主图
    private String name;        // 名称
    private String brand;       // 品牌
    private double price;       // 价格
    private String detail;      // 详情
    private int commentCount;   // 评论数
    private double goodRate;    // 好评率

    public static void main(String[] args) {
        JDBean jdBean = new JDBean();
        jdBean.parseContext("", "https://item.jd.com/3133817.html#none");
    }

    public JDBean() {

    }

    public void parseContext(String context, String url) {
        this.url = url;
        Pattern pattern;
        Matcher matcher;

        pattern = Pattern.compile("item\56jd\56com/(.*?)\56html");
        matcher = pattern.matcher(url);

        if (matcher.find()) {
            this.number = matcher.group(1);

            //由商品编号查价格
            String getPriceUrl = "http://p.3.cn/prices/mgets?skuIds=J_" + number + ",J_&type=1";
            getPriceFromUrl(getPriceUrl);

            //由商品编号查评论
            String getCommentUrl = "http://club.jd.com/productpage/p-" + number + "-s-0-t-3-p-0.html";
            getCommentFromUrl(getCommentUrl);
        }
    }

    public void getPriceFromUrl(String url) {
        CloseableHttpClient priceClient = HttpClients.createDefault();
        HttpGet get = new HttpGet();
        try {
            get.setURI(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void getCommentFromUrl(String url) {

    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public double getGoodRate() {
        return goodRate;
    }

    public void setGoodRate(double goodRate) {
        this.goodRate = goodRate;
    }
}
