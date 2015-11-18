
public class HelloWorld1 {

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
