package com.fiveplay.localhost;

import android.content.Context;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public final class LocalHttpServer {
    private final Context context;
    private volatile boolean running;
    private ServerSocket serverSocket;
    private Thread thread;
    public static final int PORT = 8000;

    public LocalHttpServer(Context context) { this.context = context.getApplicationContext(); }

    public void start() throws IOException {
        if (running) return;
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), PORT));
        running = true;
        thread = new Thread(() -> {
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    new Thread(() -> handle(client), "fiveplay-http-client").start();
                } catch (IOException e) {
                    if (running) e.printStackTrace();
                }
            }
        }, "fiveplay-local-http");
        thread.start();
    }

    private void handle(Socket socket) {
        try (Socket s = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.US_ASCII));
             OutputStream out = s.getOutputStream()) {
            String first = in.readLine();
            if (first == null) return;
            String[] parts = first.split(" ");
            String path = parts.length > 1 ? parts[1] : "/";
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) { }

            if (!"/".equals(path) && !"/index.html".equals(path)) {
                write(out, 404, "Not Found", "text/plain; charset=utf-8", "Not found".getBytes(StandardCharsets.UTF_8));
                return;
            }
            byte[] body;
            try (InputStream asset = context.getAssets().open("index.html");
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] chunk = new byte[8192]; int n;
                while ((n = asset.read(chunk)) != -1) buffer.write(chunk, 0, n);
                body = buffer.toByteArray();
            }
            write(out, 200, "OK", "text/html; charset=utf-8", body);
        } catch (Exception ignored) { }
    }

    private void write(OutputStream out, int code, String reason, String type, byte[] body) throws IOException {
        String headers = "HTTP/1.1 " + code + " " + reason + "\r\n"
            + "Content-Type: " + type + "\r\n"
            + "Content-Length: " + body.length + "\r\n"
            + "Connection: close\r\n"
            + "Cache-Control: no-store\r\n"
            + "Cross-Origin-Opener-Policy: same-origin\r\n"
            + "Cross-Origin-Embedder-Policy: require-corp\r\n"
            + "Origin-Agent-Cluster: ?1\r\n"
            + "X-Content-Type-Options: nosniff\r\n\r\n";
        out.write(headers.getBytes(StandardCharsets.US_ASCII));
        out.write(body);
        out.flush();
    }

    public void stop() {
        running = false;
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) { }
    }
}
