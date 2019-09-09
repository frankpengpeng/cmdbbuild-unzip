/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick;

public interface QuickApplicationContext {

    <T> T getItem(Class<T> type);

    void destroy();

}
