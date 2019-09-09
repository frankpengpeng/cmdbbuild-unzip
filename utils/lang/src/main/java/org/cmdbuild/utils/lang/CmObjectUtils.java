/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static com.google.common.base.Objects.equal;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmConvertUtils.extractCmPrimitiveIfAvailable;

public class CmObjectUtils {

    public static boolean cmEquals(@Nullable Object one, @Nullable Object two) {
        return equal(one, two) || equal(extractCmPrimitiveIfAvailable(one), extractCmPrimitiveIfAvailable(two));
    }

}
