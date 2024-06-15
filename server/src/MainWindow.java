import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
// import java.util.Map;

public class MainWindow implements NetEventHandler
{
    public static final int PORT = 2556;
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;

    private static MainWindow instance;
    private static Server serv;    

    private JFrame mainFrame;
    private JLabel statusLabel;
    // private Map<ByteKey, JLabel> labels;
     
    public static void main(String[] Args)
    {
        MainWindow win = GetInstance();
        serv = new Server(win);
        if(!serv.BeginListen(PORT))
        {
            System.out.println("[ERROR]: Failed to start server, exiting.");
            System.exit(0);
            return;
        }
        win.ShowWidgets();
    }

    public static MainWindow GetInstance()
    {
        if(instance == null)
        {
            instance = new MainWindow();
        }
        return instance;
    }

    private MainWindow()  { }

    public void ShowWidgets()
    {
        mainFrame = new JFrame();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = (int)screenSize.getWidth();
        final int height = (int)screenSize.getHeight();

        mainFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        mainFrame.setLocation(width/2 - WINDOW_WIDTH/2, height/2 - WINDOW_HEIGHT/2);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
        
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                serv.Shutdown();
                System.exit(0);
            }
        });

        statusLabel = new JLabel();
        mainFrame.add(statusLabel);
        statusLabel.setText("Status label here");
    }

    @Override
    public void ClientConnected(Client client) 
    {

    }

    @Override
    public void ClientDisconnected(Client client) 
    {

    }
}