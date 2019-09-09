package org.cmdbuild.test.web.utils;

import static com.google.common.base.MoreObjects.firstNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.openqa.selenium.WebDriver;

import com.google.common.base.MoreObjects;

public interface UITestRule {
	
	boolean pass(UITestContext context);
	
	String ruleName();
	
	String failureMessage();
	
	static UITestRule define(String ruleName,  String failureMessage , Function<UITestContext, Boolean> passFunction) {
		return new UITestRule() {
			
			@Override
			public String ruleName() {
				return ruleName;
			}
			
			@Override
			public boolean pass(UITestContext context) {
				return passFunction.apply(context);
			}

			@Override
			public String failureMessage() {
				return failureMessage;
			}
		};
	};

	//TODO: add doNotSupressInAnyCase category (and rename keep in keepOnly)
	static UITestRule defineClientLogCheckRule(@Nullable String nameOverride,  @Nullable List<String> supressList , @Nullable List<String> keepList, @Nullable Level minSeverity) {
		
		return new UITestRule() {
			
			String failureMessage;
			
			@Override
			public String ruleName() {
				return MoreObjects.firstNonNull(nameOverride, "Client Log Check Rule");
			}
			
			@Override
			public boolean pass(UITestContext context) {
				
				Optional<WebDriver> driver = context.getWebDriver();
				if (! driver.isPresent()) {
					failureMessage = ruleName() + " could not access client log because webdriver was null";
					return false;
				}
				ClientLog log = ClientLog.fetchFromWebDriver(driver.get());
				ClientLog filtered = log.minSeverity(firstNonNull(minSeverity, Level.SEVERE))
						.supress(firstNonNull(supressList, new ArrayList<String>()))
						.keep(firstNonNull(keepList, new ArrayList<>()));
//				filtered.printLogs(null, null);
				if (filtered.success())
					return true;
				else {
					StringBuilder failureMessageBuilder =new StringBuilder("Client Log Check found ");
					failureMessageBuilder.append(filtered.errors()).append(" not allowed log errors");
					filtered.getEntries().stream().forEach(le -> failureMessageBuilder.append(le.getMessage()));
					failureMessage = failureMessageBuilder.toString();
					return false;
				}
			}
			
			@Override
			public String failureMessage() {
				return failureMessage;
			}
		};
	};
	
	
	
	
	
	static class UITestRuleBasicImpl  implements  UITestRule {
		
		protected String ruleName;
		protected String failureMessage;
		protected Function<UITestContext, Boolean> passFunction;
		
		
		@Override
		public String ruleName() {
			return ruleName;
		}
		
		@Override
		public boolean pass(UITestContext context) {
			return passFunction.apply(context);
		}

		@Override
		public String failureMessage() {
			return failureMessage;
		}

		public void setRuleName(String ruleName) {
			this.ruleName = ruleName;
		}
		public UITestRuleBasicImpl withRuleName(String ruleName) {
			this.ruleName = ruleName;
			return this;
		}

		public void setFailureMessage(String failureMessage) {
			this.failureMessage = failureMessage;
		}
		public UITestRuleBasicImpl withFailureMessage(String failureMessage) {
			this.failureMessage = failureMessage;
			return this;
		}

		public void setPassFunction(Function<UITestContext, Boolean> passFunction) {
			this.passFunction = passFunction;
		}
		public UITestRuleBasicImpl withPassFunction(Function<UITestContext, Boolean> passFunction) {
			this.passFunction = passFunction;
			return this;
		}
		
		
	}

}
