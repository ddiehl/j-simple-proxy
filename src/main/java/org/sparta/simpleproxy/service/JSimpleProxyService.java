/* 
 * Sparta Software Co.
 * 2015
 */
package org.sparta.simpleproxy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparta.simpleproxy.entity.Proxy;
import org.sparta.simpleproxy.repository.ProxyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

/**
 *
 * Business service for JSimpleProxy.
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 10, 2016 - Daniel Conde Diehl
 *
 */
@Service
public class JSimpleProxyService {
    
    private static final Logger LOG = LoggerFactory.getLogger(JSimpleProxyService.class); 
    
    @Autowired
    private ProxyRepository proxyRepo;
    
    
    /**
     * Finds destination URL for the given URL looking at database.
     * 
     * @param url URL to match
     * @return destination if found, otherwise null
     */
    public String findDestinationUrl (String url) {
        LOG.info("Locating proxy for URL:{}", url);
        
        Iterable<Proxy> proxies = proxyRepo.findAll();
        for (Proxy proxy : proxies) {
            //Accepts wildcard
            String treatedUrl = proxy.getOrigin().replaceAll("\\*", ".\\*?");
            
            //Accepts variables
            List<String> paramNames = null;
            if (treatedUrl.contains("{")) {
                //Finds the parameters
                paramNames = new ArrayList<>();
                final Pattern pattern = Pattern.compile("\\{(.*?)\\}");
                final Matcher matcher  = pattern.matcher(treatedUrl);
                while(matcher.find()) {
                    LOG.trace("Parameter found=[{}]", matcher.group(1));
                    paramNames.add(matcher.group(1));
                }
                
                //Add replacement groups
                treatedUrl = treatedUrl.replaceAll("\\{.*?\\}", "(.\\*?)");                
            }
                
            LOG.debug("Testing proxy origin={}", treatedUrl);
            Pattern pattern = Pattern.compile(treatedUrl);
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                //Found proxy, to be used 
                String finalDestination = proxy.getDestination(); 
                
                //Replace all parameters with the value from URL
                for(int i=1; i<=matcher.groupCount(); i++) {
                    LOG.debug("Should replace=[{}] to=[{}]", "{"+paramNames.get(i-1) +"}", matcher.group(i));
                    finalDestination = StringUtils.replace(finalDestination, "{"+paramNames.get(i-1) +"}", matcher.group(i));
                }
                
                //return rsponse
                LOG.info("Proxy found to URL:{}, matches with={}, id={}, destinationURL={}", finalDestination, treatedUrl, proxy.getId(), finalDestination);
                return finalDestination;
            } else {
                LOG.trace("Proxy does not match: {}", treatedUrl);
            }
        }
        
        //Not match was found against database
        return null;
    }
    
}
