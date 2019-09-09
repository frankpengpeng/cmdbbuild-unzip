package org.cmdbuild.email;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface EmailAccountService {

    EmailAccount create(EmailAccount account);

    EmailAccount update(EmailAccount account);

    List<EmailAccount> getAll();

    @Nullable
    EmailAccount getAccountOrNull(String name);

    EmailAccount getAccount(long accountId);

    void delete(long accountId);

    @Nullable
    EmailAccount getDefaultOrNull();

    @Nullable
    default String getDefaultCodeOrNull() {
        return Optional.ofNullable(getDefaultOrNull()).map(EmailAccount::getName).orElse(null);
    }

    default EmailAccount getAccount(String name) {
        return checkNotNull(getAccountOrNull(name), "account not found for name = %s", name);
    }

    @Nullable
    default EmailAccount getAccountOrDefaultOrNull(String... accounts) {
        return getAccountOrDefaultOrNull(list(accounts));
    }

    @Nullable
    default EmailAccount getAccountOrDefaultOrNull(Collection<String> accounts) {
        return accounts.stream().filter(StringUtils::isNotBlank).map(this::getAccountOrNull).filter(not(isNull())).findFirst().orElseGet(this::getDefaultOrNull);
    }

}
