/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.legacy.etl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingStore extends ForwardingStore {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Store delegate;

	public LoggingStore(final Store delegate) {
		this.delegate = delegate;
	}

	public static Store logging(final Store delegate) {
		return new LoggingStore(delegate);
	}

	@Override
	protected Store delegate() {
		return delegate;
	}

	@Override
	public void create(final Entry entry) {
		try {
			logger.debug("creating entry '{}'", entry);
			super.create(entry);
		} catch (final RuntimeException e) {
			logger.error("error creating entry", e);
			throw e;
		}
	}

	@Override
	public Iterable<Entry> readAll() {
		try {
			logger.debug("reading all entries");
			return super.readAll();
		} catch (final RuntimeException e) {
			logger.error("error reading all entries", e);
			throw e;
		}
	}

	@Override
	public void update(final Entry entry) {
		try {
			logger.debug("updating entry '{}'", entry);
			super.update(entry);
		} catch (final RuntimeException e) {
			logger.error("error updating entry", e);
			throw e;
		}
	}

	@Override
	public void delete(final Entry entry) {
		try {
			logger.debug("deleting entry '{}'", entry);
			super.delete(entry);
		} catch (final RuntimeException e) {
			logger.error("error deleting entry", e);
			throw e;
		}
	}

}
