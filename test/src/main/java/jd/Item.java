package jd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangkuai on 2017/4/10.
 */
public class Item {

    private String url;         // url
    private String number;      // 编号
    private String image;       // 主图
    private String name;        // 名称
    private String brand;       // 品牌
    private double price;       // 价格
    private String description;  // 详情
    private int commentCount;   // 评论数
    private double goodRate;    // 好评率

    public static void main(String[] args) {
        Item jdBean = new Item();
//        jdBean.parseItemPage("https://item.jd.com/1057746.html");
        jdBean.parseItemPage("https://item.jd.com/3726844.html");
//        jdBean.parseItemPage("https://item.jd.com/547774.html");
//        jdBean.parseItemPage("https://item.jd.com/12086464.html");
    }

    public Item() {
    }

    /**
     * 商品页面分为两种
     * 1. <h1>商品名</h1>
     * 2. <div class="sku-name">商品名</div>
     *
     * @param urlStr 商品 URL
     */
    public void parseItemPage(String urlStr) {
        this.url = urlStr;

        String pageContent = HttpTool.getPageContentFromUrl(urlStr);

        Document doc = Jsoup.parse(pageContent);
        Elements itemNameType1 = doc.getElementsByTag("h1");
        Elements itemNameType2 = doc.select("div.sku-name");
        Elements itemDesLis;
        if (itemNameType1 != null && itemNameType1.size() > 0) {
            // 商品名
            this.name = itemNameType1.get(0).text();

            // 商品主图
            Elements itemImage = doc.select("img[data-img]");
            this.image = itemImage.get(0).attr("src");

            // 商品详情
            Element itemDesUl = doc.getElementById("parameter2");
            itemDesLis =  itemDesUl.select("li");
        } else {
            this.name = itemNameType2.get(0).text();

            // 商品主图
            Element itemImage = doc.getElementById("spec-img");
            this.image = itemImage.attr("data-origin");

            // 商品详情
            Elements itemDesUls = doc.select("ul.parameter2");
            itemDesLis =  itemDesUls.get(0).select("li");
        }
        JSONObject itemDesJson = new JSONObject();
        for (Element li : itemDesLis) {
            Elements liA = li.select("a");
            if (liA != null && liA.size() > 0) {
                itemDesJson.put(liA.get(0).text(), li.attr("title"));
            } else {
                String[] liKeyValue = li.text().split("：");
                itemDesJson.put(liKeyValue[0], liKeyValue[1]);
            }
        }
        this.description = itemDesJson.toJSONString();

        this.image = "https:" + this.image;

        // 商品品牌
        Element itemBrand = doc.getElementById("parameter-brand");
        this.brand = itemBrand.select("li a").get(0).text();

        System.out.println(name);
        System.out.println(image);
        System.out.println(brand);

        Pattern pattern = Pattern.compile("item\56jd\56com/(.+?)\56html");
        Matcher matcher = pattern.matcher(url);

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

    public void getPriceFromUrl(String urlStr) {
        String priceContent = HttpTool.getPageContentFromUrl(urlStr);
        JSONArray priceJsonArray = JSON.parseArray(priceContent);
        double price = priceJsonArray.getJSONObject(0).getDouble("p");
        this.price = price;
    }

    public void getCommentFromUrl(String urlStr) {
        String commentContent = HttpTool.getPageContentOfLineFromUrl(urlStr);
        JSONObject commentJson = JSON.parseObject(commentContent);
        JSONObject commentSummary = commentJson.getJSONObject("productCommentSummary");
        //总评价数
        int commentCount = commentSummary.getInteger("commentCount");
        //好评率
        double goodRate = commentSummary.getDouble("goodRate");
        this.commentCount = commentCount;
        this.goodRate = goodRate;
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

    public String getDescription() {
        return description;
    }

    public void setDetail(String description) {
        this.description = description;
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
