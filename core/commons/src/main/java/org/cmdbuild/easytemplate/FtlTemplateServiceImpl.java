/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import static org.cmdbuild.easytemplate.FtlUtils.prepareFtlTemplateFixHeaderIfRequired;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FtlTemplateServiceImpl implements FtlTemplateService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Configuration configurationDefault, configurationForHtml;

    public FtlTemplateServiceImpl() {
        configurationDefault = new Configuration(Configuration.VERSION_2_3_28);
        configurationDefault.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configurationDefault.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configurationDefault.setLogTemplateExceptions(false);
        configurationDefault.setWrapUncheckedExceptions(true);

        configurationForHtml = (Configuration) configurationDefault.clone();
        configurationForHtml.setOutputFormat(HTMLOutputFormat.INSTANCE);
    }

    @Override
    public boolean isFtlTemplate(String template) {
        return FtlUtils.isFtlTemplate(template);
    }

    @Override
    public String executeFtlTemplate(String templateStr, FtlTemplateMode mode, Map data) {
        try {
            logger.trace("execute ftl raw template = \n\n{}\n", templateStr);
            templateStr = prepareFtlTemplateFixHeaderIfRequired(templateStr);
            logger.trace("execute ftl preprocessed template = \n\n{}\n", templateStr);
            logger.trace("data for template = \n\n{}\n", mapToLoggableStringLazy(data));
            Configuration configuration;
            switch (mode) {
                case FTM_HTML:
                    configuration = configurationForHtml;
                    break;
                default:
                    configuration = configurationDefault;
            }
            Template template = new Template(hash(templateStr), new StringReader(templateStr), configuration);
            Writer writer = new StringWriter();
            template.process(data, writer);
            String output = writer.toString();
            logger.trace("ftl template output = \n\n{}\n", output);
            return output;
        } catch (IOException | TemplateException ex) {
            throw runtime(ex);
        }
    }

}
