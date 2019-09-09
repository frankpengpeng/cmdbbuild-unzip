package org.cmdbuild.config;

import java.util.Map;
import org.cmdbuild.config.GraphConfiguration;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigService;

import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.config.api.NamespacedConfigService;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.graph")
public final class GraphConfigurationImpl implements GraphConfiguration {

	@ConfigValue(key = "enabled", defaultValue = TRUE)
	private boolean isEnabled;

	@ConfigValue(key = "enableEdgeTooltip", defaultValue = TRUE)
	private boolean isEdgeTooltipEnabled;

	@ConfigValue(key = "enableNodeTooltip", defaultValue = TRUE)
	private boolean isNodeTooltipEnabled;

	@ConfigValue(key = "displayLabel", defaultValue = "none")
	private String displayLabel;

	@ConfigValue(key = "edgeColor", defaultValue = "#3D85C6")
	private String edgeColor;

	@ConfigValue(key = "baseLevel", defaultValue = "1")
	private int baseLevel;

	@ConfigValue(key = "clusteringThreshold", defaultValue = "100")
	private int clusteringThreshold;

	@ConfigValue(key = "spriteDimension", defaultValue = "20")
	private int spriteDimension;

	@ConfigValue(key = "stepRadius", defaultValue = "60")
	private int stepRadius;

	@ConfigValue(key = "viewPointDistance", defaultValue = "50")
	private int viewPointDistance;

	@ConfigValue(key = "viewPointHeight", defaultValue = "50")
	private int viewPointHeight;

	@ConfigService
	private NamespacedConfigService config;

	@Override
	public Map<String, String> getConfig() {
		return config.getAllOrDefaults();
	}

}
