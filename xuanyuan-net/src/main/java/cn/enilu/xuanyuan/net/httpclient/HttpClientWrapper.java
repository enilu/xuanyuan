package cn.enilu.xuanyuan.net.httpclient;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.log.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class HttpClientWrapper {

	protected static Logger logger = LoggerFactory.getLogger(HttpClientWrapper.class);

	private HttpClient httpClient = new DefaultHttpClient();

	/** 请求的url */
	private String url;

	/** 请求的方式 */
	private HttpMethod method = HttpMethod.GET;

	/** 超时时间 */
	private int timeout = 30 * 1000;

	/** 重试次数 */
	private int retryTimes = 1;

	/** 当前重试次数 */
	private int currentRetryNo = 0;

	/**重试间隔时间*/
	private int sleepTime = 0;

	private Map<String, String> headers = new HashMap<String, String>();

	private Map<String, String> params = new HashMap<String, String>();

	private String requestBody;

	public static HttpClientWrapper create() {
		return new HttpClientWrapper();
	}

	public HttpClientWrapper setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
		return this;
	}

	public HttpClientWrapper setUrl(String url) {
		this.url = url;
		return this;
	}

	public HttpClientWrapper setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public HttpClientWrapper setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
		return this;
	}

	public HttpClientWrapper setMethod(HttpMethod httpMethod) {
		this.method = httpMethod;
		return this;
	}

	public HttpClientWrapper setMethod(String method) {
		this.method = HttpMethod.getMethod(method);
		return this;
	}

	public HttpClientWrapper addHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	public HttpClientWrapper addHeader(String key, String value) {
		this.headers.put(key, value);
		return this;
	}

	public HttpClientWrapper addParams(Map<String, String> params) {
		this.params = params;
		return this;
	}

	public HttpClientWrapper addParam(String key, String value) {
		this.params.put(key, value);
		return this;
	}

	public HttpClientWrapper setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
		return this;
	}

	public HttpClientWrapper setRequestBody(String requestBody) {
		this.requestBody = requestBody;
		return this;
	}

	public byte[] execute1() {
		boolean isSSL = url.startsWith("https://");

		currentRetryNo = 0;
		byte[] responseData = null;
		do {
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), timeout);
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), timeout);
			try {
				if (isSSL) {
					this.initClientForHttps(httpClient);
				}
				HttpUriRequest request = this.getHttpRequest();

				for (String key : headers.keySet()) {
					request.addHeader(key, headers.get(key));
				}

				HttpResponse response = httpClient.execute(request);
				responseData = EntityUtils.toByteArray(response.getEntity());
				break;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.error("catch exception request : " + url + ", retryNo:" + currentRetryNo);
				currentRetryNo++;
			}
		} while (currentRetryNo <= retryTimes);

		return responseData;
	}

	public String execute() {
		boolean isSSL = url.startsWith("https://");

		currentRetryNo = 0;
		String responseBody = null;
		do {

			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), timeout);
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), timeout);
			try {
				if (isSSL) {
					this.initClientForHttps(httpClient);
				}
				HttpUriRequest request = this.getHttpRequest();

				for (String key : headers.keySet()) {
					request.addHeader(key, headers.get(key));
				}

                logger.info("request url:" + url + ", params:" + params);
				HttpResponse httpResponse = httpClient.execute(request);
				System.out.println("Set-Cookie:" + httpResponse.getFirstHeader("Set-Cookie"));

				Header[] headers = httpResponse.getHeaders("Content-Encoding");
				boolean isGzip = false;
				for (Header header : headers) {
					String value = header.getValue();
					if (value.equals("gzip")) {
						isGzip = true;
					}
				}

				if (!isGzip){
					responseBody = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
				}else {
					InputStream is = httpResponse.getEntity().getContent();
					GZIPInputStream gzipIn = new GZIPInputStream(is);
					responseBody = IOUtils.toString(gzipIn, "utf-8");
				}

				String bodyPart = (requestBody != null && requestBody.length() > 200) ? requestBody.substring(0, 200) : requestBody;
				logger.info("request url:" + url + ", params:" + params + ", requestBody:" + bodyPart + " ......");
				break;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.error("catch exception request : " + url + ", retryNo:" + currentRetryNo);
				currentRetryNo++;

				//设置重试间隔
				if (sleepTime != 0) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		} while (currentRetryNo <= retryTimes);

		return responseBody;
	}

	public int getCurrentRetryNo() {
		return currentRetryNo;
	}

	@SuppressWarnings("deprecation")
	private HttpUriRequest getHttpRequest() throws UnsupportedEncodingException {
		HttpUriRequest request = null;
		String requestURL = url;
		if (HttpMethod.GET == method) {
			if (params != null && params.size() != 0) {
				requestURL += "?" + getQueryParameter(params);
			}
			logger.info(requestURL);
			request = new HttpGet(requestURL);
		} else if (HttpMethod.POST == method) {
			// post for json
			request = new HttpPost(url);

			if (params.size() > 0) {
				if (params.containsKey("selected_website")) {
					Map<String, Object> map = new HashMap<String, Object>();
					for (String key : params.keySet()) {
						if (key.equals("selected_website") && "[]".equals(params.get(key))) {
							map.put(key, new ArrayList<String>());
						} else {
							map.put(key, params.get(key));
						}
					}

					requestBody = Json.toJson(map, JsonFormat.tidy());
				} else {
					requestBody = Json.toJson(params, JsonFormat.tidy());
				}
			}
			StringEntity bodyParams = new StringEntity(requestBody, HTTP.UTF_8);
			bodyParams.setContentType("application/json");
			((HttpPost) request).setEntity(bodyParams);
			String bodyPart = (requestBody != null && requestBody.length() > 200) ? requestBody.substring(0, 200) : requestBody;
			logger.info("requestBody:" + bodyPart + " ......");
		} else if (HttpMethod.POST_TEXT == method) {
			// post for text
			request = new HttpPost(url);
			StringEntity bodyParams = this.postForNormal();
			logger.info(Json.toJson(bodyParams));
			((HttpPost) request).setEntity(bodyParams);
		} else if (HttpMethod.POST_FORM == method) {
			// post form
			request = new HttpPost(url);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				nvps.add(new BasicNameValuePair(key, Strings.sNull(params.get(key))));
			}
			((HttpPost) request).setEntity(new UrlEncodedFormEntity(nvps, "utf8"));
		} else {
			throw new RuntimeException("没有指定http请求类型,eg:post,get");
		}
		return request;
	}

	@SuppressWarnings("deprecation")
	private StringEntity postForNormal() throws UnsupportedEncodingException {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		StringBuilder args = new StringBuilder();
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			args.append(key).append("=").append(params.get(key)).append("&");
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}

		logger.info("request params:" + args.toString());
		return new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
	}

	/** https协议的初始化 */
	@SuppressWarnings("deprecation")
	private void initClientForHttps(HttpClient httpClient) throws NoSuchAlgorithmException, KeyManagementException {
		String ssl = "SSL";
		if (url.contains("tongdun")) {
			ssl = "TLSv1.2";
		}
		SSLContext ctx = SSLContext.getInstance(ssl);
		X509TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}

			public void verify(String arg0, SSLSocket arg1) throws IOException {
			}

			public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {
			}

			public void verify(String arg0, X509Certificate arg1) throws SSLException {
			}
		};

		ctx.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		ssf.setHostnameVerifier(hostnameVerifier);
		ClientConnectionManager ccm = httpClient.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", 443, ssf));
	}

	public static enum HttpMethod {
		POST("post"), GET("get"), POST_TEXT("post_text"), POST_FORM("post_form");

		private String value;

		private HttpMethod(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public static HttpMethod getMethod(String value) {
			if (POST.getValue().equalsIgnoreCase(value)) {
				return POST;
			}

			if (GET.getValue().equalsIgnoreCase(value)) {
				return GET;
			}

			if (POST_TEXT.getValue().equalsIgnoreCase(value)) {
				return POST_TEXT;
			}

			if (POST_FORM.getValue().equalsIgnoreCase(value)) {
				return POST_FORM;
			}

			throw new RuntimeException("error input method value is wrong!");
		}
	}
	public static String getQueryParameter(Map<String, String> params) {
		StringBuilder queryBuilder = new StringBuilder();
		for (String key : params.keySet()) {
			if (queryBuilder.length() > 0) {
				queryBuilder.append("&");
			}
			queryBuilder.append(key).append("=").append(Strings.sNull(params.get(key)).replaceAll(" ", "%20"));
		}
		return queryBuilder.toString();
	}
}
