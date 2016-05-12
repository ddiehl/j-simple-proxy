/* 
 * Sparta Software Co.
 * 2015
 */
package org.sparta.simpleproxy.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * AOP for controller.
 *
 * @author Daniel Conde Diehl
 *
 * History:
 *    May 10, 2016 - Daniel Conde Diehl
 *
 */
@Aspect
@Component
public class AOPConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(AOPConfig.class);
	
	@Around("execution(public * org.sparta.simpleproxy.controller.SimpleProxyController.*(..))")
	public Object webMethod(ProceedingJoinPoint pjp) throws Throwable {
		LOGGER.debug("Entering {} parameters = {}", pjp.getSignature().toLongString(), pjp.getArgs());
		Object retVal = null;
		try {
			retVal = pjp.proceed();
			return retVal;
		} catch (Throwable e) {
			LOGGER.error("Error executing {}", pjp.getSignature().toLongString());
			throw e;
		} finally {
			LOGGER.debug("Done executing {}", pjp.getSignature().toLongString());
		}
	}
}
