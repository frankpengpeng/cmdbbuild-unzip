package org.cmdbuild.test.web.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class UITestContext implements ArtificialTestDelayListener{

	//TODO not used yet. Finally remove if not used
	private @Nonnull  String id = (new Date()).getTime() + "-" + random.nextInt() ;
	private String description = "UIDefaultTextContext";
	
	private  LocalDateTime start = LocalDateTime.now();
	private  LocalDateTime end = null;
	private  long artificialTestDelaySum = 0;
	
	private List<UITestRule> rules = new ArrayList<>(); 
	private @Nullable WebDriver webDriver;
	private Set<String> touchedClasses = new HashSet<>();
	private Set<String> createdClasses = new HashSet<>();
	private List<String> plainCleanUpQueries = new ArrayList<>();
	private List<TouchedAttribute> touchedAttributes = new ArrayList<>();
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
//	public UITestContext () {
//		//TODO generate some really unique id...
//		id =  LocalDateTime.now().toString() + "-" + Math.random();
//		
//	}
	
	private static Random random = new Random((new Date().getTime()));

	protected static class TouchedAttribute {

		String className;
		String attributeName;

		TouchedAttribute() {}
		TouchedAttribute(String className, String attributeName) {this.className = className; this.attributeName = attributeName;}

		static TouchedAttribute from (String className, String attributeName) {
			return new TouchedAttribute(className, attributeName);
		}
	}
	
	public UITestContext withDescription(String description) {
		this.description =description;
		return this;
	}
	
	public UITestContext withRule(UITestRule rule) {
		rules.add(rule);
		return this;
	}
	
	public UITestContext withWebDriver(WebDriver driver) {
		this.webDriver = driver;
		return this;
	}
	
	public UITestContext withTouchedClass(String touchedClass) {
		touchedClasses.add(touchedClass);
		return this;
	}
	public UITestContext withTouchedClasses(Collection<String> touchedClasses) {
		this.touchedClasses.addAll(touchedClasses);
		return this;
	}

	public UITestContext withCreatedClass(String createdClass) {
		createdClasses.add(createdClass);
		return this;
	}

	@Deprecated //TODO implement (deprecation is used as a mere warning)
	public UITestContext withTouchedAttribute(String className, String attributeName) {
		touchedAttributes.add(TouchedAttribute.from(className, attributeName));
		return this;
	}


	/**
	 * @param plainQueryToBeExecutedAfterTestEndsOrdFails plain text query to be executed after test ends or fails.<br/>Order of addition is followed when executing more than one query
	 * @return this object (in place modified)
	 */
	public UITestContext withCleanUpPlainQuery(String plainQueryToBeExecutedAfterTestEndsOrdFails) {
		this.plainCleanUpQueries.add(plainQueryToBeExecutedAfterTestEndsOrdFails);
		return this;
	}

	@Override
	public void notifyArtificialDelayTime(long millis) {
		artificialTestDelaySum += millis;
		
	} 
	
	public boolean passesRules() {
			Optional<UITestRule> failedRule = rules.stream().filter(r -> ! r.pass(this)).findFirst();
			if (failedRule.isPresent()) {
				logger.warn("Test: {} failed to pass rule: {} . Message: {} " , getDescription() , failedRule.get().ruleName(), failedRule.get().failureMessage());
				return false;
			} else {
				return true;
			}
	}
	
	
	/**
	 * sets the end timestamp of test (if not done before)
	 * Can be called several times
	 * 
	 * @return same istance (convenience method)
	 */
	public UITestContext testEnd() {
		if (end == null)
			end = LocalDateTime.now();
		return this;
	}
	
	/**
	 * @return raw millisecons between start and end without considering the artificial time that some operations may require
	 */
	public long getFullTimeSpent() {
		return ChronoUnit.MILLIS.between(start, end);
	}
	
	/**
	 * @return milliseconds test took to complete less artificial introduced delays  
	 */
	public long getMeasuredTimeSpent() {
		return ChronoUnit.MILLIS.between(start, end) - artificialTestDelaySum;
	}
	
	
	//GETTERS AND SETTERS

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime stop) {
		this.end = stop;
	}

//	public String getId() {
//		return id;
//	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String decription) {
		this.description = decription;
	}

	public long getArtificialTestTimeSum() {
		return artificialTestDelaySum;
	}

	public void setArtificialTestDelaySum(long artificialTestTimeSum) {
		this.artificialTestDelaySum = artificialTestTimeSum;
	}

	public Optional<WebDriver> getWebDriver() {
		return Optional.ofNullable(webDriver);
	}

	
	public void addTouchedClass(@Nonnull String touchedClass) {
		touchedClasses.add(touchedClass);
	}

	public @Nonnull Set<String> getTouchedClasses() {
		return touchedClasses;
	}
	public @Nonnull Set<String> getCreatedClasses() {
		return createdClasses;
	}

	public void setTouchedClasses(@Nonnull Set<String> touchedClasses) {
		this.touchedClasses = touchedClasses;
	}


	public List<String> getCleanupQueries() {
		return plainCleanUpQueries;
	}
}
