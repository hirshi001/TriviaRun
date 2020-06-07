package com.hirshi001.javafxnetworking.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Scanner;

public class TestHandler {

    private Client c;

    public TestHandler(Client c){
        this.c = c;
    }

    public void channelRead(ChannelHandlerContext ctx, String msg) throws Exception {
        String test = msg;
        System.out.println("test::-------");
        System.out.println(test);

        Scanner scanner = new Scanner(test);
        scanner.nextLine();
        Question q;
        int i=0;
        synchronized (c) {
            while (scanner.hasNextLine()) {
                i++;
                //System.out.println(i);
                String s1 = scanner.nextLine();
                String s2 = scanner.nextLine();
                String s3 = scanner.nextLine();
                String s4 = scanner.nextLine();
                String s5 = scanner.nextLine();
                String s6 = scanner.nextLine();
                q = new Question(s1,s2,s3,s4,s5,Integer.parseInt(s6));
                c.addQuestion(q);
            }
        }

    }
}
