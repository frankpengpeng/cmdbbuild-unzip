/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.utils.random.CmRandomUtils;

public class AliasBuilder {

    private final Set<String> uniqueAliases = CmCollectionUtils.set();
    private final Map<String, String> aliasStore = map();
    private int counter = 0;

    public String buildAlias(String expr) {
        String base = expr.toLowerCase().replaceAll("[^a-z]", "");
        if (StringUtils.isBlank(base)) {
            base = CmRandomUtils.randomId(4);
        }
        if (!base.startsWith("_")) {
            base = "_" + base;
        }
        String alias = base;
        while (uniqueAliases.contains(alias)) {
            alias = base + encodeString(String.valueOf(counter++));
        }
        uniqueAliases.add(alias);
        return alias;
    }

    public String buildAliasAndStore(String expr, Object... key) {
        String alias = buildAlias(expr);
        aliasStore.put(key(key), alias);
        return alias;
    }

    public String getAlias(Object... key) {
        return checkNotBlank(aliasStore.get(key(key)), "alias not found for key = %s", key);
    }

    public void addAliasesFrom(AliasBuilder source) {
        uniqueAliases.addAll(source.uniqueAliases);
        aliasStore.putAll(source.aliasStore);
        counter = source.counter;
    }

}
