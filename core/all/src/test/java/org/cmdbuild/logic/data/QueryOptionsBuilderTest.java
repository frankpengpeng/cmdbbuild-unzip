package org.cmdbuild.logic.data;

import static com.google.common.collect.Iterables.size;
import static java.util.Arrays.asList;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.data.filter.utils.CmdbSorterUtils;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.json.JSONArray;
import org.junit.Test;

public class QueryOptionsBuilderTest {

	private static final String EMPTY_OBJECT = "{}";
	private static final String EMPTY_ARRAY = "[]";

	@Test
	public void shouldCreateQueryOptionsWithDefaultValues() {
		// when
		final QueryOptionsImpl options = QueryOptionsImpl.builder()//
				.build();

		// then
		assertThat(options.getLimit(), equalTo(Integer.MAX_VALUE));
		assertThat(options.getOffset(), equalTo(0));
		assertThat(CmdbFilterUtils.serializeFilter(options.getFilter()), equalTo(EMPTY_OBJECT));
		assertThat(CmdbSorterUtils.toJsonString(options.getSorters()), equalTo(EMPTY_ARRAY));
		assertThat(options.getAttributes().toString(), equalTo(EMPTY_ARRAY));
	}

	@Test
	public void shouldReturnLimitAndOffsetValuesWhenSet() {
		// when
		final QueryOptionsImpl options = QueryOptionsImpl.builder() //
				.limit(10) //
				.offset(3) //
				.build();

		// then
		assertThat(options.getLimit(), equalTo(10));
		assertThat(options.getOffset(), equalTo(3));
		assertThat(CmdbFilterUtils.serializeFilter(options.getFilter()), equalTo(EMPTY_OBJECT));
		assertThat(CmdbSorterUtils.toJsonString(options.getSorters()), equalTo(EMPTY_ARRAY));
		assertThat(options.getAttributes().toString(), equalTo(EMPTY_ARRAY));
	}

	@Test
	public void shouldReturnSortersWhenSet() throws Exception {
		// when
		final QueryOptionsImpl options = QueryOptionsImpl.builder() //
				.orderBy(new JSONArray("[{\n"
						+ "			property:'aaa',\n"
						+ "			direction:'ASC'\n"
						+ "		},{\n"
						+ "			property:'bbb',\n"
						+ "			direction:'ASC'\n"
						+ "		},{\n"
						+ "			property:'ccc',\n"
						+ "			direction:'ASC'\n"
						+ "		}]")) //
				.build();

		// then
		assertThat(options.getSorters().count(), equalTo(3));
	}

	@Test
	public void shouldReturnEmptyArrayIfSortersIsNull() throws Exception {
		// given
		final JSONArray sorters = null;

		// when
		final QueryOptionsImpl options = QueryOptionsImpl.builder() //
				.orderBy(sorters) //
				.build();

		// then
		assertThat(CmdbSorterUtils.toJsonString(options.getSorters()), equalTo(EMPTY_ARRAY));
	}

	@Test
	public void shouldReturnAttributesWhenSet() throws Exception {
		// given
		final Iterable<String> attributes = asList("foo", "bar", "baz");

		// when
		final QueryOptionsImpl options = QueryOptionsImpl.builder() //
				.onlyAttributes(attributes) //
				.build();

		// then
		assertThat(size(options.getAttributes()), equalTo(3));
	}

	@Test
	public void shouldReturnEmptyArrayIfAttributesIsNull() throws Exception {
		// given
		final Iterable<String> attributes = null;

		// when
		final QueryOptionsImpl options = QueryOptionsImpl.builder() //
				.onlyAttributes(attributes) //
				.build();

		// then
		assertThat(size(options.getAttributes()), equalTo(0));
	}

}
