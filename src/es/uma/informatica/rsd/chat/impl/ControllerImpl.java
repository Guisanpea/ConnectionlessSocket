package es.uma.informatica.rsd.chat.impl;

import java.awt.event.ActionEvent;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import es.uma.informatica.rsd.chat.ifaces.Comunication;
import es.uma.informatica.rsd.chat.ifaces.Controler;
import es.uma.informatica.rsd.chat.ifaces.View;
import es.uma.informatica.rsd.chat.impl.PortDialog.AliasPort;

public class ControllerImpl implements Controler {
    private View view;
    private Comunication comunication;
    private Map<String, InetSocketAddress> conversations;

    private static final String SEP = ":";

    private String alias;

    public ControllerImpl() {
        conversations = new HashMap<String, InetSocketAddress>();
    }

    public void run(String[] args) {
        view = new Panels();
        comunication = new CommunicationImpl();

        comunication.setController(this);
        view.setController(this);

        JFrame frame = new JFrame();
        frame.getContentPane().add((JPanel) view);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setTitle(view.TITLE);
        frame.pack();
        frame.setVisible(true);

        AliasPort aliasPort = view.getAliasListeningPort();
        String error;

        while ((error = correctAliasPort(aliasPort)) != null) {
            view.warning("Puerto y Alias", error);
            aliasPort = view.getAliasListeningPort();
        }

        comunication.createSocket(aliasPort);
        alias = aliasPort.alias;

        Thread t = new Thread() {
            public void run() {
                comunication.runReceptor();
            }
        };

        t.setDaemon(true);
        t.start();

    }

    private String correctAliasPort(AliasPort aliasPort) {
        if (aliasPort == null) {
            return "Debe indicar el puerto de escucha y un alias";
        } else if (aliasPort.puerto < 1024 || aliasPort.puerto > 65535) {
            return "El puerto debe ser un entero entre 1024 y 65535";
        } else if (aliasPort.alias.indexOf(',') >= 0) {
            return "El alias no puede contener el s�mbolo >";
        }

        return null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new ControllerImpl().run(args);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String actionCommand = event.getActionCommand();
        if (actionCommand.equals(NUEVO)) {
            // Pedir la IP y el puerto al usuario mediante una ventana modal
            InetSocketAddress socketAddress = view.getPortIP();
            if (socketAddress == null) {
                // El usuario ha cancelado la introducci�n de datos
                return;
            }
            // else
            String name = socketAddress.toString();
            if (!conversations.containsKey(name)) {
                conversations.put(name, socketAddress);
                view.createPanel(name);
                if (socketAddress.getAddress().isMulticastAddress()) {
                    comunication.joinGroup(socketAddress.getAddress());
                }
            } else {
                view.warning("Conversaci�n", "La ventana de conversaci�n ya existe");
            }
        } else if (actionCommand.startsWith(SEND_PREFIX)) {
            String nombre = actionCommand.substring(SEND_PREFIX.length());
            String mensaje = view.getMessage(nombre);

            view.showMessages(nombre, alias + SEP + mensaje, View.OWN);
            comunication.send(conversations.get(nombre), mensaje);
        } else if (actionCommand.startsWith(CLOSE_PREFIX)) {
            String nombre = actionCommand.substring(CLOSE_PREFIX.length());
            InetSocketAddress socketAddress = conversations.get(nombre);

            if (socketAddress.getAddress().isMulticastAddress()) {
                comunication.leaveGroup(socketAddress.getAddress());
            }

            view.closePanel(nombre);
            conversations.remove(nombre);
        }
    }

    @Override
    public void showMessage(SocketAddress sa, String alias, String mensaje) {
        InetSocketAddress isa = (InetSocketAddress) sa;
        String name = isa.toString();
        if (!conversations.containsKey(name)) {
            conversations.put(name, isa);
            view.createPanel(name);
        }
        view.showMessages(name, alias + SEP + mensaje, View.FOREIGN);

    }


}
