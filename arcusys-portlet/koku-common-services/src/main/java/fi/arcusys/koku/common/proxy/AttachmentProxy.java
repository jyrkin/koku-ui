/*
 * Copyright 2010 jedwards
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*
 * Released originally without license. Original author released it
 * under Apache License, Version 2.0 2010-06-05
 *
 * http://edwardstx.net/2010/06/http-proxy-servlet/
 *
 */

package fi.arcusys.koku.common.proxy;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.util.Properties;
import fi.koku.portlet.filter.userinfo.UserInfo;

/**
 * Intalio attachment proxy
 *
 * Proxy must be placed (for now) under message-portlet, because
 * JBoss Portal doesn't share session information with other
 * wars.
 *
 * @author Toni Turunen
 *
 */
public class AttachmentProxy {

	private static final Logger LOG = LoggerFactory.getLogger(AttachmentProxy.class);

	/**
	 * Serialization UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Key for redirect location header.
	 */

	static {
		if (Properties.INTALIO_HOST == null
				|| Properties.INTALIO_PORT == null
				|| Properties.INTALIO_ATTACHMENTS_PATH == null) {
			LOG.error("Attachment proxy is not configured properly! Please check that properties 'intalio.host', 'intalio.port' and 'intalio.attachments.path' are correctly defined in koku-settings.properties file.");
		}
	}

	private final String path;
	private final UserInfo userInfo;
	private final Map<String, String> headers = new HashMap<String, String>();
	private int responseCode = 0;
	private String contentType = null;

	public AttachmentProxy(String path, UserInfo userInfo) {
		this.userInfo = userInfo;
		this.path = path;
	}

	/**
	 * Writes file to given outputStream
	 *
	 * @param outputStream
	 * @throws IOException
	 * @throws KokuCommonException
	 */
	public byte[] getFile() throws KokuCommonException {

		if (path == null || userInfo == null) {
			throw new KokuCommonException("Path or UserInfo missing. Path: '" + path + "'. UserInfo: '"+((userInfo == null) ? "No userInfo!" : userInfo.toString())+"'");
		}

		final String urlToConnect = getProxyURL(path);
		final GetMethod getMethodProxyRequest = new GetMethod(urlToConnect);
		ByteArrayOutputStream fileContent = null;
		try {
			fileContent = executeProxyRequest(getMethodProxyRequest);
			LOG.info("User:  '" + userInfo.getUid() +"' accessed to '"+urlToConnect+"'.");
		} catch (IOException ioe) {
			throw new KokuCommonException("Coulnd't connect to host: '"+urlToConnect+"'. Intalio or connection to server is down", ioe);
		}
		return fileContent.toByteArray();
	}

	/**
	 * Executes the {@link HttpMethod} passed in and sends the proxy response
	 * back to the client via the given {@link HttpServletResponse}
	 *
	 * @param proxyRequest
	 *            An object representing the proxy request to be made
	 * @param response
	 *            An object by which we can send the proxied response back to
	 *            the client
	 * @throws IOException
	 *             Can be thrown by the {@link HttpClient}.executeMethod
	 * @throws ServletException
	 *             Can be thrown to indicate that another error has occurred
	 */
	private ByteArrayOutputStream executeProxyRequest(HttpMethod proxyRequest) throws IOException, KokuCommonException {

		// Create a default HttpClient
		final HttpClient httpClient = new HttpClient();
		proxyRequest.setFollowRedirects(false);
		// Execute the request
		responseCode = httpClient.executeMethod(proxyRequest);

		if (responseCode >= HttpServletResponse.SC_MULTIPLE_CHOICES /* 300 */
				&& responseCode < HttpServletResponse.SC_NOT_MODIFIED /* 304 */) {
			throw new KokuCommonException("Server returned responseCode: '" + responseCode + "' Path: '" + proxyRequest.getURI().toString() + "'");
		} else if (responseCode == HttpServletResponse.SC_NOT_MODIFIED || responseCode == HttpServletResponse.SC_NOT_FOUND) {
			throw new KokuCommonException("Server returned responseCode: '" + responseCode + "' Path: '" + proxyRequest.getURI().toString() + "'");
		}

		// Pass response headers back to the client
		headers.clear();
		Header[] headerArrayResponse = proxyRequest.getResponseHeaders();
		for (Header header : headerArrayResponse) {
			// Our proxy doesn't support chunked transfer so we just filter this out
			headers.put(header.getName(), header.getValue());
			if (header.equals("Content-Type")) {
				contentType = (header.getValue() == null || header.getValue().isEmpty()) ? null : header.getValue();
			}
		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		// Send the content to the client
		final ByteArrayOutputStream fileContent = new ByteArrayOutputStream(4096);
		final BufferedInputStream bufferedInputStream = new BufferedInputStream(proxyRequest.getResponseBodyAsStream());
		int intNextByte;
		while ((intNextByte = bufferedInputStream.read()) != -1) {
			fileContent.write(intNextByte);
		}
		return fileContent;
	}

	// Accessors
	public String getProxyURL(String path)  {
		String proxyURL = "http://" + this.getProxyHostAndPort();
		// Check if we are proxying to a path other that the document root
		if (!getProxyPath().equalsIgnoreCase("")) {
			proxyURL += this.getProxyPath();
		}
		// Handle the path given to the servlet
		proxyURL += (path == null) ? "" : encodeWhiteSpaces(path);
//		// Handle the query string
//		if (httpServletRequest.getQueryString() != null) {
//			proxyURL += "?" + httpServletRequest.getQueryString();
//		}
		return proxyURL;
	}

	private String encodeWhiteSpaces(String path) {
		return path.replaceAll(" ", "%20");
	}

	private String getProxyHostAndPort() {
		if (getProxyPort().equals("80")) {
			return getProxyHost();
		} else {
			return getProxyHost() + ":" + getProxyPort();
		}
	}

	private String getProxyHost() {
		return Properties.INTALIO_HOST;
	}

	private String getProxyPort() {
		return Properties.INTALIO_PORT;
	}

	private String getProxyPath() {
		return Properties.INTALIO_ATTACHMENTS_PATH;
	}

	/**
	 * @return the headers
	 */
	public final Map<String, String> getHeaders() {
		return headers;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getContentType() {
		return contentType;
	}

	public String getFilename() {
		final int lastSlash = path.lastIndexOf("/");
		if (lastSlash == -1) {
			return null;
		} else {
			return path.substring(lastSlash+1, path.length());
		}

	}

}