/**
 * The ProxyServerThread class is a threaded class to handle each incoming request for the proxy
 * 
 */

package giphyproxy;

import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class ProxyServerThread extends Thread {

	private Socket socket = null;
	private ArrayList<String> validUrls;

	/**
	 * Creates a new ProxyServerThread
	 * @param socket The socket for the cleitn connection
	 * @param validUrls A list of valid urls the proxy can connect to
	 */
	public ProxyServerThread(Socket socket, ArrayList<String> validUrls) {
		super("ProxyServerthread");
		this.socket = socket;
		this.validUrls = validUrls;
	}

	/**
	 * The run method gets called when the thread starts up.This method reads
	 * input from the client that initiated the connection. If the http is 
	 * valid it then spawns two threads: one to forward packets from client
	 * to servers and the other to forward packets from server to client.
	 * @return void
	 */
	public void run() {
		try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
			String inputLine, outputLine;
            ProxyProtocol pp = new ProxyProtocol(this.validUrls);

            // parse the http request
            while (!pp.httpDone && (inputLine = in.readLine()) != null) {
            	pp.processHttp(inputLine);
            }
            // send the http response. 200 if ok 400 otherwise
            outputLine = pp.getResponse();
            out.println(outputLine);

            // proxy the communication between the server and client 
            if (pp.proxyReady) {
            	Thread t1 = new OneWayProxyThread(socket, pp.getRemoteSocket());
            	Thread t2 = new OneWayProxyThread(pp.getRemoteSocket(), socket);
            	t1.start();
            	t2.start();
            	try {
            		t1.join();
            		t2.join();
            	} catch(InterruptedException e) {
            		e.printStackTrace();
            	}
            	pp.getRemoteSocket().close();
            }
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
	

