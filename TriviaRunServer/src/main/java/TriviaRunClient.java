import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TriviaRunClient extends ByteToMessageDecoder {

    private ChannelHandlerContext ctx;
    private boolean inGame;
    private int score;
    private Game boss;
    private boolean done;
    private long endTime;

    public TriviaRunClient(ChannelHandlerContext ctx){
        this.ctx = ctx;
        this.boss = boss;
        ctx.pipeline().addLast(this);
        inGame = false;
        score = 0;
    }

    public void setBoss(Game boss){
        this.boss = boss;
    }

    public int getScore(){return score;}

    public ChannelHandlerContext getContext(){return ctx;}

    public void setInGame(boolean inGame){this.inGame = inGame;}

    public boolean inGame(){return inGame;}

    public int setScore(int score){
        this.score = score;
        return score;
    }

    public boolean isDone(){return done;}

    public long getEndTime(){return endTime;}

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()<4) return;
        int score = in.readInt();
        if(score==-1){
            done = true;
            endTime = System.currentTimeMillis();
            boss.testFinished();
            System.out.println("DONE");
            return;
        }
        this.score = score;
        if(boss!=null) boss.updateScore(score, this);
        System.out.println("Score: " + score);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        done = true;
        endTime = Long.MAX_VALUE;
        score = -1;
    }
}
