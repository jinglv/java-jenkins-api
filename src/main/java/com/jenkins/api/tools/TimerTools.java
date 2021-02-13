package com.jenkins.api.tools;

/**
 * @author jinglv
 * @date 2021/02/12
 */
public class TimerTools {
    public static void wait(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
