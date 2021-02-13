package com.jenkins.api;

import com.jenkins.api.client.ClientApi;
import com.jenkins.api.tools.TimerTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

/**
 * @author jinglv
 * @date 2021/02/12
 */
public class RunJenkins {
    public static final Logger logger = LoggerFactory.getLogger(RunJenkins.class);

    public static void main(String[] args) throws Exception {
        ClientApi clientApi = new ClientApi();
        // 60秒超时
        int maxWaitTime = 60;
        // 获取当前lastBuildNumber
        int oldBuildNumber = clientApi.getLastBuildNumber();
        //启动任务
        clientApi.runBuild();
        logger.info("启动任务：" + clientApi.getJobName());
        // 获取最新任务编号
        int newBuildNumber = clientApi.getLastBuildNumber();
        long start = System.currentTimeMillis();
        while (newBuildNumber < oldBuildNumber) {
            TimerTools.wait(2);
            newBuildNumber = clientApi.getLastBuildNumber();
            //判断超时
            long end = System.currentTimeMillis();
            if (end - start > maxWaitTime * 1000) {
                throw new TimeoutException(maxWaitTime + "秒超时");
            }
        }
        logger.info("新任务编号：" + newBuildNumber);
        //等待任务执行完毕
        boolean buildingStatus = clientApi.isJobBuilding(newBuildNumber);
        start = System.currentTimeMillis();
        while (buildingStatus) {
            TimerTools.wait(2);
            logger.info("任务" + clientApi.getJobName() + "正在运行 ...");
            buildingStatus = clientApi.isJobBuilding(newBuildNumber);
            //判断超时
            long end = System.currentTimeMillis();
            if (end - start > maxWaitTime * 1000) {
                throw new TimeoutException(maxWaitTime + "秒超时");
            }
        }
        //任务运行完毕，获取任务结果
        String buildResult = clientApi.getJobResult(newBuildNumber);
        logger.info("任务" + clientApi.getJobName() + "运行完毕，最新任务编号：" + newBuildNumber + ", 运行结果：" + buildResult);
    }
}
