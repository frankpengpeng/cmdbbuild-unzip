/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.random;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeUuid;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;

public class CmRandomUtils {

	public static final int DEFAULT_RANDOM_ID_SIZE = 24;

	public static String randomId() {
		return randomId(DEFAULT_RANDOM_ID_SIZE);
	}

	public static String randomId(int size) {
		byte[] data = new byte[size * 2];
		ThreadLocalRandom.current().nextBytes(data);
		return hash(data, size);
	}

	public static String randomIdFromUuid() {
		return encodeUuid(UUID.randomUUID());
	}
}
