package org.coco.mvcframework.controller;

import org.coco.mvcframework.annotation.CocoAutowired;
import org.coco.mvcframework.annotation.CocoController;
import org.coco.mvcframework.annotation.CocoRequestMapping;
import org.coco.mvcframework.annotation.CocoRequestParam;
import org.coco.mvcframework.service.KekeService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by KEKE on 2019/6/25
 */
@CocoController
@CocoRequestMapping("/keke")
public class KekeController {

    @CocoAutowired("KekeServiceImpl") // map.get("KekeServiceImpl");
    private KekeService kekeService;

    @CocoRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @CocoRequestParam("name") String name, @CocoRequestParam("age") String age){
        try {
            PrintWriter writer = response.getWriter();
            String res = kekeService.query(name,age);
            writer.write(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
