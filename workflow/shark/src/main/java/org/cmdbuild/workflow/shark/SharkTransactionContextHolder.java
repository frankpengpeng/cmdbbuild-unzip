/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.shark;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import org.enhydra.shark.api.client.wfmc.wapi.WMSessionHandle;

public class SharkTransactionContextHolder {

	private static final ThreadLocal<Stack<SharkTransactionContext>> THREAD_LOCAL_HOLDER = ThreadLocal.withInitial(() -> new Stack<>());

	public static SharkTransactionContext get() {
		try {
			return THREAD_LOCAL_HOLDER.get().peek();
		} catch (EmptyStackException ex) {
			throw new IllegalStateException("missing shark transaction context", ex);
		}
	}

	public static void push(SharkTransactionContext context) {
		THREAD_LOCAL_HOLDER.get().push(checkNotNull(context));
	}

	public static void pop() {
		THREAD_LOCAL_HOLDER.get().pop();
	}

	public static boolean hasContext() {
		return !THREAD_LOCAL_HOLDER.get().isEmpty();
	}

	public static boolean hasMany() {
		return THREAD_LOCAL_HOLDER.get().size() > 1;
	}

	public static WMSessionHandle handle() {
		return get().getHandle();
	}

	public static List<SharkEvent> getAndRemoveCollectedEvents() {
		return get().getEventCollector().getAndRemoveCollectedEvents();
	}

}
