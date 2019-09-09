/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import java.util.Collection;
import javax.activation.DataHandler;
import javax.annotation.Nullable;

public interface TranslationExportHelper {

    TranslationExportHelper withLanguages(@Nullable Collection<String> languages);

    TranslationExportHelper withSeparator(@Nullable String separator);

    DataHandler export();

}
