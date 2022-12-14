package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {

    private static final String WWW = "/Users/aleks/dev/tel-http-server/www";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server started!");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream(), StandardCharsets.UTF_8));
                     PrintWriter writer = new PrintWriter(socket.getOutputStream())) {

                    while (!reader.ready()) ;

                    String firstLine = reader.readLine();
                    String fileName = firstLine.split(" ")[1];
                    while (reader.ready()) {
                        System.out.println(reader.readLine());
                    }

                    Path filePath = Paths.get(WWW, fileName);
                    if (Files.exists(filePath)) {
                        writer.println("HTTP/1.1 200 OK");
                        writer.println("Content-Type: text/html; charset=utf-8");
                        writer.println();
                        Files.newBufferedReader(filePath).transferTo(writer);
                    } else {
                        writer.println("HTTP/1.1 404 OK");
                        writer.println("Content-Type: text/html; charset=utf-8");
                        writer.println();
                        writer.println("<h1>File not found</h1>");
                    }
                    writer.flush();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
