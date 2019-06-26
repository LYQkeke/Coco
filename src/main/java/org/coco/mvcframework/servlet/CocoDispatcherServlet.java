package org.coco.mvcframework.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.coco.mvcframework.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KEKE on 2019/6/25
 */
public class CocoDispatcherServlet extends HttpServlet{

//    private final Logger LOGGER = LogManager.getLogger(this);

    private List<String> classNames = new ArrayList<>();

    Map<String, Object> beans = new HashMap<>();

    Map<String, CocoInvoker> handerMap = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        String uri = req.getRequestURI(); // example/keke/query;
        String context = req.getContextPath(); // example/
        String path = uri.replace(context,""); // keke/query

//        System.out.println(uri);
//        System.out.println(path);
//        LOGGER.info("----------Http Request-----------");
        System.out.println("----------Http Request-----------");

        if (handerMap.containsKey(path)){
//            LOGGER.info("----------Good URL Mapping-----------");
            System.out.println("----------Good URL Mapping-----------");
            CocoInvoker invoker = handerMap.get(path);
            // 方法调用点
            Method method = invoker.method;
            Object[] args = hand(req, resp, method);
            try {
                method.invoke(invoker.object,args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }else {
//            LOGGER.info("----------Bad URL Mapping-----------");
            System.out.println("----------Bad URL Mapping-----------");
        }

    }

    @Override
    // 在tomcat启动的时候初始化，实例化的bean ioc容器
    public void init(ServletConfig config) throws ServletException {
        // 扫描所有.class文件
        System.out.println("---init---");
        basePackageScan("org.coco");

        // 对 class 进行实例化
        doInstance();

        // 进行注入
        doAutowired();

        // UrlMapping 建立URL到方法的映射 HandlerMapping
        doUrlMapping();
//        super.init(config);
    }

    public void doInstance(){
        for (String className : classNames){
            // 去掉 .class
            String realName = className.substring(0,className.length()-6);

            try {
                Class<?> clazz = Class.forName(realName);

                if (clazz.isAnnotationPresent(CocoController.class)){
                    // 控制类
                    Object instance = clazz.newInstance(); // map.put(??? ,instance);
                    CocoRequestMapping mapping = clazz.getAnnotation(CocoRequestMapping.class);
                    String key = mapping.value();
                    // 将实例放入Map中
                    beans.put(key,instance);
                }else if (clazz.isAnnotationPresent(CocoService.class)){
                    // 服务类
                    Object instace = clazz.newInstance();
                    CocoService service = clazz.getAnnotation(CocoService.class);
                    String key = service.value();
                    // 将实例放入Map中
                    beans.put(key,instace);
                }else{
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void doAutowired(){
        for (Map.Entry<String,Object> entry: beans.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(CocoController.class)){
                Field[] fields = clazz.getDeclaredFields();
                for (Field field:fields){
                    if (field.isAnnotationPresent(CocoAutowired.class)){
                        CocoAutowired autowired = field.getAnnotation(CocoAutowired.class);
                        String key = autowired.value();
                        Object bean = beans.get(key);

                        // 打开域的访问权限
                        field.setAccessible(true);

                        try {
                            field.set(instance,bean);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else {
                        continue;
                    }
                }
            }else {
                continue;
            }
        }
    }

    private void doUrlMapping(){
        for (Map.Entry<String, Object> entry:beans.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(CocoController.class)){
                CocoRequestMapping mapping1 = clazz.getAnnotation(CocoRequestMapping.class);
                String classPath = mapping1.value();

                Method[] methods = clazz.getMethods();
                for (Method method: methods){
                    if (method.isAnnotationPresent(CocoRequestMapping.class)){
                        CocoRequestMapping mapping2 = method.getAnnotation(CocoRequestMapping.class);
                        String methodPath = mapping2.value();

                        String requestPath = classPath + methodPath; // */query -> method
                        handerMap.put(requestPath, new CocoInvoker(instance,method));

                    }else {
                        continue;
                    }
                }
            }else {
                continue;
            }
        }
    }

    private Object[] hand(HttpServletRequest request, HttpServletResponse response, Method method){
        // 拿到当前执行的方法有哪些参数
        Class<?>[] paramClazzs = method.getParameterTypes();
        // 根据参数的个数，new一个参数的数组，将方法里面的所有参数赋值到args来
        Object[] args = new Object[paramClazzs.length];
        args[0] = request;
        args[1] = response;
        if (args.length>2){
            for (int i = 2; i < args.length; i++) {
                // 暂时设置，每个参数智能有一个 parameter annotation
                CocoRequestParam rp = (CocoRequestParam) method.getParameterAnnotations()[i][0];
                args[i] = request.getParameter(rp.value());
            }
        }
        return args;
    }

    private void basePackageScan(String basePackage){
        // 扫描编译好的类路径 *.class
        URL url = this.getClass().getClassLoader().getResource(basePackage.replace(".","/"));
        String fileStr = url.getFile();
        File root = new File(fileStr);
        String[] filesStr = root.list();
        for (String path: filesStr){
            File filePath = new File(fileStr+"/"+path);
            if (filePath.isDirectory()){
                basePackageScan(basePackage+"."+filePath.getName());
            }else{
                classNames.add(basePackage+"."+filePath.getName());
            }
        }
    }

    private static class CocoInvoker{
        Object object;
        Method method;

        public CocoInvoker(Object object, Method method) {
            this.object = object;
            this.method = method;
        }
    }
}
