import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.IOException;

public class Game implements Runnable{

    private TriviaRunClient client1, client2;

    private boolean client1loaded, client2loaded;
    private long startTime;

    public Game(TriviaRunClient c1, TriviaRunClient c2){
        this.client1 = c1;
        this.client2 = c2;
        c1.setBoss(this);
        c2.setBoss(this);

    }


    @Override
    public void run() {
        try {
            String test = TestHandler.getRandomTest();
            StringBuilder sb = new StringBuilder();
            String length = String.valueOf(test.length()+1);
            for(int i=0;i<4-length.length();i++){
                sb.append(0);
            }
            sb.append(length);
            sb.append("\n");
            sb.append(test);
            ByteBuf b1 = Unpooled.wrappedBuffer(sb.toString().getBytes());
            ByteBuf b2 = Unpooled.wrappedBuffer(sb.toString().getBytes());

            ChannelHandlerContext ctx1 = client1.getContext();
            final ChannelFuture f1 = ctx1.writeAndFlush(b1);
            f1.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(client2loaded) new Thread(Game.this::runGame).start();
                    client1loaded = true;
                }
            });

            ChannelHandlerContext ctx2 = client2.getContext();
            final ChannelFuture f2 = ctx2.writeAndFlush(b2);
            f2.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(client1loaded) new Thread(Game.this::runGame).start();
                    client2loaded = true;
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runGame(){
        startTime = System.currentTimeMillis();
        ChannelHandlerContext ctx1 = client1.getContext();
        ChannelHandlerContext ctx2 = client2.getContext();


        ByteBuf b1 = ctx1.alloc().buffer(1);
        b1.writeByte(0);
        ctx1.writeAndFlush(b1);

        ByteBuf b2 =  ctx2.alloc().buffer(1);
        b2.writeByte(0);
        ctx2.writeAndFlush(b2);

    }

    private void endGame(){
        //this will garbage collect Client objects
        client1 = null;
        client2 = null;

        //Client objects lose reference to this game object so this is garbage collected as well.

    }

    public void testFinished(){
        if(client1.isDone() && client2.isDone()){
            if(client1.getScore()>client2.getScore()){
                winner(client1, client2);
            }
            else if(client1.getScore()<client2.getScore()){
                winner(client2, client1);
            }
            else{
                if(client1.getEndTime()<client2.getEndTime()){
                    winner(client1, client2);
                }
                else{
                    winner(client2, client1);
                }
            }
        }
    }

    private void winner(TriviaRunClient winner, TriviaRunClient loser){
        updateOpponentClientScore(-1, winner);
        updateOpponentClientScore(-2, loser);
    }

    public void updateScore(int score, TriviaRunClient c){
        if(c.equals(client1)){
            updateOpponentClientScore(score, client2);
        }
        else if(c.equals(client2)){
            updateOpponentClientScore(score, client1);
        }
    }

    private void updateOpponentClientScore(int score, TriviaRunClient c){
        ChannelHandlerContext ctx = c.getContext();
        ByteBuf b = ctx.alloc().buffer(4);
        b.writeInt(score);
        ctx.writeAndFlush(b);

    }

}
