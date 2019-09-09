/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler.utils;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import static java.lang.String.format;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class JobKeyUtils {

	public static String serializeJobKey(String group, String name) {
		checkNotBlank(group);
		checkNotBlank(name);
		checkArgument(!group.contains(".") && !name.contains("."));
		return format("%s.%s", group, name);
	}

	public static Pair<String, String> deserializeJobKey(String key) {
		checkNotBlank(key);
		List<String> split = Splitter.on('.').splitToList(key);
		checkArgument(split.size() == 2, "invalid key syntax for str = %s", key);
		return Pair.of(checkNotBlank(split.get(0)), checkNotBlank(split.get(1)));
	}

}
