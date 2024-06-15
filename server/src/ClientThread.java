import java.io.*;
import java.time.LocalDate;

public class ClientThread extends Thread 
{
    private Client owner;

    public ClientThread(Client own)
    {
        owner = own;
    }

    @Override
    public void run()
    {
        while(owner.isConnected)
        {
            OutputStream out;
            try {
                out = owner.GetSocket().getOutputStream();
                byte[] buff = new byte[] { 0x5F, 0x00 }; // Sending to check wether a client is still connected
                out.write(buff);
            } catch (Exception e) {
                System.out.println("[INFO]: Client disconnected!");
                owner.lastOnline = LocalDate.now();
                owner.isConnected = false;
                if(owner.netEventHandler != null)
                {
                    owner.netEventHandler.ClientDisconnected(owner);
                }
                break;
            } 
        }
        System.out.println("[INFO]: Thread " + threadId() + " finisheed");
    }
}
