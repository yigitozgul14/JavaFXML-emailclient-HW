package advancejavaproject3;

public class HostConfig {
    private String protocol;
    private String receiveHost;
    private int receivePort;
    private String sendHost;
    private int sendPort;
    private String username;
    private String password;

    public HostConfig(String protocol, String receiveHost, int receivePort,
                     String sendHost, int sendPort, String username, String password) {
        this.protocol = protocol;
        this.receiveHost = receiveHost;
        this.receivePort = receivePort;
        this.sendHost = sendHost;
        this.sendPort = sendPort;
        this.username = username;
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getReceiveHost() {
        return receiveHost;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public String getSendHost() {
        return sendHost;
    }

    public int getSendPort() {
        return sendPort;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return receiveHost + " (" + username + ")";
    }
}
