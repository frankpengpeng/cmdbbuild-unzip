package org.cmdbuild.servlets.json.management.export;

import static org.cmdbuild.dao.query.clause.AnyAttribute.anyAttribute;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.cmdbuild.dao.query.CMQueryRow;

import com.google.common.collect.Lists;
import org.cmdbuild.dao.query.QueryResult;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.view.DataView;
import org.cmdbuild.dao.beans.DatabaseRecord;

public class DBDataSource implements CMDataSource {

	private final DataView view;
	private final Classe sourceClass;

	public DBDataSource(final DataView view, final Classe sourceClass) {
		Validate.notNull(sourceClass);
		this.view = view;
		this.sourceClass = sourceClass;
	}

	@Override
	public List<String> getHeaders() {
		final List<String> attributeNames = Lists.newArrayList();
		final List<Attribute> attributes = Lists.newArrayList(sourceClass.getCoreAttributes());

		final Comparator<Attribute> comp = new Comparator<Attribute>() {
			@Override
			public int compare(Attribute o1, Attribute o2) {
				if (o1.getIndex() < o2.getIndex()) {
					return -1;
				} else if (o1.getIndex() > o2.getIndex()) {
					return 1;
				} else {
					return 0;
				}
			}
		};

		Collections.sort(attributes, comp);

		for (final Attribute attribute: attributes) {
			attributeNames.add(attribute.getName());
		}

		return attributeNames;
	}

	@Override
	public Iterable<DatabaseRecord> getEntries() {
		final List<DatabaseRecord> entries = Lists.newArrayList();
		final QueryResult result = view.select(anyAttribute(sourceClass)).from(sourceClass).run();
		for (final CMQueryRow row : result) {
			entries.add(row.getCard(sourceClass));
		}
		return entries;
	}

}
