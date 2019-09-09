/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.PrintWriter;
import java.io.StringWriter;
import static java.lang.String.format;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CmExceptionUtils {

    public static Object lazyString(Supplier<String> supplier) {
        checkNotNull(supplier);
        return new Object() {
            @Override
            public String toString() {
                return supplier.get();
            }
        };
    }

    public static RuntimeException cause(Throwable ex) {
        return extractCause(ex);
    }

    public static RuntimeException extractCause(Throwable ex) {
        if (ex.getCause() != null) {
            return toRuntimeException(ex.getCause());
        } else {
            return toRuntimeException(ex);
        }
    }

    public static RuntimeException runtime(Throwable ex) {
        return toRuntimeException(ex);
    }

    public static RuntimeException runtime(Throwable ex, String message, Object... args) {
        return new RuntimeException(format(message, args), ex);
    }

    public static RuntimeException runtime(String message, Object... args) {
        return new RuntimeException(format(message, args));
    }

    public static Exception inner(Exception ex) {
        if ((ex instanceof ExecutionException || ex instanceof InvocationTargetException) && (ex.getCause() != null) && (ex.getCause() instanceof Exception)) {
            ex = (Exception) ex.getCause();
        }
        return ex;
    }

    public static RuntimeException toRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            return ((RuntimeException) ex);
        } else {
            return new RuntimeException(ex);
        }
    }

    public static String exceptionToMessage(Throwable ex) {
        List<String> messages = list();
        while (ex != null) {
            messages.add(ex.toString());
            ex = ex.getCause();
        }
        return Joiner.on(", caused by: ").join(messages);
    }

    public static String exceptionToUserMessage(Throwable ex) {
        while (ex != null) {
            String str = ex.toString();
            Matcher matcher = Pattern.compile(".*CM *: *(.*)").matcher(str);//TODO improve pattern
            if (matcher.find()) {
                return matcher.group(1);
            }
            ex = ex.getCause();
        }
        return "generic error";//TODO
    }

    public static String printStackTrace(Throwable ex) {
        StringWriter writer = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(writer)) {
            ex.printStackTrace(printWriter);
        }
        return writer.toString();
    }

    public static UnsupportedOperationException unsupported(String message, Object... args) {
        return new UnsupportedOperationException(format(message, args));
    }

    public static @Nullable
    <T extends Throwable> T extractExceptionOrNull(Throwable ex, Class<T> type) {
        if (type.isInstance(ex)) {
            return type.cast(ex);
        } else {
            if (ex.getCause() == null) {
                return null;
            } else {
                return extractExceptionOrNull(ex.getCause(), type);
            }
        }
    }
}
