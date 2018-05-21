public interface Comunication {
        public void crearSocket(PuertoAlias puerto);
        public void setControlador (Controlador c);
        public void runReceptor();
        public void envia(InetSocketAddress sa, String mensaje);
        public void joinGroup(InetAddress multi);
        public void leaveGroup(InetAddress multi);
}
