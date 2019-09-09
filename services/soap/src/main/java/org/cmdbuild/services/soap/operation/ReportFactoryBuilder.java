package org.cmdbuild.services.soap.operation;

import java.util.Map;

import javax.sql.DataSource;

import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.data2.api.DataAccessService;
import org.cmdbuild.report.ReportProcessor;

public interface ReportFactoryBuilder<T extends ReportProcessor> {

	ReportFactoryBuilder<ReportProcessor> withOperationUser(OperationUser operationUser);

	ReportFactoryBuilder<ReportProcessor> withDataSource(DataSource dataSource);

	ReportFactoryBuilder<ReportProcessor> withDataAccessLogic(DataAccessService dataAccessLogic);

	ReportFactoryBuilder<T> withExtension(String extension);

	ReportFactoryBuilder<T> withProperties(Map<String, String> properties);

	T build();

}
