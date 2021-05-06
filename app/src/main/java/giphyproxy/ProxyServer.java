/*
 * This ProxyServer program is a proxy server for HTTPS traffic.
 * Clients can request using HTTP Connect verb and giving a url. If the url is
 * in the config.json file under valid urls then the sevrer will spin up 
 * threads to handle communication between the two endpoints.
 * See 
 * 
 */
package giphyproxy;

import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class ProxyServer {

	public static String CONFIG_FILE = "config.json";

	/**
	 * The main method  for the server. Listens on the configured port and 
	 * spawns threads to handle each new connection. Config.json sets the value
	 * of the port number and valid urls to proxy.
	 * @param args unused.
	 * @return void
	 * @exception IOException on socket bind errors
	 */
	public static void main(String[] args) throws IOException {
		boolean listening = true;
		int portNumber = 3128;
		ArrayList<String> validUrls = new ArrayList<String>();

		// parse the config file and set value as appropriate
		String config = loadConfig();
		if (config.length() == 0) {
			System.err.println("Could not open config file");
			System.exit(-1);
		}
		try {
		     JSONObject jsonObject = new JSONObject(config);
		     portNumber = jsonObject.getInt("portNumber");
		     JSONArray urls = jsonObject.getJSONArray("validUrls");
		     if (urls != null) {
		     	for (int i = 0; i < urls.length(); i++) {
		     		validUrls.add(urls.getString(i));
		     	}
		     }
		     System.out.println(validUrls);
		}catch (JSONException err){
			System.err.println("Could not parse config file");
			System.exit(-1);
		}		

		// open up the listening port
		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			while (listening) {
				new ProxyServerThread(serverSocket.accept(), validUrls).start();
			}
		} catch(IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
	}

	/**
	 * Loads the config file set by CONFIG_FILE and located in the resources folder.
	 * @return String The config file as string. Empty if an error occured.
	 */
	public static String loadConfig() {
        ClassLoader classLoader = ProxyServer.class.getClassLoader();
 
        File file = new File(classLoader.getResource(CONFIG_FILE).getFile());
        if (!file.exists()) {
        	return "";
        }
         
        //Read File Content
        String content = "";
        try {
        	content = new String(Files.readAllBytes(file.toPath()));
        } catch(IOException e) {
        	e.printStackTrace();
        }
        return content;
	}

}