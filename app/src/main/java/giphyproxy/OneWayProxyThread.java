/**
 * OneWayProxyThread is a threaded class to handle one way threaded 
 * communication. All communication that comes in on inSocket get 
 * sent out on outSocket. If in and out socket are the same this 
 * would be a mirror server.
 */

package giphyproxy;

import java.net.*;
import java.io.*;

public class OneWayProxyThread extends Thread {

	private Socket outSocket = null;
	private Socket inSocket = null;
	// max size of the reading/writing buffer
	private static int SIZE = 1024;

	/**
	 * Creates a new OneWayProxyThread
	 * @param outSocket The socket to forward traffic to
	 * @param inSocket The socket to get traffic to forward
	 */
	public OneWayProxyThread(Socket outSocket, Socket inSocket) {
		super("OneWayProxyThread");
		this.outSocket = outSocket;
		this.inSocket = inSocket;
	}

	/**
	 * The run method gets called when the thread starts up. The thread will 
	 * read data in on inSocket and forward that to the outSocket until one 
	 * side closes the connection.
	 * @return void
	 */
	public void run() {
		try (
            OutputStream out = this.outSocket.getOutputStream();
            InputStream in = this.inSocket.getInputStream();
        ) {
        	int readData = 0;
        	byte[] b = new byte[SIZE];
        	readData = in.read(b, 0, SIZE);
        	while (readData > -1) {
        		out.write(b, 0, readData);
        		readData = in.read(b, 0, SIZE);
        	}
        } catch (IOException e) {
            // socket was closed on one end
        }
	}
}
	

