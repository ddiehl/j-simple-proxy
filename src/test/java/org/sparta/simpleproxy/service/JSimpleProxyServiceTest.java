/*
 * Copyright (c) Bright House Networks. All Rights Reserved.
 * This software is the confidential and proprietary information of
 * Bright House Networks or its affiliates. You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Bright House Networks.
 */
package org.sparta.simpleproxy.service;

import java.util.LinkedList;
import java.util.List;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

import org.junit.Assert;
import org.junit.Test;
import org.sparta.simpleproxy.entity.Proxy;
import org.sparta.simpleproxy.repository.ProxyRepository;

/**
 *
 * Unit Tests for JSImpleProxy Service class.
 *
 * @author dxdiehl
 *
 * History:
 *    May 10, 2016 - dxdiehl
 *
 */
public class JSimpleProxyServiceTest {

    @Tested
    private JSimpleProxyService tested;
    
    @Injectable
    private ProxyRepository proxyRepo;
    
    @Test
    public void testFindDestinationUrlEmptyDB() {
        final Iterable<Proxy> proxies = new LinkedList<>();
        
        new Expectations() {{
            proxyRepo.findAll();
            result = proxies;
        }};
        
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
        
        new Expectations() {{
            proxyRepo.findAll();
            result = proxies;
        }};
        
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
        
        new Expectations() {{
            proxyRepo.findAll();
            result = proxies;
        }};
        
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
        
        new Expectations() {{
            proxyRepo.findAll();
            result = proxies;
        }};
        
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
        
        new Expectations() {{
            proxyRepo.findAll();
            result = proxies;
        }};
        
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
        
        new Expectations() {{
            proxyRepo.findAll();
            result = proxies;
        }};
        
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
        
        new Expectations() {{
            proxyRepo.findAll();
            result = proxies;
        }};
        
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
        
        new Expectations() {{
            proxyRepo.findAll();
            result = proxies;
        }};
        
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
        
        new Expectations() {{
            proxyRepo.findAll();
            result = proxies;
        }};
        
        final String actualDestination = tested.findDestinationUrl("/users/daniel/secret/test");
        Assert.assertEquals("http://localhost/proxy2/name/daniel/password/secret", actualDestination);
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
        
        new Expectations() {{
            proxyRepo.findAll();
            result = proxies;
        }};
        
        final String actualDestination = tested.findDestinationUrl("/users/daniel/secret/test");
        Assert.assertEquals("http://localhost/proxy2/name/daniel", actualDestination);
    }
}
