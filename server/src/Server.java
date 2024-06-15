import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.locks.*;

public class Server
{
    public static final int KEY_LEN = 128;

    private ServerSocket serverSocket;
    private ArrayList<Client> clients;
    private NetEventHandler netHandler;

    private Thread acceptThread;
    private ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private Lock wlock = rwlock.writeLock();

    public Server(NetEventHandler handler)
    {
        netHandler = handler;
    }

    public boolean BeginListen(int port) 
    {
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        clients = new ArrayList<Client>();

        System.out.println("[INFO]: Server is listening!");

        acceptThread = new Thread() { 
            @Override
            public void run()
            {
                while(!serverSocket.isClosed())
                {
                    try {                        
                        Socket sock = serverSocket.accept();

                        byte[] key = new byte[KEY_LEN];
                        InputStream inputStream = sock.getInputStream();
                        int bytesRead = 0;
                        while (bytesRead < KEY_LEN) {
                            int result = inputStream.read(key, bytesRead, KEY_LEN - bytesRead);
                            if (result == -1) {
                                break;
                            }
                            bytesRead += result;
                        }

                        if(wlock.tryLock())
                        {
                            ByteKey bKey = new ByteKey(key);
                            
                            int exists = -1;
                            for(int i = 0 ; i < clients.size(); ++i)
                            {
                                if(clients.get(i).GetKey().equals(bKey))
                                {
                                    exists = i;
                                    break;
                                }
                            }
                            Client cl;
                            if(exists >= 0)
                            {
                                cl = clients.get(exists);
                            }
                            else
                            {
                                cl = new Client(bKey, netHandler);
                                clients.add(cl);
                                wlock.unlock();
                            }
                            cl.BeginListen(sock);
                            System.out.println("[INFO]: Connected: " + cl.GetKey().toString());
                            System.out.println("[INFO]: Seen earlier: " + (exists >= 0));
                        }
                    } catch (Exception e) {
                    }
                }
                System.out.println("[INFO]: Thread " + threadId() + " finisheed");
            }
        };

        acceptThread.start();

        return true;
    }

    public void Shutdown()
    {
        for (Client cl : clients) 
        {
            cl.EndListen();
        }
        
        try {
            serverSocket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());;
        }

        try {
            acceptThread.join();    
        } catch (Exception e) {
            System.out.println(e.getMessage());;
        }

        clients.clear();
        System.out.println("[INFO]: Server finished");
    }
}