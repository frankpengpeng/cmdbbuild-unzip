package org.cmdbuild.auth;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.LoginUserIdentity.LoginType;
import org.junit.Test;

public class LoginTest {

	@Test
	public void theAtCharacterDiscriminatesBetweenEmailAndUsername() {
		final String STRING_WITHOUT_AT = "anything without the at char";
		final String STRING_WITH_AT = "anything with the @ char"; // "firstname.surname@example.com";

		final LoginUserIdentity usernameLogin = LoginUserIdentity.builder() //
				.withValue(STRING_WITHOUT_AT) //
				.build();
		final LoginUserIdentity emailLogin = LoginUserIdentity.builder() //
				.withValue(STRING_WITH_AT) //
				.build();

		assertThat(usernameLogin.getValue(), is(STRING_WITHOUT_AT));
		assertThat(usernameLogin.getType(), is(LoginType.USERNAME));

		assertThat(emailLogin.getValue(), is(STRING_WITH_AT));
		assertThat(emailLogin.getType(), is(LoginType.EMAIL));
	}

	@Test(expected = NullPointerException.class)
	public void disallowsNullLoginStrings() {
		LoginUserIdentity.builder() //
				.build();
	}

}
