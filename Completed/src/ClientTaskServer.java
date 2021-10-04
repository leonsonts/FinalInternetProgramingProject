import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class ClientTaskServer implements Runnable {
    private final Socket clientSocket;
    private IHandler requestHandler;
    private ThreadPoolExecutor threadPool;

    ClientTaskServer(Socket clientSocket, IHandler handle) {
        this.clientSocket = clientSocket;
        this.requestHandler = handle;
        threadPool = null;
    }

    @Override
    public void run() {

        System.out.println("Client connected");
        threadPool = new ThreadPoolExecutor(3, 5, 10,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        Runnable clientHandling = () -> {
            try {
                requestHandler.handle(clientSocket.getInputStream(),
                        clientSocket.getOutputStream());
                // finished handling client. now close all streams


                clientSocket.getInputStream().close();
                clientSocket.getOutputStream().close();
                clientSocket.close();
            } catch (IOException ioException) {
                System.err.println(ioException.getMessage());
            } catch (ClassNotFoundException | ExecutionException | InterruptedException ce) {
                System.err.println(ce.getMessage());
            }

        };
        threadPool.execute(clientHandling);
    }
    public synchronized void stop()
    {
        if(threadPool!=null) threadPool.shutdown();

    }
}

