import es.uma.informatica.rsd.chat.ifaces.Controler;
import es.uma.informatica.rsd.chat.impl.PortDialog.AliasPort;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface Comunication {
    void createSocket(AliasPort puerto);
    void setController (Controler c);
    void runReceptor();
    void send(InetSocketAddress sa, String message);
    void joinGroup(InetAddress multi);
    void leaveGroup(InetAddress multi);
}
