/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.utils.quick.test.cp1;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.inject.Named;

@Named
public class MyComponentTwo {
    
    public MyComponentTwo(MyComponentOne in){
        checkNotNull(in);
    }

}
