package util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestObject {

    private String requestMethod;
    private String requestPath;
    private Map<String, String> requestParams;
    private String httpVersion;
    private Map<String, String> requestHeaders;
    private byte[] requestBody;

    private HttpRequestObject(String requestMethod, String requestPath, Map<String, String> requestParams, String httpVersion) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.requestParams = requestParams;
        this.httpVersion = httpVersion;
        this.requestHeaders = new HashMap<>();
    }

    public static HttpRequestObject from(String requestLine) {
        String[] requestLineElements = Arrays.stream(requestLine.split(StringUtil.SPACES)).map(String::trim).toArray(String[]::new);
        String requestMethod = requestLineElements[0];
        String[] requestURIElements = Arrays.stream(requestLineElements[1].split(StringUtil.QUESTION_MARK)).map(String::trim).toArray(String[]::new);
        String requestPath = requestURIElements[0];
        Map<String, String> requestParams = new HashMap<>();
        if(requestURIElements.length > 1) {
            String[] params = requestURIElements[1].split(StringUtil.AND);
            for (String param : params) {
                String[] keyValue = param.split(StringUtil.EQUAL);
                requestParams.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }
        String requestVersion = requestLineElements[2];
        return new HttpRequestObject(requestMethod, requestPath, requestParams, requestVersion);
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public byte[] getBody(){
        return requestBody;
    }

    public String getBodyString() {
        return new String(requestBody, StandardCharsets.UTF_8);
    }

    public Map<String, String> getBodyMap() {
        Map<String, String> bodyMap = new HashMap<>();

        // byte[] to String, "&"으로 split
        String restoredString = new String(requestBody, StandardCharsets.UTF_8);
        String[] pairs = restoredString.split(StringUtil.AND);
        for (String pair : pairs) {
            String[] keyValue = pair.split(StringUtil.EQUAL);
            bodyMap.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return bodyMap;
    }

    public void putHeaders(String headerLine){
        if(headerLine.isEmpty()) return;
        headerLine = headerLine.replaceAll(StringUtil.SPACES, StringUtil.SPACE); // remove multiple spaces
        int idx = headerLine.indexOf(StringUtil.COLON);
        if(idx == -1) {
            throw new IllegalArgumentException("Header is invalid");
        }
        String[] header = {headerLine.substring(0, idx), headerLine.substring(idx + 1)};
        requestHeaders.put(header[0].trim(), header[1].trim());
    }

    public void putBody(List<Byte> body){
        // List<Byte> to byte[]
        byte[] byteArray = new byte[body.size()];
        for (int i = 0; i < body.size(); i++) {
            byteArray[i] = body.get(i);
        }
        // byte[] to String 후 디코딩하여 다시 byte[]로 변환
        this.requestBody = new String(byteArray, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
    }
}
