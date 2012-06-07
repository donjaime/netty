/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.qotm;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ChannelBuffers;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioEventLoop;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * A UDP broadcast client that asks for a quote of the moment (QOTM) to
 * {@link QuoteOfTheMomentServer}.
 *
 * Inspired by <a href="http://java.sun.com/docs/books/tutorial/networking/datagrams/clientServer.html">the official Java tutorial</a>.
 */
public class QuoteOfTheMomentClient {

    private final int port;

    public QuoteOfTheMomentClient(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        Bootstrap b = new Bootstrap();
        try {
            b.eventLoop(new NioEventLoop())
             .channel(new NioDatagramChannel())
             .localAddress(new InetSocketAddress(0))
             .option(ChannelOption.SO_BROADCAST, true)
             .handler(new QuoteOfTheMomentClientHandler());

            Channel ch = b.bind().sync().channel();

            // Broadcast the QOTM request to port 8080.
            ch.write(new DatagramPacket(
                    ChannelBuffers.copiedBuffer("QOTM?", CharsetUtil.UTF_8),
                    new InetSocketAddress("255.255.255.255", port)));

            // QuoteOfTheMomentClientHandler will close the DatagramChannel when a
            // response is received.  If the channel is not closed within 5 seconds,
            // print an error message and quit.
            if (!ch.closeFuture().await(5000)) {
                System.err.println("QOTM request timed out.");
            }
        } finally {
            b.shutdown();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new QuoteOfTheMomentClient(port).run();
    }
}