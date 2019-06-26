package org.coco.mvcframework.service.impl;

import org.coco.mvcframework.annotation.CocoService;
import org.coco.mvcframework.service.KekeService;

/**
 * Created by KEKE on 2019/6/25
 */
@CocoService("KekeServiceImpl") // map.put("KekeServiceImpl", new JamesServiceImpl())
public class KekeServiceImpl implements KekeService{
    @Override
    public String query(String name, String age) {
        return "name: "+ name +", age: " + age ;
    }
}
