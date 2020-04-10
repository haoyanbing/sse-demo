package com.haoyanbing.sse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * @author haoyanbing
 * @since 2020/4/10
 */
@RestController
public class PushController {

    @RequestMapping("/push")
    public void getStreamDataImprove(HttpServletResponse response) throws IOException {
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

}
