/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli;

import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import org.junit.Ignore;
import org.junit.Test;

public class ConsoleTest {

	@Test
	@Ignore("only useful for manual testing")
	public void testBackspace() {
		System.out.println("1 4 3");
		sleepSafe(1000);
		System.out.print("\033[1A");
		System.out.print("\033[2K");
		System.out.println("      ");
		sleepSafe(1000);
		System.out.print("\033[1A");
		System.out.print("\033[2K");
		System.out.println("1 2 3");
	}
}
