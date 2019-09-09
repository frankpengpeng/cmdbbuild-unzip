package org.cmdbuild.task.wizardconnector;

import org.cmdbuild.legacy.etl.Entry;
import org.cmdbuild.legacy.etl.ForwardingStore;
import org.cmdbuild.legacy.etl.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PermissionBasedStore extends ForwardingStore {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Store delegate;
	private final Permission permission;

	public PermissionBasedStore(final Store delegate, final PermissionBasedStore.Permission permission) {
		this.delegate = delegate;
		this.permission = permission;
	}

	@Override
	protected Store delegate() {
		return delegate;
	}

	@Override
	public void create(final Entry entry) {
		if (permission.allowsCreate(entry)) {
			super.create(entry);
		} else {
			logger.debug("create not allowed");
		}
	}

	@Override
	public void update(final Entry entry) {
		if (permission.allowsUpdate(entry)) {
			super.update(entry);
		} else {
			logger.debug("update not allowed");
		}
	}

	@Override
	public void delete(final Entry entry) {
		if (permission.allowsDelete(entry)) {
			super.delete(entry);
		} else {
			logger.debug("delete not allowed");
		}
	}

	public static interface Permission {

		boolean allowsCreate(Entry entry);

		boolean allowsUpdate(Entry entry);

		boolean allowsDelete(Entry entry);

	}
}
