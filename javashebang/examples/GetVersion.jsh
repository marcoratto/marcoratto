#!/home/rattom/bin/javashebang -f
#START_JSH
#JAVA_HOME=/opt/java
#JAVA_OPTS=-Xms32m -Xmx64m
#END_JSH

public class GetVersion {

    public static void main(String[] args) {
        System.out.println("java.version is '" + System.getProperty("java.version") + "'");
    }
}
