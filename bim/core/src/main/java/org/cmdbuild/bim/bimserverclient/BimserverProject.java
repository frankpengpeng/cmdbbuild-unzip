package org.cmdbuild.bim.bimserverclient;

import javax.annotation.Nullable;
import org.joda.time.DateTime;

public interface BimserverProject {

	String getName();

	String getProjectId();

	String getIfcFormat();

	@Nullable
	String getDescription();

	boolean isActive();

	@Nullable
	DateTime getLastCheckin();

}
