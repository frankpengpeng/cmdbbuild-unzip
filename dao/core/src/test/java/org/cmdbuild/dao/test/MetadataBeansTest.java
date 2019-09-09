/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.test;

import static java.util.Collections.emptyMap;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.beans.EntryTypeMetadataImpl;
import org.cmdbuild.dao.beans.FunctionMetadataImpl;
import org.junit.Test;

public class MetadataBeansTest {

	@Test
	public void testSimpleAttributeMetadata() {
		AttributeMetadataImpl simpleAttributeMetadata = new AttributeMetadataImpl(emptyMap());
	}

	@Test
	public void testSimpleClassMetadata() {
		ClassMetadataImpl simpleClassMetadata = new ClassMetadataImpl(emptyMap());
	}

	@Test
	public void testSimpleDomainMetadata() {
		DomainMetadataImpl simpleDomainMetadata = new DomainMetadataImpl(emptyMap());
	}

	@Test
	public void testSimpleFunctionMetadata() {
		FunctionMetadataImpl simpleFunctionMetadata = new FunctionMetadataImpl(emptyMap());
	}

	@Test
	public void testSimpleEntryTypeMetadata() {
		EntryTypeMetadataImpl simpleEntryTypeMetadata = new EntryTypeMetadataImpl(emptyMap(), emptyMap()) {
		};
	}

}
