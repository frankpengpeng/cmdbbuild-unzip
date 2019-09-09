/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.io.File;
import java.util.Map;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.email.beans.EmailAccountImpl;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.etl.ImportExportColumnConfigImpl;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_CSV;
import static org.cmdbuild.etl.ImportExportMergeMode.IEM_UPDATE_ATTR_ON_MISSING;
import org.cmdbuild.etl.ImportExportService;
import org.cmdbuild.etl.ImportExportTemplate;
import org.cmdbuild.etl.ImportExportTemplateImpl;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_EXPORT;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_IMPORT;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_IMPORT_EXPORT;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.jobs.beans.JobDataImpl;
import org.cmdbuild.jobs.inner.JobRunHelperService;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_DATE_FORMAT_EXTJS;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_TIMEZONE;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_TIME_FORMAT_EXTJS;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.io.CmIoUtils.readLines;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class ImportExportJobIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final ImportExportService service;
    private final JobService jobService;
    private final JobRunHelperService jobRunHelperService;
    private final EmailTemplateService emailTemplateService;
    private final EmailAccountService emailAccountService;
    private final LookupService lookupService;
    private final SessionService sessionService;
    private final UserRepository userRepository;//TODO improve user creation
    private final RoleRepository roleRepository;//TODO improve user creation
    private final UserConfigService userConfigService;

    private EmailTemplate emailTemplate;
    private EmailAccount emailAccount;
    private Classe myClass;
    private ImportExportTemplate simpleTemplate;

    public ImportExportJobIT(DaoService dao, ImportExportService service, JobService jobService, JobRunHelperService jobRunHelperService, EmailTemplateService emailTemplateService, EmailAccountService emailAccountService, LookupService lookupService, SessionService sessionService, UserRepository userRepository, RoleRepository roleRepository, UserConfigService userConfigService) {
        this.dao = checkNotNull(dao);
        this.service = checkNotNull(service);
        this.jobService = checkNotNull(jobService);
        this.jobRunHelperService = checkNotNull(jobRunHelperService);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.emailAccountService = checkNotNull(emailAccountService);
        this.lookupService = checkNotNull(lookupService);
        this.sessionService = checkNotNull(sessionService);
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
        this.userConfigService = checkNotNull(userConfigService);
    }

    @Before
    public void init() {
        prepareTuid();
        emailTemplate = emailTemplateService.createEmailTemplate(EmailTemplateImpl.builder().withName(tuid("myEmailTemplate")).withTextPlainContentType().build());
        emailAccount = emailAccountService.create(EmailAccountImpl.builder().withName(tuid("myEmailAccount")).withAddress("my.email@account.com").build());

        myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());
        dao.updateAttribute(AttributeImpl.copyOf(myClass.getAttribute(ATTR_CODE)).withClassOrderInMeta(1).build());
        dao.createAttribute(AttributeImpl.builder().withType(new StringAttributeType()).withName("MyAttr").withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new DateTimeAttributeType()).withName("DateTimeAttr").withOwner(myClass).build());
        simpleTemplate = ImportExportTemplateImpl.builder()
                .withCode("myCsvTemplate")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("MyAttr").withColumnName("ATTR").build()))
                .withTarget(myClass)
                .withType(IETT_IMPORT_EXPORT)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("MyAttr")
                .build();

        UserData user = userRepository.create(UserDataImpl.builder().withUsername(tuid("my_username")).build());//TODO improve user creation
        roleRepository.setUserGroupsByName(user.getId(), list("SuperUser"), null);//TODO improve user creation

        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(user.getUsername()));

        userConfigService.setForCurrent(USER_CONFIG_TIMEZONE, "Europe/Rome");
        userConfigService.setForCurrent(USER_CONFIG_DATE_FORMAT_EXTJS, "d/m/Y");
        userConfigService.setForCurrent(USER_CONFIG_TIME_FORMAT_EXTJS, "H:i:s");
    }

    @Test
    public void testImportJob() {
        ImportExportTemplate importTemplate = ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode("myImportTemplate")
                .withAttributeNameForUpdateAttrOnMissing("MyAttr")
                .withAttributeValueForUpdateAttrOnMissing("myValue")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("MyAttr").withColumnName("ATTR").build()))
                .withMergeMode(IEM_UPDATE_ATTR_ON_MISSING)
                .withTarget(myClass)
                .withType(IETT_IMPORT)
                .withFileFormat(IEFF_CSV)
                .withErrorEmailTemplateId((long) emailTemplate.getId())
                .withErrorEmailAccountId((long) emailAccount.getId())
                .build();

        ImportExportTemplate createdTemplate = service.create(importTemplate);

        File tempDir = tempDir();
        File fileToImport = new File(tempDir, "file.csv");

        writeToFile(readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/ImportCSV/testImport.csv")), fileToImport);

        Map<String, Object> config = map(
                "template", createdTemplate.getCode(),
                "source", "file",
                "directory", tempDir.getAbsolutePath(),
                "postImportAction", "do_nothing",
                "cronExpression", ""
        );

        JobData createdJob = jobService.createJob(JobDataImpl.builder()
                .withCode("ImportTestJob")
                .withDescription("ImportTestJob")
                .withType("import_file")
                .withEnabled(false)
                .withConfig(config)
                .build());

        jobRunHelperService.runJob(createdJob);
        assertEquals(3, dao.selectCount().from(myClass.getName()).getCount());
        Card due = dao.selectAll().from(myClass).where(ATTR_CODE, EQ, "testCode2").getCard();
        assertEquals("testDesc2", due.getDescription());

        deleteQuietly(tempDir);
    }

    @Test
    public void testExportJob() {
        dao.create(CardImpl.buildCard(myClass, map("Code", "one", "Description", "unooo", "MyAttr", "1")));
        dao.create(CardImpl.buildCard(myClass, map("Code", "two", "Description", "due2322", "MyAttr", "2")));
        dao.create(CardImpl.buildCard(myClass, map("Code", "three", "Description", "333tre", "MyAttr", "3")));
        ImportExportTemplate exportTemplate = ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode("myExportTemplate1")
                .withAttributeNameForUpdateAttrOnMissing("MyAttr")
                .withAttributeValueForUpdateAttrOnMissing("myValue")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("MyAttr").withColumnName("ATTR").build()))
                .withMergeMode(IEM_UPDATE_ATTR_ON_MISSING)
                .withTarget(myClass)
                .withType(IETT_EXPORT)
                .withFileFormat(IEFF_CSV)
                .withErrorEmailTemplateId((long) emailTemplate.getId())
                .withErrorEmailAccountId((long) emailAccount.getId())
                .build();

        ImportExportTemplate createdTemplate = service.create(exportTemplate);

        File tempDir = tempDir();

        Map<String, Object> config = map(
                "template", createdTemplate.getCode(),
                "directory", tempDir.getAbsolutePath(),
                "fileName", "testExport.csv",
                "notificationMode", "never",
                "cronExpression", ""
        );

        JobData createdJob = jobService.createJob(JobDataImpl.builder()
                .withCode("ExportTestJob")
                .withDescription("ExportTestJob")
                .withType("export_file")
                .withEnabled(false)
                .withConfig(config)
                .build());

        jobRunHelperService.runJob(createdJob);

        File file = getOnlyElement(list(tempDir.listFiles()));
        String csvData = readToString(file);
        assertTrue(csvData.startsWith("CODE,DESCRIPTION,ATTR"));
        assertEquals(4, readLines(csvData).size());

        deleteQuietly(tempDir);
    }

}
