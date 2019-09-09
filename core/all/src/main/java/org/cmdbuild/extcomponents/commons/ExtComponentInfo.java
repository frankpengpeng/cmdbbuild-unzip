/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.extcomponents.commons;

public interface ExtComponentInfo {

    long getId();

    String getName();

    boolean getActive();

    String getDescription();

    String getExtjsComponentId();

    String getExtjsAlias();

}
