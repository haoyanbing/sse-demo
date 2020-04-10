package com.haoyanbing.sse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author haoyanbing
 * @since 2020/4/10
 */
@RestController
public class PushController {

    public static Map<String, SseEmitter> map = new ConcurrentHashMap<>();

    /**
     * 原生实现
     * @param response
     * @throws IOException
     */
    @RequestMapping("/push")
    public void push(HttpServletResponse response) throws IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("utf-8");
        String s = "retry:1000\n";
        PrintWriter pw = response.getWriter();
        while (!pw.checkError()) {
            try {
                Thread.sleep(3000);
                pw.write(s + "data:received message, time：" + LocalDateTime.now().toLocalTime() + "\n\n");
                pw.flush();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Spring sseEmitter 实现
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/connect")
    public SseEmitter connect(@RequestParam("id") String id) throws IOException {
        SseEmitter sseEmitter = new SseEmitter(0L);
        map.put(id, sseEmitter);
        sseEmitter.send(SseEmitter.event().comment("comment"));
        return sseEmitter;
    }

    /**
     * 发送消息
     * @param id
     * @param message
     * @throws IOException
     */
    @GetMapping("send")
    public void send(@RequestParam("id") String id, @RequestParam("message") String message) throws IOException {
        if (map.containsKey(id)) {
            try {
                map.get(id).send(SseEmitter.event().reconnectTime(2000).data(message));
            } catch (Exception e) {
                map.remove(id);
            }
        }
    }

}
