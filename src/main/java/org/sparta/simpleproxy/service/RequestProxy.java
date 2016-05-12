/* 
 * Sparta Software Co.
 * 2015
 */
package org.sparta.simpleproxy.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * Proxy the request.
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 10, 2016 - Daniel Conde Diehl
 *
 */
@Component
public class RequestProxy {

    private static final Logger LOG = LoggerFactory.getLogger(RequestProxy.class);
    
    /**
     * Forward a request to the destination, 
     * Currently only forward POST requests.
     * 
     * @param finalDestination Proxied destination
     * @param method Method from the request
     * @param req request object
     * @param resp response object
     * @param body Body message
     */
    public void forwardRequest(String finalDestination, String method, HttpServletRequest req, HttpServletResponse resp, String body) throws Exception {
        try {
            final URL url = new URL(finalDestination);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            
            final Enumeration<String> headers = req.getHeaderNames();
            while (headers.hasMoreElements()) {
                final String header = headers.nextElement();
                final Enumeration<String> values = req.getHeaders(header);
                while (values.hasMoreElements()) {
                    final String value = values.nextElement();
                    conn.addRequestProperty(header, value);
                }
            }

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            final byte[] buffer = new byte[16384];
            if (body != null) {
                conn.getOutputStream().write(body.getBytes());
            } else {
                while (true) {
                    final int read = req.getInputStream().read(buffer);
                    if (read <= 0) break;
                    conn.getOutputStream().write(buffer, 0, read);
                }
            }
            
            resp.setStatus(conn.getResponseCode());
            Map<String, List<String>> header = conn.getHeaderFields();
            for (String headerName : header.keySet()) {
                final String value = conn.getHeaderField(headerName);
                resp.setHeader(headerName, value);
            }

            while (true) {
                final int read = conn.getInputStream().read(buffer);
                if (read <= 0) break;
                resp.getOutputStream().write(buffer, 0, read);
            }
        } catch (Exception e) {
            LOG.error("Error proxying message", e);
            throw e;
        }
    }
}
