/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.minions.InnerBean;
import static org.cmdbuild.minions.MinionUtils.isMinionBean;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class MinionAnnotationHandler implements BeanPostProcessor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final MinionBeanRepository minionBeanRepository;

	public MinionAnnotationHandler(MinionBeanRepository minionBeanRepository) {
		this.minionBeanRepository = checkNotNull(minionBeanRepository);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (isMinionBean(bean)) {
			logger.info("register minion bean = {} ( {} )", beanName, bean.getClass().getName());
			minionBeanRepository.addMinionBean(new InnerBeanImpl(beanName, bean));
		}
		return bean; // nothing to do
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	private final static class InnerBeanImpl implements InnerBean {

		final String name;
		final Object bean;

		public InnerBeanImpl(String name, Object bean) {
			this.name = checkNotBlank(name);
			this.bean = checkNotNull(bean);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Object getBean() {
			return bean;
		}

	}

}
