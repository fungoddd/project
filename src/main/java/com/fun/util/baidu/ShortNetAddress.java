package com.fun.util.baidu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ShortNetAddress {
    private static Logger log = LoggerFactory.getLogger(ShortNetAddress.class);
    //30s
    public static int TIMEOUT = 30 * 1000;
    public static String ENCODING = "UTF-8";


    /**
     * 根据传入的url,通过访问百度短视频接口,将其转为短url
     *
     * @param originURL
     * @return
     */
    public static String getShortURL(String originURL) {
        String tinyUrl = null;
        try {
            //指定百度短视频接口
            URL url = new URL("http://dwz.cn/create.php");
            //建立连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //设置连接参数,使用连接参数进行输出
            connection.setDoOutput(true);
            //使用连接进行输入
            connection.setDoInput(true);
            //不使用缓存
            connection.setUseCaches(false);
            //设置连接超时时间30s
            connection.setConnectTimeout(TIMEOUT);
            //请求模式POST
            connection.setRequestMethod("POST");
            //设置POST信息,为传入的原始URL
            String postData = URLEncoder.encode(originURL.toString(), "utf-8");
            //输出原始URL
            connection.getOutputStream().write(("url=" + postData).getBytes());
            //连接百度短视频接口
            connection.connect();
            //获取返回的字符串
            String responseStr = getResponseStr(connection);
            log.info("response string: " + responseStr);
            //在字符串里获取tinyurl,目标短链接
            tinyUrl = getValueByKey_JSON(responseStr, "tinyurl");
            log.info("tinyurl: " + tinyUrl);
            //关闭连接
            connection.disconnect();
        } catch (IOException e) {
            log.error("getshortURL error:" + e.toString());
        }
        return tinyUrl;
    }

    /**
     * 通过HttpConnection 获取返回的字符串
     *
     * @param connection
     * @return
     * @throws IOException
     */
    private static String getResponseStr(HttpURLConnection connection) throws IOException {
        StringBuffer result = new StringBuffer();
        //从连接中获取http状态码
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            //如果返回的状态码是OK,则取出连接的输入流
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, ENCODING));
            String inputLine = "";
            while ((inputLine = reader.readLine()) != null) {
                //将消息逐行读入到结果中
                result.append(inputLine);
            }
        }
        //转成String返回
        return String.valueOf(result);
    }

    /**
     * JSON 根据传入的key获取value
     *
     * @param replyText
     * @param key
     * @return
     */
    private static String getValueByKey_JSON(String replyText, String key) {
        ObjectMapper mapper = new ObjectMapper();
        //定义json节点
        JsonNode node;
        String tinyUrl = null;
        try {
            //把调用返回的消息转成json串
            node = mapper.readTree(replyText);
            //通过key从json对象中获取对应的value,以text形式赋给tinyUrl目标短链接
            tinyUrl = node.get(key).asText();
        } catch (JsonProcessingException e) {
            log.error("getValueByKey_JSON error:" + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("getValueByKey_JSON error:" + e.toString());
        }
        return tinyUrl;
    }

    /**
     * ‘ 百度短链接接口 无法处理不知名网站，会安全识别报错
     *
     * @param args
     */
    public static void main(String[] args) {
        getShortURL("https://open.weixin.qq.com/connect/oauth2/authorize?appid==wx09656c4475dbc253&redirect_uri=http://119.23.187.207/O2O/wechatLogin/loginCheck&role_type=1&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect");
    }
}
