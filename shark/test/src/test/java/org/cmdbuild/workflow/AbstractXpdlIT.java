package org.cmdbuild.workflow;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper;
import org.cmdbuild.workflow.shark.xpdl.XpdlDocumentHelper.StandardAndCustomTypes;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.DataType;
import org.enhydra.jxpdl.elements.DataTypes;
import org.enhydra.jxpdl.elements.DeclaredType;
import org.junit.Before;

import static org.cmdbuild.workflow.XpdlTest.randomName;

public abstract class AbstractXpdlIT implements XpdlTest {

	protected final String TEST_PKG_ID = randomName();
	protected XpdlDocumentHelper xpdlDocument;

	@Before
	public void createDocument() {
		xpdlDocument = new XpdlDocumentHelper(TEST_PKG_ID);
	}

	@Override
	public XpdlDocumentHelper getXpdlDocument() {
		return xpdlDocument;
	}

	protected static void assertMatchesType(final DataType dt, final StandardAndCustomTypes t) {
		final DataTypes dataTypes = dt.getDataTypes();
		if (t.isCustom()) {
			assertThat(dataTypes.getChoosen(), is(instanceOf(DeclaredType.class)));
			assertThat(dataTypes.getDeclaredType().getId(), is(t.getDeclaredTypeId()));
		} else {
			assertThat(dataTypes.getChoosen(), is(instanceOf(BasicType.class)));
			assertThat(dataTypes.getBasicType().getType(), is(t.name()));
		}
	}

}
