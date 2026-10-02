import com.ys.foods.*;
import java.io.*;
import java.util.*;
public class ParserTest {
    public static void main(String[] a) throws Exception {
        String[] f={"blessings","smell","shehechiyanu"}; String[] c={"hanenin","reach","tolahim"};
        for (int i=0;i<3;i++){
            BufferedReader r=new BufferedReader(new InputStreamReader(new FileInputStream("app/src/main/assets/"+f[i]+".txt"),"UTF-8"));
            List<Entry> l=EntryParser.parse(c[i],r);
            int empty=0; for(Entry e:l) if(e.info.length()==0) empty++;
            System.out.println(f[i]+": entries="+l.size()+" emptyInfo="+empty+" firstKey="+l.get(0).key().length());
        }
    }
}
