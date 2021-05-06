/**
 * The ProxyProtocol takes a single clients http traffic and parses it line by
 * line to validate the traffic. It then will generate an http response if 
 * the http traffic is valid and the remote server is allowed and up.
 */

package giphyproxy;

import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyProtocol {

	private ArrayList<String> validUrls;

    // denotes the proxy socket is setup and connected
    public boolean proxyReady;
    
    // denotes read the last line of the HTTP request
    public boolean httpDone;
    // host of the server to proxy to
    public String host;

    // port number to connect to the remote server
    public int port;

    private Socket remoteSocket = null;
    private static String CONNECT_PATTERN = "CONNECT (?<host>.*) HTTP/1.1";
    private static String HTTP_ERROR = "HTTP/1.1 400 Bad Request\r\n";
    private static String HTTP_SUCCESS = "HTTP/1.1 200 OK\r\n";

    /**
     * Creates a new ProxyProtocol
     * @param validUrls A list of valid urls the proxy can connect to
     */
	public ProxyProtocol(ArrayList<String> validUrls) {
		this.validUrls = validUrls;
        this.proxyReady = false;
        this.host = "";
        this.port = -1;
        this.httpDone = false;
	}

    /**
     * Resets the stat of the ProxyProtocol in case of error when parsing 
     * the request.
     * @return void
     */
    private void resetState() {
        // set to NA only when parsing first line. If the first line is bad want 
        // to ignore all other lines
        this.host = "NA";
        this.port = -1;
        this.proxyReady = false;
        this.remoteSocket = null;
    }

    /**
     * Parses the HTTP request line by line. Uses url length to determine if 
     * on first line or later lines of http request.
     * @param input Single line of HTTP request
     * @return void
     */
	public void processHttp(String input) {
        if (this.host.length() == 0) {
            this.processHttpRequestMethod(input);
        } else {
            this.processHeaders(input);
        }
    }

    /**
     * Parses the HTTP request first line and opens a socket to the remote
     * server if valid. For now only looking for CONNECT verb but in future
     * could parse plaintext GET verbs as well. URL parsing done mostly by URL
     * class.
     * @param input First line of HTTP request
     * @return void
     */
    private void processHttpRequestMethod(String input) {
        Pattern connectR = Pattern.compile(CONNECT_PATTERN);
        Matcher connectM = connectR.matcher(input);
        if (connectM.find()) {
            String inputHost = connectM.group("host");
            URL urlObj = null;
            try {
                // need to add a protocol to have a valid url 
                // thus added http:// to front of inpur url
                urlObj = new URL("http://" + inputHost);
            } catch(MalformedURLException e) {
                // error formating url
                e.printStackTrace();
                this.resetState();
                return;
            }

            this.host = urlObj.getHost();
            
            if (!this.validUrls.contains(this.host)) {
                this.resetState();
                return;
            }

            this.port = urlObj.getPort();
            if (this.port == -1) {
                this.port = 443;
            }
            
            try {
                this.remoteSocket = new Socket(this.host, this.port);
            } catch(IOException e) {
                this.resetState();
                e.printStackTrace();
                return;
            }
            this.proxyReady = true;
        } else {
            this.resetState();
        }
    }

    /**
     * Processes the HTTP headers. If the length is 0 denotes last line in 
     * HTTP request.
     * TODO: process more later on as needs arise
     * @param input Single line of HTTP request
     * @return void
     */
    private void processHeaders(String input) {
        // process some headers
        // for now do nothing with them
        if (input.length() == 0) {
            // at the end of the headers
            this.httpDone = true;
        }
    }

    /**
     * Gets the HTTP resoonse body to send back to client. 200 if parsed the 
     * HTTP request ok, 400 if not.
     * @return String The HTTP response
     */
    public String getResponse() {
        if (this.httpDone && this.proxyReady) {
            return HTTP_SUCCESS;
        }
        return HTTP_ERROR;
    }

    /**
     * Getter for the remoteSocket set when socket is open to remote server.
     * @return Socket
     */
    public Socket getRemoteSocket() {
        return this.remoteSocket;
    }
}
	

