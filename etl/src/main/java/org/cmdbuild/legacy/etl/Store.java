package org.cmdbuild.legacy.etl;

public interface Store {

	void create(Entry entry);

	Iterable<Entry> readAll();

	void update(Entry entry);

	void delete(Entry entry);

}
