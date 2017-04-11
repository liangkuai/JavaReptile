package jd;

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

/**
 * Created by liangkuai on 2017/4/10.
 */


public class HttpTool {

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
                    new InputStreamReader(inputStream, "gbk"));
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

    public static String getPageContentOfLineFromUrl(String urlStr) {
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
                    new InputStreamReader(inputStream, "gbk"));
            return bufferedReader.readLine();
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
}
