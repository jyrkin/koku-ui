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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

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
//	private static final String LOCATION_HEADER = "Location";

	/**
	 * Key for content length header.
	 */
//	private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";
	/**
	 * Key for host header
	 */
//	private static final String HOST_HEADER_NAME = "Host";


	static {
		if (Properties.INTALIO_HOST == null
				|| Properties.INTALIO_PORT == null
				|| Properties.INTALIO_ATTACHMENTS_PATH == null) {
			LOG.error("Attachment proxy is not configured properly! Please check that properties 'intalio.host', 'intalio.port' and 'intalio.attachments.path' are correctly defined in koku-settings.properties file.");
		}
	}

	private final String path;
	private final UserInfo userInfo;

	public AttachmentProxy(String path, UserInfo userInfo) {
		this.userInfo = userInfo;
		this.path = path;

		LOG.info("Intalio host: '"+Properties.INTALIO_HOST+"' Port: '"+Properties.INTALIO_PORT + "' Attachments path: '"+ Properties.INTALIO_ATTACHMENTS_PATH + "'");
	}

	/**
	 * Writes file to given outputStream
	 *
	 * @param outputStream
	 * @throws IOException
	 * @throws KokuCommonException
	 */
	public void getFile(OutputStream outputStream) throws KokuCommonException {

		if (path == null || userInfo == null) {
			throw new KokuCommonException("Path or UserInfo missing. Path: '" + path + "'. UserInfo: '"+userInfo.toString()+"'");
		}

		final String urlToConnect = getProxyURL(path);
		final GetMethod getMethodProxyRequest = new GetMethod(urlToConnect);
		try {
			executeProxyRequest(getMethodProxyRequest, outputStream);
			LOG.debug("User:  '" + userInfo.getPic() +"' accessed to '"+urlToConnect+"'.");
		} catch (IOException ioe) {
			throw new KokuCommonException("Coulnd't connect to host: '"+urlToConnect+"'. Intalio or connection to server is down", ioe);
		}
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
	private void executeProxyRequest(HttpMethod proxyRequest, OutputStream out) throws IOException, KokuCommonException {

		// Create a default HttpClient
		final HttpClient httpClient = new HttpClient();
		proxyRequest.setFollowRedirects(false);
		// Execute the request
		final int proxyResponseCode = httpClient.executeMethod(proxyRequest);

		// Check if the proxy response is a redirect
		// The following code is adapted from
		// org.tigris.noodle.filters.CheckForRedirect
		// Hooray for open source software
		if (proxyResponseCode >= HttpServletResponse.SC_MULTIPLE_CHOICES /* 300 */
				&& proxyResponseCode < HttpServletResponse.SC_NOT_MODIFIED /* 304 */) {
			throw new KokuCommonException("Server returned responseCode: '" + proxyResponseCode + "'");

//			String stringStatusCode = Integer.toString(intProxyResponseCode);
//			String stringLocation = proxyRequest.getResponseHeader(
//					LOCATION_HEADER).getValue();
//			if (stringLocation == null) {
//				throw new KokuCommonException("Recieved status code: "
//						+ stringStatusCode + " but no "
//						+ LOCATION_HEADER
//						+ " header was found in the response");
//			}
//			// Modify the redirect to go to this proxy servlet rather that the
//			// proxied host
//			String stringMyHostName = request.getServerName();
//			if (request.getServerPort() != 80) {
//				stringMyHostName += ":" + request.getServerPort();
//			}
//			stringMyHostName += request.getContextPath();
//			response.sendRedirect(stringLocation.replace(
//					getProxyHostAndPort() + this.getProxyPath(),
//					stringMyHostName));
//			return;
		} else if (proxyResponseCode == HttpServletResponse.SC_NOT_MODIFIED) {
			// 304 needs special handling. See:
			// http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
			// We get a 304 whenever passed an 'If-Modified-Since'
			// header and the data on disk has not changed; server
			// responds w/ a 304 saying I'm not going to send the
			// body because the file has not changed.
			throw new KokuCommonException("Server returned responseCode: '" + proxyResponseCode + "'");

//			response.setIntHeader(CONTENT_LENGTH_HEADER_NAME, 0);
//			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
//			return;
		}

		// Pass the response code back to the client
//		response.setStatus(proxyResponseCode);

		// Pass response headers back to the client
//		Header[] headerArrayResponse = proxyRequest.getResponseHeaders();
//		for (Header header : headerArrayResponse) {
//			// Our proxy doesn't support chunked transfer so we just filter this out
//			if (header.getValue().equals("chunked")) {
//				LOG.warn("Chuncked file");
//				response.setHeader(header.getName(), "");
//			} else {
//				response.setHeader(header.getName(), header.getValue());
//			}
//
//		}

		// Send the content to the client
		final InputStream inputStreamProxyResponse = proxyRequest.getResponseBodyAsStream();
		final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStreamProxyResponse);
		int intNextByte;
		while ((intNextByte = bufferedInputStream.read()) != -1) {
			out.write(intNextByte);
		}
	}

	/**
	 * Retreives all of the headers from the servlet request and sets them on
	 * the proxy request
	 *
	 * @param httpServletRequest
	 *            The request object representing the client's request to the
	 *            servlet engine
	 * @param httpMethodProxyRequest
	 *            The request that we are about to send to the proxy host
	 */
//	@SuppressWarnings("unchecked")
//	private void setProxyRequestHeaders(HttpServletRequest httpServletRequest,
//			HttpMethod httpMethodProxyRequest) {
//		// Get an Enumeration of all of the header names sent by the client
//		Enumeration enumerationOfHeaderNames = httpServletRequest.getHeaderNames();
//		while (enumerationOfHeaderNames.hasMoreElements()) {
//			String stringHeaderName = (String) enumerationOfHeaderNames.nextElement();
//			if (stringHeaderName.equalsIgnoreCase(CONTENT_LENGTH_HEADER_NAME))
//				continue;
//			// As per the Java Servlet API 2.5 documentation:
//			// Some headers, such as Accept-Language can be sent by clients
//			// as several headers each with a different value rather than
//			// sending the header as a comma separated list.
//			// Thus, we get an Enumeration of the header values sent by the
//			// client
//			Enumeration enumerationOfHeaderValues = httpServletRequest.getHeaders(stringHeaderName);
//			while (enumerationOfHeaderValues.hasMoreElements()) {
//				String stringHeaderValue = (String) enumerationOfHeaderValues.nextElement();
//				// In case the proxy host is running multiple virtual servers,
//				// rewrite the Host header to ensure that we get content from
//				// the correct virtual server
//				if (stringHeaderName.equalsIgnoreCase(HOST_HEADER_NAME)) {
//					stringHeaderValue = getProxyHostAndPort();
//				}
//				Header header = new Header(stringHeaderName, stringHeaderValue);
//				// Set the same header on the proxy request
//				httpMethodProxyRequest.setRequestHeader(header);
//			}
//		}
//	}

	// Accessors
	private String getProxyURL(String path)  {
		// Set the protocol to HTTP
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

}