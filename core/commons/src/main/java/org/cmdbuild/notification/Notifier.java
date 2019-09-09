package org.cmdbuild.notification;

import org.cmdbuild.exception.CMDBException;

@Deprecated
public interface Notifier {

	void warn(CMDBException e);

}
