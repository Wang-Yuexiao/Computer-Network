import java.io.*;
import java.net.*;
import java.util.*;

final class UDPHttpRequest implements Runnable {

    final static String CRLF = "\r\n";

    private DatagramSocket socket; // 存储UDP服务器的socket
    private DatagramPacket packet; // 存储接收到的UDP数据包

    // 构造函数，接受socket和接收到的数据包
    public UDPHttpRequest(DatagramSocket socket, DatagramPacket packet) throws Exception {
        this.socket = socket;
        this.packet = packet;
    }

    // 实现Runnable接口的run方法
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        // 从接收到的DatagramPacket中提取请求数据
        byte[] requestData = packet.getData();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestData);
        InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        // 读取HTTP请求的第一行
        String requestLine = br.readLine();
        System.out.println("Request Line: " + requestLine);

        // 从请求行中提取文件名
        StringTokenizer tokens = new StringTokenizer(requestLine);
        String method = tokens.nextToken(); // HTTP方法，例如GET
        String fileName = tokens.nextToken(); // 请求的文件名

        // 确保文件名的路径在当前目录
        fileName = "." + fileName;

        // 尝试打开请求的文件
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        // 构造HTTP响应消息
        String statusLine = null;
        String contentTypeLine = null;
        String contentLengthLine = null;
        String entityBody = null;

        if (fileExists) {
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
            contentLengthLine = "Content-Length: " + getFileSizeBytes(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML><HEAD><TITLE>Not Found</TITLE></HEAD><BODY>Not Found</BODY></HTML>";
        }

        // 将响应消息写入ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(statusLine.getBytes());
        baos.write(contentTypeLine.getBytes());
        if (fileExists) {
            baos.write(contentLengthLine.getBytes());
        }
        baos.write(CRLF.getBytes());

        // 如果文件存在，将文件内容写入响应
        if (fileExists) {
            sendBytes(fis, baos);
            fis.close();
        } else {
            baos.write(entityBody.getBytes());
        }

        // 将响应转换为字节数组并发送回客户端
        byte[] responseData = baos.toByteArray();
        DatagramPacket responsePacket = new DatagramPacket(
            responseData, responseData.length, packet.getAddress(), packet.getPort()
        );
        socket.send(responsePacket);

        // 关闭流
        br.close();
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024]; // 1KB缓冲区
        int bytes = 0;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    /**
     * 根据文件扩展名返回内容类型
     * @param fileName 文件名
     * @return 内容类型
     */
    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".ram") || fileName.endsWith(".ra")) {
            return "audio/x-pn-realaudio";
        }
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    /**
     * 获取文件大小
     * @param fileName 文件名
     * @return 文件大小（字节）
     * @throws IOException
     */
    private static long getFileSizeBytes(String fileName) throws IOException {
        File file = new File(fileName);
        return file.length();
    }
}