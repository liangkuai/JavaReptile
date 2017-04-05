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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reptile of JD
 */
public class JDReptile {

    public static void main(String[] args) {
        JDReptile jdReptile = new JDReptile();
        jdReptile.parseHomePage("https://www.jd.com/");
    }

    public void parseHomePage(String sUrl) {
        URL url;
        InputStream in = null;
        try {
            url = new URL(sUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            in = url.openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\\n");
            }

            String linkRegex = "href.*?>";
            Pattern linkPattern = Pattern.compile(linkRegex);
            Matcher link = linkPattern.matcher(sb.toString());
            while (link.find()) {
                String typeLinkRegex = "href=\"//.*?\"|href=\"http://.*?\"";    // 首页链接
//                String typeLinkRegex = "href=\"//.*?.jd.com/\"|href=\"//channel.jd.com/.*?.html\"";
                Pattern typeLinkPattern = Pattern.compile(typeLinkRegex);
                Matcher typeLink = typeLinkPattern.matcher(link.group());
                while (typeLink.find()) {
                    String typeUrl = typeLink.group().replaceAll("href=\"|\"", "");
                    typeUrl = "http:" + typeUrl;
                    System.out.println(typeUrl);
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

}
