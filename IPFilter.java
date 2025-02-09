

public class IPFilter {
    String[] allowedIPRange = {"192.168.0.*"};
    String[] blockedIPs = {"10.202.133.212","127.0.0.1"};

    public boolean isRequestIPAllowed(String ipAddress) {
        // first check for a blocked ips
        System.out.println(ipAddress);
        for (int i = 0; i < blockedIPs.length; i++) {
            if (blockedIPs[i].equals(ipAddress)) {
                return false;
            }
        }
        // for (int i = 0; i < allowedIPRange.length; i++) {
        //     if (ipAddress)
        // }
        return true;
    }
}
