package com.hirshi001.javafxnetworking.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class GameConnectionHandler extends ByteToMessageDecoder {

    private Client c;
    private boolean sizeRead = false;
    private boolean testRead = false;
    private int size;


    public GameConnectionHandler(Client c){
        this.c = c;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(sizeRead){
            readTest(ctx, in,4);
            return;
        }
        else if (in.readableBytes() < 4){
            return;
        }
        sizeRead = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) sb.append((char) in.readByte());
        size = Integer.parseInt(sb.toString());
        readTest(ctx, in,4);

    }


    private void readTest(ChannelHandlerContext ctx, ByteBuf in, int index) throws Exception{
        if(testRead) return;
        System.out.println("readable bytes" + in.readableBytes() + " : size"+size);
        if(in.readableBytes()<size) return;
        testRead = true;
        String test = in.readBytes(size).toString(Charset.defaultCharset());
        System.out.println("TEST "+test.length());
        new TestHandler(c).channelRead(ctx,test);
    }
}
