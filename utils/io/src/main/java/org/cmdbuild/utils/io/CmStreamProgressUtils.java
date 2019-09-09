package org.cmdbuild.utils.io;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.currentTimeMillis;
import java.util.function.Consumer;
import org.apache.commons.io.FileUtils;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.getAvailableLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmStreamProgressUtils {

    public static InputStream listenToStreamProgress(InputStream in, Consumer<StreamProgressEvent> listener) {
        return listenToStreamProgress("stream", "stream progress", in, listener);
    }

    public static String detailedProgressDescription(long processed, long total, long beginTimestampMillis) {
        return String.format("%s  %s / %s (%s)", progressDescription(processed, total), FileUtils.byteCountToDisplaySize(processed), total == 0 ? "unknown" : FileUtils.byteCountToDisplaySize(total), progressDescriptionEta(processed, total, beginTimestampMillis));
    }

    public static String progressDescriptionEta(long processed, long total, long beginTimestampMillis) {
        long remaining = total - processed;
        long elapsed = System.currentTimeMillis() - beginTimestampMillis,
                eta = processed == 0 ? -1 : (elapsed * remaining / processed);
        return String.format("eta %s", eta > 0 ? CmDateUtils.toUserDuration(eta) : "unknown");
    }

    public static String progressDescription(long processed, long total) {
        return total == 0 ? "    " : String.format("%s%%", Math.round(processed / (double) total * 1000) / 10d);
    }

    public static InputStream listenToStreamProgress(String id, String description, InputStream in, Consumer<StreamProgressEvent> listener) {
        checkNotBlank(id);
        checkNotBlank(description);
        checkNotNull(in);
        return new InputStream() {

            private final Logger logger = LoggerFactory.getLogger(getClass());

            private long estimateTotal = 0;
            private long count = 0;
            private double perc = 0;
            private Long lastNotifyTimestamp = null, beginTimestamp = null;
            private Double lastNotifyPerc = null;

            @Override
            public int read() throws IOException {
                if (beginTimestamp == null) {
                    beginTimestamp = currentTimeMillis();
                }
                int res = in.read();
                if (res >= 0) {
                    count++;
                }
                notifyEvent();
                return res;
            }

            @Override
            public void close() throws IOException {
                in.close();
            }

            @Override
            public int available() throws IOException {
                return in.available();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (beginTimestamp == null) {
                    beginTimestamp = currentTimeMillis();
                }
                int res = in.read(b, off, len);
                if (res > 0) {
                    count += res;
                }
                notifyEvent();
                return res;
            }

            private void notifyEvent() {
                checkAvailable();
                perc = estimateTotal == 0 ? 0 : count / ((double) estimateTotal);
                long now = currentTimeMillis(), elapsed = now - beginTimestamp;
                if ((lastNotifyTimestamp == null || (now - lastNotifyTimestamp) > 1000 || (lastNotifyPerc != null && perc - lastNotifyPerc > 10) || count == estimateTotal) && (perc == 0 || lastNotifyPerc == null || lastNotifyPerc != perc)) {
                    lastNotifyTimestamp = now;
                    lastNotifyPerc = perc;
                    logger.debug("perc = {} estimateTotal = {} count = {} elapsed = {}", perc, estimateTotal, count, elapsed);
                    listener.accept(new StreamProgressEventImpl(id, description, perc, count, estimateTotal, elapsed, beginTimestamp));
                }
            }

            private void checkAvailable() {
                try {
                    estimateTotal = getAvailableLong(in) + count;
                } catch (IOException ex) {
                    throw runtime(ex);
                }
            }

        };
    }
}
