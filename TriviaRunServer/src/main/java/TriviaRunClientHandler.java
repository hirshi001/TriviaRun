import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;

public class TriviaRunClientHandler extends ChannelInboundHandlerAdapter{

    private static final Queue<TriviaRunClient> channels = new LinkedList<>();
    private static boolean running = false;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        synchronized (channels){
            channels.add(new TriviaRunClient(ctx));
            System.out.println(channels.size());
            while(channels.size()>=2){
                TriviaRunClient c1 = channels.remove();
                TriviaRunClient c2 = channels.remove();
                c1.getContext().pipeline().remove("Queue");
                c2.getContext().pipeline().remove("Queue");
                c1.setInGame(true);
                c2.setInGame(true);
                new Thread(new Game(c1,c2)).start();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        synchronized (channels) {
            channels.removeIf(new Predicate<TriviaRunClient>() {
                @Override
                public boolean test(TriviaRunClient triviaRunClient) {
                    return triviaRunClient.getContext().equals(ctx);
                }
            });
        }
    }


}
