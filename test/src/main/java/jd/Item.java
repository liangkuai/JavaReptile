package jd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
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
    private String id;      // 编号
    private String image;       // 主图
    private String name;        // 名称
    private String brand;       // 品牌
    private double price;       // 价格
    private String description;  // 详情
    private int commentCount;   // 评论数
    private double goodRate;    // 好评率

    public static void main(String[] args) {
        Item.parseItemPage("https://item.jd.com/1057746.html");
//        Item.parseItemPage("https://item.jd.com/3726844.html");
//        Item.parseItemPage("https://item.jd.com/547774.html");
//        Item.parseItemPage("https://item.jd.com/12086464.html");
    }

    private Item() {
    }

    /**
     * 商品页面分为两种
     * 1. <h1>商品名</h1>
     * 2. <div class="sku-name">商品名</div>
     *
     * @param urlStr 商品 URL
     */
    public static Item parseItemPage(String urlStr) {
        Item item = new Item();
        item.setUrl(urlStr);

        String pageContent = HttpTool.getPageContentFromUrl(urlStr);
        if (pageContent == null || pageContent.isEmpty()) {
            return null;
        }

        Document doc = Jsoup.parse(pageContent);
        Elements itemNameType1 = doc.select("div.sku-name");
        Elements itemNameType2 = doc.getElementsByTag("h1");
        if (itemNameType1 != null && itemNameType1.size() > 0) {
            item.setName(itemNameType1.get(0).text());

            // 商品主图
            item.setImage(Item.getImageFromDocument1(doc));

            // 商品详情
            item.setDescription(Item.getDescFromDocument1(doc));
        } else {
            // 商品名
            item.setName(itemNameType2.get(0).text());

            // 商品主图
            item.setImage(Item.getImageFromDocument2(doc));

            // 商品详情
            item.setDescription(Item.getDescFromDocument2(doc));
        }

        // 设置商品品牌
        item.setBrand(Item.getBrandFromDocument(doc));

        // 设置商品价格和评价信息
        Pattern pattern = Pattern.compile("item\56jd\56com/(.+?)\56html");
        Matcher matcher = pattern.matcher(urlStr);
        if (matcher.find()) {
            String itemId = matcher.group(1);
            item.setId(itemId);

            //由商品编号查价格
            String getPriceUrl = "http://p.3.cn/prices/mgets?skuIds=J_" + itemId + ",J_&type=1";
            Double itemPrice = Item.getPriceFromUrl(getPriceUrl);
            if (itemPrice != null) {
                item.setPrice(itemPrice);
            } else {
                item.setPrice(0.0);
            }

            //由商品编号查评论
            String getCommentUrl = "http://club.jd.com/productpage/p-" + itemId + "-s-0-t-3-p-0.html";
            String comment = Item.getCommentFromUrl(getCommentUrl);
            if (comment != null && !comment.isEmpty()) {
                JSONObject commentJson = JSON.parseObject(comment);
                item.setGoodRate(commentJson.getDouble("goodRate"));
                item.setCommentCount(commentJson.getInteger("commentCount"));
            } else {
                item.setGoodRate(1.0);
                item.setCommentCount(0);
            }
        } else {
            item.setId("无");
            item.setPrice(0.0);
            item.setGoodRate(1.0);
            item.setCommentCount(0);
        }

        return item;
    }

    private static String getDescFromDocument1(Document doc) {
        Elements itemDescUls = doc.select("ul.parameter2");
        if (itemDescUls != null) {
            Elements itemDescLis = itemDescUls.get(0).select("li");
            JSONObject itemDescJson = new JSONObject();
            if (itemDescLis != null && itemDescLis.size() > 0) {
                for (Element li : itemDescLis) {
                    String[] liKeyValue = li.text().split("：");
                    itemDescJson.put(liKeyValue[0], liKeyValue[1]);
                }
                return itemDescJson.toJSONString();
            }
        }
        return "无";
    }

    private static String getDescFromDocument2(Document doc) {
        Element itemDescUl = doc.getElementById("parameter2");
        if (itemDescUl != null) {
            Elements itemDescLis = itemDescUl.select("li");
            JSONObject itemDescJson = new JSONObject();
            if (itemDescLis != null && itemDescLis.size() > 0) {
                for (Element li : itemDescLis) {
                    String[] liKeyValue = li.text().split("：");
                    itemDescJson.put(liKeyValue[0], liKeyValue[1]);
                }
                return itemDescJson.toJSONString();
            }
        }
        return "无";
    }

    private static String getImageFromDocument1(Document doc) {
        Element itemImageElement = doc.getElementById("spec-img");
        if (itemImageElement != null) {
            String image = itemImageElement.attr("data-origin");
            if (image != null && !image.isEmpty()) {
                return "https:" + image;
            }
        }
        return "无";
    }

    private static String getImageFromDocument2(Document doc) {
        Elements itemImageElement = doc.select("img[data-img]");
        if (itemImageElement != null && itemImageElement.size() > 0) {
            String image = itemImageElement.get(0).attr("src");
            if (image != null && !image.isEmpty()) {
                return "https:" + image;
            }
        }
        return "无";
    }

    /**
     * 从 Document 对象中获取商品品牌
     * @param doc Jsoup 创建 Document 对象
     * @return 品牌
     */
    private static String getBrandFromDocument(Document doc) {
        Element itemBrand = doc.getElementById("parameter-brand");
        if (itemBrand != null) {
            String brand = itemBrand.select("li a").get(0).text();
            if (brand != null && !brand.isEmpty()) {
                return brand;
            }
        }
        return "无";
    }

    /**
     * 从 URL 中获取价格
     * @param urlStr 商品 URL
     * @return 价格
     */
    private static Double getPriceFromUrl(String urlStr) {
        String priceContent = HttpTool.getPageContentFromUrl(urlStr);
        if (priceContent == null || priceContent.isEmpty()) {
            return null;
        }

        try {
            JSONArray priceJsonArray = JSON.parseArray(priceContent);
            return priceJsonArray.getJSONObject(0).getDouble("p");
        } catch (JSONException e) {
//            LOG.severe("JSON 解析错误");
            return null;
        }
    }

    /**
     * 从 URL 中获取评价信息
     * @param urlStr 商品 URL
     * @return 评价信息
     */
    private static String getCommentFromUrl(String urlStr) {
        String commentContent = HttpTool.getPageContentOfLineFromUrl(urlStr);
        if (commentContent == null || commentContent.isEmpty()) {
            return null;
        }

        try {
            JSONObject commentJson = JSON.parseObject(commentContent);
            JSONObject comment = commentJson.getJSONObject("productCommentSummary");
            return comment.toJSONString();
        } catch (JSONException e) {
//            LOG.severe("JSON 解析错误");
            return null;
        }
    }

    @Override
    public String toString() {
        JSONObject itemJson = new JSONObject();
        itemJson.put("url", this.url);
        itemJson.put("id", this.id);
        itemJson.put("name", this.name);
        itemJson.put("image", this.image);
        itemJson.put("brand", this.brand);
        itemJson.put("price", this.price);
        itemJson.put("description", this.description);
        itemJson.put("commentCount", this.commentCount);
        itemJson.put("goodRate", this.goodRate);
        return itemJson.toJSONString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setDescription(String description) {
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
