import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;



public class TcpServer {

    private final int port;
    private volatile boolean stopServer;
    private ThreadPoolExecutor threadPool;
    private IHandler requestHandler;
    private ThreadPoolExecutor clientProcessingPool;

    public TcpServer(int port) {
        this.port = port;
        stopServer = false;
        threadPool = null;
        requestHandler = null;
        clientProcessingPool =null;
    }

    public void run(IHandler concreteHandler) {
        this.requestHandler = concreteHandler;
        new Thread(() -> {
            // lazy loading
            threadPool = new ThreadPoolExecutor(3, 5, 10,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>());
            clientProcessingPool = new ThreadPoolExecutor(3, 5, 10,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>());

            threadPool.execute( ()->
                    {

                        ServerSocket serverSocket = null;
                        try {
                            // Connect & bind phase
                            serverSocket = new ServerSocket(port);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Waiting for clients to connect...");
                        while (!stopServer) {
                            Socket serverToSpecificClient = null;
                            try {
                                serverToSpecificClient = serverSocket.accept(); //Blocking call - define a task and submit to our threadPool.
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // 1 or more clients to handle, potentially.
                            clientProcessingPool.submit(new ClientTaskServer(serverToSpecificClient, requestHandler));

                        }
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }).start();
    }

    /**
     * stop server and shutdown thread pools immediately
     */
    public synchronized void stop() {

        if(!stopServer) {
            stopServer = true;
        }
        if (threadPool != null) threadPool.shutdownNow();
        if(clientProcessingPool !=null) clientProcessingPool.shutdownNow();

    }

    public static void main(String[] args) {
        TcpServer myServer = new TcpServer(8010);
        myServer.run(new MatrixIHandler());
        // Operation running in separated thread, meanwhile current thread will sleep for 5 minutes in try block code
        try {
            Thread.sleep(300000);
            // Server runs for 5 minutes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Stopping Server");
        myServer.stop();
        System.exit(0);

    }
}

