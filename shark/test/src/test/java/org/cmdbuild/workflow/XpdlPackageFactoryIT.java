package org.cmdbuild.workflow;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.cmdbuild.workflow.shark.xpdl.XpdlException;
import org.cmdbuild.workflow.shark.xpdl.XpdlPackageFactory;
import org.enhydra.jxpdl.elements.Package;
import org.junit.Test;

public class XpdlPackageFactoryIT {

	private final Charset XPDL_CHARSET = Charset.forName("UTF-8");

	private static final String PACKAGE_NAME = "MyPkg";
	private static final String EMPTY_XML_FORMAT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
			+ "<xpdl:Package xmlns=\"http://www.wfmc.org/2008/XPDL2.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" Id=\"%s\" xsi:schemaLocation=\"http://www.wfmc.org/2008/XPDL2.1 http://www.wfmc.org/standards/docs/bpmnxpdl_31.xsd\"/>\n";

	@Test
	public void emptyPackageSerializationWorks() throws XpdlException {
		Package pkg = new Package();
		pkg.setId(PACKAGE_NAME);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		XpdlPackageFactory.writeXpdl(pkg, os);
		String xpdl = new String(os.toByteArray(), XPDL_CHARSET);

		assertThat(xpdl, is(String.format(EMPTY_XML_FORMAT, PACKAGE_NAME)));
	}

	@Test
	public void emptyPackageDeserializationWorks() throws Exception {
		String xpdl = String.format(EMPTY_XML_FORMAT, PACKAGE_NAME);

		ByteArrayInputStream is = new ByteArrayInputStream(xpdl.getBytes(XPDL_CHARSET));
		Package pkg = XpdlPackageFactory.readXpdl(is);

		assertThat(pkg.getId(), is(PACKAGE_NAME));
	}

	@Test
	public void packageSerializationRoundtripCreatesTheSameFile() throws Exception {
		byte[] resourceData = IOUtils.toByteArray(getClass().getResourceAsStream("/xpdl/testpkg.xpdl"));
		ByteArrayInputStream is = new ByteArrayInputStream(resourceData);
		Package pkg = XpdlPackageFactory.readXpdl(is);

		assertThat(pkg.getParticipant("Role"), is(notNullValue()));

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		XpdlPackageFactory.writeXpdl(pkg, os);

		String inputXml = new String(resourceData, XPDL_CHARSET);
		String outputXml = new String(os.toByteArray(), XPDL_CHARSET);

		assertThat(outputXml, is(equalTo(inputXml)));
	}

}
