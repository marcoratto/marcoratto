#!/home/rattom/bin/javashebang
#START_JSH
#JAVA_HOME=/opt/java
#JAVA_OPTS=-Xms32m -Xmx64m
#END_JSH

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FormatDate {

	private final static String SYNTAX = " * Parameters: " +
										 "\n" +
										 "               <INPUT_FORMAT> <OUTPUT_FORMAT> <DATE|now>" +
										 "\n" +
										 "\n" +
										 "<INPUT_FORMAT>,<OUTPUT_FORMAT> could be the pattern of the class java.text.SimpleDateFormat:" +
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
			String inputFormat = args[0];
			String outputFormat = args[1];
			String date = args[2];
			SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat, Locale.getDefault());
			Calendar c = Calendar.getInstance();
			if (!date.equalsIgnoreCase("now")) {
				c.setTime(inputFormatter.parse(date));				
			}

			SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat, Locale.getDefault());
			System.out.print(outputFormatter.format(c.getTime()));			
			System.exit(0);			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(3);			
		} 
	}

}
