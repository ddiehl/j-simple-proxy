/* 
 * Sparta Software Co.
 * 2015
 */
package org.sparta.simpleproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * Initializes Class for Spring boot application creates the context
 * and autoconfigure all necessary,
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 10, 2016 - Daniel Conde Diehl
 *
 */
@SpringBootApplication
public class SimpleProxyBootApplication {
	   
    /**
     * Initializes spring boot application.
     * 
     * @param args command-line parameters
     */
    public static void main(String[] args) {
        SpringApplication.run(SimpleProxyBootApplication.class, args);
    }
}
