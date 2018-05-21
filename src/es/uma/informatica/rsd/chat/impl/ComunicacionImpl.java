package es.uma.informatica.rsd.chat.impl;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import es.uma.informatica.rsd.chat.ifaces.Comunicacion;
import es.uma.informatica.rsd.chat.ifaces.Controler;
import es.uma.informatica.rsd.chat.impl.DialogoPuerto.AliasPort;

// Clase a implementar 
public class ComunicacionImpl implements Comunicacion {

	@Override
	public void crearSocket(AliasPort pa) {
	}

	@Override
	public void setControlador(Controler c) {
	}

	@Override
	public void runReceptor() {
	}

	@Override
	public void envia(InetSocketAddress sa, String mensaje) {
	}

	@Override
	public void joinGroup(InetAddress multi) {
	}

	@Override
	public void leaveGroup(InetAddress multi) {
	}

}
