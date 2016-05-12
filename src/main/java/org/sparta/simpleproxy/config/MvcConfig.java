/* 
 * Sparta Software Co.
 * 2015
 */
package org.sparta.simpleproxy.config;

import nz.net.ultraq.thymeleaf.LayoutDialect;

import org.sparta.simpleproxy.constant.SimpleProxyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

/**
 *
 * Web MVC configurations
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 10, 2016 - Daniel Conde Diehl
 *
 */
@Configuration
public class MvcConfig extends WebMvcAutoConfigurationAdapter {

    @Autowired
    private ResourceProperties resourceProperties = new ResourceProperties();
    
    private static final String[] SERVLET_RESOURCE_LOCATIONS = { "/" };
    
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {"classpath:/static/"};

    /*
     * (non-Javadoc)
     * @see org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter#addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(SimpleProxyConstants.ADMIN_URL+"/static/**")
            .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS)
            .setCachePeriod(resourceProperties.getCachePeriod());
        
        registry.addResourceHandler("/**")
            .addResourceLocations(SERVLET_RESOURCE_LOCATIONS)
            .setCachePeriod(resourceProperties.getCachePeriod());
    }
        
    
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	    registry.addViewController(SimpleProxyConstants.ADMIN_URL+"/login").setViewName("login");
	}
	
	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}
	
	@Bean
	public SpringSecurityDialect securityDialect() {
	    return new SpringSecurityDialect();
	}
}
