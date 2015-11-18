
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DiffDate {

        private final static String SYNTAX = "Parameters: <FORMAT> <DATE|now> <[-]NUMBER> <D|M|Y>" +
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
                if (args.length != 4) {
                        System.err.println(SYNTAX);
                        System.exit(1);
                }
                try {
                        String pattern = args[0];
                        String date = args[1];
                        int number = Integer.parseInt(args[2], 10);
                        String what = args[3];
                        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                        Calendar c = Calendar.getInstance();
                        if (!date.equalsIgnoreCase("now")) {
                                c.setTime(formatter.parse(date));
                        }
                        if (what.equalsIgnoreCase("Y")) {
                                c.add(Calendar.YEAR, number);
                        } else if (what.equalsIgnoreCase("M")) {
                                c.add(Calendar.MONTH, number);
                        } else if (what.equalsIgnoreCase("D")) {
                                c.add(Calendar.DATE, number);
                        } else {
                                System.err.println(SYNTAX);
                                System.exit(2);
                        }
                        System.out.print(formatter.format(c.getTime()));
                        System.exit(0);
                } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(3);
                }
        }

}
