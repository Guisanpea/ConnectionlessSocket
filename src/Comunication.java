import es.uma.informatica.rsd.chat.ifaces.Controler;
import es.uma.informatica.rsd.chat.impl.DialogoPuerto.AliasPort;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface Comunication {
        public void createSocket(AliasPort puerto);
        public void setController (Controler c);
        public void runReceptor();
        public void send(InetSocketAddress sa, String message);
        public void joinGroup(InetAddress multi);
        public void leaveGroup(InetAddress multi);
}
