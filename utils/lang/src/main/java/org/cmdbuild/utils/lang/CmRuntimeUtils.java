/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Hex;

public class CmRuntimeUtils {

	public static String getCurrentPidOrRuntimeId() {
		Matcher matcher = Pattern.compile("([^@]+)@.*").matcher(ManagementFactory.getRuntimeMXBean().getName());
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return Hex.encodeHexString(Runtime.getRuntime().toString().getBytes());
		}
	}
}
