/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static org.cmdbuild.scheduler.JobClusterMode.CM_RUN_ON_ALL_NODES;

/**
 * run this method with CMDBuild scheduler service (currently quartz scheduler).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledJob {

	/**
	 * a cron pattern, like
	 * <pre><code>
	 * {@code
	 * 0/5 * * * * ?
	 * }</code></pre>
	 *
	 * @return
	 */
	String value();

	JobClusterMode clusterMode() default CM_RUN_ON_ALL_NODES;

	String user() default "";

}
