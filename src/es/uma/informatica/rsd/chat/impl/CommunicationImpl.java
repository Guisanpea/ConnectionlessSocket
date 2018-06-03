package es.uma.informatica.rsd.chat.impl;

import es.uma.informatica.rsd.chat.ifaces.Comunication;
import es.uma.informatica.rsd.chat.ifaces.Controler;
import es.uma.informatica.rsd.chat.impl.PortDialog.AliasPort;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class CommunicationImpl implements Comunication {

    private Controler messageDisplayer;
    private MulticastSocket udpSocket;
    private String alias;
    private final List<InetAddress> localAddress;

    CommunicationImpl() {
        try {
            localAddress = Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
                    .map(NetworkInterface::getInetAddresses)
                    .map(Collections::list)
                    .flatMap(List::stream)
                    .collect(toList());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static final int BUFFER_LENGTH = 1460;

    @Override
    public void createSocket(AliasPort puerto) {

        try {
            udpSocket = new MulticastSocket(puerto.puerto);
            alias = puerto.alias;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setController(Controler c) {
        messageDisplayer = c;
    }

    @Override
    public void runReceptor() {
        while (true) {
            String message;
            byte[] buffer = new byte[BUFFER_LENGTH];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try {
                udpSocket.receive(packet);
                message = new String(packet.getData(), UTF_8);

                InetSocketAddress sender = InetSocketAddress.class.cast(packet.getSocketAddress());
                if (!sender.getAddress().isMulticastAddress() || !localAddress.contains(sender.getAddress()))
                    messageDisplayer.showMessage(sender, "", message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void send(InetSocketAddress socketAddress, String message) {
        byte[] buffer = String.format("%s!%s!%s",
                socketAddress.getAddress().isMulticastAddress() ? "" : socketAddress.getAddress(),
                alias,
                message).getBytes(UTF_8);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socketAddress);

        try {
            udpSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joinGroup(InetAddress multi) {
        try {
            udpSocket.joinGroup(multi);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leaveGroup(InetAddress multi) {
        try {
            udpSocket.leaveGroup(multi);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
