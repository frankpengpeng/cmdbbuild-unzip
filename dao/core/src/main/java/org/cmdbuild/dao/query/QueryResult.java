package org.cmdbuild.dao.query;

import com.google.common.collect.Streams;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.Collections.emptyIterator;
import java.util.stream.Stream;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.beans.Card;

/*
 * Immutable interface to mask result object building
 */
public interface QueryResult extends Iterable<CMQueryRow> { //TODO extend Collection !

	int size();

	boolean isEmpty();

	int totalSize();

	default boolean hasContent() {
		return !isEmpty();
	}

	/**
	 * Returns the first and only row in the result.
	 *
	 * @return the first and only row in the result
	 *
	 * @throws IllegalArgumentException if there is no unique element
	 */
	CMQueryRow getOnlyRow();

	default Stream<CMQueryRow> stream() {
		return Streams.stream(iterator());
	}

	default Stream<Card> streamCards(Classe classe) {
		return stream().map((r) -> r.getCard(classe));
	}

	final QueryResult EMPTY = new QueryResult() {

		@Override
		public Iterator<CMQueryRow> iterator() {
			return emptyIterator();
		}

		@Override
		public CMQueryRow getOnlyRow() {
			throw new NoSuchElementException();
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public int totalSize() {
			return 0;
		}

	};
}
