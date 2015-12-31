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

package com.github.eduardoaw.simplesocketio.control;

import com.github.eduardoaw.simplesocketio.view.SimpleSocketIOView;
import com.github.eduardoaw.simplesocketio.model.Mode;
import java.awt.Color;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Eduardo Adams Wohlfahrt
 * @date   2015-12-29
 */
public class ClientMode implements Mode {

    private Socket socket = null;
    private SimpleSocketIOView sView = null;
    private HashMap<String, ConnectionThread> listConnections = null;
    
    public ClientMode(SimpleSocketIOView sView) {
        this.sView = sView;
        listConnections = new HashMap<String, ConnectionThread>();            
    }

    public void connectToServer() {
        
        try {
            
            String idConn = sView.getRemoteHost() + ":" + sView.getRemotePort();
            if(!listConnections.containsKey(idConn)) {
                
                socket = new Socket(sView.getRemoteHost(), sView.getRemotePort());
                ConnectionThread handlerConn = new ConnectionThread(socket, this);
                handlerConn.start();
            }
            else {
                sView.setMessageClientRec(new Color(174, 182, 193), "This connection is already in use", null, null);
            }
        } catch (IOException e) {
            sView.setMessageClientRec(new Color(174, 182, 193), "The address is not accessible", null, null);
            e.printStackTrace();
        }
    }
    
    @Override
    public void addConnection(String idConn, ConnectionThread handlerConn) {
        
        listConnections.put(idConn, handlerConn);
        sView.setMessageClientRec(new Color(0, 180, 255), "Server " + idConn + " accepted connection", null, null);                
        sView.addClientList(idConn);
        sView.clearInputsClientMode();
    }
    
    @Override
    public void setNameThreadConn(String idConn, ConnectionThread handlerConn) {
        
        handlerConn.setName("Client connection " + idConn);
    }    
    
    @Override
    public void printMsgReceived(String idConn, String msg) {
        
        sView.setMessageClientRec(new Color(155, 255, 0), "Received from " + idConn + " - ", new Color(255, 255, 0), msg);    
    }
        
    @Override
    public void printMsgSend(String idConn, String msg, boolean send) {
        
        if (send) {
            sView.setMessageClientRec(new Color(0, 255, 255), "Sending to " + idConn + " - ", new Color(255, 255, 0), msg);
        } else {
            sView.setMessageClientRec(new Color(0, 255, 255), "Server " + idConn + " inaccessible - ", null, null);
            clearDisconnected(idConn);
        }
    }        

    @Override
    public void clearDisconnected(String idConn) {
        
        sView.setMessageClientRec(new Color(174, 182, 193), "Disconnected " + idConn, null, null);
        if(listConnections.get(idConn).statusConn())
            listConnections.get(idConn).closeConnection();
        listConnections.remove(idConn);
        sView.removeServerList(idConn);
    }    
    
    @Override
    public void disconnect(String idConn) {
        
        listConnections.get(idConn).closeConnection();
    }    
    
    @Override
    public void send(String id, String msg) {
        
        listConnections.get(id).sendMessage(msg);
    }    
    
    @Override
    public void close() {
       
        for (Map.Entry<String, ConnectionThread> entry : listConnections.entrySet())
            entry.getValue().closeConnection();
            
        try {    
            if(socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }      
    
}
