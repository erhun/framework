package org.erhun.framework.basic.utils.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * weichao
 */
public class HttpUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtility.class);

    private static final int BUFFER_SIZE = 1024 * 10;

    private static final int TIMEOUT = 45 * 1000;

    private HttpUtility() {
    }

    /**
     *
     * @param url
     * @param contentType
     * @param params
     * @param encoded
     * @param timeout
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String contentType, String params, boolean encoded, Integer timeout) throws IOException {
        if (timeout == null) {
            timeout = 10000;
        }
        StringBuilder buf = new StringBuilder();
        InputStream is = null;
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        try {
            URL _url = new URL(url);
            conn = (HttpURLConnection) _url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setAllowUserInteraction(false);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestProperty("Content-Type", contentType);
            if (encoded) {
                conn.getOutputStream().write(URLEncoder.encode(params, "utf-8").getBytes());
            } else {
                conn.getOutputStream().write(params.getBytes("UTF-8"));
            }
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
            is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), BUFFER_SIZE);
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                buf.append(line);
            }
        } catch (Throwable t) {
            LOGGER.error("post exception, " + params, t);
        } finally {
            reader.close();
            is.close();
        }
        if (conn != null) {
            conn.disconnect();
        }
        return buf.toString();
    }

    /**
     *
     * @param url
     * @param params
     * @param timeout
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String params, boolean encoded, Integer timeout) throws IOException {
        return doPost(url, "application/x-www-form-urlencoded;charset=utf-8", params, encoded, timeout);
    }

    /**
     *
     * @param url
     * @param contentType
     * @param params
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String contentType, String params) throws IOException {
        return doPost(url, contentType, params, true, TIMEOUT);
    }

    /**
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String params) throws IOException {
        return doPost(url, params, true, TIMEOUT);
    }

    /**
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String params, int timeout) throws IOException {
        return doPost(url, params, true, timeout);
    }

    /**
     *
     * @param url
     * @param params
     * @param encoded
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String params, boolean encoded) throws IOException {
        return doPost(url, params, encoded, TIMEOUT);
    }

}
