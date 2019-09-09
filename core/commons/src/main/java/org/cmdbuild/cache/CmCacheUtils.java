/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cache;

import static com.google.common.base.Functions.identity;
import com.google.common.collect.Ordering;
import java.util.Map;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.cache.CmCacheStats;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;

public class CmCacheUtils {

	public static Map<String, CmCacheStats> getStats(Map<String, CmCache> caches) {
		return caches.values().stream().map((c) -> new CmCacheStatsImpl(c)).sorted(Ordering.natural().onResultOf(CmCacheStats::getName)).collect((toMap(CmCacheStats::getName, identity())));
	}

	private static class CmCacheStatsImpl implements CmCacheStats {

		private final String name;
		private final long size;
		private final long memSize;

		public CmCacheStatsImpl(CmCache cache) {
			name = cache.getName();
			size = cache.estimatedSize();
			memSize = cache.approxMemSize();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public long getSize() {
			return size;
		}

		@Override
		public long getEstimateMemSize() {
			return memSize;
		}

	}
}
