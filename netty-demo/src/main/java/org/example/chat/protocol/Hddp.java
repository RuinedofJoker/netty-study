package org.example.chat.protocol;

public class Hddp {
    private HddpProtocol hddpProtocol;

    public Hddp() {
        this.hddpProtocol = new HddpProtocol();

        hddpProtocol.setSerializationType(HddpProtocol.STRING);
        hddpProtocol.setPath("/index");
        requestType("GET");
        hddpProtocol.setContent("".getBytes());
    }

    public Hddp(HddpProtocol hddpProtocol) {
        this.hddpProtocol = hddpProtocol;
    }

    public String getPath() {
        return hddpProtocol.getPath().trim();
    }

    public String getSerializationType() {
        if (HddpProtocol.STRING == hddpProtocol.getSerializationType()) {
            return "string";
        }else if (HddpProtocol.JSON == hddpProtocol.getSerializationType()) {
            return "json";
        }else {
            hddpProtocol.setSerializationType(HddpProtocol.STRING);
            return "string";
        }
    }

    public String getRequestType() {
        String requestType = "get";
        if (hddpProtocol.getRequestType() == HddpProtocol.GET) {
            requestType = "get";
        }else if (hddpProtocol.getRequestType() == HddpProtocol.POST) {
            requestType = "post";
        }else if (hddpProtocol.getRequestType() == HddpProtocol.PUT) {
            requestType = "put";
        }else if (hddpProtocol.getRequestType() == HddpProtocol.DELETE) {
            requestType = "delete";
        }else {
            hddpProtocol.setRequestType(HddpProtocol.GET);
        }

        return requestType;
    }

    public String getContent() {
        return new String(hddpProtocol.getContent());
    }

    public Hddp requestType(String requestType) {
        if ("get".equals(requestType)) {
            hddpProtocol.setRequestType(HddpProtocol.GET);
        }else if ("post".equals(requestType)) {
            hddpProtocol.setRequestType(HddpProtocol.POST);
        }else if ("put".equals(requestType)) {
            hddpProtocol.setRequestType(HddpProtocol.PUT);
        }else if ("delete".equals(requestType)) {
            hddpProtocol.setRequestType(HddpProtocol.DELETE);
        }else {
            hddpProtocol.setRequestType(HddpProtocol.GET);
        }

        return this;
    }

    public Hddp serializationType(String serializationType) {
        if (serializationType.equals("string")) {
            hddpProtocol.setSerializationType(HddpProtocol.STRING);
        }else if (serializationType.equals("json")) {
            hddpProtocol.setSerializationType(HddpProtocol.JSON);
        }
        return this;
    }

    public Hddp path(String path) {
        hddpProtocol.setPath(path.trim());
        return this;
    }

    public Hddp content(String content) {
        hddpProtocol.setContent(content.getBytes());
        return this;
    }

    public HddpProtocol build() {
        return hddpProtocol;
    }
}
