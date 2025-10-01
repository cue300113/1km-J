package com.sky.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {

        System.out.println("设置用户ID: " + id + ", 线程: " + Thread.currentThread().getName());threadLocal.set(id);
    }

    public static Long getCurrentId() {
        Long id = threadLocal.get();
        System.out.println("设置用户ID: " + id + ", 线程: " + Thread.currentThread().getName());return threadLocal.get();
    }

    public static void removeCurrentId() {

        System.out.println("移除用户ID, 线程: " + Thread.currentThread().getName());threadLocal.remove();
    }

}
