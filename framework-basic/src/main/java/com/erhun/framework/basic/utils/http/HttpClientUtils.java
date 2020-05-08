package com.erhun.framework.basic.utils.http;

import com.erhun.framework.basic.utils.string.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.ssl.SSLSocketFactoryImpl;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author gorilla
 *
 */
public class HttpClientUtils {
	
	private static Logger log = LoggerFactory.getLogger(HttpClientUtils.class);
	
	/**
	 * 连接超时时间，由bean factory设置，缺省为8秒钟 
	 */
	private static int defaultConnectionTimeout = 60000;

	/** 
	 * 回应超时时间, 由bean factory设置，缺省为30秒钟 
	 */

	private static int defaultSoTimeout = 60000;
    
    private static HttpClientUtils httpProtocolHandler = new HttpClientUtils();
    
    private static SSLConnectionSocketFactory sslSocketFactory;

	private static PoolingHttpClientConnectionManager poolingConnectionManager;
    
    static{
		try{
		    SSLSocketFactory s = new SSLSocketFactoryImpl();
			sslSocketFactory = new SSLConnectionSocketFactory(s, new DefaultHostnameVerifier());
			poolingConnectionManager = new PoolingHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslSocketFactory).build());
		}catch (Exception e) {
			log.error("init poolingConnectionManager failed.", e);
		}
    }


    public static HttpClientUtils getInstance() {
        return httpProtocolHandler;
    }
   
    private HttpClientUtils(){
    }

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String doGet(String url) {
		return doGet(url, null);
	}
	
	/**
	 * 
	 * @param url
	 * @param headers
	 * @return
	 */
	public static String doGet(String url, Map <String, String> headers) {
		
		long startTime = System.currentTimeMillis();

		CloseableHttpClient client = null;
		HttpGet httpGet = null;
		CloseableHttpResponse response = null;
		
		try {
			String charset = getCharset(headers, "utf-8");
			client = createHttpClient();
			httpGet = createHttpMethod(new HttpGet(url), headers);
			return execute(client, httpGet, headers, url, charset);
		} catch (Exception e) {
			log.warn(url + " request error: " + e.getMessage(), e);
		} finally {
			log.info( "execute " + url + " used time: " + (System.currentTimeMillis() - startTime) + "ms");
			if(response != null){
	            EntityUtils.consumeQuietly(response.getEntity());
			}
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}

		return null;
		
	}

	/**
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static String doPost(String url, String param) {
		return doPost(url, param, (Map<String, String>) null);
	}
	
	/**
	 * 
	 * @param url
	 * @param params
	 * @param contentType
	 * @return
	 */
	public static String doPost(String url, String params, String contentType) {
		return doPost(url, params, contentType, null) ;
	}
	
	/**
	 * 
	 * @param url
	 * @param params
	 * @param requestProperties
	 * @return
	 */
	public static String doPost(String url, String params, Map <String, String> requestProperties) {
		return doPost(url, params, "application/x-www-form-urlencoded", requestProperties) ;
	}
	
	/**
	 * 
	 * @param url
	 * @param params
	 * @param requestProperties
	 * @return
	 */
	public static String doPostByStream(String url, String params, Map <String, String> requestProperties) {
		return doPost(url, params, "*/*", requestProperties) ;
	}
	
	/**
	 * 
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 */
	public static String doPost(String url, String params, String contentType, Map <String, String> headers) {
		
		long startTime = System.currentTimeMillis();
		
		CloseableHttpClient client = null;
		HttpPost httpPost = null;
		
		try {
			String charset = getCharset(headers, "utf-8");
			client = createHttpClient();
			httpPost = createHttpMethod(new HttpPost(url), headers);
			if(StringUtils.isNotEmpty(params)){
				httpPost.setEntity(new StringEntity(params, ContentType.create(contentType, charset)));
			}
			return execute(client, httpPost, headers, url, charset);
		} catch (Exception e) {
			log.warn(url + " request error: " + e.getMessage(), e);
		} finally {
			log.info( "execute " + url + " used time: " + (System.currentTimeMillis() - startTime) + "ms");
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
		
		return null;
	}

	/**
	 *
	 * @param url
	 * @param headers
	 * @return
	 */
	public static String doPut(String url, Map <String, String> headers) {

		long startTime = System.currentTimeMillis();

		CloseableHttpClient client = null;
		HttpPut httpPut = null;
		CloseableHttpResponse response = null;

		try {
			String charset = getCharset(headers, "utf-8");
			client = createHttpClient();
			httpPut = createHttpMethod(new HttpPut(url), headers);
			return execute(client, httpPut, headers, url, charset);
		} catch (Exception e) {
			log.warn(url + " request error: " + e.getMessage(), e);
		} finally {
			log.info( "execute " + url + " used time: " + (System.currentTimeMillis() - startTime) + "ms");
			if(response != null){
				EntityUtils.consumeQuietly(response.getEntity());
			}
			if (httpPut != null) {
				httpPut.releaseConnection();
			}
		}

		return null;

	}

	/**
	 *
	 * @param url
	 * @param headers
	 * @return
	 */
	public static String doDelete(String url, Map <String, String> headers) {

		long startTime = System.currentTimeMillis();

		CloseableHttpClient client = null;
		HttpDelete httpDelete = null;
		CloseableHttpResponse response = null;

		try {

			String charset = getCharset(headers, "utf-8");
			client = createHttpClient();
			httpDelete = createHttpMethod(new HttpDelete(url), headers);
			return execute(client, httpDelete, headers, url, charset);

		} catch (Exception e) {
			log.warn(url + " request error: " + e.getMessage(), e);
		} finally {
			log.info( "execute " + url + " used time: " + (System.currentTimeMillis() - startTime) + "ms");
			if(response != null){
				EntityUtils.consumeQuietly(response.getEntity());
			}
			if (httpDelete != null) {
				httpDelete.releaseConnection();
			}
		}

		return null;

	}

	private static String execute(CloseableHttpClient client, HttpRequestBase httpMethod, Map <String, String> headers, String url, String charset) throws IOException, ClientProtocolException {
		
		CloseableHttpResponse response = null;
		
		boolean returnHttpStatus = getParameter(headers, "Http-Status", false);
		boolean acceptErrorResult = getParameter(headers, "Read-Error", false);
	
		response = client.execute(httpMethod);
		
		int statusCode = response.getStatusLine().getStatusCode();
		String responseText = EntityUtils.toString(response.getEntity(), charset);
		
		if (statusCode == HttpStatus.SC_OK || acceptErrorResult) {
			return responseText;
		}else{
			log.warn(url + " request failed: " + responseText);
			if(returnHttpStatus){
				if(StringUtils.isNotEmpty(responseText) && responseText.length() > 256){
					responseText = responseText.substring(0, 256);
				}
				return "httpStatus=" + statusCode + "&httpMessage=" + StringUtils.defaultString(responseText);
			}
		}
			
		return null;
	}

	/**
	 *
	 * @param requestUrl
	 * @param params
	 * @param headers
	 * @return
	 */
	public static String doPostByMimePart(String requestUrl, Map <String, String> params, Map <String, String> headers) {

		long startTime = System.currentTimeMillis();

		HttpPost httppost = null;
		CloseableHttpResponse response = null;

		try {

			CloseableHttpClient httpclient = HttpClients.createDefault();

			String partContentType = getParameter(headers, "Part-Content-Type", "application/xml");
			int connectTimeout = getParameter(headers, "Connection-Timeout", defaultConnectionTimeout);
			String charset = getCharset(headers, "UTF-8");

			httppost = new HttpPost(requestUrl);
			httppost.setConfig(RequestConfig.custom().setConnectionRequestTimeout(connectTimeout).setConnectTimeout(connectTimeout).setSocketTimeout(connectTimeout).build());

			if(headers != null && headers.size() > 0){
				for(Map.Entry <String, String> entry : headers.entrySet()){
					httppost.addHeader(entry.getKey(), entry.getValue());
				}
			}

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

			if(params != null && params.size() > 0){
				for(Map.Entry <String, String> entry : params.entrySet()){
					entityBuilder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.create(partContentType, charset)));
				}
			}

			httppost.setEntity(entityBuilder.build());

			response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(response.getEntity(), charset);
			}else{
				log.warn(requestUrl + " request failed: " + EntityUtils.toString(response.getEntity(), charset));
			}

		} catch (Exception e) {
			log.warn(requestUrl + " request failed: " + e.getMessage(), e);
		} finally {
			log.info( "execute " + requestUrl + " used time: " + (System.currentTimeMillis() - startTime) + "ms");
			if(httppost != null){
				httppost.releaseConnection();
			}
			if(response != null){
				EntityUtils.consumeQuietly(response.getEntity());
			}
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T getParameter(Map<String, String> headers, String propertyName, T defaultValue) {
		
		if(headers == null || headers.size() == 0){
			return (T) defaultValue;
		}
		
		String value = headers.get(propertyName);
		
		if(StringUtils.isNotBlank(value)){
			Class<T> clazz = (Class<T>) defaultValue.getClass();
			if(clazz == Integer.class){
				return (T) Integer.valueOf(value);
			}
			if(clazz == Boolean.class){
				return (T) Boolean.valueOf("true".equals(value));
			}
			return (T) value;
		}
		
		return defaultValue;
		
	}

	private static String getCharset(Map<String, String> headers, String defaultValue) {
		if(headers == null || headers.size() == 0){
			return defaultValue;
		}
		String ct = headers.get("Content-Type");
		if(StringUtils.isNotEmpty(ct)){
			int a = ct.lastIndexOf("charset");
			if(a > -1){
				return ct.substring(a+8);
			}
		}
		return defaultValue;
	}

	private static CloseableHttpClient createHttpClient() {
		return HttpClients.custom().setConnectionManager(poolingConnectionManager).build();
	}
	
	public HttpClient getHttpClient(){
	    CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionTimeToLive(defaultConnectionTimeout, TimeUnit.SECONDS).build();
		return httpClient;
	}
	
	private static <T extends HttpRequestBase> T createHttpMethod(T method, Map<String,String> headers){
		
		int connectTimeout = getParameter(headers, "Connection-Timeout", defaultConnectionTimeout);
		method.setConfig(RequestConfig.custom().setConnectionRequestTimeout(connectTimeout).setConnectTimeout(connectTimeout).setSocketTimeout(connectTimeout).build());
		
		if(headers == null || headers.size() == 0){
			return method;
		}
		
		Set<Entry<String, String>> entrySet = headers.entrySet();
		Iterator<Entry<String, String>> iterator = entrySet.iterator();
		
		while(iterator.hasNext()){
			Entry<String, String> next = iterator.next();
			String key = next.getKey();
			if("Http-Status".equals(key) || "Read-Error".equals(key) || "Connection-Timeout".equals(key)){
				continue;
			}
			String value = next.getValue();
			method.addHeader(key,value);
		}
		
		return (T) method;
	}
	
	private static class HttpsX509TrustManager implements X509TrustManager{
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
	
}
