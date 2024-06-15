public interface NetEventHandler 
{
    void ClientConnected(Client client);
    void ClientDisconnected(Client client);    
}
