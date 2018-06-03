package es.uma.informatica.rsd.chat.impl;

import es.uma.informatica.rsd.chat.ifaces.Comunication;
import es.uma.informatica.rsd.chat.ifaces.Controler;
import es.uma.informatica.rsd.chat.impl.PortDialog.AliasPort;

import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class CommunicationImpl implements Comunication {

    private static final String EMPTY = "";
    public static final String SPLITTER = "!";
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
    public void setController(Controler controler) {
        messageDisplayer = controler;
    }

    @Override
    public void runReceptor() {
        while (true) {
            DatagramPacket packet = receivePacket();
            String message = new String(packet.getData(), UTF_8);

            InetSocketAddress sender = toInetSocketAddress(packet.getSocketAddress());
            if (!isMulticast(message) || !localAddress.contains(sender.getAddress())) {
                messageDisplayer.showMessage(sender, EMPTY, message);
            }
        }
    }

    private DatagramPacket receivePacket() {
        DatagramPacket packet = new DatagramPacket(new byte[BUFFER_LENGTH], BUFFER_LENGTH);
        try {
            udpSocket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }

    private InetSocketAddress toInetSocketAddress(SocketAddress socketAddress) {
        return InetSocketAddress.class.cast(socketAddress);
    }

    private boolean isMulticast(String message) {
        return !message.split(SPLITTER)[0].isEmpty();
    }


    @Override
    public void send(InetSocketAddress socketAddress, String message) {
        byte[] buffer = toBuffer(socketAddress, message);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socketAddress);

        try {
            udpSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] toBuffer(InetSocketAddress socketAddress, String message) {
        return Stream.of(
                socketAddress.getAddress().isMulticastAddress() ? socketAddress.getAddress().toString() : EMPTY,
                alias,
                message)
                .collect(joining(SPLITTER)).getBytes(UTF_8);
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
