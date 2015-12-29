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
import java.net.Socket;
import java.util.HashMap;

/**
 *
 * @author Eduardo Adams Wohlfahrt
 * @date   2015-12-29
 */
public class ClientMode implements Runnable {

    private SimpleSocketIOView sView;
    private HashMap<String, ClientThread> listClients = null;
    
    public ClientMode(SimpleSocketIOView sView) {
        this.sView = sView;
    }

    @Override
    public void run() {
        
    }
    
}