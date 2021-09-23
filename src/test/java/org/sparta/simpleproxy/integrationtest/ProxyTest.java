/* 
 * Sparta Software Co.
 * 2015
 */
package org.sparta.simpleproxy.integrationtest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sparta.simpleproxy.SimpleProxyBootApplication;
import org.sparta.simpleproxy.entity.Proxy;
import org.sparta.simpleproxy.repository.ProxyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * Integration test for a simple proxy.
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 20, 2016 - Daniel Conde Diehl
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SimpleProxyBootApplication.class)
@WebIntegrationTest(randomPort=true)
@ActiveProfiles("test")
public class ProxyTest {


    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProxyRepository proxyRepo; 
    
    private MockMvc mockMvc;

    private static final SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }
    
    @Test
    public void testDirectAccess () throws Exception {
        mockMvc.perform(get("/_jSimpleProxyAdmin/static/test.json"))
                 .andExpect(status().isOk())
                 .andExpect(content().json("{\"test\": 1}"));
    }
    
    
    @Test
    public void testAdminUIIsAvailable () throws Exception {
        mockMvc.perform(get("/_jSimpleProxyAdmin/"))
                 .andExpect(status().isOk());
    }
    
    @Test
    public void testAdminConfigIsSecureUnauthenticated () throws Exception {
        mockMvc.perform(get("/_jSimpleProxyAdmin/config").with(user("mockedUser")))
                 .andExpect(status().isOk());
    }
    
    @Test
    public void testAdminConfigIsSecureAuthenticated () throws Exception {
        mockMvc.perform(get("/_jSimpleProxyAdmin/config"))
                 .andExpect(unauthenticated());
    }
    
    @Test
    public void testProxyDirectExistent() throws Exception {
        proxyRepo.deleteAll();
        Proxy proxy = new Proxy();
        proxy.setOrigin("/mock-test");
        proxy.setDestination("http://www.google.com/robots.txt");
        proxyRepo.save(proxy);
        
        mockMvc.perform(get("/mock-test"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN));
    }
    
    @Test
    public void testProxyNotConfigured() throws Exception {
        proxyRepo.deleteAll();
        
        mockMvc.perform(get("/mock-test"))
        .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testProxyWildcard1() throws Exception {
        proxyRepo.deleteAll();
        Proxy proxy = new Proxy();
        proxy.setOrigin("/mock-test/*");
        proxy.setDestination("http://www.google.com/robots.txt");
        proxyRepo.save(proxy);
        
        mockMvc.perform(get("/mock-test/123"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN));
        
        mockMvc.perform(get("/mock-test/12312/333-444"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN));
        
        mockMvc.perform(get("/mock-test312312"))
                .andExpect(status().isBadRequest());
        
        mockMvc.perform(get("/asa/312312"))
        .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testProxyWildcard2() throws Exception {
        proxyRepo.deleteAll();
        Proxy proxy = new Proxy();
        proxy.setOrigin("/mock-test/*/END");
        proxy.setDestination("http://www.google.com/robots.txt");
        proxyRepo.save(proxy);
        
        mockMvc.perform(get("/mock-test/123/END"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN));
        
        mockMvc.perform(get("/mock-test/12312/333123/END"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN));
        
        mockMvc.perform(get("/mock-test/312312/2"))
                .andExpect(status().isBadRequest());
        
        mockMvc.perform(get("/asa/312312"))
        .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testProxyParameters1() throws Exception {
        proxyRepo.deleteAll();
        Proxy proxy = new Proxy();
        proxy.setOrigin("/mock-test/{param1}/{param2}.{param3}");
        proxy.setDestination("http://www.{param1}.com/{param2}.{param3}");
        proxyRepo.save(proxy);
        
        mockMvc.perform(get("/mock-test/google/robots.txt"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN));
    }
    
    
    @Test
    public void testProxyHeaders() throws Exception {
        proxyRepo.deleteAll();
        Proxy proxy = new Proxy();
        proxy.setOrigin("/mock-test/{param1}/{param2}.{param3}");
        proxy.setDestination("http://www.{param1}.com/{param2}.{param3}");
        proxyRepo.save(proxy);
        
        mockMvc.perform(get("/mock-test/google/robots.txt").header("IF-MODIFIED-SINCE", httpDateFormat.format(new Date())))
                .andExpect(status().isNotModified());
                
    }
}
