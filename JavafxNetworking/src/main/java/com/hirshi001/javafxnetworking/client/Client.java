package com.hirshi001.javafxnetworking.client;

import com.hirshi001.javafxnetworking.MainApp;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import javafx.application.Platform;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Client extends ByteToMessageDecoder {

    private int score;
    private int opponentScore;

    private ArrayList<Question> questions;
    private int currQuestion;

    private MainApp app;

    private ChannelHandlerContext ctx;

    public Client(MainApp app){
        this.app = app;
        score = 0;
        opponentScore = 0;
        currQuestion = 0;
        questions = new ArrayList<>();
    }

    public void addQuestion(Question q){
        questions.add(q);
    }

    public ArrayList<Question> getQuestions(){
        return questions;
    }


    public Question getNextQuestion(){
        return questions.get(currQuestion++);
    }

    public boolean hasNextQuestion(){
        return questions.size()>currQuestion;
    }

    public void ready(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                app.startingGame();
            }
        });
    }

    public void setScore(int score){this.score = score;}
    public void incrementScore(){this.score++;}
    public int getScore(){return this.score;}

    public void updateScore(){
        ByteBuf out = ctx.alloc().buffer(4);
        out.writeInt(getScore());
        ctx.writeAndFlush(out);
    }

    public void isDone(){
        ByteBuf out = ctx.alloc().buffer(4);
        out.writeInt(-1);
        ctx.writeAndFlush(out);
    }


    private int readState = 0;
    private int size = 0;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.ctx = ctx;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(readState == 3){
            if(in.readableBytes()<4) return;
            opponentScore = in.readInt();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    app.updateOpponentScore(opponentScore);
                }
            });


        }
        if (readState == 2) {
            if(in.readableBytes()<1) return;
            in.readByte();
            readState = 3;
            this.ready();

        }
        if(readState == 1){
            readTest(ctx, in);
            return;
        }
        if (readState == 0 && in.readableBytes() >= 4){
            readState = 1;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) sb.append((char) in.readByte());
            size = Integer.parseInt(sb.toString());
            readTest(ctx, in);
            return;
        }


    }


    private void readTest(ChannelHandlerContext ctx, ByteBuf in) throws Exception{
        if(readState>=2) return;
        System.out.println("readable bytes" + in.readableBytes() + " : size"+size);
        if(in.readableBytes()<size) return;
        readState = 2;
        String test = in.readBytes(size).toString(Charset.defaultCharset());
        System.out.println("TEST "+test.length());
        new TestHandler(this).channelRead(ctx,test);
    }
}
