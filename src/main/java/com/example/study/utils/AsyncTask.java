package com.example.study.utils;

import com.example.study.controller.ExecuteJobWebSocket;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;

@Component
public class AsyncTask {

    private static Logger logger = LoggerFactory.getLogger(AsyncTask.class);

    @Async
    public void testTask(String sessionId){
        ThreadContext.put("AsyncTaskContext","true");
        ThreadContext.put("sessionId",sessionId);

        logger.info("开始执行testTask");
        for(int i=0;i<10;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("执行中... {}",i);
        }
        logger.info("testTask执行成功！！");


        //执行后清除ThreadContext
        ThreadContext.remove("AsyncTaskContext");
        ThreadContext.remove("sessionId");
    }

}
