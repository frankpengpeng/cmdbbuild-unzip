/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.cmdbuild.common.java.sql.ForwardingDataSource;
import org.cmdbuild.dao.ConfigurableDataSource;
import org.cmdbuild.dao.config.inner.DatabaseCreator;

/**
 *
 */
public class DummyConfigurableDataSource extends ForwardingDataSource implements ConfigurableDataSource {

	private final DatabaseCreator databaseCreator;
	private final EventBus eventBus = new EventBus();

	public DummyConfigurableDataSource(DatabaseCreator databaseCreator) {
		this.databaseCreator = checkNotNull(databaseCreator);
	}

	@Override
	public String getDatabaseUrl() {
		return databaseCreator.getDatabaseUrl();
	}

	@Override
	protected DataSource delegate() {
		return databaseCreator.getCmdbuildDataSource();
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public BasicDataSource getInner() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void closeInner() {
		//do nothing
	}

	@Override
	public void reloadInner() {
		//do nothing
	}

	@Override
	public boolean hasAdminDataSource() {
		return databaseCreator.getConfig().hasAdminUser();
	}

	@Override
	public String getDatabaseUser() {
		return databaseCreator.getConfig().getCmdbuildUser();
	}

	@Override
	public void withAdminDataSource(Consumer<DataSource> consumer) {
		DataSource adminDataSource = databaseCreator.getAdminDataSource();
		consumer.accept(adminDataSource);
	}

}
