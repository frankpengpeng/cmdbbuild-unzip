/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.job;

public interface EmailReceiverService {

	void receiveEmailsWithConfig(EmailReaderConfig config);

}
