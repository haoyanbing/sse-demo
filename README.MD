# Server-Sent Events Demo程序
## 简介
所谓的SSE(Sever-Sent Events)，就是浏览器向服务器发送了一个HTTP请求，
然后保持长连接，服务器可以不断单向地向浏览器推送消息。

SSE是HTML5规范中的一个组成部分，主流的浏览器都支持（IE除外）。

## 与其他方案对比


## 服务端示例
```java
// 原生实现
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

// Spring的SseEmitter实现
public static Map<String, SseEmitter> map = new ConcurrentHashMap<>();

@RequestMapping(value = "/connect")
public SseEmitter connect(@RequestParam("id") String id) throws IOException {
    SseEmitter sseEmitter = new SseEmitter(0L);
    map.put(id, sseEmitter);
    return sseEmitter;
}
// 发送消息
@GetMapping("send")
public void send(@RequestParam("id") String id, @RequestParam("message") String message) throws IOException {
    if (map.containsKey(id)) {
        map.get(id).send(message);
    }
}
```
## 客户端示例
```javascript
if (typeof (EventSource) !== "undefined") {
    var source = new EventSource('push');
    source.addEventListener('message', function (e) {
        // 接收到服务端的消息
    });

    source.addEventListener('open', function (e) {
        // 连接打开
    });

    source.addEventListener('error', function (e) {
        // 连接发生异常
    });
} else {
    // 您的浏览器不支持 Server-Sent Events!
}
```
## 使用demo
1. 以springboot方式启动demo程序
2. 浏览器打开：http://localhost:8080
3. 填写id
4. 点击 `start` 按钮开始建立连接
5. 点击 `stop` 按钮断开连接