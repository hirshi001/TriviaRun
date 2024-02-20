import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;


public class TestHandler {


    public static String getRandomTest() throws IOException {

        int num = new SecureRandom().nextInt(3)+1;
        if(num==1) return Tests.test1;
        if(num==2) return Tests.test2;
        return Tests.test3;
        /*
        BufferedReader br = new BufferedReader(new FileReader(TestHandler.class.getClassLoader().getResource("./tests/test1.txt").toString()));

        StringBuilder sb = new StringBuilder();
        String s;
        while((s=br.readLine())!=null){
            sb.append(s);
            sb.append('\n');
        }
        System.out.println(sb.toString());
        return sb.toString();

         */



    }

}
