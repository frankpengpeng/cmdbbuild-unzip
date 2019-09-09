/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import java.util.List;

public interface JobRepository {

	List<JobData> getAllJobs();

	JobData getOne(long jobId);

	JobData getOneByCode(String code);

	JobData create(JobData data);

	JobData update(JobData data);

	void delete(long id);
}
