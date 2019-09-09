/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import java.util.Map;
import org.cmdbuild.dao.beans.Card;

public interface UserCardService {

	Card getUserCard(String classId, long cardId);

	Card createCard(String classId, Map<String, Object> values);

	Card updateCard(String classId, long cardId, Map<String, Object> values);

	void deleteCard(String classId, long cardId);

	UserCardAccess getUserCardAccess(String classId);

}
