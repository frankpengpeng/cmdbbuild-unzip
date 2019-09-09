/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import java.io.File;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.cmdbuild.ecql.EcqlId;
import org.cmdbuild.ecql.EcqlSource;
import org.cmdbuild.ecql.utils.EcqlUtils;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import org.cmdbuild.utils.crypto.Cm3PasswordUtils;
import org.cmdbuild.utils.crypto.CmLegacyPasswordUtils;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;

public class CryptoCommandRunner extends AbstractCommandRunner {

    public CryptoCommandRunner() {
        super("crypto", "manage encrypted stuff (encrypt/decrypt password, etc)");
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("hashfile", true, "hash file content");
        options.addOption("legacy", "use legacy password algorithm");
        options.addOption("cm3easy", "use cm3easy password algorithm (aes, symmetric)");
        options.addOption("cm3", "use cm3 password algorithm (PBKDF2, one way)");
        options.addOption("ecqlid", "use ecqlid algorytm (es: -ecqlid -encrypt CLASS_ATTRIBUTE,InternalEmployee,Email)");
        options.addOption("encrypt", true, "encrypt password");
        options.addOption("decrypt", true, "decrypt password");
        return options;
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        if (cmd.hasOption("legacy")) {
            if (cmd.hasOption("encrypt")) {
                String encryptedPsw = CmLegacyPasswordUtils.encrypt(checkNotNull(cmd.getOptionValue("encrypt")));
                System.err.println("=== BEGIN encrypted value ===\n" + encryptedPsw + "\n=== END encrypted value ===");
            } else if (cmd.hasOption("decrypt")) {
                String decryptedPsw = CmLegacyPasswordUtils.decrypt(checkNotNull(cmd.getOptionValue("decrypt")));
                System.err.println("=== BEGIN decrypted value ===\n" + decryptedPsw + "\n=== END decrypted value ===");
            }
        } else if (cmd.hasOption("cm3easy")) {
            if (cmd.hasOption("encrypt")) {
                String encryptedPsw = Cm3EasyCryptoUtils.encryptValue(checkNotNull(cmd.getOptionValue("encrypt")));
                System.err.println("=== BEGIN encrypted value ===\n" + encryptedPsw + "\n=== END encrypted value ===");
            } else if (cmd.hasOption("decrypt")) {
                String decryptedPsw = Cm3EasyCryptoUtils.decryptValue(checkNotNull(cmd.getOptionValue("decrypt")));
                System.err.println("=== BEGIN decrypted value ===\n" + decryptedPsw + "\n=== END decrypted value ===");
            }
        } else if (cmd.hasOption("cm3")) {
            if (cmd.hasOption("encrypt")) {
                String encryptedPsw = Cm3PasswordUtils.hash(checkNotNull(cmd.getOptionValue("encrypt")));
                System.err.println("=== BEGIN encrypted value ===\n" + encryptedPsw + "\n=== END encrypted value ===");
            }
        } else if (cmd.hasOption("ecqlid")) {
            if (cmd.hasOption("encrypt")) {
                List<String> values = Splitter.on(",").splitToList(cmd.getOptionValue("encrypt"));
                String encodedId = EcqlUtils.buildEcqlId(EcqlSource.valueOf(values.get(0).toUpperCase()), values.subList(1, values.size()).toArray(new String[]{}));
                System.err.println("encoded ecql id = " + encodedId);
            } else if (cmd.hasOption("decrypt")) {
                String encodedId = cmd.getOptionValue("decrypt");
                EcqlId ecqlId = EcqlUtils.parseEcqlId(encodedId);
                System.err.println("parsed ecql id = " + ecqlId);
            }
        } else if (cmd.hasOption("hashfile")) {
            String fileName = cmd.getOptionValue("hashfile");
            byte[] data = toByteArray(new File(fileName));
            String hash = hash(data);
            System.err.printf("file hash = %s\n", hash);
        } else {
            for (String value : cmd.getArgList()) {
                if (Cm3EasyCryptoUtils.isEncrypted(value)) {
                    System.err.println("=== BEGIN decrypt input ===\n" + value + "\n=== END decrypt input, BEGIN decrypt output ===\n" + Cm3EasyCryptoUtils.decryptValue(value) + "\n=== END decrypt output ===");
                } else {
                    System.err.println("=== BEGIN encrypt input ===\n" + value + "\n=== END encrypt input, BEGIN encrypt output ===\n" + Cm3EasyCryptoUtils.encryptValue(value) + "\n=== END encrypt output ===");
                }
            }
        }
    }

}
