package org.cmdbuild.auth;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.cmdbuild.auth.login.ClientRequestAuthenticator;
import org.cmdbuild.auth.login.header.HeaderAuthenticator;
import org.junit.Test;
import org.cmdbuild.auth.login.RequesthAuthenticatorResponse;
import org.cmdbuild.auth.login.header.HeaderAuthenticatorConfiguration;
import org.cmdbuild.auth.login.AuthRequestInfo;

public class HeaderAuthenticatorTest {

	private static final String USER_HEADER_NAME = "X-Username";
	private static final String USER_HEADER_VALUE = "username";
	private static final HeaderAuthenticatorConfiguration CONFIGURATION = new HeaderAuthenticatorConfiguration() {

		@Override
		public String getHeaderAttributeName() {
			return USER_HEADER_NAME;
		}
	};

	private final AuthRequestInfo request = mock(AuthRequestInfo.class);

	@Test(expected = NullPointerException.class)
	public void configurationCannotBeNull() {
		@SuppressWarnings("unused")
		final HeaderAuthenticator authenticator = new HeaderAuthenticator(null);
	}

	@Test
	public void doesNotAuthenticateIfTheHeaderIsNotPresent() {
		final HeaderAuthenticator authenticator = new HeaderAuthenticator(CONFIGURATION);

		final RequesthAuthenticatorResponse response = authenticator.authenticate(request);
		assertThat(response, is(nullValue()));
	}

	@Test
	public void doesAuthenticateIfTheHeaderIsPresent() {
		final HeaderAuthenticator authenticator = new HeaderAuthenticator(CONFIGURATION);

		when(request.getHeader(USER_HEADER_NAME)).thenReturn(USER_HEADER_VALUE);

		final RequesthAuthenticatorResponse response = authenticator.authenticate(request);
		assertThat(response.getLogin().getValue(), is(USER_HEADER_VALUE));
		assertThat(response.getRedirectUrl(), is(nullValue()));

		verify(request, only()).getHeader(USER_HEADER_NAME);
	}
}
