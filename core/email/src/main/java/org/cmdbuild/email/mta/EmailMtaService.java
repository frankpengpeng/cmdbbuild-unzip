package org.cmdbuild.email.mta;

import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface EmailMtaService {

    Email send(Email email);

    void receive(EmailAccount account, String incomingFolder, @Nullable String receivedFolder, @Nullable String rejectedFolder, Consumer<Email> callback);

    default List<Email> receive(EmailAccount account, String incomingFolder, @Nullable String receivedFolder) {
        List<Email> list = list();
        receive(account, incomingFolder, receivedFolder, null, list::add);
        return list;
    }

}
