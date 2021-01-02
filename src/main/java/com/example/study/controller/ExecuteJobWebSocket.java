package com.example.study.controller;

import com.example.study.utils.AsyncTask;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws")
@Component
public class ExecuteJobWebSocket {

    private static Logger logger = LoggerFactory.getLogger(ExecuteJobWebSocket.class);

    private static Map<String,Session> sessionMap = new ConcurrentHashMap<>();

    public static Session getSession(String sessionId){
        return sessionMap.get(sessionId);
    }


    /**
     * 在WebSocket中使用bean，必须用static + set注入的方式
     */
    private static AsyncTask asyncTask;

    @Autowired
    public void setAsyncTask(AsyncTask asyncTask) {
        ExecuteJobWebSocket.asyncTask = asyncTask;
    }


    @OnOpen
    public void onOpen(Session session) {
        sessionMap.put(session.getId(),session);
        logger.info("新客户端连接了 {}",session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessionMap.remove(session.getId());
        logger.info("客户端断开 {}", session.getId());
    }


    @OnMessage
    public void onMessage(Session session,String message) {
        if("executeJob".equals(message)){
            asyncTask.testTask(session.getId());
        }
    }

}
