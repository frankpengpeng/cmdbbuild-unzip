/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.io.File;
import static java.lang.String.format;
import java.util.Map;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleImpl;
import org.cmdbuild.auth.role.RoleRepository;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.etl.ImportExportColumnConfigImpl;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_CSV;
import org.cmdbuild.etl.ImportExportService;
import org.cmdbuild.etl.ImportExportTemplate;
import org.cmdbuild.etl.ImportExportTemplateImpl;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_IMPORT;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobException;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.jobs.inner.JobRunHelperService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class ImportExportJob2IT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final ImportExportService service;
    private final JobService jobService;
    private final JobRunHelperService jobRunHelperService;
    private final EmailTemplateService emailTemplateService;
    private final EmailService emailService;
    private final RoleRepository roleRepository;

    public ImportExportJob2IT(DaoService dao, ImportExportService service, JobService jobService, JobRunHelperService jobRunHelperService, EmailTemplateService emailTemplateService, EmailService emailService, RoleRepository roleRepository) {
        this.dao = checkNotNull(dao);
        this.service = checkNotNull(service);
        this.jobService = checkNotNull(jobService);
        this.jobRunHelperService = checkNotNull(jobRunHelperService);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.emailService = checkNotNull(emailService);
        this.roleRepository = checkNotNull(roleRepository);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testImportJobEmailOnErrorFromTemplate() {
        Role role = roleRepository.create(RoleImpl.builder().withName(tuid("MyRole")).withEmail("role.email@host.something").build());

        EmailTemplate emailTemplate = emailTemplateService.createEmailTemplate(EmailTemplateImpl.builder()
                .withName(tuid("myEmailTemplate"))
                .withTextHtmlContentType()
                .withTo(format("[#ftl]${cmdb.getRole('%s').email}", role.getName()))
                .withSubject("[#ftl]import report for file = ${data.cm_import_source!'<file not available>'} : [#if data.cm_import_failed ]import failed[#elseif data.cm_import_errors_count > 0]import completed with errors[#else]import completed without errors[/#if]")
                .withBody("[#ftl][#if data.cm_import_failed ]Import failed: ${data.cm_import_error_desc}.[#elseif data.cm_import_errors_count > 0]Import errors: <ul>[#list data.cm_import_errors as record]<li>error at line <b>${record.row}</b>: ${record.message}</li>[/#list]</ul>[#else]No errors.[/#if]<br><br><i>job run ${data.cm_job_run?c}</i>").build());

        Classe myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());
        dao.updateAttribute(AttributeImpl.copyOf(myClass.getAttribute(ATTR_CODE)).withClassOrderInMeta(1).build());
        dao.createAttribute(AttributeImpl.builder().withType(new StringAttributeType()).withName("MyAttr").withMeta(m -> m.withRequired(true)).withOwner(myClass).build());

        ImportExportTemplate importTemplate = ImportExportTemplateImpl.builder()
                .withCode(tuid("myImportTemplate"))
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("MyAttr").withColumnName("ATTR").build()))
                .withTarget(myClass)
                .withType(IETT_IMPORT)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("MyAttr")
                .build();
        importTemplate = service.create(importTemplate);

        File tempDir = tempDir();
        File fileToImport = new File(tempDir, "file.csv");

        Map<String, Object> config = map(
                "template", importTemplate.getCode(),
                "source", "file",
                "directory", tempDir.getAbsolutePath(),
                "postImportAction", "do_nothing",
                "cronExpression", "",
                "notificationMode", "always",
                "errorEmailTemplate", emailTemplate.getName()
        );

        JobData createdJob = jobService.createJob(JobDataImpl.builder()
                .withCode(tuid("ImportTestJob"))
                .withDescription(tuid("ImportTestJob"))
                .withType("import_file")
                .withEnabled(false)
                .withConfig(config)
                .build());

        {
            writeToFile("CODE,DESCRIPTION,ATTR\nA,ally,one\nB,bob,two\nC,charlie,\nD,della,four\nE,enna,\n\n", fileToImport);

            JobRun jobRun = jobRunHelperService.runJob(createdJob);

            assertTrue(jobRun.isCompleted());
            assertFalse(jobRun.hasErrors());
            assertFalse(jobRun.getErrorOrWarningEvents().isEmpty());

            Email email = getOnlyElement(emailService.getAllForTemplate(emailTemplate.getId()));

            assertEquals("import report for file = " + fileToImport.getAbsolutePath() + " : import completed with errors", email.getSubject());
            assertEquals(format("Import errors: <ul><li>error at line <b>4</b>: missing value for required attr = MyAttr</li><li>error at line <b>6</b>: missing value for required attr = MyAttr</li></ul><br><br><i>job run %s</i>", jobRun.getId()), email.getContent());
            assertEquals("role.email@host.something", email.getToAddresses());

            dao.delete(email);
        }

        {
            writeToFile("CODE,DESCRIPTION,ATTR\nA,ally,one\nB,bob,two\nC,charlie,three\nD,della,four\nE,enna,five\n\n", fileToImport);

            JobRun jobRun = jobRunHelperService.runJob(createdJob);

            assertTrue(jobRun.isCompleted());
            assertFalse(jobRun.hasErrors());

            Email email = getOnlyElement(emailService.getAllForTemplate(emailTemplate.getId()));

            assertEquals("import report for file = " + fileToImport.getAbsolutePath() + " : import completed without errors", email.getSubject());
            assertEquals(format("No errors.<br><br><i>job run %s</i>", jobRun.getId()), email.getContent());
            assertEquals("role.email@host.something", email.getToAddresses());

            dao.delete(email);
        }

        {
            writeToFile("CODE,DESCRIPTION,ATTR_ERRRR\nA,ally,one\nB,bob,two\nC,charlie,three\nD,della,four\nE,enna,five\n\n", fileToImport);

            try {
                jobRunHelperService.runJob(createdJob);
                fail("expected exception at runJob()");
            } catch (JobException expected) {
                logger.debug("expected exception", expected);
            }

            Email email = getOnlyElement(emailService.getAllForTemplate(emailTemplate.getId()));

            assertEquals("import report for file = " + fileToImport.getAbsolutePath() + " : import failed", email.getSubject());
            assertThat(email.getContent(), matchesPattern("Import failed: generic error.<br><br><i>job run [0-9]+</i>"));
//            assertThat(email.getContent(), matchesPattern("Import failed: generic error.br..br..i.job run [0-9]+./i."));
            assertEquals("role.email@host.something", email.getToAddresses());

            dao.delete(email);
        }

        deleteQuietly(tempDir);
    }

}
