/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Streams.stream;
import static java.lang.String.format;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.client.rest.model.SimpleClassData;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.client.rest.model.ClassData;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.client.rest.api.ClassApi;
import org.cmdbuild.client.rest.model.AttributeData;
import org.cmdbuild.client.rest.model.AttributeRequestData;
import org.cmdbuild.client.rest.model.SimpleAttributeData;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toPrettyJson;

public class ClasseApiImpl extends AbstractServiceClientImpl implements ClassApi {

	public ClasseApiImpl(RestWsClient restClient) {
		super(restClient);
	}

	@Override
	public ClassData getById(String classeId) {
		logger.debug("get classe for classeId = {}", classeId);
		JsonNode response = get("classes/" + encodeUrlPath(checkNotBlank(classeId, "classeId cannot be blank"))).asJackson();
		return responseToClassData(response);
	}

	@Override
	public String getRawJsonById(String classId) {
		return toPrettyJson(get("classes/" + encodeUrlPath(checkNotBlank(classId, "classeId cannot be blank"))).asJackson().get("data"));
	}

	@Override
	public List<ClassData> getAll() {
		logger.debug("get all classes");
		JsonNode response = get("classes/").asJackson();
		return stream(response.get("data")).map(element -> fromJson(element, SimpleClassData.class)).collect(toList());
	}

	@Override
	public ClassApiWithClassData create(ClassData data) {
		JsonNode response = post("classes/", classDataToRequest(data)).asJackson();
		data = responseToClassData(response);
		return new ClasseApiWithClassDataImpl(data);
	}

	@Override
	public ClassApiWithClassData update(ClassData data) {
		JsonNode response = put("classes/" + encodeUrlPath(data.getName()), classDataToRequest(data)).asJackson();
		data = responseToClassData(response);
		return new ClasseApiWithClassDataImpl(data);
	}

	@Override
	public ClassApiWithClassData update(String classId, String rawJsonData) {
		JsonNode response = put("classes/" + encodeUrlPath(checkNotBlank(classId)), checkNotBlank(rawJsonData)).asJackson();
		ClassData data = responseToClassData(response);
		return new ClasseApiWithClassDataImpl(data);
	}

	@Override
	public ClassApi deleteById(String classeId) {
		delete("classes/" + encodeUrlPath(checkNotBlank(classeId, "classeId cannot be blank")));
		return this;
	}

	private Object classDataToRequest(ClassData data) {
		return map("name", data.getName(),
				"description", data.getDescription(),
				"prototype", data.isSuperclass(),
				"active", data.isActive(),
				"parent", data.getParentId(),
				"type", data.getType());
	}

	private ClassData responseToClassData(JsonNode response) {
		return fromJson(response.get("data"), SimpleClassData.class);
	}

	@Override
	public List<AttributeData> getAttributes(String classId) {
		JsonNode response = get("classes/" + encodeUrlPath(checkNotBlank(classId, "classId cannot be blank")) + "/attributes/").asJackson();
		return stream(response.get("data")).map(element -> fromJson(element, SimpleAttributeData.class)).collect(toList());//TODO deserialize metadata, filter
	}

	@Override
	public ClassApiWithAttrData createAttr(String classId, AttributeRequestData data) {
		JsonNode response = post(format("classes/%s/attributes/", encodeUrlPath(checkNotBlank(classId, "classId cannot be blank"))), attrDataToRequest(data)).asJackson();
		AttributeData responseData = responseToAttrData(response);
		return new ClassApiWithAttrDataImpl(responseData);
	}

	@Override
	public ClassApiWithAttrData updateAttr(String classId, AttributeRequestData data) {
		JsonNode response = put(format("classes/%s/attributes/%s", encodeUrlPath(checkNotBlank(classId, "classId cannot be blank")), encodeUrlPath(data.getName())), attrDataToRequest(data)).asJackson();
		AttributeData responseData = responseToAttrData(response);
		return new ClassApiWithAttrDataImpl(responseData);
	}

	@Override
	public AttributeData getAttr(String classId, String attrId) {
		JsonNode response = get("classes/" + encodeUrlPath(checkNotBlank(classId, "classId cannot be blank")) + "/attributes/" + encodeUrlPath(checkNotBlank(attrId, "attrId cannot be blank"))).asJackson();
		return responseToAttrData(response);
	}

	private AttributeData responseToAttrData(JsonNode jsonNode) {
//		try {
		return fromJson(jsonNode.get("data"), SimpleAttributeData.class);
//		} catch (IOException ex) {
//			throw runtime(ex);
//		}
	}

	private Object attrDataToRequest(AttributeRequestData data) {
		return map("type", data.getType(),
				"name", data.getName(),
				"description", data.getDescription(),
				"showInGrid", data.getShowInGrid(),
				"domainName", data.getDomainName(),
				"unique", data.getUnique(),
				"mandatory", data.getRequired(),
				"active", data.getActive(),
				"index", data.getIndex(),
				"defaultValue", data.getDefaultValue(),
				"group", data.getGroup(),
				"precision", data.getPrecision(),
				"scale", data.getScale(),
				"targetClass", data.getTargetClass(),
				"length", data.getLength(),
				"editorType", data.getEditorType(),
				"lookupTypeName", data.getLookupType(),
				"filter", data.getFilter(),
				"mode", data.getMode().name(),
				"metadata", data.getMetadata(),
				"classOrder", data.getClassOrder(),
				"ipType", data.getIpType());
	}

	@Override
	public ClassApi deleteAttr(String classId, String attrId) {
		delete("classes/" + encodeUrlPath(checkNotBlank(classId, "classId cannot be blank")) + "/attributes/" + encodeUrlPath(checkNotBlank(attrId, "attrId cannot be blank")));
		return this;
	}

	private class ClasseApiWithClassDataImpl implements ClassApiWithClassData {

		private final ClassData classe;

		public ClasseApiWithClassDataImpl(ClassData classe) {
			this.classe = checkNotNull(classe);
		}

		@Override
		public ClassApi then() {
			return ClasseApiImpl.this;
		}

		@Override
		public ClassData getClasse() {
			return classe;
		}

	}

	private class ClassApiWithAttrDataImpl implements ClassApiWithAttrData {

		private final AttributeData classe;

		public ClassApiWithAttrDataImpl(AttributeData classe) {
			this.classe = checkNotNull(classe);
		}

		@Override
		public ClassApi then() {
			return ClasseApiImpl.this;
		}

		@Override
		public AttributeData getAttr() {
			return classe;
		}

	}

}
