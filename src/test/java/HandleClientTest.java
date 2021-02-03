import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HandleClientTest {

    @BeforeAll
    static void setUp() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
                new HandleClient(serverSocket.accept()).start();


        } catch (
                UnknownHostException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void run() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/")).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String expected = Files.readString(Paths.get("src/main/resources/index.html"))
                    .replaceAll("\\s", "");
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expected, response.body().replaceAll("\\s", "")));
            Assertions.assertEquals(200, response.statusCode());
        }catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getFilePath() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/json")).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String expected = Files.readString(Paths.get("src/main/resources/details.json"))
                    .replaceAll("\\s", "");
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expected, response.body().replaceAll("\\s", "")));
            Assertions.assertEquals(200, response.statusCode());
        }catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    void getContentType() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/wro")).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String expected = Files.readString(Paths.get("src/main/resources/error.html"))
                    .replaceAll("\\s", "");
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expected, response.body().replaceAll("\\s", "")));
            Assertions.assertEquals(404, response.statusCode());
        }catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}