package com.example.study.utils;

import com.example.study.controller.ExecuteJobWebSocket;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import javax.websocket.Session;
import java.io.IOException;
import java.io.Serializable;

@Plugin(name = "WebSocketAppender", category = "Core", elementType = "appender", printObject = true)
public class WebSocketAppender extends AbstractAppender {

    @SuppressWarnings("deprecation")
    protected WebSocketAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent logEvent) {
        if("true".equals(ThreadContext.get("AsyncTaskContext"))){
            String sessionId = ThreadContext.get("sessionId");
            Session session = ExecuteJobWebSocket.getSession(sessionId);
            if(session == null){
                //websocket session已断开，移除
                ThreadContext.remove("AsyncTaskContext");
                ThreadContext.remove("sessionId");
                return;
            }
            //发送日志内容到Websocket
            String str = getLayout().toSerializable(logEvent).toString();
            try {
                session.getBasicRemote().sendText(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PluginFactory
    public static WebSocketAppender createAppender(@PluginAttribute("name") String name,
                                              @PluginElement("Filter") final Filter filter,
                                              @PluginElement("Layout") Layout<? extends Serializable> layout,
                                              @PluginAttribute("ignoreExceptions") boolean ignoreExceptions) {
        if (name == null) {
            LOGGER.error("no name defined in conf.");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new WebSocketAppender(name, filter, layout, ignoreExceptions);
    }
}
