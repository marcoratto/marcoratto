#!/usr/bin/javashebang 
#START_JSH
#JAVA_HOME=${JAVA_HOME}
#JAVA_CLASSPATH=${JAVA_LIBS_DIR}/javamail-1.5.4/gimap-1.5.4.jar:${JAVA_LIBS_DIR}/javamail-1.5.4/smtp-1.5.4.jar:${JAVA_LIBS_DIR}/javamail-1.5.4/logging-mailhandler-1.5.4.jar:${JAVA_LIBS_DIR}/javamail-1.5.4/javax.mail-api-1.5.4.jar:${JAVA_LIBS_DIR}/javamail-1.5.4/mailapi-1.5.4.jar:${JAVA_LIBS_DIR}/javamail-1.5.4/dsn-1.5.4.jar:${JAVA_LIBS_DIR}/javamail-1.5.4/imap-1.5.4.jar:${JAVA_LIBS_DIR}/javamail-1.5.4/pop3-1.5.4.jar:${JAVA_LIBS_DIR}/javamail-1.5.4/javax.mail.jar
#JAVA_OPTS=-Xms256m -Xmx1024m
#END_JSH

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

public class SendMail {

    private Utility utility = new Utility();

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println(help);
			System.exit(1);
		}
		SendMail sendMail = new SendMail();
		int retCode = 0;
		try {
			retCode = sendMail.runme(args);
		} catch (Throwable t) {
			System.err.println(t.getMessage());
		}
		if (retCode == 0) {
			System.out.println("Email sent.");				
		}
		System.exit(retCode);
	}

	private int runme(String[] args) throws Exception {
		String mailHost = null;
		int mailPort = -1;
		String mailUser = null;
		String mailPass = null;
		String mailFROM = null;
		String[] mailTO = null;
		String[] mailCC = null;
		String[] mailBCC = null;
		String subject = null;
		String date = null;
		String formatDate = null;
		String text = null;
		File[] file = null;
		boolean ssl = false;
		boolean debug = false;
		boolean verbose = false;
		boolean html = false;
		String charset = utility.DEFAULT_FILE_ENCODING;
			
		for (int i = 0; i < args.length; i++) {
			if ((args[i].equalsIgnoreCase("-help") == true) || 
				(args[i].equalsIgnoreCase("-?") == true) ||
				(args[i].equalsIgnoreCase("--help") == true) ||
				(args[i].equalsIgnoreCase("-h") == true)) {
				System.err.println(help);
				return 1;
			} else if (args[i].equalsIgnoreCase("-host")) {
				if (++i < args.length) {
					mailHost = args[i];
				} else {
					System.err.println("Parameter '-host' required a value!");
					return 1;
				}
			} else if (args[i].equalsIgnoreCase("-port")) {
				if (++i < args.length) {
					mailPort = Integer.parseInt(args[i]);
				} else {
					System.err.println("Parameter '-port' required a value!");
					return 1;
				}
			} else if (args[i].equalsIgnoreCase("-user")) {
				if (++i < args.length) {
					mailUser = args[i];
				} else {
					System.err.println("Parameter '-user' required a value!");
					return 1;
				}
			} else if (args[i].equalsIgnoreCase("-pass")) {
				if (++i < args.length) {
					mailPass = args[i];
				} else {
					System.err.println("Parameter '-pass' required a value!");
					return 1;
				}
			} else if (args[i].equalsIgnoreCase("-from")) {
				if (++i < args.length) {
					mailFROM = args[i];
				} else {
					System.err.println("Parameter '-from' required a value!");
					return 1;
				}
			} else if (args[i].equalsIgnoreCase("-date")) {
				if (++i < args.length) {
					date = args[i];
				} else {
					System.err.println("Parameter '-date' required a value!");
					return 1;
				}
			} else if (args[i].equalsIgnoreCase("-format_date")) {
				if (++i < args.length) {
					formatDate = args[i];
				} else {
					System.err.println("Parameter '-format_date' required a value!");
					return 1;
				} 			
			} else if (args[i].equalsIgnoreCase("-to")) {
				if (++i < args.length) {
					mailTO = utility.stringToStringArray(args[i], ",");
				} else {
					System.err.println("Parameter '-to' required a value!");
					return 1;
				} 
			} else if (args[i].equalsIgnoreCase("-cc")) {
				if (++i < args.length) {
					mailCC = utility.stringToStringArray(args[i], ",");
				} else {
					System.err.println("Parameter '-cc' required a value!");
					return 1;
				} 
			} else if (args[i].equalsIgnoreCase("-bcc")) {
				if (++i < args.length) {
					mailBCC = utility.stringToStringArray(args[i], ",");
				} else {
					System.err.println("Parameter '-bcc' required a value!");
					return 1;
				} 
			} else if (args[i].equalsIgnoreCase("-subject")) {
				if (++i < args.length) {
					subject = args[i];
				} else {
					System.err.println("Parameter '-subject' required a value!");
					return 1;
				} 
			} else if (args[i].equalsIgnoreCase("-text")) {
				if (++i <= args.length) {
					text = args[i];
				} else {
					System.err.println("Parameter '-text' required a value!");
					return 1;
				} 
			} else if (args[i].equalsIgnoreCase("-charset")) {
				if (++i < args.length) {
					charset = args[i];
				} else {
					System.err.println("Parameter '-charset' required a value!");
					return 1;
				}				
			} else if (args[i].equalsIgnoreCase("-ssl")) {
				ssl = true;
			} else if (args[i].equalsIgnoreCase("-debug")) {
				debug = true;
				utility.setDebug(true);
			} else if (args[i].equalsIgnoreCase("-verbose")) {
				verbose = true;
				utility.setVerbose(true);
			} else if (args[i].equalsIgnoreCase("-html")) {
				html = true;
			} else if (args[i].equalsIgnoreCase("-file")) {
				if (++i < args.length) {
					file = utility.stringToFileArray(args[i], ",");
				} else {
					System.err.println("Parameter '-file' required a value!");
					return 1;
				} 
			} else {
				System.err.println("Parameter '" + args[i] + "' unknown!");
				return 2;
			}
		}
		
		if ((mailHost == null) || (mailHost.trim().length() == 0)) {
			System.err.println("Parameter '-host' is mandatory!");
			return 3;
		}
		if (mailPort == -1) {
			System.err.println("Parameter '-port' is mandatory!");
			return 4;
		}
		if ((mailFROM == null) || (mailFROM.trim().length() == 0)) {
			System.err.println("Parameter '-from' is mandatory!");
			return 5;
		}
		if ((mailTO == null) || (mailTO.length == 0)) {
			System.err.println("Parameter '-to' is mandatory!");
			return 6;
		}
		if ((subject == null) || (subject.trim().length() == 0)) {
			System.err.println("Parameter '-subject' is mandatory!");
			return 7;
		}
		if ((text == null) || (text.trim().length() == 0)) {
			System.err.println("Parameter '-text' is mandatory!");
			return 8;
		}
		
		SmtpUtility smtp = new SmtpUtility();
		smtp.setUsername(mailUser);
		smtp.setPassword(mailPass);
		smtp.setHost(mailHost);
		smtp.setPort(mailPort);
		smtp.setDebug(debug);
		smtp.setVerbose(verbose);
		smtp.setFormatHTML(html);
		smtp.setFROM(mailFROM);
		smtp.setSSL(ssl);
		smtp.setSentDate(date, formatDate);
		smtp.connect();

		for (int i = 0; i < mailTO.length; i++) {
			smtp.addTO(mailTO[i]);
		}
		if (mailCC != null) {
			for (int i = 0; i < mailCC.length; i++) {
				smtp.addCC(mailCC[i]);
			}
		}
		if (mailBCC != null) {
			for (int i = 0; i < mailBCC.length; i++) {
				smtp.addBCC(mailBCC[i]);
			}
		}
		if (file != null) {
			for (int i = 0; i < file.length; i++) {
				smtp.addFile(file[i]);
			}
		}

		File fileText = new File(text);
		if (fileText.exists()) {
			smtp.setText(utility.fileToString(fileText, charset));
		} else {
			smtp.setText(text);
		}
		smtp.setSubject(subject);

		smtp.send();
		
		return 0;
	}
	
	private static String help = " * Mandatory Parameters:" +
			 "\n" +
			"-h|-?|-help|--help\n\tThis help" +
			 "\n" +
			"-host\n\thostname or servername of the SMTP Server" +
			 "\n" +
			"-port\n\tPort (default is 25)" +
			 "\n" +
			"-user\n\tUsername" +			
			 "\n" +
			"-pass\n\tPassword" +
			 "\n" +
			"-from\n\tEmail sender address" +
			 "\n" +
			"-to\n\tList of email address (comma separated)" +
			 "\n" +
			"-subject\n\tSubject of the email" +
			 "\n" +
			"-text\n\tContent of the email (string or a file)" +
			 "\n" +
			 "\n" +
			"* Optional Parameter:" +
			 "\n" +
			 "\n" +
			"-file\n\tList of files (comma separated) to attach to the email" +
			 "\n" +
			"-cc\n\tList of email address (comma separated)" +
			"\n" +
			"-bcc\n\tList of files (comma separated) to attach to the email" +
			"\n" +
			"-date\n\tDate of the email" +
			 "\n" +
			"-format_date\n\tFormat of the date" +
			 "\n" +
			"-html\n\tThe email message is in HTML format (default is false)" +
			 "\n" +
			"-ssl\n\tThe server uses SSL" +	
			"\n" +
			"-debug\n\tDebug (default is false)" +
			"\n" +
			"-verbose\n\tVerbose (default is false)" +
			"\n";

class Utility {
		
	public final String NEWLINE = System.getProperty("line.separator");	
	public final String DEFAULT_FILE_ENCODING = "UTF-8";
	
	private boolean flagDebug = false;
	private boolean flagVerbose = false;
	   
	public String fileToString(File file, String encoding) throws IOException {
		
		BufferedReader br = null;
		StringBuffer out = new StringBuffer("");
		try {
			this.verbose("Try to read file " + file.getAbsolutePath().toString());
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			this.debug("Opened file '" + file + " ' with the encoding '" + encoding + "'");
			String line = null;
			String space = "";
			while ((line = br.readLine()) != null) {
				this.debug("Read from file the line '" + line + "'");
				out.append(space);
				out.append(line);
				space = NEWLINE;
			}
			this.verbose("Buffer length is " + out.length());
		} finally {
			if (br != null) {
				try {
						br.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
		return out.toString();
	}
	
	public File[] stringToFileArray(String s, String sep) {
		File[] out = null;
		if (s == null) {
			return out;
		}
		try {
			Vector<File> v = new Vector<File>(0, 1);
			StringTokenizer t = new StringTokenizer(s, sep);
			while (t.hasMoreTokens()) {
				String token = t.nextToken();
				this.debug("token is '" + token + "'");
				File f = new File(token);
				if (f.getAbsoluteFile().exists()) {
					this.debug("File is '" + f + "'");
					v.addElement(f.getAbsoluteFile());
				}
			}
			out = new File[v.size()];
			for (int i = 0; i < v.size(); i++) {
				out[i] = (File) v.elementAt(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	public String[] stringToStringArray(String s, String sep) {
		if (s == null) {
			return null;
		}
		try {
			Vector<String> v = new Vector<String>();
			StringTokenizer t = new StringTokenizer(s, sep);
			while (t.hasMoreTokens()) {
				String token = t.nextToken();
				this.debug("token is '" + token + "'");
				v.addElement(token);
			}
			return (String[]) v.toArray(new String[v.size()]);
		} catch (Exception e) {
		}
		return null;
	}
	
	public void debug(String msg) {
		if (this.flagDebug) {
			System.out.println(msg);
		}
	}

	public void verbose(String msg) {
		if (this.flagVerbose) {
			System.out.println(msg);
		}
	}

	public boolean isDebug() {
		return flagDebug;
	}

	public void setDebug(boolean value) {
		this.flagDebug = value;
	}

	public boolean isVerbose() {
		return flagVerbose;
	}

	public void setVerbose(boolean value) {
		this.flagVerbose = value;
	}
	
}

class SmtpUtilityException extends Exception {

	private static final long serialVersionUID = 8890350194295543450L;

  public SmtpUtilityException() {
    super();
  }

  public SmtpUtilityException(String msg) {
    super(msg);
  }

  public SmtpUtilityException(Exception e) {
    super(e.toString());
    e.printStackTrace();
  }
}            

class SmtpUtility {

	public SmtpUtility() {
		this.init();
	}

	private void init() {
		this.toAddressList = new Vector<Address>();
		this.ccAddressList = new Vector<Address>();
		this.bccAddressList = new Vector<Address>();

		this.fileList = new Vector<File>();
	}

	public boolean isSSL() {
		return flagSSL;
	}

	public void setSSL(boolean value) {
		this.flagSSL = value;
		utility.debug("flagSSL set to '" + this.flagSSL + "'");
	}

	public void setUsername(String value) {
		this.user = value;
		utility.debug("user set to '" + this.user + "'");
	}

	public String getUsername() {
		return this.user;
	}

	public void setPassword(String value) {
		this.pass = value;
		utility.debug("pass set to '" + this.pass + "'");
	}

	public String getPassword() {
		return this.pass;
	}

	public void setHost(String value) {
		this.host = value;
		utility.debug("host set to '" + this.host + "'");
	}

	public String getHost() {
		return this.host;
	}

	public void setPort(int value) {
		this.port = value;
		utility.debug("port set to '" + this.port + "'");
	}

	public int getPort() {
		return this.port;
	}

	public void addFile(File value) {
		if (value != null) {
			this.fileList.addElement(value);
		}
	}

	public void setFROM(String email, String name) throws SmtpUtilityException {
		try {
			this.fromAddress = new InternetAddress(email, name);
		} catch (UnsupportedEncodingException e) {
			throw new SmtpUtilityException(e);
		}
	}

	public void setFROM(String email) throws SmtpUtilityException {
		try {
			this.fromAddress = new InternetAddress(email);
		} catch (AddressException e) {
			throw new SmtpUtilityException(e);
		}
	}

	public void addTO(String email) throws SmtpUtilityException {
		try {
			Address address = new InternetAddress(email, email);
			this.toAddressList.addElement(address);
		} catch (UnsupportedEncodingException e) {
			throw new SmtpUtilityException(e);
		}
	}

	public void addCC(String email) throws SmtpUtilityException {
		try {
			Address address = new InternetAddress(email, email);
			this.ccAddressList.addElement(address);
		} catch (UnsupportedEncodingException e) {
			throw new SmtpUtilityException(e);
		}
	}

	public void addBCC(String email) throws SmtpUtilityException {
		try {
			Address address = new InternetAddress(email, email);
			this.bccAddressList.addElement(address);
		} catch (UnsupportedEncodingException e) {
			throw new SmtpUtilityException(e);
		}
	}

	public void setSubject(String value) {
		this.subject = value;
	}

	public void setText(String value) {
		this.text = value;
	}

	public void setText(StringBuffer value) {
		this.text = value.toString();
	}

	public void setDebug(boolean value) {
		this.flagDebug = value;
	}

	public void setFormatHTML(boolean value) {
		this.flagFormatHTML = value;
	}

	public void connect() {
		Properties props = new Properties();

		// Setup mail server
		props.put("mail.smtp.host", this.host);
		props.put("mail.smtp.port", String.valueOf(this.port));
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.user", this.user);
		if (this.flagSSL) {
			props.setProperty("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
		}

		// Get session
		if ((this.user != null) && (this.pass != null)) {
			props.put("mail.smtp.user", this.user);
			props.put("mail.smtp.auth", "true");
			Authenticator auth = new SMTPAuthenticator();
			session = Session.getDefaultInstance(props, auth);
		} else {
			session = Session.getInstance(props);
		}
		if (this.flagDebug) {
			Enumeration keys = props.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = props.getProperty(key);
				utility.debug(key + " is '" + value + "'");
			}
		}

		this.session.setDebug(this.flagDebug);
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public void setSentDate(String value, String format) {
		if ((value != null) && (format != null)) {
			SimpleDateFormat dt = new SimpleDateFormat(format);
			try {
				this.sentDate = dt.parse(value);
			} catch (ParseException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public boolean isVerbose() {
		return flagVerbose;
	}

	public void setVerbose(boolean value) {
		this.flagVerbose = value;
	}

	public void send() throws Exception {
		// Define message
		MimeMessage message = new MimeMessage(session);

		Address[] toAddress = new Address[this.toAddressList.size()];
		for (int j = 0; j < this.toAddressList.size(); j++) {
			toAddress[j] = (Address) this.toAddressList.elementAt(j);
		}

		Address[] ccAddress = new Address[this.ccAddressList.size()];
		for (int j = 0; j < this.ccAddressList.size(); j++) {
			ccAddress[j] = (Address) this.ccAddressList.elementAt(j);
		}

		Address[] bccAddress = new Address[this.bccAddressList.size()];
		for (int j = 0; j < this.bccAddressList.size(); j++) {
			bccAddress[j] = (Address) this.bccAddressList.elementAt(j);
		}

		message.setFrom(fromAddress);

		message.setRecipients(Message.RecipientType.TO, toAddress);

		message.setRecipients(Message.RecipientType.CC, ccAddress);

		message.setRecipients(Message.RecipientType.BCC, bccAddress);

		message.setSubject(this.subject);

		// Create the multi-part
		Multipart multipart = new MimeMultipart();

		// Create part one
		MimeBodyPart messageBodyPart = new MimeBodyPart();

		// Fill the message
		if (this.flagFormatHTML) {
			messageBodyPart.setContent(this.text, "text/html");
		} else {
			// Fill the message
			messageBodyPart.setText(this.text, "text/plain");
		}

		// Add the first part
		multipart.addBodyPart(messageBodyPart);

		for (int j = 0; j < this.fileList.size(); j++) {
			File file = (File) this.fileList.elementAt(j);

			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			// DataSource source = new ByteArrayDataSource("ciao",
			// "application/x-any");
			DataSource source = new FileDataSource(file);

			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(file.getAbsoluteFile().getName());

			// Add the second part
			multipart.addBodyPart(messageBodyPart);
		}
		// Put parts in message
		message.setContent(multipart);

		// Put sent date
		if (this.sentDate != null) {
			System.out.println("SendDate forced to " + this.sentDate);
			message.setSentDate(this.sentDate);
		} else {
			message.setSentDate(new Date());
		}

		Transport trans = session.getTransport();
		trans.connect(host, this.port, user, pass);
		Transport.send(message);

	}

	private class SMTPAuthenticator extends Authenticator {

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, pass);
		}
	}

	public final static int DEFAULT_PORT_SMTP = 25;
	public final static int DEFAULT_PORT_SMTPS = 465;

	private String user = null;
	private String pass = null;

	private String host = null;
	private int port = DEFAULT_PORT_SMTP;

	private Address fromAddress = null;

	private Vector<Address> toAddressList = null;
	private Vector<Address> ccAddressList = null;
	private Vector<Address> bccAddressList = null;

	private Session session = null;

	private String subject = null;
	private String text = null;
	private Date sentDate = null;

	private Vector<File> fileList = null;

	private boolean flagSSL = false;
	private boolean flagDebug = false;
	private boolean flagVerbose = false;
	private boolean flagFormatHTML = false;

}

class Pop3UtilityException extends Exception {

	private static final long serialVersionUID = -4843168423010062714L;

public Pop3UtilityException() {
    super();
  }

  public Pop3UtilityException(String msg) {
    super(msg);
  }

  public Pop3UtilityException(Exception e) {
    super(e.toString());
    e.printStackTrace();
  }
}

class Pop3Utility {

  public Pop3Utility()  {
  }

  public boolean isSSL() {
		return flagSSL;
	}

  public void setSSL(boolean flagSSL) {
		this.flagSSL = flagSSL;
  }
	
  public void setHost(String s) {
    this.host = s;
  }

  public String getHost() {
    return this.host;
  }

  public void setPort(int i) {
    this.port = i;
  }

  public int getPort() {
    return this.port;
  }

  public void setUser(String s) {
    this.user = s;
  }

  public String getUser() {
    return this.user;
  }

  public void setPass(String s) {
    this.pass = s;
  }

  public String getPass() {
    return this.pass;
  }

  public void setDebug(boolean b) {
    this.flagDebug = b;
  }

  public int getTotalInboxMessage() {
    return this.message.length;
  }

  public Message getInboxMessage(int i) {
    return this.message[i];
  }

  public void readInbox() throws Pop3UtilityException{
    try {
      this.folder = this.store.getFolder("INBOX");
      this.folder.open(Folder.READ_WRITE);
      this.message = folder.getMessages();
    } catch (MessagingException e) {
      throw new Pop3UtilityException(e);
    }
  }

  public void connect() throws Pop3UtilityException {
     try {
      Properties props = new Properties();
      props.setProperty("mail.pop3.host", host);
      props.setProperty("mail.pop3.port", String.valueOf(port));
      if (this.flagSSL) {
          props.setProperty("javax.mail.pop3.starttls.enable", "true");  
          props.setProperty("javax.mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");  
          props.setProperty("javax.mail.pop3.socketFactory.fallback", "false");  
      }

      this.session = Session.getInstance(props, null);
	  this.session.setDebug(this.flagDebug);

	  if (this.flagSSL) {
	      this.store = session.getStore(PROTOCOL_POP3S);
  	  } else {
	      this.store = session.getStore(PROTOCOL_POP3);		  
	  }

      this.store.connect(this.host,
                         this.port,
                         this.user,
                         this.pass);
      

    } catch (MessagingException e) {
    	throw new Pop3UtilityException(e);
    }
  }

  public InternetAddress getFrom(Message m) throws Pop3UtilityException {
    return this.getListFrom(m)[0];
  }

  public InternetAddress[] getListFrom(Message m) throws Pop3UtilityException {
    InternetAddress[] out = null;
    try {
      InternetAddress address[] = (InternetAddress[]) m.getFrom();
      out = new InternetAddress[address.length];
      for (int i = 0; i < address.length; i++) {
        out[i] = address[i];
      }
    } catch (MessagingException e) {
    	throw new Pop3UtilityException(e);
    }
    return out;
  }

  public int getTotalAttachFile(Message m) throws Pop3UtilityException {
    int out = 0;
    try {
      Object obj = m.getContent();
      if (obj instanceof Multipart) {
        Multipart multipart = (Multipart) obj;
        out = multipart.getCount();
      }
    } catch (MessagingException e) {
    	throw new Pop3UtilityException(e);
    } catch (IOException e) {
    	throw new Pop3UtilityException(e);
    }
    return out;
  }

  public void saveAttachFile(Message m, int index, File aDirectory) throws Pop3UtilityException {
	  if (aDirectory.isDirectory() == false) {
		  throw new Pop3UtilityException(aDirectory + " is not a valid directory!");
	  }
    try {
      Object obj = m.getContent();
      if (obj instanceof Multipart) {
        Multipart multipart = (Multipart) obj;
          Part part = multipart.getBodyPart(index);
          String disposition = part.getDisposition();
          if ((disposition != null) &&
                  (disposition.equals(Part.ATTACHMENT) ||
                  (disposition.equals(Part.INLINE)))) {
	             File fileName = new File(aDirectory, part.getFileName());			         	
				 System.out.println("saveAttachFile():" + fileName);
                   this.saveFile(part.getInputStream(), fileName);
          }
      } else if (obj instanceof MimeMultipart) {
		  System.out.println("saveAttachFile(): allegato di tipo MimeMultipart");
		}
    } catch (MessagingException e) {
    	throw new Pop3UtilityException(e);
    } catch (IOException e) {
    	throw new Pop3UtilityException(e);
    }
  }

  public String getContent(Message m) throws Pop3UtilityException {
    String out = null;
    try {
      Object obj = m.getContent();
      if (obj instanceof String) {
        out = (String) obj;
      } else if (obj instanceof Multipart) {
        Multipart multiPart = (Multipart) m.getContent();
        BodyPart bodyPart = multiPart.getBodyPart(0);
        out = bodyPart.getContent().toString();
      } else if (obj instanceof MimeMultipart) {
        MimeMultipart mimeMultiPart = (MimeMultipart) m.getContent();
		  System.out.println("getContent():mimeMultiPart.getCount()=" + mimeMultiPart.getCount());
        BodyPart bodyPart = mimeMultiPart.getBodyPart(0);
        out = bodyPart.getContent().toString();
		}
    } catch (MessagingException e) {
    	throw new Pop3UtilityException(e);
    } catch (IOException e) {
    	throw new Pop3UtilityException(e);
    }
    return out;
  }

  public String getSubject(Message m) throws Pop3UtilityException {
    String out = null;
    try {
      out = m.getSubject();
    } catch (MessagingException e) {
    	throw new Pop3UtilityException(e);
    }
    return out;
  }

  public void delete(Message m) throws Pop3UtilityException {
    try {
      m.setFlag(Flags.Flag.DELETED, true);
    } catch (MessagingException e) {
    	throw new Pop3UtilityException(e);
    }
  }

  public void archive(Message m) throws Pop3UtilityException {
	    try {
	      m.setFlag(Flags.Flag.SEEN, true);
	    } catch (MessagingException e) {
	    	throw new Pop3UtilityException(e);
	    }
  }

  public Date getSentDate(Message m) throws Pop3UtilityException {
    Date out = null;
    try {
      out = m.getSentDate();
    } catch (MessagingException e) {
    	throw new Pop3UtilityException(e);
    }
    return out;
  }

  public void close() throws Pop3UtilityException {
    try {
      this.folder.close(true);
      this.store.close();
    } catch (MessagingException e) {
    	throw new Pop3UtilityException(e);
    }
  }

  private void saveFile(InputStream is, File output) throws FileNotFoundException, IOException {
    System.out.println("Pop3utility.saveFile():" + output.getAbsoluteFile().toString());
    FileOutputStream fos = null;
    try {
      byte[] b = new byte[1024];
      fos = new FileOutputStream(output);
      while (is.read(b,0,1024) != -1) {
         fos.write(b,0,1024);
      }
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
        }
       }
    }
  }

  public final static int DEFAULT_PORT_POP3 = 110;
  public final static int DEFAULT_PORT_POP3S = 995;

  private final static String PROTOCOL_POP3 = "pop3";
  private final static String  PROTOCOL_POP3S = "pop3s";

  private String host = null;
  private int port = -1;

  private String user = null;
  private String pass = null;

  private boolean flagSSL = false;
  
  private boolean flagDebug = false;

  private Session session = null;
  private Folder folder = null;
  private Store store = null;

  private Message message[] = null;
}

class Imap4UtilityException extends Exception {
	
	private static final long serialVersionUID = -8665260326410730217L;

public Imap4UtilityException() {
    super();
  }

  public Imap4UtilityException(String msg) {
    super(msg);
  }

  public Imap4UtilityException(Exception e) {
    super(e.toString());
    e.printStackTrace();
  }
}

class Imap4Utility {

  public Imap4Utility()  {
  }

  public boolean isSSL() {
		return flagSSL;
	}

  public void setSSL(boolean flagSSL) {
		this.flagSSL = flagSSL;
  }
	
  public void setHost(String s) {
    this.host = s;
  }

  public String getHost() {
    return this.host;
  }

  public void setPort(int i) {
    this.port = i;
  }

  public int getPort() {
    return this.port;
  }

  public void setUser(String s) {
    this.user = s;
  }

  public String getUser() {
    return this.user;
  }

  public void setPass(String s) {
    this.pass = s;
  }

  public String getPass() {
    return this.pass;
  }

  public void setDebug(boolean b) {
    this.flagDebug = b;
  }

  public int getTotalInboxMessage() {
    return this.message.length;
  }

  public Message getInboxMessage(int i) {
    return this.message[i];
  }

  public void readInbox() throws Imap4UtilityException{
    try {
      this.folder = this.store.getFolder("INBOX");
      this.folder.open(Folder.READ_WRITE);
      this.message = folder.getMessages();
    } catch (MessagingException e) {
    	throw new Imap4UtilityException(e);
    }
  }

  public void readInbox(String aFolderName) throws Imap4UtilityException{
	    try {
	      this.folder = this.store.getFolder(aFolderName);
	      this.folder.open(Folder.READ_WRITE);
	      this.message = folder.getMessages();
	    } catch (MessagingException e) {
	    	throw new Imap4UtilityException(e);
	    }
  }

  public void connect() throws Imap4UtilityException {
     try {
      Properties props = new Properties();      
      props.setProperty("mail.imap.host", host);
      props.setProperty("mail.imap.port", String.valueOf(port));
      props.put("mail.mime.base64.ignoreerrors", "true");
      props.put("mail.imaps.partialfetch", "false");
      if (this.flagSSL) {
          props.setProperty("javax.mail.imap.starttls.enable", "true");  
          props.setProperty("javax.mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");  
          props.setProperty("javax.mail.imap.socketFactory.fallback", "false");  
      }

      this.session = Session.getInstance(props, null);
	  this.session.setDebug(this.flagDebug);

	  if (this.flagSSL) {
	      this.store = session.getStore(PROTOCOL_IMAP4S);
  	  } else {
	      this.store = session.getStore(PROTOCOL_IMAP4);		  
	  }

      this.store.connect(this.host,
                         this.port,
                         this.user,
                         this.pass);
      

    } catch (MessagingException e) {
    	throw new Imap4UtilityException(e);
    }
  }

  public InternetAddress getFrom(Message m) throws Imap4UtilityException {
    return this.getListFrom(m)[0];
  }

  public InternetAddress[] getListFrom(Message m) throws Imap4UtilityException {
    InternetAddress[] out = null;
    try {
      InternetAddress address[] = (InternetAddress[]) m.getFrom();
      out = new InternetAddress[address.length];
      for (int i = 0; i < address.length; i++) {
        out[i] = address[i];
      }
    } catch (MessagingException e) {
    	throw new Imap4UtilityException(e);
    }
    return out;
  }

  public int getTotalAttachFile(Message m) throws Imap4UtilityException {
    int out = 0;
    try {
      Object obj = m.getContent();
      if (obj instanceof Multipart) {
        Multipart multipart = (Multipart) obj;
        out = multipart.getCount();
      }
    } catch (MessagingException e) {
    	throw new Imap4UtilityException(e);
    } catch (IOException e) {
    	throw new Imap4UtilityException(e);
    }
    return out;
  }

  public void saveAttachFile(Message m, int index, File aDirectory) throws Imap4UtilityException {
	  if (aDirectory.isDirectory() == false) {
		  throw new Imap4UtilityException(aDirectory + " is not a valid directory!");
	  }

    try {
      Object obj = m.getContent();
      if (obj instanceof Multipart) {
    	  Multipart multipart = (Multipart) obj;

          for (int i = 0; i < multipart.getCount(); i++) {
            Part part = multipart.getBodyPart(i);
	        String disposition = part.getDisposition();
	
	        if ((disposition != null) && 
	           ((disposition.equalsIgnoreCase(Part.ATTACHMENT) || 
	           (disposition.equalsIgnoreCase(Part.INLINE))))) {
	        		        
	             MimeBodyPart mimeBodyPart = (MimeBodyPart) part;
	             File fileName = new File(aDirectory, mimeBodyPart.getFileName());
	
				 System.out.println("saveAttachFile():" + fileName);
	             mimeBodyPart.saveFile(fileName);
	         }
         }
      } else if (obj instanceof MimeMultipart) {
		  System.err.println("saveAttachFile(): allegato di tipo MimeMultipart");
	  } else {
		  System.err.println("saveAttachFile(): allegato di tipo sconosciuto");
	  }
    } catch (MessagingException e) {
    	throw new Imap4UtilityException(e);
    } catch (IOException e) {
    	throw new Imap4UtilityException(e);
    }
  }

  public String getContent(Message m) throws Imap4UtilityException {
    String out = null;
    try {
      Object obj = m.getContent();
      if (obj instanceof String) {
        out = (String) obj;
      } else if (obj instanceof Multipart) {
        Multipart multiPart = (Multipart) m.getContent();
        BodyPart bodyPart = multiPart.getBodyPart(0);
        out = bodyPart.getContent().toString();
      } else if (obj instanceof MimeMultipart) {
        MimeMultipart mimeMultiPart = (MimeMultipart) m.getContent();
		  System.out.println("getContent():mimeMultiPart.getCount()=" + mimeMultiPart.getCount());
		  BodyPart bodyPart = mimeMultiPart.getBodyPart(0);
          out = bodyPart.getContent().toString();
		}
    } catch (MessagingException e) {
    	throw new Imap4UtilityException(e);
    } catch (IOException e) {
      throw new Imap4UtilityException(e);
    }
    return out;
  }

  public String getSubject(Message m) throws Imap4UtilityException {
    String out = null;
    try {
      out = m.getSubject();
    } catch (MessagingException e) {
    	throw new Imap4UtilityException(e);
    }
    return out;
  }

  public void delete(Message m) throws Imap4UtilityException {
    try {
      m.setFlag(Flags.Flag.DELETED, true);
    } catch (MessagingException e) {
    	throw new Imap4UtilityException(e);
    }
  }

  public void archive(Message m) throws Imap4UtilityException {
	    try {
	      m.setFlag(Flags.Flag.SEEN, true);
	    } catch (MessagingException e) {
	    	throw new Imap4UtilityException(e);
	    }
  }

  public Date getSentDate(Message m) throws Imap4UtilityException {
    Date out = null;
    try {
      out = m.getSentDate();
    } catch (MessagingException e) {
    	throw new Imap4UtilityException(e);
    }
    return out;
  }

  public void close() throws Imap4UtilityException {
    try {
      this.folder.close(true);
      this.store.close();
    } catch (MessagingException e) {
    	throw new Imap4UtilityException(e);
    }
  }

  public final static int DEFAULT_PORT_IMAP4 = 143;
  public final static int DEFAULT_PORT_IMAP4S = 993;

  private final static String PROTOCOL_IMAP4 = "imap";
  private final static String  PROTOCOL_IMAP4S = "imaps";

  private String host = null;
  private int port = -1;

  private String user = null;
  private String pass = null;

  private boolean flagSSL = false;
  
  private boolean flagDebug = false;

  private Session session = null;
  private Folder folder = null;
  private Store store = null;

  private Message message[] = null;
}

}

