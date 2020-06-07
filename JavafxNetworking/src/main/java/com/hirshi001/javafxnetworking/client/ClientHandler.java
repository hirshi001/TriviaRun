package com.hirshi001.javafxnetworking.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientHandler{

    private static EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static final String host = "localhost";
    private static final int port = 8080;


    public static void run(Client c){

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(c);
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            workerGroup.shutdownGracefully();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void stop(){
        if(workerGroup==null) return;
        workerGroup.shutdownGracefully();
    }


}
