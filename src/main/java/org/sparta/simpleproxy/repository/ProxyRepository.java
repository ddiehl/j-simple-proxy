/* 
 * Sparta Software Co.
 * 2015
 */
package org.sparta.simpleproxy.repository;

import org.sparta.simpleproxy.entity.Proxy;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * Crud repository to Proxy.
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 10, 2016 - Daniel Conde Diehl
 *
 */
public interface ProxyRepository extends CrudRepository<Proxy, Long>{
    
    /**
     * Finds a Proxy by its origin.
     * 
     * @param origin to perform the search
     * @return Proxy if found, otherwise null
     */
    public Proxy findByOrigin(String origin);
}
