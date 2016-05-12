/* 
 * Sparta Software Co.
 * 2015
 */
package org.sparta.simpleproxy.controller;

import static org.sparta.simpleproxy.constant.SimpleProxyConstants.ADMIN_PREFIX;
import static org.sparta.simpleproxy.constant.SimpleProxyConstants.FAILURE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparta.simpleproxy.service.JSimpleProxyService;
import org.sparta.simpleproxy.service.RequestProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * EndPoint for Core API Mock web services.
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 10, 2016 - Daniel Conde Diehl
 *
 */
@RestController
@Order(2)
public class SimpleProxyController {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleProxyController.class);
       
    @Autowired
    private RequestProxy proxy;
    
    @Autowired
    private JSimpleProxyService jsProxyService;

    
	/**
	 * Mock for method of payment notification
	 * 
	 * @param request Request Parameters
	 * @return AddPaymentMethodResponse Response	
	 */
    @RequestMapping(value = { "{path:(?!"+ADMIN_PREFIX +").*$}", "{path:(?!"+ADMIN_PREFIX +").*$}/**"})
	public void proxy(@RequestBody(required=false) String body, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String destination = jsProxyService.findDestinationUrl(request.getRequestURI());
        final String method = request.getMethod();
        if (destination != null) {
            proxy.forwardRequest(destination, method, request, response, body);
        } else {
            LOG.error("Proxy not found.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, FAILURE);
        }
	}
}
