import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {

    private static final String DEFAULT_STATIC_DIR = "simple-http-server/src/static";
    private static final int PORT = 8081;

    private static String staticDir;

    public static void main(String[] args) throws IOException {
        staticDir = (args.length > 0) ? args[0] : DEFAULT_STATIC_DIR;

        Path dirPath = Paths.get(staticDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            System.out.println("Created directory: " + dirPath.toAbsolutePath());
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started at http://localhost:" + PORT);
            System.out.println("Serving files from: " + dirPath.toAbsolutePath());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (InputStream inputStream = clientSocket.getInputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
             OutputStream outputStream = clientSocket.getOutputStream()) {

            while (!in.ready());

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            System.out.println("Request: " + requestLine);

            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                System.out.println(line);
            }

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                sendResponse(outputStream,
                             400,
                             "text/plain",
                             "Bad Request".getBytes());
                return;
            }

            String urlPath = requestParts[1];
            String fileName = extractFileName(urlPath);

            if (fileName == null) {
                sendResponse(outputStream,
                             400,
                             "text/plain",
                             "Bad Request: Invalid file name".getBytes());
                return;
            }

            Path filePath = Paths.get(staticDir, fileName).normalize();
            Path staticPath = Paths.get(staticDir).normalize();

            if (!filePath.normalize().startsWith(staticPath.normalize())) {
                sendResponse(outputStream,
                             403,
                             "text/plain",
                             "Forbidden".getBytes());
                return;
            }

            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                byte[] fileContent = Files.readAllBytes(filePath);
                String contentType = determineContentType(fileName);
                sendResponse(outputStream, 200, contentType, fileContent);
                System.out.println("File served: " + filePath);
            } else {
                String notFoundMessage = "<h1>404 Not Found</h1>";
                sendResponse(outputStream,
                             404,
                             "text/html; charset=UTF-8",
                             notFoundMessage.getBytes());
                System.out.println("File not found: " + filePath);
            }

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private static String extractFileName(String urlPath) {
        int queryIndex = urlPath.indexOf('?');
        if (queryIndex != -1) {
            urlPath = urlPath.substring(0, queryIndex);
        }

        if (urlPath.equals("/") || urlPath.isEmpty()) {
            return "index.html";
        }

        int lastSlashIndex = urlPath.lastIndexOf('/');
        String fileName = urlPath.substring(lastSlashIndex + 1);

        return fileName.isEmpty() ? null : fileName;
    }

    private static String determineContentType(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html; charset=UTF-8";
        } else if (fileName.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        } else if (fileName.endsWith(".json")) {
            return "application/json; charset=UTF-8";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain; charset=UTF-8";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".ico")) {
            return "image/x-icon";
        } else {
            return "application/octet-stream";
        }
    }

    private static void sendResponse(OutputStream outputStream,
                                     int statusCode,
                                     String contentType,
                                     byte[] content) throws IOException {
        String statusMessage = getStatusMessage(statusCode);

        StringBuilder responseHeaders = new StringBuilder();
        responseHeaders.append("HTTP/1.1 ").append(statusCode).append(" ")
                       .append(statusMessage).append("\r\n");
        responseHeaders.append("Content-Type: ").append(contentType).append("\r\n");
        responseHeaders.append("Content-Length: ").append(content.length).append("\r\n");
        responseHeaders.append("Connection: close\r\n");
        responseHeaders.append("\r\n");

        outputStream.write(responseHeaders.toString().getBytes());
        outputStream.write(content);
        outputStream.flush();
    }

    private static String getStatusMessage(int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }
}
