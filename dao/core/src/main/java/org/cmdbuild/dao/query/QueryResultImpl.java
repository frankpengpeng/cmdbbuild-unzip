package org.cmdbuild.dao.query;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.jcip.annotations.NotThreadSafe;

/*
 * Mutable classes used by the driver implementations
 */
@NotThreadSafe
public class QueryResultImpl implements QueryResult {

	private final Collection<CMQueryRow> rows;
	private int totalSize;

	public QueryResultImpl() {
		rows = new ArrayList<>();
		totalSize = 0;
	}

	public void add(CMQueryRow row) {
		rows.add(row);
	}

	@Override
	public Iterator<CMQueryRow> iterator() {
		return rows.iterator();
	}

	@Override
	public int size() {
		return rows.size();
	}

	@Override
	public boolean isEmpty() {
		return rows.isEmpty();
	}

	@Override
	public int totalSize() {
		return totalSize;
	}

	public void setTotalSize(int size) {
		this.totalSize = size;
	}

	@Override
	public CMQueryRow getOnlyRow() {
		return Iterators.getOnlyElement(iterator());
	}

}
