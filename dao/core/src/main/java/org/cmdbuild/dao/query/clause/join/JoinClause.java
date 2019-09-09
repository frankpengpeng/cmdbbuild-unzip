package org.cmdbuild.dao.query.clause.join;

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.cmdbuild.dao.query.clause.where.OrWhereClause.or;
import static org.cmdbuild.dao.query.clause.where.TrueWhereClause.trueWhereClause;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;
import org.cmdbuild.dao.query.clause.QueryDomain;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.view.DataView;

public class JoinClause {

	private final Alias targetAlias;
	private final Alias domainAlias;
	private final boolean domainHistory;
	private final boolean left;

	private final Map<Classe, WhereClause> targetsWithFilters;
	private final Set<QueryDomain> queryDomains;
	private final Map<QueryDomain, Iterable<Classe>> disabled;

	private JoinClause(JoinClauseBuilder builder) {
		this.targetAlias = builder.targetAlias;
		this.domainAlias = builder.domainAlias;
		this.targetsWithFilters = builder.targetsWithFilters;
		this.queryDomains = builder.queryDomains;
		this.domainHistory = builder.domainHistory;
		this.left = builder.left;
		this.disabled = builder.disabled;
	}

	public Alias getTargetAlias() {
		return targetAlias;
	}

	public Alias getDomainAlias() {
		return domainAlias;
	}

	public boolean hasTargets() {
		return !targetsWithFilters.isEmpty();
	}

	public Iterable<Entry<Classe, WhereClause>> getTargets() {
		return targetsWithFilters.entrySet();
	}

	public boolean hasQueryDomains() {
		return !queryDomains.isEmpty();
	}

	public Iterable<QueryDomain> getQueryDomains() {
		return queryDomains;
	}

	public boolean isDomainHistory() {
		return domainHistory;
	}

	public boolean isLeft() {
		return left;
	}

	public Map<QueryDomain, Iterable<Classe>> getDisabled() {
		return disabled;
	}

	public static JoinClauseBuilder newJoinClause(DataView viewForRun, DataView viewForBuild, Classe source) {
		return new JoinClauseBuilder(viewForRun, viewForBuild, source);
	}

	@Override
	public String toString() {
		return "JoinClause{" + "targetAlias=" + targetAlias + ", domainAlias=" + domainAlias + '}';
	}

	public static class JoinClauseBuilder implements Builder<JoinClause> {

		private final Logger logger = LoggerFactory.getLogger(getClass());

		private final DataView viewForRun;
		private final DataView viewForBuild;
		private final Classe source;

		private Alias targetAlias;
		private Alias domainAlias;
		private final Map<Classe, WhereClause> targetsWithFilters;
		private final Set<QueryDomain> queryDomains;
		private boolean domainHistory;
		private boolean left;
		private final Map<QueryDomain, Iterable<Classe>> disabled;

		private JoinClauseBuilder(DataView viewForRun, DataView viewForBuild, Classe source) {
			Validate.notNull(source);
			this.viewForRun = viewForRun;
			this.viewForBuild = viewForBuild;
			this.source = source;
			this.queryDomains = newHashSet();
			this.targetsWithFilters = newHashMap();
			this.disabled = newHashMap();
		}

		public JoinClauseBuilder withDomain(Domain domain, Alias domainAlias) {
			throw new UnsupportedOperationException("BROKEN - TODO");
//			Validate.notNull(domain, "domain is null");
//			Validate.notNull(domainAlias, "domain alias is null");
//			if (domain instanceof DomainHistory) {
//				domain = ((DomainHistory) domain).getCurrent();
//				domainHistory = true;
//			}
//			if (domain instanceof AnyDomain) {
//				addAllDomains();
//			} else {
//				addQueryDomain(new QueryDomain(domain, Source._1));
//				addQueryDomain(new QueryDomain(domain, Source._2));
//			}
//			this.domainAlias = domainAlias;
//			return this;
		}

		public JoinClauseBuilder withDomain(QueryDomain queryDomain, Alias domainAlias) {
			Validate.notNull(queryDomain);
			Validate.notNull(domainAlias);
			throw new UnsupportedOperationException();
//			addQueryDomain(queryDomain);
//			this.domainAlias = domainAlias;
//			return this;
		}

		public JoinClauseBuilder withTarget(Classe target, Alias targetAlias) {
			throw new UnsupportedOperationException("BROKEN - TODO");
//			Validate.notNull(target);
//			Validate.notNull(targetAlias);
//			/**
//			 * Add here the where condition for privilege filters on rows?
//			 */
//			if (target instanceof AnyClass) {
//				addAnyTarget();
//			} else {
//				addTarget(target);
//			}
//			this.targetAlias = targetAlias;
//			return this;
		}

		public JoinClauseBuilder left() {
			this.left = true;
			return this;
		}

		@Override
		public JoinClause build() {
			return new JoinClause(this);
		}

		private void addAllDomains() {
			throw new UnsupportedOperationException("BROKEN - TODO");
//			List<Domain> list = stream(viewForBuild.getDomains()).filter(Domain::isActive).filter(domainFor(source)).collect(toList());
//			logger.debug("add allo domains = {}", list);
//			list.forEach((domain) -> {
//				if (domain(disabled1(), not(contains(source.getName()))).apply(domain)) {
//					QueryDomain queryDomain = new QueryDomain(domain, Source._1);
//					addQueryDomain(queryDomain);
//					Collection<Classe> targets = newHashSet();
//					domain.getTargetClass().getLeaves().forEach(input -> {
//						if (!input.isActive()) {
//							targets.add(input);
//						}
//					});
//					domain.getDisabledTargetDescendants().forEach(input -> {
//						Classe disabledClasse = viewForBuild.findClasse(input);
//						if (disabledClasse == null) {
//							logger.warn("unable to find class by name = {} that is disabled in domain = {}", input, domain);
//						} else {
//							targets.add(disabledClasse);
//						}
//					});
//					disabled.put(queryDomain, targets);
//				}
//				if (domain(disabled2(), not(contains(source.getName()))).apply(domain)) {
//					QueryDomain queryDomain = new QueryDomain(domain, Source._2);
//					addQueryDomain(queryDomain);
//					Collection<Classe> targets = newHashSet();
//					domain.getSourceClass().getLeaves().forEach(input -> {
//						if (!input.isActive()) {
//							targets.add(input);
//						}
//					});
//					domain.getDisabledSourceDescendants().forEach(input -> {
//						Classe disabledClasse = viewForBuild.findClasse(input);
//						if (disabledClasse == null) {
//							logger.warn("unable to find class by name = {} that is disabled in domain = {}", input, domain);
//						} else {
//							targets.add(disabledClasse);
//						}
//					});
//					disabled.put(queryDomain, targets);
//				}
//			});
		}

//		private void addQueryDomain(QueryDomain queryDomain) {
//			if (queryDomain.getSourceClass().isAncestorOf(source)) {
//				queryDomains.add(queryDomain);
//			}
//		}
//
//		private void addAnyTarget() {
//			for (QueryDomain queryDomain : queryDomains) {
//				addTargetLeaves(queryDomain.getTargetClass());
//			}
//		}
//
//		private void addTarget(Classe target) {
//			for (QueryDomain queryDomain : queryDomains) {
//				if (queryDomain.getTargetClass().isAncestorOf(target)) {
//					addTargetLeaves(target);
//				}
//			}
//		}
//
//		private void addTargetLeaves(Classe targetDomainClass) {
//			for (Classe leaf : targetDomainClass.getLeaves()) {
//				Iterable<? extends WhereClause> whereClauses = viewForRun.getAdditionalFiltersFor(leaf);
//				targetsWithFilters.put(leaf, isEmpty(whereClauses) ? trueWhereClause() : or(whereClauses));
//			}
//		}
	}
}
