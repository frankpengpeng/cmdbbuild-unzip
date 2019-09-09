package org.cmdbuild.gis.geoserver.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cmdbuild.config.GisConfiguration;
import org.cmdbuild.gis.geoserver.GeoserverLayerInfo;
import org.cmdbuild.gis.geoserver.GeoserverLayerInfoImpl;
import org.cmdbuild.utils.Command;
import org.dom4j.Document;
import org.dom4j.XPath;

public class GetLayer extends AbstractGeoCommand implements Command<GeoserverLayerInfo> {

	private final String name;

	private static final Pattern storeNamePattern = java.util.regex.Pattern
			.compile("/([^/]+)/(featuretype|coverage)s/[^/]+$");

	public static GeoserverLayerInfo exec(final GisConfiguration configuration, final String name) {
		return new GetLayer(configuration, name).run();
	}

	private GetLayer(final GisConfiguration configuration, final String name) {
		super(configuration);
		this.name = name;
	}

	@Override
	public GeoserverLayerInfo run() {
		String url = String.format("%s/rest/layers/%s", getGeoServerURL(), name);
		Document xmlLayer = get(url);
		String dataStoreName = extractDataStoreName(xmlLayer);
		return new GeoserverLayerInfoImpl(name, dataStoreName);
	}

	private String extractDataStoreName(final Document xmlLayer) {
		final XPath xpath = xmlLayer.createXPath("//layer/resource/atom:link/@href");
		xpath.setNamespaceURIs(AbstractGeoCommand.atomNS);
		final String featureTypeUrl = xpath.valueOf(xmlLayer);
		final Matcher matcher = storeNamePattern.matcher(featureTypeUrl);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

}
