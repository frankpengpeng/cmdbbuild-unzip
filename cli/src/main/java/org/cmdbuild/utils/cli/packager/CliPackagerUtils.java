/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.packager;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.io.IOUtils.readLines;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.io.CmIoUtils.fetchFileWithCache;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CliPackagerUtils {

    public static void main(String[] args) {
        File warFile = new File(checkNotBlank(args[0]));
        File targetFile = new File(checkNotBlank(args[1]));
        buildExecutableBashFileFromWarFile(warFile, targetFile);
    }

    public static void buildExecutableBashFileFromWarFile(File warFile, File targetFile) {
        try {

            String headerCode = readToString(CliPackagerUtils.class.getResourceAsStream("/org/cmdbuild/utils/cli/packager/cmdbuild_sh_header.sh"));
            byte[] headerBytes = headerCode.getBytes();
            int headerSize = -1;

            while (headerSize != headerBytes.length) {
                headerBytes = (headerCode = headerCode.replaceAll("header_size=[0-9]+", "header_size=" + (headerSize = headerBytes.length))).getBytes();
            }

            Map<String, String> params = readLines(new StringReader(headerCode)).stream().filter(l -> l.matches("[a-z_]+=.*")).map(l -> {
                Matcher matcher = Pattern.compile("([^=]+)=(.*)").matcher(l);
                checkArgument(matcher.find());
                return Pair.of(matcher.group(1), matcher.group(2));
            }).collect(toMap(Pair::getKey, Pair::getValue));

            String javaUrl = checkNotBlank(params.get("java_archive_url")),
                    javaChecksum = checkNotBlank(params.get("java_archive_checksum")),
                    mavenUrl = checkNotBlank(params.get("maven_archive_url")),
                    mavenChecksum = checkNotBlank(params.get("maven_archive_checksum"));

            File javaFile = fetchFileWithCache(javaChecksum, javaUrl),
                    mavenFile = fetchFileWithCache(mavenChecksum, mavenUrl);

            try (FileOutputStream out = new FileOutputStream(targetFile); FileInputStream warIn = new FileInputStream(warFile); FileInputStream javaIn = new FileInputStream(javaFile); FileInputStream mavenIn = new FileInputStream(mavenFile)) {
                out.write(headerBytes);
                IOUtils.copy(javaIn, out);
                IOUtils.copy(mavenIn, out);
                IOUtils.copy(warIn, out);
            }
        } catch (Exception ex) {
            throw runtime(ex, "error building executable cmdbuild from war = %s with target = %s", warFile, targetFile);
        }
    }

}
