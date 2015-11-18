#!/home/rattom/bin/javashebang -f
#START_JSH
#JAVA_HOME=${JAVA_HOME}
#JAVA_OPTS=-Xms32m -Xmx64m
#END_JSH

import java.util.Properties;
import java.util.Enumeration;

public class JavaProperties {

    public static void main(String[] args) {
        Properties p = System.getProperties();
        Enumeration keys = p.keys();
        while (keys.hasMoreElements()) {
          String key = (String)keys.nextElement();
          String value = (String)p.get(key);
          System.out.println(key + "=" + value);
        }
    }

}
