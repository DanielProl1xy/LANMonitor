import java.net.Socket;
import java.time.LocalDate;

public class Client 
{
    public boolean isConnected;
    public LocalDate lastOnline;
    public NetEventHandler netEventHandler;
    
    private Socket socket;
    private ClientThread listenThread;
    private ByteKey key;


    public Client(ByteKey bKey, NetEventHandler netHandler)
    {
        key = bKey;
        netEventHandler = netHandler;
    }

    public void BeginListen(Socket sock)
    {
        isConnected = true;
        socket = sock;

        listenThread = new ClientThread(this);

        listenThread.start();
    }

    public Socket GetSocket()
    {
        return socket;
    }

    public ByteKey GetKey()
    {
        return key;
    }

    public LocalDate GetLastOnline()
    {
        if(isConnected)
        {
            return LocalDate.now();
        }
        return lastOnline;
    }

    public void EndListen()
    {
        isConnected = false;
        try
        {            
            socket.close();
            listenThread.join();
        } 
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
