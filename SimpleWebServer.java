import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public final class SimpleWebServer {
    private static final int PORT = 1989;
    private static final StudentProfile PROFILE = new StudentProfile(
            "Sara Yasser Hammed Helmmy", "5240505092",
            "Öğrenmeye ve kendimi geliştirmeye meraklı bir öğrenciyim. Java programlama dilini ve web teknolojilerini öğreniyor, edindiğim bilgileri küçük projeler geliştirerek pekiştiriyorum.");

    public static void main(String[] args) throws IOException {
        PageRenderer renderer = new StudentPageRenderer(PROFILE);
        HttpRequestHandler handler = new HttpRequestHandler(renderer);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Sunucu http://localhost:" + PORT + " adresinde çalışıyor.");
            while (true) {
                Socket client = serverSocket.accept();
                Thread worker = new Thread(() -> {
                    try (client) { handler.handle(client); }
                    catch (IOException e) { System.err.println("Bağlantı işlenemedi: " + e.getMessage()); }
                });
                worker.start();
            }
        }
    }

    /** Öğrenci sayfasında gösterilecek bilgileri tutar. */
    static final class StudentProfile {
        final String fullName, studentNumber, biography;
        StudentProfile(String fullName, String studentNumber, String biography) {
            this.fullName = fullName; this.studentNumber = studentNumber; this.biography = biography;
        }
    }

    /** HTML sayfası üreten sınıfların uyguladığı küçük arayüz. */
    interface PageRenderer { String render(); }

    /** Öğrenci bilgilerini biçimlendirilmiş HTML sayfasına dönüştürür. */
    static final class StudentPageRenderer implements PageRenderer {
        private final StudentProfile profile;
        StudentPageRenderer(StudentProfile profile) { this.profile = profile; }
        @Override public String render() {
            return "<!doctype html><html lang=\"tr\"><head><meta charset=\"UTF-8\">" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><title>Kişisel Sayfa</title><style>" +
                    "body{font-family:Arial,sans-serif;background:#f3f7fb;color:#263238;margin:0;padding:40px}" +
                    ".card{max-width:760px;margin:auto;background:white;padding:32px;border-radius:16px;box-shadow:0 8px 24px #16324f22}" +
                    "h1{color:#145da0;font-size:2.2rem}h2{color:#d35400;font-size:1.5rem}" +
                    "p{font-size:1.08rem;line-height:1.9;color:#37474f}.tag{color:#16826c;font-weight:bold}</style></head><body>" +
                    "<main class=\"card\"><h1>" + escapeHtml(profile.fullName) + "</h1>" +
                    "<h2>Öğrenci numarası: " + escapeHtml(profile.studentNumber) + "</h2>" +
                    "<p><span class=\"tag\">Hakkımda</span><br>" + escapeHtml(profile.biography) + "</p></main></body></html>";
        }
        private String escapeHtml(String text) {
            return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                    .replace("\"", "&quot;").replace("'", "&#39;");
        }
    }

    /** Basit bir HTTP isteğini okur ve HTML yanıtı gönderir. */
    static final class HttpRequestHandler {
        private final PageRenderer renderer;
        HttpRequestHandler(PageRenderer renderer) { this.renderer = renderer; }
        void handle(Socket client) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
            String requestLine = reader.readLine();
            if (requestLine == null) return;
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) { /* HTTP başlıklarını geç */ }
            byte[] body = renderer.render().getBytes(StandardCharsets.UTF_8);
            String headers = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + body.length + "\r\nConnection: close\r\n\r\n";
            OutputStream output = client.getOutputStream();
            output.write(headers.getBytes(StandardCharsets.US_ASCII)); output.write(body); output.flush();
            System.out.println("İstek: " + requestLine);
        }
    }
}
