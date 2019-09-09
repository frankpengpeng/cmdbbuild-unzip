package org.cmdbuild.task.cardeventprocessing;

public interface CardObserverService {

	void add(String id, Object listener);

	void remove(String id);

}
