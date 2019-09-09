/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.shark;

import static com.google.common.base.Preconditions.checkNotNull;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Aspect
@Configuration
@EnableAspectJAutoProxy
public class TransactedSharkMethodAspectjConfig {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final SharkTransactionService sharkTransactionService;

	public TransactedSharkMethodAspectjConfig(SharkTransactionService sharkTransactionService) {
		this.sharkTransactionService = checkNotNull(sharkTransactionService);
	}

	@Before("@annotation(org.cmdbuild.workflow.shark.TransactedSharkMethod)")
	public void initSharkTransaction(JoinPoint joinPoint) {
		logger.trace("initSharkTransaction for method = {}", joinPoint.getSignature().getName());
		if (SharkTransactionContextHolder.hasContext()) {
			logger.trace("transaction already opened, nesting context");
			SharkTransactionContextHolder.push(SharkTransactionContextHolder.get());
		} else {
			SharkTransactionContext context = sharkTransactionService.beginTransaction();
			SharkTransactionContextHolder.push(context);
		}
	}

	@AfterReturning("@annotation(org.cmdbuild.workflow.shark.TransactedSharkMethod)")
	public void commitSharkTransaction(JoinPoint joinPoint) {
		logger.trace("commitSharkTransaction for method = {}", joinPoint.getSignature().getName());
		if (SharkTransactionContextHolder.hasMany()) {
			logger.trace("nested transaction, skipping commit");
			SharkTransactionContextHolder.pop();
		} else {
			SharkTransactionContext context = SharkTransactionContextHolder.get();
			try {
				sharkTransactionService.commitTransaction(context);
			} finally {
				SharkTransactionContextHolder.pop();
			}
		}
	}

	@AfterThrowing("@annotation(org.cmdbuild.workflow.shark.TransactedSharkMethod)")
	public void rollbackSharkTransaction(JoinPoint joinPoint) {
		logger.trace("rollbackSharkTransaction for method = {}", joinPoint.getSignature().getName());
		if (SharkTransactionContextHolder.hasMany()) {
			logger.trace("nested transaction, skipping rollback");
			SharkTransactionContextHolder.pop();
		} else {
			if (SharkTransactionContextHolder.hasContext()) { //context is null if there was an error in initSharkTransaction
				SharkTransactionContext context = SharkTransactionContextHolder.get();
				try {
					sharkTransactionService.rollbackTransaction(context);
				} finally {
					SharkTransactionContextHolder.pop();
				}
			}
		}
	}

	public <T> T createAspectjProxy(T impl) {
		AspectJProxyFactory factory = new AspectJProxyFactory(impl);
		factory.addAspect(this);
		T proxy = factory.getProxy();
		return proxy;
	}
}
