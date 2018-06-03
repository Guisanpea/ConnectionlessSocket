package es.uma.informatica.rsd.chat.ifaces;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.net.SocketAddress;

/**
 * Interfaz del controlador.
 *
 */

public interface Controler extends ActionListener
{
	// Cadenas de comando para botones
	String NUEVO = "nuevo";
	String SEND_PREFIX = "enviar";
	String CLOSE_PREFIX = "cerrar";
	
	/**
	 * Este metodo es invocado por el componente de comunicaci�n para indicar que se ha recibido
	 * un mensaje de un determinado proceso cuya direcci�n de socket se indica.
	 * @param sa Direcci�n de socket del proceso que envi� el mensaje.
	 * @param mensaje Mensaje que envi� dicho proceso.
	 */
	public void showMessage(SocketAddress sa, String alias, String mensaje);
}
