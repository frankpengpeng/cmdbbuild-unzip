/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import static com.google.common.base.Preconditions.checkNotNull;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;

@Component
public class SysmonRepositoryImpl implements SysmonRepository {

	private final DaoService dao;

	public SysmonRepositoryImpl(DaoService dao) {
		this.dao = checkNotNull(dao);
	}

	@Override
	public void store(SystemStatusLog systemStatusRecord) {
		dao.createOnly(systemStatusRecord);
	}

}
