/*
 * Sparta Software Co.
 * 2015
 */
package org.sparta.simpleproxy.service;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sparta.simpleproxy.entity.Proxy;
import org.sparta.simpleproxy.repository.ProxyRepository;

import static org.mockito.Mockito.when;

/**
 *
 * Unit Tests for JSImpleProxy Service class.
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 10, 2016 - Daniel Conde Diehl
 *    Sep 23, 2021 - Daniel Conde Diehl - adding extra tests for URL with variables.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JSimpleProxyServiceTest {

    @InjectMocks
    private JSimpleProxyService tested;

    @Mock
    private ProxyRepository proxyRepo;
    
    @Test
    public void testFindDestinationUrlEmptyDB() {
        final Iterable<Proxy> proxies = new LinkedList<>();

        when(proxyRepo.findAll()).thenReturn(proxies);

        final String actualDestination = tested.findDestinationUrl("/teste/x");
        Assert.assertNull(actualDestination);
    }
 
    @Test
    public void testFindDestinationUrlNoMatch() {
        final List<Proxy> proxies = new LinkedList<>();
        
        final Proxy proxy1 = new Proxy();
        proxy1.setOrigin("/test/123");
        proxy1.setDestination("http://localhost/proxy1");
        proxies.add(proxy1);

        when(proxyRepo.findAll()).thenReturn(proxies);
        
        final String actualDestination = tested.findDestinationUrl("/test/x");
        Assert.assertNull(actualDestination);
    }
    
    @Test
    public void testFindDestinationUrlExactMatch() {
        final List<Proxy> proxies = new LinkedList<>();
        
        final Proxy proxy1 = new Proxy();
        proxy1.setOrigin("/test/123");
        proxy1.setDestination("http://localhost/proxy1");
        proxies.add(proxy1);
        
        final Proxy proxy2 = new Proxy();
        proxy2.setOrigin("/test/x");
        proxy2.setDestination("http://localhost/proxy2");
        proxies.add(proxy2);

        when(proxyRepo.findAll()).thenReturn(proxies);
        
        final String actualDestination = tested.findDestinationUrl("/test/x");
        Assert.assertEquals(proxy2.getDestination(), actualDestination);
    }
    
    @Test
    public void testFindDestinationUrlWildcardMatch() {
        final List<Proxy> proxies = new LinkedList<>();
        
        final Proxy proxy1 = new Proxy();
        proxy1.setOrigin("/notThisOne");
        proxy1.setDestination("http://localhost/proxy1");
        proxies.add(proxy1);
        
        final Proxy proxy2 = new Proxy();
        proxy2.setOrigin("/test/*");
        proxy2.setDestination("http://localhost/proxy2");
        proxies.add(proxy2);

        when(proxyRepo.findAll()).thenReturn(proxies);
        
        final String actualDestination = tested.findDestinationUrl("/test/x");
        Assert.assertEquals(proxy2.getDestination(), actualDestination);
    }
    
    @Test
    public void testFindDestinationUrlWildcardNotMatch() {
        final List<Proxy> proxies = new LinkedList<>();
        
        final Proxy proxy1 = new Proxy();
        proxy1.setOrigin("/notThisOne");
        proxy1.setDestination("http://localhost/proxy1");
        proxies.add(proxy1);
        
        final Proxy proxy2 = new Proxy();
        proxy2.setOrigin("/test/*");
        proxy2.setDestination("http://localhost/proxy2");
        proxies.add(proxy2);

        when(proxyRepo.findAll()).thenReturn(proxies);
        
        final String actualDestination = tested.findDestinationUrl("/invalid/x");
        Assert.assertNull(actualDestination);
    }

    @Test
    public void testFindDestinationUrlWildcardNotMatchAfterStar() {
        final List<Proxy> proxies = new LinkedList<>();
        
        final Proxy proxy1 = new Proxy();
        proxy1.setOrigin("/notThisOne");
        proxy1.setDestination("http://localhost/proxy1");
        proxies.add(proxy1);
        
        final Proxy proxy2 = new Proxy();
        proxy2.setOrigin("/test/*/123");
        proxy2.setDestination("http://localhost/proxy2");
        proxies.add(proxy2);

        when(proxyRepo.findAll()).thenReturn(proxies);
        
        final String actualDestination = tested.findDestinationUrl("/test/x");
        Assert.assertNull(actualDestination);
    }
    
    @Test
    public void testFindDestinationUrlWildcardMatchAfterStar() {
        final List<Proxy> proxies = new LinkedList<>();
        
        final Proxy proxy1 = new Proxy();
        proxy1.setOrigin("/notThisOne");
        proxy1.setDestination("http://localhost/proxy1");
        proxies.add(proxy1);
        
        final Proxy proxy2 = new Proxy();
        proxy2.setOrigin("/test/*/123");
        proxy2.setDestination("http://localhost/proxy2");
        proxies.add(proxy2);

        when(proxyRepo.findAll()).thenReturn(proxies);
        
        final String actualDestination = tested.findDestinationUrl("/test/x/123");
        Assert.assertEquals(proxy2.getDestination(), actualDestination);
    }
    
    @Test
    public void testFindDestinationUrlVariableMatchNoReplacements() {
        final List<Proxy> proxies = new LinkedList<>();
        
        final Proxy proxy1 = new Proxy();
        proxy1.setOrigin("/notThisOne");
        proxy1.setDestination("http://localhost/proxy1");
        proxies.add(proxy1);
        
        final Proxy proxy2 = new Proxy();
        proxy2.setOrigin("/users/{username}/{password}/test");
        proxy2.setDestination("http://localhost/proxy2");
        proxies.add(proxy2);

        when(proxyRepo.findAll()).thenReturn(proxies);
        
        final String actualDestination = tested.findDestinationUrl("/users/daniel/secret/test");
        Assert.assertEquals(proxy2.getDestination(), actualDestination);
    }
    
    @Test
    public void testFindDestinationUrlVariableMatchAllReplacements() {
        final List<Proxy> proxies = new LinkedList<>();
        
        final Proxy proxy1 = new Proxy();
        proxy1.setOrigin("/notThisOne");
        proxy1.setDestination("http://localhost/proxy1");
        proxies.add(proxy1);
        
        final Proxy proxy2 = new Proxy();
        proxy2.setOrigin("/users/{username}/{password}/test");
        proxy2.setDestination("http://localhost/proxy2/name/{username}/password/{password}");
        proxies.add(proxy2);

        when(proxyRepo.findAll()).thenReturn(proxies);
        
        final String actualDestination = tested.findDestinationUrl("/users/daniel/secret/test");
        Assert.assertEquals("http://localhost/proxy2/name/daniel/password/secret", actualDestination);
    }

    @Test
    public void testFindDestinationUrlVariableMatchWholeEnding() {
        final List<Proxy> proxies = new LinkedList<>();

        final Proxy proxy1 = new Proxy();
        proxy1.setOrigin("/notThisOne");
        proxy1.setDestination("http://localhost/proxy1");
        proxies.add(proxy1);

        final Proxy proxy2 = new Proxy();
        proxy2.setOrigin("/users/test/{var1}");
        proxy2.setDestination("http://localhost/proxy2/{var1}");
        proxies.add(proxy2);

        when(proxyRepo.findAll()).thenReturn(proxies);

        final String actualDestination = tested.findDestinationUrl("/users/test/secret/new?abc=1");
        Assert.assertEquals("http://localhost/proxy2/secret/new?abc=1", actualDestination);
    }


    @Test
    public void testFindDestinationUrlVariableMatchWholeEnding2() {
        final List<Proxy> proxies = new LinkedList<>();

        final Proxy proxy1 = new Proxy();
        proxy1.setOrigin("/notThisOne");
        proxy1.setDestination("http://localhost/proxy1");
        proxies.add(proxy1);

        final Proxy proxy2 = new Proxy();
        proxy2.setOrigin("/swagger/api/{parameter_name}");
        proxy2.setDestination("http://new-server/swagger/api/{parameter_name}");
        proxies.add(proxy2);

        when(proxyRepo.findAll()).thenReturn(proxies);

        final String actualDestination = tested.findDestinationUrl("/swagger/api/diehl-service/v0/diehl?countryCode=US&validOnDate=2018-01-01&asOfDate=2019-12-01T00%3A15%3A10.000Z");
        Assert.assertEquals("http://new-server/swagger/api/diehl-service/v0/diehl?countryCode=US&validOnDate=2018-01-01&asOfDate=2019-12-01T00%3A15%3A10.000Z", actualDestination);
    }


    @Test
    public void testFindDestinationUrlVariableMatchPartialReplacements() {
        final List<Proxy> proxies = new LinkedList<>();
        
        final Proxy proxy1 = new Proxy();
        proxy1.setOrigin("/notThisOne");
        proxy1.setDestination("http://localhost/proxy1");
        proxies.add(proxy1);
        
        final Proxy proxy2 = new Proxy();
        proxy2.setOrigin("/users/{username}/{password}/test");
        proxy2.setDestination("http://localhost/proxy2/name/{username}");
        proxies.add(proxy2);

        when(proxyRepo.findAll()).thenReturn(proxies);
        
        final String actualDestination = tested.findDestinationUrl("/users/daniel/secret/test");
        Assert.assertEquals("http://localhost/proxy2/name/daniel", actualDestination);
    }
}
