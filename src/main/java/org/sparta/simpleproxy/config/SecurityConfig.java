/* 
 * Sparta Software Co.
 * 2015
 */
package org.sparta.simpleproxy.config;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.sparta.simpleproxy.constant.SimpleProxyConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 *
 * Spring Security configuration.
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 11, 2016 - Daniel Conde Diehl
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
    
    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers(SimpleProxyConstants.ADMIN_URL+"/static/**").permitAll()
                .antMatchers(SimpleProxyConstants.ADMIN_URL).permitAll()
                .antMatchers(SimpleProxyConstants.ADMIN_URL+"/").permitAll()
                .antMatchers(SimpleProxyConstants.ADMIN_URL+"/index*").permitAll()
                .antMatchers(SimpleProxyConstants.ADMIN_URL+"/**").hasRole("ADMIN")
                .anyRequest().permitAll()   
            .and()
            .formLogin()
                .loginPage(SimpleProxyConstants.ADMIN_URL+"/login")
                .defaultSuccessUrl(SimpleProxyConstants.ADMIN_URL)
                .failureUrl(SimpleProxyConstants.ADMIN_URL+"/login?error")
                .permitAll()
            .and()
            .logout()
                .logoutUrl(SimpleProxyConstants.ADMIN_URL+"/logout")
                .logoutSuccessUrl(SimpleProxyConstants.ADMIN_URL)
            .and()
            .csrf().requireCsrfProtectionMatcher(new RequestMatcher() {
                
                @Override
                public boolean matches(HttpServletRequest request) {
                    // No CSRF due to allowedMethod
                    if(allowedMethods.matcher(request.getMethod()).matches()) {
                        return false;
                    } else if (request.getRequestURI().startsWith(SimpleProxyConstants.ADMIN_PREFIX)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
    }
}
