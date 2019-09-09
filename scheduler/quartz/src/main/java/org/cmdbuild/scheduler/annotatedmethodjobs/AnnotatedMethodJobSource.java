/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler.annotatedmethodjobs;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.lang.String.format;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.jobs.JobExecutorService.JOBUSER_NOBODY;
import org.cmdbuild.jobs.JobSessionService;
import org.cmdbuild.scheduler.beans.JobConfig;
import org.cmdbuild.scheduler.beans.JobConfigImpl;
import org.cmdbuild.scheduler.beans.JobDetail;
import org.cmdbuild.scheduler.JobSource;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AnnotatedMethodJobSource implements JobSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AnnotatedMethodJobSupplier annotatedMethodJobSupplier;
    private final CmCache<JobDetail> jobs;
    private final JobSessionService jobSessionService;
    private final ApplicationContext applicationContext;

    public AnnotatedMethodJobSource(CacheService cacheService, ApplicationContext applicationContext, JobSessionService jobSessionService, AnnotatedMethodJobSupplier annotatedMethodJobSupplier) {
        this.applicationContext = checkNotNull(applicationContext);
        this.jobSessionService = checkNotNull(jobSessionService);
        this.annotatedMethodJobSupplier = checkNotNull(annotatedMethodJobSupplier);
        jobs = cacheService.newCache("annotated_method_jobs");
    }

    @Override
    public String getJobSourceName() {
        return "core";
    }

    @Override
    public void register(Object listener) {
        //nothing to do: this jobs won't change at runtime
    }

    @Override
    public Collection<JobConfig> getJobs() {
        return annotatedMethodJobSupplier.getAnnotatedMethodJobs().stream().map(j -> new JobConfigImpl(j.getTrigger(), new AnnotatedMethodJobDetailImpl(j))).collect(toImmutableList());
    }

    @Override
    public void runJob(String key) {
        checkNotBlank(key);
        AnnotatedMethodJobDetailImpl job = (AnnotatedMethodJobDetailImpl) jobs.get(key, () -> checkNotNull(getJobs().stream().filter(j -> equal(j.getKey(), key)).collect(toOptional()).map(JobConfig::getJob).orElse(null), "job not found for key = %s", key));
        try {
            String beanName = job.getInner().getBeanName(), methodName = job.getInner().getMethodName();
            Object bean;
            Method method;
            String user;
            try {
                logger.trace("get job bean for name = {}", beanName);
                bean = applicationContext.getBean(beanName);//TODO load bean when building job detail, below
                logger.trace("get job method for bean name = {} method name = {}", beanName, methodName);
                method = bean.getClass().getMethod(methodName);
                checkArgument(method.isAnnotationPresent(ScheduledJob.class), "this method is not annotated with ScheduledJob"); // throw error if the bean is not annotated as a scheduled job
                user = firstNotBlank(method.getAnnotation(ScheduledJob.class).user(), JOBUSER_NOBODY);
            } catch (Exception ex) {
                logger.warn("error loading job for bean = {} method = {}", beanName, methodName);
                logger.warn("error loading job", ex);
                return;
            }
            jobSessionService.createJobSessionContext(user, format("quartz service method %s.%s", beanName, methodName));
            logger.debug("invoke bean method = {}.{}", beanName, methodName);
            method.invoke(bean);
            logger.debug("job execution completed for bean name = {} method name = {}", beanName, methodName);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException ex) {
            throw new RuntimeException(ex);
        } finally {
            jobSessionService.destroyJobSessionContext();
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private final static class AnnotatedMethodJobDetailImpl implements JobDetail {

        private final AnnotatedMethodJob inner;
        private final String key;

        public AnnotatedMethodJobDetailImpl(AnnotatedMethodJob inner) {
            this.inner = checkNotNull(inner);
            key = format("%s_%s_%s", inner.getBeanName(), inner.getMethodName(), inner.getTrigger().toUniqueKey());
        }

        public AnnotatedMethodJob getInner() {
            return inner;
        }

        @Override
        public String getKey() {
            return key;
        }

    }

}
