#!/home/rattom/bin/javashebang -D -f -V
#START_JSH
#JAVA_HOME=/opt/java
#JAVA_CLASSPATH=/opt/MQJExplorer/lib/log4j/log4j-1.2.15.jar
#JAVAC_CLASSPATH=/opt/MQJExplorer/lib/jhbasic.jar:/opt/MQJExplorer/lib/log4j/log4j-1.2.15.jar
#JAVA_OPTS=-Xms256m -Xmx1024m
#END_JSH

public class HelloWorld7 {

    public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Parameters: VALUE");
			System.exit(1);			
		}
		System.out.println("There are " + args.length + " parameters:");		
        for (int j=0; j<args.length; j++) {
			System.out.println("args[" + j + "]=" + args[j]);
        }
        System.exit(0);
    }
}
