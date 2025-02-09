

public class LoadBalancer {
    String[] availableServers = { "http://localhost:3000", "http://localhost:3001" };
    int nextSeverToUse = 0;
    
    public String getServerURL() {
        String url = availableServers[nextSeverToUse];
        if (nextSeverToUse == availableServers.length -1) {
            nextSeverToUse = 0;
        } else {
            nextSeverToUse = nextSeverToUse + 1;
        }
        return url;
    }
}
