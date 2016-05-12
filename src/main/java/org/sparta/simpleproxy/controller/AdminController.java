/* 
 * Sparta Software Co.
 * 2015
 */
package org.sparta.simpleproxy.controller;

import javax.validation.Valid;

import org.sparta.simpleproxy.constant.SimpleProxyConstants;
import org.sparta.simpleproxy.entity.Proxy;
import org.sparta.simpleproxy.repository.ProxyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * Admin UI controller.
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 10, 2016 - Daniel Conde Diehl
 *
 */
@Controller
@RequestMapping(SimpleProxyConstants.ADMIN_URL)
@Order(1)
public class AdminController {
    
    @Autowired
    private ProxyRepository proxyRepo;
    
    @RequestMapping(path={"/", "", "/index"})
    public ModelAndView index() {
        return new ModelAndView("index");
    }
    
    @RequestMapping("/config")
    public ModelAndView config() {
        return new ModelAndView("config");
    }
    
    @RequestMapping(value="/proxies", method=RequestMethod.GET)
    public @ResponseBody Iterable<Proxy> listProxies() {
        return proxyRepo.findAll();
    }
    
    @RequestMapping(value="/proxies", method=RequestMethod.POST)
    public @ResponseBody void saveProxy(@Valid Proxy proxy) throws Exception {
        if (proxy.getId() == null && proxyRepo.findByOrigin(proxy.getOrigin()) != null) {
            throw new Exception("Proxy Origin already exists");
        }
        proxyRepo.save(proxy);
    }
    
    @RequestMapping(value="/proxies/{proxyId}", method=RequestMethod.DELETE)
    public @ResponseBody void removeProxy(@PathVariable Long proxyId) {
        proxyRepo.delete(proxyId);
    }
}
