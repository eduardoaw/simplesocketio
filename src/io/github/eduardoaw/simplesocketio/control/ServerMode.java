/*
* The MIT License (MIT)
* 
* Copyright (c) 2015 Eduardo Adams Wohlfahrt
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package io.github.eduardoaw.simplesocketio.control;

import io.github.eduardoaw.simplesocketio.SimpleSocketIOView;
import java.awt.Color;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eduardo Adams Wohlfahrt
 * @date   2015-12-29
 */
public class ServerMode extends Thread {

    private ServerSocket sSocket = null;
    private SimpleSocketIOView sView = null;
    private boolean runThread = false;
    private HashMap<String, ClientThread> listClients = null;
    
    public ServerMode(SimpleSocketIOView sView) {
        this.sView = sView;
    }

    @Override
    public void run() {
        
        waitForClientConnection();
    }
        
    private void waitForClientConnection() {

        try {
            sSocket = new ServerSocket(sView.getPort());
        } catch (BindException e) {
            sView.resetServer("Port " + sView.getPort() + " already in use", true);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        listClients = new HashMap<String, ClientThread>();            
        
        runThread = true;
        
        while (runThread) {
            
            try {
                ClientThread handlerClient = new ClientThread(sSocket.accept(), sView);
                
                synchronized(handlerClient) {
                
                    handlerClient.start();
                    handlerClient.wait();
                    sleep(1000);
                    listClients.put(handlerClient.getID(), handlerClient);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
        }   
    }
    
    public void sendToClient(String id, String msg) {
        
        listClients.get(id).sendMessage(msg);
    }
    
    public void disconnectClient(String id) {
        
        listClients.get(id).closeConnection();
        listClients.remove(id);
        sView.setMessageServerRec(new Color(174, 182, 193), "Disconnected client " + id, null, null);
        sView.removeClientList(id);
    } 
    
    public void close() {
       
        runThread = false;

        for (Map.Entry<String, ClientThread> entry : listClients.entrySet())
            entry.getValue().closeConnection();
            
        try {    
            if(sSocket != null)
                sSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerMode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
