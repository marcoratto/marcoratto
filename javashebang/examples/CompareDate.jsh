#!/home/rattom/bin/javashebang
#START_JSH
#JAVA_HOME=/opt/java
#JAVA_OPTS=-Xms32m -Xmx64m
#END_JSH

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CompareDate {

	private final static String SYNTAX = "Parameters: <FORMAT> <FIRST_DATE|now> <LAST_DATE|now>" +
                                        "\n" +
                                        "\n" +
                                        "<FORMAT> could be the pattern of the class java.text.SimpleDateFormat:" +
                                        "\n" +
                                        "\n" +
										 "Letter 	Date or Time Component" + 	
										 "\n" +
										 "G 	Era designator (Text)" +
										 "\n" +
										 "y 	Year (Year)" +
										 "\n" +
										 "M 	Month in year (Month)" +
										 "\n" +
										 "w 	Week in year (Number)" +
										 "\n" +
										 "W 	Week in month (Number)" +
										 "\n" +
										 "D 	Day in year (Number)" +
										 "\n" +
										 "d 	Day in month (Number)" +
										 "\n" +
										 "F 	Day of week in month (Number)" +
										 "\n" +
										 "E 	Day in week (Text)" +
										 "\n" +
										 "a 	Am/pm marker (Text)" +
										 "\n" +
										 "H 	Hour in day (0-23) (Number)" +
										 "\n" +
										 "k 	Hour in day (1-24) (Number)" +
										 "\n" +
										 "K 	Hour in am/pm (0-11) (Number)" +
										 "\n" +
										 "h 	Hour in am/pm (1-12) (Number)" +
										 "\n" +
										 "m 	Minute in hour (Number)" +
										 "\n" +
										 "s 	Second in minute (Number)" +
										 "\n" +
										 "S 	Millisecond (Number)" +
										 "\n" +
										 "z 	Time zone (General time zone)" +
										 "\n" +
										 "Z 	Time zone (RFC 822 time zone)"; 
	
	public static void main(String[] args){
		if (args.length != 3) {
			System.err.println(SYNTAX);
			System.exit(1);			
		}
		try {
			String pattern = args[0];
			String firstDate = args[1].trim().toLowerCase();
			String lastDate = args[2].trim().toLowerCase();
			SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
			Calendar fc = Calendar.getInstance();
			if (!firstDate.equalsIgnoreCase("now")) {
				fc.setTime(formatter.parse(firstDate));				
			}
			Calendar lc = Calendar.getInstance();
			if (!lastDate.equalsIgnoreCase("now")) {
				lc.setTime(formatter.parse(lastDate));				
			}						
			long interval = (lc.getTimeInMillis() - fc.getTimeInMillis()) / (24 * 60 * 60 * 1000);
			System.out.print(interval);			
			System.exit(0);			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(3);			
		} 
	}

}
