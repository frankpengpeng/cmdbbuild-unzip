/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report;

import javax.activation.DataHandler;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.view.View;

public interface SysReportService {

    DataHandler executeClassSchemaReport(Classe classe, ReportFormat format);

    DataHandler executeSchemaReport(ReportFormat format);

    DataHandler executeClassReport(Classe classe, ReportFormat format, DaoQueryOptions queryOptions);

    DataHandler executeCardReport(Card card, ReportFormat format);

    DataHandler executeViewReport(View view, ReportFormat format, DaoQueryOptions queryOptions);

}
