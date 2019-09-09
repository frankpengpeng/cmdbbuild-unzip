/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler.annotatedmethodjobs;

import com.google.common.collect.Lists;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class AnnotatedMethodJobService implements AnnotatedMethodJobStore, AnnotatedMethodJobSupplier {

	private final List<AnnotatedMethodJob> list = Lists.newCopyOnWriteArrayList();

	@Override
	public List<AnnotatedMethodJob> getAnnotatedMethodJobs() {
		return unmodifiableList(list);
	}

	@Override
	public synchronized void addJob(AnnotatedMethodJob annotatedMethodJobImpl) {
		list.add(annotatedMethodJobImpl);
	}

}
