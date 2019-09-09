package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.auth.login.LoginUserIdentity;
import static org.cmdbuild.auth.login.LoginUserIdentity.LoginType.EMAIL;
import static org.cmdbuild.auth.login.LoginUserIdentity.LoginType.USERNAME;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.data.filter.utils.CmdbFilterUtils.noopFilter;
import static org.cmdbuild.data.filter.utils.CmdbSorterUtils.noopSorter;

public interface UserRepository {

	@Nullable
	LoginUser getUserOrNull(LoginUserIdentity login);

	@Nullable
	LoginUser getUserByIdOrNull(Long userId);

	default LoginUser getUser(LoginUserIdentity identity) {
		return checkNotNull(getUserOrNull(identity), "user not found for identity = %s", identity);
	}

	@Nullable
	default LoginUser getUserByEmailOrNull(String email) {
		return getUserOrNull(LoginUserIdentity.builder().withType(EMAIL).withValue(email).build());
	}

	@Nullable
	default LoginUser getUserByUsernameOrNull(String username) {
		return getUserOrNull(LoginUserIdentity.builder().withType(USERNAME).withValue(username).build());
	}

	default LoginUser getUserByUsername(String username) {
		return checkNotNull(getUserByUsernameOrNull(username), "user not found for username = %s", username);
	}

	PagedElements<UserData> getMany(CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit);

	PagedElements<UserData> getAllWithRole(long roleId, CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit);

	default List<UserData> getAllWithRole(long roleId) {
		return getAllWithRole(roleId, noopFilter(), noopSorter(), null, null).elements();
	}

	PagedElements<UserData> getAllWithoutRole(long roleId, CmdbFilter filter, CmdbSorter sorter, @Nullable Long offset, @Nullable Long limit);

	UserData get(long id);

	UserData create(UserData user);

	UserData update(UserData user);

}
