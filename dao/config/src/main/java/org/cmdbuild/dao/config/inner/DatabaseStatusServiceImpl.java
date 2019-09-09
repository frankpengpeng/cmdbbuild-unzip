/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.ConfigurableDataSource;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.cmdbuild.dao.DatasourceConfiguredEvent;
import org.cmdbuild.services.PostStartup;
import org.springframework.stereotype.Component;

/**
 *
 * @author davide
 */
@Component
public class DatabaseStatusServiceImpl implements DatabaseStatusService {

	private final EventBus eventBus = new EventBus();

	private final ConfigurableDataSource dataSource;
	private final PatchService patchService;

	private boolean isReady = false;

	public DatabaseStatusServiceImpl(ConfigurableDataSource dataSource, PatchService patchManager) {
		this.dataSource = checkNotNull(dataSource);
		this.patchService = checkNotNull(patchManager);
		dataSource.getEventBus().register(new Object() {
			@Subscribe
			public void handleDatasourceConfiguredEvent(DatasourceConfiguredEvent event) {
				checkStatus();
			}
		});
		patchManager.getEventBus().register(new Object() {
			@Subscribe
			public void handleAllPatchAppliedAndDatabaseReadyEvent(AllPatchesAppliedEvent event) {
				checkStatus();
			}
		});
	}

	@PostStartup
	public void init() {
		checkStatus();
	}

	private void checkStatus() {
		if (!dataSource.isReady()) {
			isReady = false;
		} else if (!patchService.isUpdated()) {
			isReady = false;
		} else {
			boolean wasNotReady = !isReady;
			isReady = true;
			if (wasNotReady) {
				eventBus.post(DatabaseBecomeReadyEvent.INSTANCE);
			}
		}
	}

	@Override
	public boolean isReady() {
		if (!isReady) {
			checkStatus();// TODO: not sure about this; events should be enough...
		}
		return isReady;
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

}
