#!/home/rattom/bin/javashebang
#START_JSH
#JAVA_HOME=${JAVA_HOME}
#JAVA_OPTS=-Xms256m -Xmx1024m
#END_JSH

public class HelloWorld {

    public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Parameters: VALUES");
			System.exit(1);
		}
		System.out.println("There are " + args.length + " parameters:");
        for (int j=0; j<args.length; j++) {
			System.out.println("args[" + j + "]=" + args[j]);
        }
        System.exit(0);
    }
}