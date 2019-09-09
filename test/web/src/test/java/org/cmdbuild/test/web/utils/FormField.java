package org.cmdbuild.test.web.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Use with card details form.
 * Maybe can be specialized to address other use cases.
 *
 */
public interface FormField {

	//TODO check if differentiation beetween text and combos is needed

	public static String CHECKBOX_CHECKED = "[true]";
	public static String CHECKBOX_UNCHECKED = "[false]";

	String getName();
	String getContent();

	//TODO manage nulls?
	default boolean matches(@Nonnull  FormField anotherFormField) {
		if (this.getName().trim().equals(anotherFormField.getName().trim()) && this.getContent().trim().equals(anotherFormField.getContent().trim()))
			return true;
		return false;
	}

	default boolean matchesIgnoreCase(@Nonnull  FormField anotherFormField) {
		if (this.getName().equalsIgnoreCase(anotherFormField.getName().trim()) && this.getContent().equalsIgnoreCase(anotherFormField.getContent().trim()))
			return true;
		return false;
	}

	default boolean matchesFieldOf(@Nonnull FormField anotherFormField) {
		if (getName().trim().equals(anotherFormField.getName().trim()))
			return true;
		return false;
	}
	default boolean matchesFieldButNotContentOf(@Nonnull FormField anotherFormField) {
		if (matchesFieldOf(anotherFormField))
			if (! getContent().trim().equals(anotherFormField.getContent().trim()))
				return true;
		return false;
	}

	default boolean isCheckBoxChecked() {
		return (CHECKBOX_CHECKED.equals(getContent()));
	}

	default boolean isCheckBoxUnChecked() {
		return (CHECKBOX_UNCHECKED.equals(getContent()));
	}

	default boolean isCheckBox() {
		return  (isCheckBoxChecked() || isCheckBoxUnChecked());
	}


	static String asString(FormField ff) {
		return "Form Field (" + ff.getName() +"," + ff.getContent() + ")";
	}
	
	static FormField of(String name, String content) {
		return new FormField() {
			
			
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public String getContent() {
				return content;
			}
		};
	}
	
	/**
	 * @param fieldNames Not null list of field names
	 * @param fieldContents Not null list of field contents
	 * @return
	 * 
	 * fieldNames and fieldContents must have the same dimension, otherwise an IllegalArgumentException is thrown
	 */
	static List<FormField> listOf(@Nonnull List<String> fieldNames, @Nonnull List<String> fieldContents) {
		
		if (fieldNames == null || fieldContents == null || fieldNames.size() != fieldContents.size())
			throw new IllegalArgumentException("Field names and contents must be not null and have the same size");
		ArrayList<FormField> fields = new ArrayList<>();
		for (int f = 0; f < fieldNames.size(); f++) {
			fields.add(of(fieldNames.get(f), fieldContents.get(f)));
		}
		return  fields;
	}

	static String asString(List<FormField> fieldList) {
		StringBuilder builder = new StringBuilder("FieldList: [ ");
		fieldList.stream().forEach(f -> {builder.append(FormField.asString(f));builder.append(" ");});
		return builder.toString();
//		fieldList.stream().forEach(f -> {builder.append("(");builder.append(f.getName());builder.append(" , ");builder.append(f.getContent())});
	}



}
