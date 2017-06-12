package com.justinfletch.encdec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.security.Key;
import java.util.Scanner;

import org.jose4j.keys.AesKey;

import com.justinfletch.encdec.pojo.Output;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Hello world!
 *
 */
public class App 
{
	
	public static Key key = null;
	public static int port = -1;
	
    public static void main( String[] args ) throws IOException
    {
        
    	if (args.length < 2 || args.length > 3) {
    		System.out.println("Missing arguments!");
    		System.out.println("Usage A (server mode) : encdec {secret_key(16bit)} {port}");
    		System.out.println("Usage B (single use mode) : encdec {key_file_name} -{e/d} {string to encrypt or decrypt}");
    		System.exit(1);
    	}
    	
    	key = null;
    	String secondArg = args[1];
    	
    	if (args.length == 3) {
    		
    		try(Scanner s = new Scanner(new File(args[0]));) {
    			if (!s.hasNext())
        			throw new IOException("File empty : " + args[0]);
    			String filename = s.next();
        		key = new AesKey(filename.getBytes());
    		}
    		
	    	if (secondArg.equals("-e")) {
	    		try {
					String payload = Crypto.encrypt(key, args[2]);
					sendProgramOutput(200, "ecrypt success", payload);
				} catch (Exception e) {
					try {
						sendProgramOutput(500, "ecrypt failed", e.getMessage());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
	    	}
	    	else if (secondArg.equals("-d")) {
	    		try {
					String payload = Crypto.decrypt(key, args[2]);
					sendProgramOutput(200, "decrypt success", payload);
				} catch (Exception e) {
					try {
						sendProgramOutput(500, "decrypt failed", e.getMessage());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
	    	}
    	}
    	
    	
    	
    	else {
    		
    		key = new AesKey(args[0].getBytes());
    	
	    	try {
	    		port = Integer.parseInt(secondArg);
	    		if (port < 1 || port > 65535)
	    			throw new Exception("Port out of range");
	    	} catch(Exception e) {
	    		try {
					sendProgramOutput(500, "Bad Argument", "Argument 2 should be an integer between 1 and 65535");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	    		System.exit(1);
	    	}
	    	
	    	
	    	
	    	HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
	    	
	    	server.createContext("/encrypt", new HttpHandler() {
	
				public void handle(HttpExchange t) throws IOException {
					try {
						String payload = Crypto.encrypt(key, streamToString(t.getRequestBody()));
						sendResponse(t, 200, "ecrypt success", payload);
					} catch (Exception e) {
						try {
							sendResponse(t, 500, "encrypt failed", e.getMessage());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				} 
	    		
	    	});
	    	
	    	
	    	server.createContext("/decrypt", new HttpHandler() {
	
				public void handle(HttpExchange t) throws IOException {
					try {
						String payload = Crypto.decrypt(key, streamToString(t.getRequestBody()));
						sendResponse(t, 200, "decrypt success", payload);
					}catch (Exception e) {
						try {
							sendResponse(t, 500, "decrypt failed", e.getMessage());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
	    		
	    	});
	    	
	    	
	    	server.setExecutor(null);
	    	server.start();
	    	
    	}
    	
    	
    }
    
    
    static void sendResponse(HttpExchange t, int code, String message, String data) throws Exception {
    	Output o = new Output(code, message, data);
    	String payload = o.toString();
    	
    	t.sendResponseHeaders(code, payload.length());
        OutputStream os = t.getResponseBody();
        os.write(payload.getBytes());
        os.close();
    }
    
    static void sendProgramOutput(int code, String message, String data) throws Exception {
    	Output o = new Output(code, message, data);
    	String payload = o.toString();
    	System.out.println(payload);
    }
    
    
    static String streamToString(InputStream is) throws Exception {
    	final int bufferSize = 1024;
    	final char[] buffer = new char[bufferSize];
    	final StringBuilder out = new StringBuilder();
    	Reader in = new InputStreamReader(is, "UTF-8");
    	for (; ; ) {
    	    int rsz = in.read(buffer, 0, buffer.length);
    	    if (rsz < 0)
    	        break;
    	    out.append(buffer, 0, rsz);
    	}
    	return out.toString();
    }
    
}
