package org.cmdbuild.workflow;

import static java.lang.String.format;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.api.fluent.MailApi;

import org.cmdbuild.workflow.api.SharkWorkflowApiFactory;
import org.enhydra.shark.api.internal.working.CallbackUtilities;

public class ConfigurationHelper {

    private static final String CMDBUILD_API_CLASSNAME_PROPERTY = "org.cmdbuild.workflow.api.classname";
    private static final String CMDBUILD_MAIL_API_CLASSNAME_PROPERTY = "org.cmdbuild.mail.api.classname";

    private static final String MAIL_DEBUG = "DefaultMailMessageHandler.debug";
    private static final String MAIL_USE_SSL = "DefaultMailMessageHandler.useSSL";
    private static final String MAIL_SMTP_SERVER = "DefaultMailMessageHandler.SMTPMailServer";
    private static final String MAIL_SMTP_PORT = "DefaultMailMessageHandler.SMTPPortNo";
    private static final String MAIL_STARTTLS = "DefaultMailMessageHandler.starttls";
    private static final String MAIL_USE_AUTHENTICATION = "DefaultMailMessageHandler.useAuthentication";
    private static final String MAIL_USERNAME = "DefaultMailMessageHandler.Login";
    private static final String MAIL_PASSWORD = "DefaultMailMessageHandler.Password";
    private static final String MAIL_FROM_ADDRESS = "DefaultMailMessageHandler.SourceAddress";

    private static final String MAIL_MULTIPLES_SEPARATOR = ",";

    private static final String MAIL_PROTOCOL_SMTP = "smtp";
    private static final String MAIL_PROTOCOL_SMTPS = "smtps";

    private static final String NO_AUTHENTICATION_USERNAME = null;

    private final CallbackUtilities cus;

    public ConfigurationHelper(CallbackUtilities cus) {
        this.cus = cus;
    }

    public SharkWorkflowApiFactory getWorkflowApiFactory() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String classname = cus.getProperty(CMDBUILD_API_CLASSNAME_PROPERTY);
        cus.info(null, format("loading workflow api '%s'", classname));
        return loadClass(classname, SharkWorkflowApiFactory.class);
    }

    public MailApi getMailApiFactory() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        throw new UnsupportedOperationException("TODO");
//		String classname = cus.getProperty(CMDBUILD_MAIL_API_CLASSNAME_PROPERTY);
//		cus.info(null, format("loading mail api '%s'", classname));
//		return loadClass(classname, MailApiFactory.class);
    }

    private <T> T loadClass(String classname, Class<T> classToBeLoaded) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        cus.info(null, format("loading class '%s' that should be a '%s'", classname, classToBeLoaded.getName()));
        Class<? extends T> loadedClass = Class.forName(classname).asSubclass(classToBeLoaded);
        T instance = loadedClass.newInstance();
        return instance;
    }

    public EmailAccount getMailApiConfiguration() {

        return new EmailAccount() {
            @Override
            public Long getId() {
                return null;
            }

            @Override
            public String getName() {
                return "shark";
            }

            @Override
            public String getUsername() {
                boolean useAuthentication = Boolean.valueOf(cus.getProperty(MAIL_USE_AUTHENTICATION));
                return useAuthentication ? cus.getProperty(MAIL_USERNAME) : NO_AUTHENTICATION_USERNAME;
            }

            @Override
            public String getPassword() {
                return cus.getProperty(MAIL_PASSWORD);
            }

            @Override
            public String getAddress() {
                return cus.getProperty(MAIL_FROM_ADDRESS);
            }

            @Override
            public String getSmtpServer() {
                return cus.getProperty(MAIL_SMTP_SERVER);
            }

            @Override
            public Integer getSmtpPort() {
                return Integer.parseInt(cus.getProperty(MAIL_SMTP_PORT));
            }

            @Override
            public boolean getSmtpSsl() {
                return Boolean.valueOf(cus.getProperty(MAIL_USE_SSL));
            }

            @Override
            public boolean getSmtpStartTls() {
                return Boolean.valueOf(cus.getProperty(MAIL_STARTTLS));
            }

            @Override
            public boolean isSmtpConfigured() {
                return true;
            }

            @Override
            public String getSentEmailFolder() {
                return null;
            }

            @Override
            public String getImapServer() {
                return null;
            }

            @Override
            public Integer getImapPort() {
                return null;
            }

            @Override
            public boolean getImapSsl() {
                return false;
            }

            @Override
            public boolean getImapStartTls() {
                return false;
            }

            @Override
            public boolean isImapConfigured() {
                return false;
            }
//			@Override TODO
//			public List<String> getOutputFromRecipients() {
//				return asList(cus.getProperty(MAIL_FROM_ADDRESS), MAIL_MULTIPLES_SEPARATOR);
//			}

        };
    }

//		return new Configuration.All() {
//			private final Configuration.Input INPUT_NOT_SUPPORTED = newProxy(Configuration.Input.class, unsupported("method not supported"));
//
//			@Override
//			public boolean isDebug() {
//				return Boolean.valueOf(cus.getProperty(MAIL_DEBUG));
//			}
//
//			@Override
//			public Logger getLogger() {
//				return LoggerFactory.getLogger(LOGGER_NAME);
//			}
//
//			@Override
//			public String getOutputProtocol() {
//				final boolean useSsl = Boolean.valueOf(cus.getProperty(MAIL_USE_SSL));
//				return useSsl ? MAIL_PROTOCOL_SMTPS : MAIL_PROTOCOL_SMTP;
//			}
//
//			@Override
//			public boolean isOutputStartTlsEnabled() {
//				return Boolean.valueOf(cus.getProperty(MAIL_STARTTLS));
//			}
//
//			@Override
//			public String getOutputHost() {
//				return cus.getProperty(MAIL_SMTP_SERVER);
//			}
//			@Override
//			public Integer getOutputPort() {
//				return Integer.parseInt(cus.getProperty(MAIL_SMTP_PORT));
//			}
//			@Override
//			public String getOutputUsername() {
//				final boolean useAuthentication = Boolean.valueOf(cus.getProperty(MAIL_USE_AUTHENTICATION));
//				return useAuthentication ? cus.getProperty(MAIL_USERNAME) : NO_AUTHENTICATION_USERNAME;
//			}
//
//			@Override
//			public String getOutputPassword() {
//				return cus.getProperty(MAIL_PASSWORD);
//			}
//
//			@Override
//			public List<String> getOutputFromRecipients() {
//				return asList(cus.getProperty(MAIL_FROM_ADDRESS), MAIL_MULTIPLES_SEPARATOR);
//			}
// 
//
//		};
//	}
}
