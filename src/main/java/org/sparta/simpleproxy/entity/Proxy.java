/* 
 * Sparta Software Co.
 * 2016
 */
package org.sparta.simpleproxy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * Proxy Entity.
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 10, 2016 - Daniel Conde Diehl
 *
 */
@Entity
public class Proxy {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Column(nullable=false, length=80, unique=true)
    @NotBlank
    private String origin;
    
    @Column(nullable=false, length=80)
    @NotBlank
    private String destination;

    /**
     * Getter Method for id.
     * 
     * @return the id
     */
    public final Long getId() {
        return id;
    }

    /**
     * Setter method for id.
     *
     * @param id the id to set
     */
    public final void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter Method for origin.
     * 
     * @return the origin
     */
    public final String getOrigin() {
        return origin;
    }

    /**
     * Setter method for origin.
     *
     * @param origin the origin to set
     */
    public final void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Getter Method for destination.
     * 
     * @return the destination
     */
    public final String getDestination() {
        return destination;
    }

    /**
     * Setter method for destination.
     *
     * @param destination the destination to set
     */
    public final void setDestination(String destination) {
        this.destination = destination;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
