/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.scanner;

import java.util.List;

public interface QuickItemBindingConfig {

    QuickItemBindingType getType();

    String getSingleItemId();

    List<String> getMultipleItemId();

}
