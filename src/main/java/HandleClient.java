import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HandleClient extends Thread {
    private Socket client;
    static String path;

    public HandleClient(Socket client) {
        this.client= client;
    }

    @Override
    public void run() {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            StringBuilder requestBuilder = new StringBuilder();
            String line;

            while(!(line = br.readLine()).isBlank()) {
                requestBuilder.append(line + "\r\n");
            }

            String request = requestBuilder.toString();
            String[] requestsLines = request.split("\r\n");
            String[] requestLine = requestsLines[0].split(" ");
            String method = requestLine[0];
            String path = requestLine[1];
            String version = requestLine[2];
            String host = requestsLines[1].split(" ")[1];
            this.path =path;

            List<String> headers = new ArrayList<>();
            for (int h = 2; h < requestsLines.length; h++) {
                String header = requestsLines[h];
                headers.add(header);
            }

            String accessLog = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s",
                    client.toString(), method, path, version, host, headers.toString());

            Path filePath = getFilePath();

            if (Files.exists(filePath)) {
                // file exist
                String contentType = getContentType(filePath);
                sendResponse(client, "HTTP/1.1 200 OK", contentType, Files.readAllBytes(filePath));
            } else {
                System.out.println("****************");
                // 404
                byte[] notFoundContent = "<h1>Not found :(</h1>".getBytes();
                sendResponse(client, "404 Not Found", "text/html", notFoundContent);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void sendResponse(Socket client, String status, String contentType, byte[] content) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write((status +"\r\n").getBytes());
        clientOutput.write((contentType +"\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write(content);


        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
        client.close();
    }


    public static Path getFilePath(){
        if (path.equals("/")){
            path = "file.html";
        }
        else if (path.equals("/json") || path.equals("/json/")){
            path = "jsonFile";
        }
        else path = "errorFile.html";
        return Paths.get(System.getProperty("user.dir"), "src", "main", "java", path);
    }

    public static String getContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }
}
