package org.cmdbuild.servlets.json.management.dataimport.csv;


import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.DataHandler;

import org.cmdbuild.servlets.json.management.dataimport.CardFiller;
import org.json.JSONException;
import org.slf4j.Logger;

import com.google.common.collect.Maps;
import org.cmdbuild.dao.entrytype.Classe;
import org.slf4j.LoggerFactory;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.dao.view.DataView;

public class CSVImporter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	// a casual number from which start
	private static Long idCounter = 1000L;

	private final CsvReader csvReaded;
	private final DataView view;
	private final Classe importClass;
	private final LookupRepository lookupStore;

	public CSVImporter(final CsvReader csvReader, final DataView view, final LookupRepository lookupStore,
			final Classe importClass) {
		this.csvReaded = csvReader;
		this.view = view;
		this.lookupStore = lookupStore;
		this.importClass = importClass;
	}

	public CSVData getCsvDataFrom(final DataHandler csvFile) throws IOException, JSONException {
		final CsvReader.CsvData data = csvReaded.read(csvFile);
		return new CSVData(data.headers(), getCsvCardsFrom(data.lines()), importClass.getName());
	}

	private Map<Long, CSVCard> getCsvCardsFrom(final Iterable<CsvReader.CsvLine> lines) throws JSONException {
		final Map<Long, CSVCard> csvCards = Maps.newHashMap();
		for (final CsvReader.CsvLine line : lines) {
			final CardFiller cardFiller = new CardFiller(importClass, view, lookupStore);
			final Long fakeId = getAndIncrementIdForCsvCard();
			logger.debug("importing line '{}' using fake id '{}'", line, fakeId);
			final CsvCard mutableCard = new CsvCard(importClass, view.createCardFor(importClass));
			final CSVCard csvCard = new CSVCard(mutableCard, fakeId);
			for (final Entry<String, String> entry : line.entries()) {
				try {
					cardFiller.fillCardAttributeWithValue( //
							mutableCard, //
							entry.getKey(), //
							entry.getValue() //
					);
				} catch (final CardFiller.CardFillerException ex) {
					csvCard.addInvalidAttribute(ex.attributeName, ex.attributeValue);
				}
			}
			csvCards.put(fakeId, csvCard);
		}

		return csvCards;
	}

	private static synchronized Long getAndIncrementIdForCsvCard() {
		return idCounter++;
	}

}
