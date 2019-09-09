package org.cmdbuild.email.mta;

import com.google.common.base.Joiner;
import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.transformValues;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import static java.util.stream.Collectors.joining;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.annotation.Nullable;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.tika.Tika;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.EmailQueueConfiguration;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailException;
import static org.cmdbuild.email.EmailStatus.ES_RECEIVED;
import static org.cmdbuild.email.EmailStatus.ES_SENT;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.utils.EmailUtils;
import static org.cmdbuild.email.utils.EmailUtils.formatEmailHeaderToken;
import static org.cmdbuild.email.utils.EmailUtils.parseEmailHeaderToken;
import static org.cmdbuild.email.utils.EmailUtils.parseEmailReferencesHeader;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import static org.cmdbuild.utils.io.CmIoUtils.getCharsetFromContentType;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.log.LogUtils.printStreamFromLogger;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.setCharsetInContentType;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowBiConsumer;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowSupplier;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Component
public class EmailMtaServiceImpl implements EmailMtaService {

    private static final String //
            CONTENT_TYPE_TEXT_HTML = "text/html", //
            CONTENT_TYPE_TEXT_PLAIN = "text/plain", //
            IMAPS = "imaps", //
            MAIL_DEBUG = "mail.debug", //
            MAIL_IMAP_HOST = "mail.imap.host", //
            MAIL_IMAP_PORT = "mail.imap.port", //
            MAIL_IMAPS_HOST = "mail.imaps.host", //
            MAIL_IMAP_SOCKET_FACTORY_CLASS = "mail.imap.socketFactory.class", //
            MAIL_IMAPS_PORT = "mail.imaps.port", //
            MAIL_IMAP_STARTTLS_ENABLE = "mail.imap.starttls.enable", //
            MAIL_SMPT_SOCKET_FACTORY_CLASS = "mail.smpt.socketFactory.class", //
            MAIL_SMPT_SOCKET_FACTORY_FALLBACK = "mail.smtp.socketFactory.fallback", //
            MAIL_SMTP_AUTH = "mail.smtp.auth", //
            MAIL_SMTP_HOST = "mail.smtp.host", //
            MAIL_SMTP_PORT = "mail.smtp.port", //
            MAIL_SMTPS_AUTH = "mail.smtps.auth", //
            MAIL_SMTPS_HOST = "mail.smtps.host", //
            MAIL_SMTPS_PORT = "mail.smtps.port", //
            MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable", //
            MAIL_STORE_PROTOCOL = "mail.store.protocol", //
            MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol", // 
            JAVAX_NET_SSL_SSL_SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";

    private static final String EMAIL_HEADER_MESSAGE_ID = "Message-ID",
            EMAIL_HEADER_REFERENCES = "References",
            EMAIL_HEADER_IN_REPLY_TO = "In-Reply-To";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Tika tika = new Tika();

    private final EmailAccountService emailAccountService;
    private final EmailQueueConfiguration config;

    public EmailMtaServiceImpl(EmailQueueConfiguration config, EmailAccountService emailAccountService) {
        this.emailAccountService = checkNotNull(emailAccountService);
        this.config = checkNotNull(config);

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());//TODO check this
    }

    @Override
    public Email send(Email email) {
        try {
            EmailAccount account;
            if (email.getAccount() == null) {
                account = checkNotNull(emailAccountService.getDefaultOrNull(), "no account supplied for email, and no default account found");
            } else {
                account = emailAccountService.getAccount(email.getAccount());
            }
            return new EmailSender(account).sendEmail(email);
        } catch (Exception ex) {
            throw new EmailException(ex);
        }
    }

    @Override
    public void receive(EmailAccount account, String incomingFolder, @Nullable String receivedFolder, @Nullable String rejectedFolder, Consumer<Email> callback) {
        try {
            new EmailReader(account).receiveMails(incomingFolder, receivedFolder, rejectedFolder, callback);
        } catch (Exception ex) {
            throw new EmailException(ex, "error receiving email for account = %s with folder = %s", account, incomingFolder);
        }
    }

    private void store(EmailAccount account, Email email, String storeToFolder) {
        try {
            new EmailReader(account).storeEmail(email, storeToFolder);
        } catch (Exception ex) {
            throw new EmailException(ex, "error storing email = %s to account = %s folder = %s", email, account, storeToFolder);
        }
    }

    private boolean isEmailTraceEnabled() {
        return logger.isTraceEnabled();
    }

    private static Address toAddress(String emailAddress) {
        try {
            return new InternetAddress(emailAddress);
        } catch (AddressException ex) {
            throw new EmailException(ex);
        }
    }

    private static class MyAuthenticator extends javax.mail.Authenticator {

        private final PasswordAuthentication authentication;

        public MyAuthenticator(String username, String password) {
            authentication = new PasswordAuthentication(username, password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }

    }

    private class EmailReader extends EmailHelper {

        private Store store;

        public EmailReader(EmailAccount account) {
            super(account);
        }

        public void receiveMails(String incomingFolder, @Nullable String receivedFolder, @Nullable String rejectedFolder, Consumer<Email> callback) throws MessagingException {
            createSession();
            try {
                logger.debug("open incoming folder = {}", incomingFolder);
                Folder folder = store.getFolder(checkNotBlank(incomingFolder));
                checkArgument(folder.exists(), "incoming folder not found for name = %s; available folders = %s", incomingFolder, lazyString(rethrowSupplier(() -> list(store.getDefaultFolder().list()).stream().map(Folder::getName).collect(joining(", ")))));
                folder.open(Folder.READ_WRITE);
                List<Message> messages = list(folder.getMessages());
                logger.debug("processing {} incoming messages for account = {} folder = {}", messages.size(), account, folder);
                messages.forEach((m) -> {
                    Email email = null;
                    try {
                        email = new MessageParser(m).parseEmail();
                        logger.debug("processing email = {}", email);
                        callback.accept(email);
                        if (isNotBlank(receivedFolder)) {
                            moveToFolderSafe(m, receivedFolder);
                        }
                    } catch (Exception ex) {
                        logger.warn(marker(), "error processing message = {} email = {}", getMessageInfoSafe(m), email, ex);
                        if (isNotBlank(rejectedFolder)) {
                            moveToFolderSafe(m, rejectedFolder);
                        }
                    }
                });
                logger.info("processed {} incoming messages for account = {} folder = {}", messages.size(), account, folder);
                folder.close(true);
            } finally {
                closeSession();
            }
        }

        public void storeEmail(Email email, String storeToFolder) throws Exception {
            createSession();
            try {
                Message message = buildMessage(email);
                logger.debug("open folder = {}", storeToFolder);
                Folder folder = store.getFolder(checkNotBlank(storeToFolder));
                if (!folder.exists()) {
                    folder.create(Folder.HOLDS_MESSAGES);
                }
                folder.open(Folder.READ_WRITE);
                folder.appendMessages(new Message[]{message});
                message.setFlag(Flags.Flag.RECENT, true);
                folder.close(false);
            } finally {
                closeSession();
            }
        }

        private class MessageParser {

            private final Message message;
            private final List<EmailAttachment> attachments = list(), parts = list();

            public MessageParser(Message message) {
                this.message = checkNotNull(message);
            }

            public Email parseEmail() throws MessagingException, IOException {
                parseEmailPart(message);
                EmailAttachment contentPart = parts.stream().findFirst().get();//TODO check this
                String messageId = parseEmailHeaderToken(getMessageHeader(EMAIL_HEADER_MESSAGE_ID));
                String inReplyTo = parseEmailHeaderToken(getMessageHeader(EMAIL_HEADER_IN_REPLY_TO));
                List<String> references = parseEmailReferencesHeader(getMessageHeader(EMAIL_HEADER_REFERENCES));
                return EmailImpl.builder()
                        .withSentOrReceivedDate(firstNotNull(toDateTime(message.getReceivedDate()), now()))
                        .withStatus(ES_RECEIVED)
                        .withMessageId(messageId)
                        .withAccount(account.getId())
                        .withSubject(message.getSubject())
                        .withFromAddress(parseAddresses(message.getFrom()))
                        .withToAddresses(parseAddresses(message.getRecipients(RecipientType.TO)))
                        .withCcAddresses(parseAddresses(message.getRecipients(RecipientType.CC)))
                        .withBccAddresses(parseAddresses(message.getRecipients(RecipientType.BCC)))
                        .withReplyTo(parseAddresses(message.getReplyTo()))
                        .withContent(contentPart.getDataAsString())
                        .withContentType(contentPart.getMimeType())
                        .withInReplyTo(inReplyTo)
                        .withReferences(references)
                        .withAttachments(attachments)
                        .build();
            }

            private String getMessageHeader(String key) throws MessagingException {
                return EmailMtaServiceImpl.getMessageHeader(message, key);
            }

            private String parseAddresses(@Nullable Address[] list) {
                if (list == null) {
                    return "";
                } else {
                    return stream(list).map(Address::toString).collect(joining(","));
                }
            }

            private void parseEmailPart(Part part) throws MessagingException, IOException {
                logger.debug("parsing email part with content type = {}", part.getContentType());
                if (part instanceof Multipart) {
                    parseEmailMultipart((Multipart) part);
                } else {
                    byte[] data = toByteArray(part.getDataHandler());
                    String contentType = firstNotBlank(part.getContentType(), tika.detect(data));
                    if (part.isMimeType("multipart/*")) {
                        parts.add(EmailAttachmentImpl.builder()
                                .withData(data)
                                .withFileName("dummy")//TODO check this
                                .withMimeType(contentType)
                                .build());
                        parseEmailMultipart(new MimeMultipart(newDataSource(data, contentType)));
                    } else {
                        logger.debug("processing email part with content type = {} disposition = {}", contentType, part.getDisposition());
                        switch (nullToEmpty(part.getDisposition()).toLowerCase()) {
                            case Part.ATTACHMENT:
                                attachments.add(EmailAttachmentImpl.builder()
                                        .withData(data)
                                        .withFileName(part.getFileName())
                                        .withMimeType(contentType)
                                        .build());
                            case Part.INLINE:
                                parts.add(EmailAttachmentImpl.builder()
                                        .withData(data)
                                        .withFileName(part.getFileName())
                                        .withMimeType(contentType)
                                        .build());
                            default:
                                parts.add(EmailAttachmentImpl.builder()
                                        .withData(data)
                                        .withFileName("dummy")//TODO check this
                                        .withMimeType(contentType)
                                        .build());
                        }
                    }
                }
            }

            private void parseEmailMultipart(Multipart multipart) throws MessagingException, IOException {
                for (int i = 0; i < multipart.getCount(); i++) {
                    parseEmailPart(multipart.getBodyPart(i));
                }
            }
        }

        private void moveToFolderSafe(Message message, String targetFolderName) {
            checkNotBlank(targetFolderName);
            try {
                logger.debug("moving message = {} from folder = {} to folder = {}", getMessageInfoSafe(message), message.getFolder(), targetFolderName);
                Folder source = checkNotNull(message.getFolder());
                Folder target = store.getFolder(targetFolderName);
                if (!target.exists()) {
                    target.create(Folder.HOLDS_MESSAGES);
                }
                target.open(Folder.READ_WRITE);
                source.copyMessages(new Message[]{message}, target);
                source.setFlags(new Message[]{message}, new Flags(Flags.Flag.DELETED), true);
                source.expunge();
            } catch (Exception ex) {
                logger.warn(marker(), "error moving message = {} from folder = {} to folder = {}", getMessageInfoSafe(message), message.getFolder(), targetFolderName, ex);
            }
        }

        private void createSession() throws MessagingException {
            checkArgument(account.isImapConfigured(), "cannot open imap connection, imap not configured for account = %s", account);
            Properties properties = new Properties(System.getProperties());
            properties.setProperty(MAIL_STORE_PROTOCOL, account.getImapSsl() ? "imaps" : "imap");
            properties.setProperty(MAIL_IMAP_STARTTLS_ENABLE, (account.getImapStartTls() ? TRUE : FALSE).toString());
            if (account.getImapSsl()) {
                properties.setProperty(MAIL_IMAPS_HOST, account.getImapServer());
                if (isNotNullAndGtZero(account.getImapPort())) {
                    properties.setProperty(MAIL_IMAPS_PORT, account.getImapPort().toString());
                }
                properties.setProperty(MAIL_IMAP_SOCKET_FACTORY_CLASS, JAVAX_NET_SSL_SSL_SOCKET_FACTORY);
            } else {
                properties.setProperty(MAIL_IMAP_HOST, account.getImapServer());
                if (isNotNullAndGtZero(account.getImapPort())) {
                    properties.setProperty(MAIL_IMAP_PORT, account.getImapPort().toString());
                }
            }
            Authenticator authenticator;
            if (account.isAuthenticationEnabled()) {
                authenticator = new MyAuthenticator(account.getUsername(), account.getPassword());
            } else {
                authenticator = null;
            }
            logger.trace("imap server configuration:\n{}", mapToLoggableString(properties));
            logger.debug("open imap connection");
            session = Session.getInstance(properties, authenticator);
            if (isEmailTraceEnabled()) {
                session.setDebugOut(printStreamFromLogger(logger::trace));
                session.setDebug(true);
            }
            store = session.getStore();
            store.connect();
        }

        private void closeSession() {
            if (store != null) {
                logger.debug("close imap connection");
                try {
                    if (store.isConnected()) {
                        store.close();
                    }
                } catch (Exception ex) {
                    logger.warn("error closing receiver mta session", ex);
                }
                store = null;
            }
            session = null;
        }

    }

    private class EmailSender extends EmailHelper {

        private Transport transport;

        public EmailSender(EmailAccount emailAccount) {
            super(emailAccount);
        }

        public Email sendEmail(Email email) throws Exception {
            checkArgument(email.hasDestinationAddress(), "invalid email: no destination address found (TO, CC or BCC)");
            email = prepareEmail(email);
            createSession();
            try {
                logger.debug("send email = {}", email);
                Message message = buildMessage(email);
                logger.debug("send message = {}", getMessageInfoSafe(message));
                transport.sendMessage(message, message.getAllRecipients());
                String messageId = checkNotBlank(parseEmailHeaderToken(getMessageHeader(message, EMAIL_HEADER_MESSAGE_ID)), "error: message sent, but message id header is null");
                logger.debug("sent message, id = {}", messageId);
                email = EmailImpl.copyOf(email)
                        .withSentOrReceivedDate(firstNotNull(toDateTime(message.getSentDate()), now()))
                        .withStatus(ES_SENT)
                        .withMessageId(messageId)
                        .build();
                if (isNotBlank(account.getSentEmailFolder())) {
                    store(account, email, account.getSentEmailFolder());
                }
                return email;
            } finally {
                closeSession();
            }
        }

        private Email prepareEmail(Email email) {
            if (email.getFromRawAddressList().isEmpty()) {
                email = EmailImpl.copyOf(email).withFromAddress(account.getAddress()).build();
            }
            return email;
        }

        private void createSession() throws MessagingException {
            FluentMap<String, String> properties = map(System.getProperties());
            if (isNotNullAndGtZero(config.getSmtpTimeoutSeconds())) {
                String timeout = Integer.toString(config.getSmtpTimeoutSeconds() * 1000);
                properties.put(
                        "mail.smtp.connectiontimeout", timeout,
                        "mail.smtp.timeout", timeout,
                        "mail.smtp.writetimeout", timeout,
                        "mail.smtps.connectiontimeout", timeout,
                        "mail.smtps.timeout", timeout,
                        "mail.smtps.writetimeout", timeout
                );
            }
            properties.put(MAIL_TRANSPORT_PROTOCOL, account.getSmtpSsl() ? "smtps" : "smtp");
            properties.put(MAIL_SMTP_STARTTLS_ENABLE, (account.getSmtpStartTls() ? TRUE : FALSE).toString());
            if (account.getSmtpSsl()) {
                properties.put(MAIL_SMTPS_HOST, account.getSmtpServer());
                if (isNotNullAndGtZero(account.getSmtpPort())) {
                    properties.put(MAIL_SMTPS_PORT, account.getSmtpPort().toString());
                }
                properties.put(MAIL_SMTPS_AUTH, Boolean.toString(account.isAuthenticationEnabled()));
                properties.put(MAIL_SMPT_SOCKET_FACTORY_CLASS, JAVAX_NET_SSL_SSL_SOCKET_FACTORY);
                properties.put(MAIL_SMPT_SOCKET_FACTORY_FALLBACK, FALSE.toString());
            } else {
                properties.put(MAIL_SMTP_HOST, account.getSmtpServer());
                if (isNotNullAndGtZero(account.getSmtpPort())) {
                    properties.put(MAIL_SMTP_PORT, account.getSmtpPort().toString());
                }
                properties.put(MAIL_SMTP_AUTH, Boolean.toString(account.isAuthenticationEnabled()));
            }
            logger.trace("smtp server configuration:\n{}", mapToLoggableString(properties));
            Authenticator authenticator;
            if (account.isAuthenticationEnabled()) {
                authenticator = new MyAuthenticator(account.getUsername(), account.getPassword());
            } else {
                authenticator = null;
            }
            logger.debug("opening smtp connection for account = {}", account);
            session = Session.getInstance(properties.toProperties(), authenticator);
            if (isEmailTraceEnabled()) {
                session.setDebugOut(printStreamFromLogger(logger::trace));
                session.setDebug(true);
            }
            transport = session.getTransport();
            transport.connect();
        }

        private void closeSession() {
            if (transport != null) {
                logger.debug("closing smtp connection for account = {}", account);
                try {
                    if (transport.isConnected()) {
                        transport.close();
                    }
                } catch (Exception ex) {
                    logger.warn("error closing sender mta session", ex);
                }
                transport = null;
            }
            session = null;
        }

    }

    private class EmailHelper {

        protected final EmailAccount account;
        protected Session session;

        public EmailHelper(EmailAccount emailAccount) {
            this.account = checkNotNull(emailAccount);
        }

        protected Message buildMessage(Email email) throws MessagingException, UnsupportedEncodingException {
            Message message = new MimeMessage(session);

            message.addFrom(transform(email.getFromRawAddressList(), EmailMtaServiceImpl::toAddress).toArray(new Address[]{}));

            if (isNotBlank(email.getReplyTo())) {
                message.setReplyTo(new Address[]{toAddress(email.getReplyTo())});
            }

            transformValues(CmMapUtils.<RecipientType, List<String>>map(
                    Message.RecipientType.TO, email.getToRawAddressList(),
                    Message.RecipientType.CC, email.getCcRawAddressList(),
                    Message.RecipientType.BCC, email.getBccRawAddressList()),
                    (a) -> transform(a, EmailMtaServiceImpl::toAddress)).forEach(rethrowBiConsumer((t, a) -> {
                        message.addRecipients(t, a.toArray(new Address[]{}));
                    }));

            message.setSubject(email.getSubject());
            message.setSentDate(toJavaDate(now()));

            String emailContent = nullToEmpty(email.getContent());
            String emailContentType = email.getContentType();

            if (isBlank(getCharsetFromContentType(emailContentType))) {
                emailContentType = setCharsetInContentType(emailContentType, StandardCharsets.UTF_8.name());
            }

            String inReplyTo = email.getInReplyTo();
            List<String> references = email.getReferences();
            if (isNotBlank(inReplyTo) && (references.isEmpty() || !equal(getLast(references), inReplyTo))) {
                references = list(references).with(inReplyTo);
            }
            if (!references.isEmpty()) {
                message.addHeader(EMAIL_HEADER_REFERENCES, references.stream().map(EmailUtils::formatEmailHeaderToken).collect(joining(" ")));
            }
            if (isNotBlank(inReplyTo)) {
                message.addHeader(EMAIL_HEADER_IN_REPLY_TO, formatEmailHeaderToken(inReplyTo));
            }

            if (!email.hasAttachments()) {
                message.setContent(emailContent, emailContentType);
            } else {
                Multipart multipart = new MimeMultipart("mixed");
                MimeBodyPart contentPart = new MimeBodyPart();
                contentPart.setContent(emailContent, emailContentType);
                multipart.addBodyPart(contentPart);
                for (EmailAttachment a : email.getAttachments()) {
                    BodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.setDataHandler(newDataHandler(a.getData(), a.getMimeType(), a.getFileName()));
                    attachmentPart.setFileName(a.getFileName());//TODO is this required? test
                    multipart.addBodyPart(attachmentPart);
                }
                message.setContent(multipart);
            }

            return message;
        }

    }

    private static String getMessageInfoSafe(Message message) {
        try {
            return toStringHelper(message).add("subject", message.getSubject()).add("from", Joiner.on(",").join(message.getFrom())).add("to", Joiner.on(",").join(message.getAllRecipients())).toString();
        } catch (Exception ex) {
            return message.toString();
        }
    }

    private static String getMessageHeader(Message message, String key) throws MessagingException {
        String[] rawValue = message.getHeader(key);
        if (rawValue == null || rawValue.length == 0) {
            return "";
        } else {
            checkArgument(rawValue.length == 1);
            return nullToEmpty(rawValue[0]);
        }
    }

}
