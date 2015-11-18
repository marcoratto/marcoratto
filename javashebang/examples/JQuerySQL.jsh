#!/home/rattom/bin/javashebang 
#START_JSH
#JAVA_HOME=/opt/java
#JAVA_CLASSPATH=/opt/java_libs/apache-log4j-1.2.16/log4j-1.2.16.jar
#JAVAC_CLASSPATH=/opt/java_libs/apache-log4j-1.2.16/log4j-1.2.16.jar
#JAVA_OPTS=-Xms256m -Xmx1024m
#END_JSH
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;

public class JQuerySQL {

	private static Logger logger = Logger.getLogger(JQuerySQL.class);
	
    private static JQuerySQL jquerySQL = null;
    
	public JQuerySQL() {		
	}
    
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println(help);
			System.exit(1);
		}
		int retCode = 1;
		try {
			jquerySQL = new JQuerySQL();
			retCode = jquerySQL.runme(args);
		} catch (Throwable t) {
			logger.error(t);
			t.printStackTrace(System.err);
			retCode = 2;
		}
		System.exit(retCode);
	}	
	
	public int runme(String[] args) throws JQuerySQLException {
		if (args == null) {
			throw new JQuerySQLException("Why the param 'args' is null ?");
		}
		if (args.length == 0) {
			throw new JQuerySQLException("Why the param 'args' is empty ?");
		}
		for (int j = 0; j < args.length; j++) {
			logger.debug(args[j]);
			if ((args[j].equalsIgnoreCase("-help") == true) || 
				(args[j].equalsIgnoreCase("-h") == true)) {
				System.err.println(help);
				return 0;
			} else if (args[j].equalsIgnoreCase("-driver") == true) {
				if (++j < args.length) {
					jquerySQL.setDbDriver(args[j]);
				}					
			} else if (args[j].equalsIgnoreCase("-username") == true) {
				if (++j < args.length) {
					jquerySQL.setDbUser(args[j]);
				}					
			} else if (args[j].equalsIgnoreCase("-password") == true) {
				if (++j < args.length) {
					jquerySQL.setDbPass(args[j]);
				}
			} else if (args[j].equalsIgnoreCase("-locale") == true) {
				if (++j < args.length) {
					jquerySQL.setDbLocale(args[j]);
				}				
			} else if (args[j].equalsIgnoreCase("-url") == true) {
				if (++j < args.length) {
					jquerySQL.setDbUrl(args[j]);
				}				
			} else if (args[j].equalsIgnoreCase("-append") == true) {
				if (++j < args.length) {
					jquerySQL.setFlagAppend(true);
				}					
			} else if (args[j].equalsIgnoreCase("-xml") == true) {
				jquerySQL.setFlagXml(true);					
			} else if (args[j].equalsIgnoreCase("-output") == true) {
				if (++j < args.length) {
					jquerySQL.setOutputFile(new File(args[j]));
				}					
			} else if (args[j].equalsIgnoreCase("-list") == true) {
				if (++j < args.length) {
					jquerySQL.setListOfSqlCmd(new File(args[j]));
				}
			} else if (args[j].equalsIgnoreCase("-query") == true) {
				if (++j < args.length) {
					jquerySQL.setListOfQuery(JQuerySQL.stringToStringArray(args[j], ";"));						
				}
			} else if (args[j].equalsIgnoreCase("-sep") == true) {
				if (++j < args.length) {						
					jquerySQL.setSep(args[j]);
				}					
			} else if (args[j].equalsIgnoreCase("-enc") == true) {
				if (++j < args.length) {						
					jquerySQL.setFileEncoding(args[j]);
				}					
			} else if (args[j].equalsIgnoreCase("-header") == true) {
				jquerySQL.setFlagHeaders(true);					
			} else if (args[j].equalsIgnoreCase("-trim") == true) {
				jquerySQL.setFlagTrim(true);					
			} else if (args[j].equalsIgnoreCase("-sql") == true) {
				jquerySQL.setFlagSql(true);					
			} else if (args[j].equalsIgnoreCase("-skipFailure") == true) {
				jquerySQL.setFlagSkipFailure(true);				
			} else if (args[j].equalsIgnoreCase("-verbose") == true) {
				jquerySQL.setFlagVerbose(true);					
			} else if (args[j].equalsIgnoreCase("-debug") == true) {
				jquerySQL.setFlagDebug(true);					
			} else if (args[j].equalsIgnoreCase("-null") == true) {
				jquerySQL.setFlagNullToEmptyString(true);					
			} else {
				System.err.println("ERROR: Parameter '" + args[j] + "' unknown!");
				System.exit(1);
			}
		}
			
		jquerySQL.execute();
			
		return 0;
	}

	public void execute() throws JQuerySQLException {
		try {
			if ((dbDriver == null) || (dbDriver.trim().length() == 0)) {
				System.err.println("ERROR: Parameter 'dbDriver' is mandatory!");
				System.exit(1);
			}
			if ((dbUrl == null) || (dbUrl.trim().length() == 0)) {
				System.err.println("ERROR: Parameter 'dbUrl' is mandatory!");
				System.exit(1);
			}
			if (listOfQuery == null) {
				System.err.println("ERROR: Parameter 'query' is mandatory!");
				System.exit(1);				
			}
			if (this.outputFile == null) {
				this.output = new PrintStream(System.out, true, this.fileEncoding);
			} else {
				this.output = new PrintStream(new FileOutputStream(this.outputFile, this.flagAppend), true, this.fileEncoding);
			}
			if (this.output == null) {
				throw new JQuerySQLException("I cannot open the file for writing!");
			}
			this.connect();
			for (int i = 0; i < listOfQuery.length; i++) {
				String s = listOfQuery[i];
				try {
					this.execQuery(s);					
				} catch (JQuerySQLException e) {					
					if (this.flagSkipFailure) {
						System.err.println("Skip Exception " + e.getMessage());
						e.printStackTrace(System.err);
					} else {
						throw e;
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new JQuerySQLException(e);
		} catch (FileNotFoundException e) {
			throw new JQuerySQLException(e);
		} finally {
			this.closeAll();
		}
	}
	
	public void connect() throws JQuerySQLException {
		if (this.dbLocale != null) {
			Locale.setDefault(new Locale(this.dbLocale));
		}
		logger.debug("Locale is " + Locale.getDefault());
		
		logger.debug("Search for the JDBC drivers " + dbDriver);
		try {
			Class driver = Class.forName(dbDriver);
			DriverManager.registerDriver((Driver) driver.newInstance());
			logger.debug("Try to connect to the DB " + dbUrl);
			if ((this.dbUser == null) && (this.dbPass == null)) {
				this.con = DriverManager.getConnection(this.dbUrl);			
			} else {
				this.con = DriverManager.getConnection(this.dbUrl, 
						this.dbUser,
						this.dbPass);
			}
			this.stmt = this.con.createStatement();
		} catch (ClassNotFoundException e) {
			throw new JQuerySQLException(e);
		} catch (SQLException e) {
			throw new JQuerySQLException(e);
		} catch (InstantiationException e) {
			throw new JQuerySQLException(e);
		} catch (IllegalAccessException e) {
			throw new JQuerySQLException(e);
		}
	}

	public void closeAll() {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				logger.warn(e);
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				logger.warn(e);
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (Exception e) {
				logger.warn(e);
			}
		}
		// File ? Flush & Close
		if ((this.outputFile != null) && (this.output != null)) {
			try {
				this.output.flush();
				this.output.close();
			} catch (Exception e) {
				logger.warn(e);
			}
		}
	}

	public void setQuery(String value) {
		this.listOfQuery = stringToStringArray(value, ";");
	}
	
	public void setDbUrl(String value) {
		this.dbUrl = value;
		logger.info("The url is " + this.dbUrl);
	}

	public String getDbUrl() {
		return this.dbUrl;
	}

	public void setDbDriver(String value) {
		this.dbDriver = value;
		logger.info("The driver is " + this.dbDriver);
	}

	public String getDbDriver() {
		return this.dbDriver;
	}

	public void setDbLocale(String value) {
		this.dbLocale = value;
		logger.info("The locale is " + this.dbLocale);
	}

	public String getDbLocale() {
		return this.dbLocale;
	}
	
	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String value) {
		this.dbUser = value;
		logger.info("The username is " + this.dbUser);
	}

	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String value) {
		this.dbPass = value;
		logger.info("The password is " + this.dbPass);
	}

	public String getSep() {
		return sep;
	}

	public void setSep(String value) {
		this.sep = value;
		logger.info("The separator is " + this.sep);
	}

	public File getListOfSqlCmd() {
		return listOfSqlCmd;
	}

	public void setListOfSqlCmd(File listOfSqlCmd) throws JQuerySQLException {
		this.listOfSqlCmd = listOfSqlCmd;
		logger.info("The listOfSqlCmd is " + this.listOfSqlCmd);
		if (this.listOfSqlCmd != null) {
			try {
				logger.info("The list of SQL command is " + listOfSqlCmd.getCanonicalPath());
				listOfQuery = fileToStringArray(this.listOfSqlCmd);
			} catch (IOException e) {
				throw new JQuerySQLException(e);
			}									
		}
	}

	public boolean isFlagXml() {
		return flagXml;
	}

	public void setFlagXml(boolean value) {
		this.flagXml = value;
		logger.info("The flag append is " + this.flagAppend);
	}

	public boolean isFlagSql() {
		return flagSql;
	}

	public void setFlagSql(boolean value) {
		this.flagSql = value;
		logger.info("The flag 'sql' is " + this.flagSql);
	}

	public boolean isFlagDebug() {
		return flagDebug;
	}

	public void setFlagDebug(boolean value) {
		this.flagDebug = value;
		logger.info("The flag 'debug' is " + this.flagDebug);
	}

	public boolean isFlagVerbose() {
		return flagVerbose;
	}

	public void setFlagVerbose(boolean value) {
		this.flagVerbose = value;
		logger.info("The flag 'verbose' is " + this.flagVerbose);
	}

	public boolean isFlagAppend() {
		return flagAppend;
	}

	public void setFlagAppend(boolean value) {
		this.flagAppend = value;
		logger.info("The flag append is " + this.flagAppend);
	}

	public boolean isFlagSkipFailure() {
		return flagSkipFailure;
	}

	public void setFlagSkipFailure(boolean value) {
		this.flagSkipFailure = value;
		logger.info("The flag 'skipFailure' is " + this.flagSkipFailure);
	}

	public boolean isFlagTrim() {
		return flagTrim;
	}

	public void setFlagTrim(boolean value) {
		this.flagTrim = value;
		logger.info("The flag 'trim' is " + this.flagTrim);
	}

	public boolean isFlagHeaders() {
		return flagHeaders;
	}

	public void setFlagHeaders(boolean value) {
		this.flagHeaders = value;
		logger.info("The flag 'header' is " + this.flagHeaders);
	}

	public boolean isFlagNullToEmptyString() {
		return flagNullToEmptyString;
	}

	public void setFlagNullToEmptyString(boolean value) {
		this.flagNullToEmptyString = value;
		logger.info("The flag 'null' is " + this.flagNullToEmptyString);
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File value) {
		this.outputFile = value;
		logger.info("Output file is " + outputFile);
	}

	public String getFileEncoding() {
		return this.fileEncoding;
	}

	public void setFileEncoding(String value) {
		this.fileEncoding = value;
		logger.info("fileEncoding is " + fileEncoding);
	}

	public void execQuery(String sql) throws JQuerySQLException {
		logger.info("Try to exec the query: " + sql);
		if (sql.toLowerCase().trim().startsWith("select ")) {
			this.execSelect(sql);
		} else if (sql.toLowerCase().trim().startsWith("insert ") || 
				   sql.toLowerCase().trim().startsWith("update ") ||
				   sql.toLowerCase().trim().startsWith("delete ")) {
			this.execUpdate(sql);
		} else {
			throw new JQuerySQLException("SQL command not supported!");
		}
	}
	
	private void execUpdate(String sql) throws JQuerySQLException {
		int rowCounter = -1;
		try {
			rowCounter = this.stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new JQuerySQLException(e);
		}
		this.output.println(rowCounter);
	}
	
	private void printHeaders() throws JQuerySQLException {
		String virgola = "";
		StringBuffer sb = new StringBuffer("");
		try {
			for (int j=0, i=1; j < this.rsmd.getColumnCount(); i++, j++) {
				sb.append(virgola);
				sb.append(this.rsmd.getColumnName(i));
				virgola = this.sep;
			}
		} catch (SQLException e) {
			throw new JQuerySQLException(e);
		}
		this.output.println(sb.toString());	
	}
	
	private void execSelect(String sql) throws JQuerySQLException {
		try {
			this.rs = this.stmt.executeQuery(sql);
			this.rsmd = this.rs.getMetaData();
			if (this.flagSql) {
				this.execSelectAsSql(sql);
				return;
			} 
			if (this.flagXml) {
				this.execSelectAsXml(sql);
				return;
			} 
			this.execSelectAsCsv(sql);
		} catch (SQLException e) {
			throw new JQuerySQLException(e);
		}

	}

	private void execSelectAsXml(String sql) throws JQuerySQLException {		
		factory = XMLOutputFactory.newInstance();
		eventFactory = XMLEventFactory.newInstance();

		StringWriter stringWriter = new StringWriter();

		String result = null;

		try {
			// getting instance of writer
			writer = factory.createXMLEventWriter(stringWriter);

			// writing start document

			writeStartDocument(writer);

			// writing JCO start element

			writeCharacters(XmlConstants.EOL);
			writeStartElement(XmlConstants.TAG_ROOT);

			writeCharacters(XmlConstants.EOL);
			
			writeStartElement(XmlConstants.TAG_JDBC);
			writeCharacters(XmlConstants.EOL);
			
			this.writeStartElement(XmlConstants.TAG_JDBC_DRIVER);
			this.writeCharacters(this.dbDriver);
			this.writeEndElement(XmlConstants.TAG_JDBC_DRIVER);

			this.writeStartElement(XmlConstants.TAG_JDBC_URL);
			this.writeCharacters(this.dbUrl);
			this.writeEndElement(XmlConstants.TAG_JDBC_URL);

			this.writeStartElement(XmlConstants.TAG_JDBC_QUERY);
			this.writeCharacters(sql);
			this.writeEndElement(XmlConstants.TAG_JDBC_QUERY);
			
			this.writeStartElement(XmlConstants.TAG_STRUCTURE);
			this.writeCharacters(XmlConstants.EOL);
			for (int j=0, i=1; j < this.rsmd.getColumnCount(); i++, j++) {
				this.writeStartElement(XmlConstants.TAG_FIELD);
				this.writeAttribute(XmlConstants.ROW_ATTR_ID, Integer.toString(i));
				this.writeCharacters(XmlConstants.EOL);
				
				this.writeStartElement(XmlConstants.TAG_STRUCTURE_FIELD_NAME);
				this.writeCharacters(this.rsmd.getColumnName(i));
				this.writeEndElement(XmlConstants.TAG_STRUCTURE_FIELD_NAME);
				
				this.writeStartElement(XmlConstants.TAG_STRUCTURE_FIELD_TYPE);
				this.writeCharacters(this.columnTypeToString(this.rsmd.getColumnType(i)));
				this.writeEndElement(XmlConstants.TAG_STRUCTURE_FIELD_TYPE);

				this.writeStartElement(XmlConstants.TAG_STRUCTURE_FIELD_JAVA_TYPE);
				this.writeCharacters(this.columnTypeToString(this.rsmd.getColumnType(i)));
				this.writeEndElement(XmlConstants.TAG_STRUCTURE_FIELD_JAVA_TYPE);
				
				this.writeEndElement(XmlConstants.TAG_FIELD);
			}
			this.writeEndElement(XmlConstants.TAG_STRUCTURE);
			
			this.writeStartElement(XmlConstants.TAG_TABLE);		
			this.writeCharacters(XmlConstants.EOL);
			
			long recordCounter = 0;
			while (this.rs.next()) {
				this.writeStartElement(XmlConstants.TAG_ROW);
				this.writeAttribute(XmlConstants.ROW_ATTR_ID, Long.toString(recordCounter));
				this.writeCharacters(XmlConstants.EOL);
				for (int j = 0, i = 1; j < this.rsmd.getColumnCount(); i++, j++) {
					this.writeStartElement(XmlConstants.TAG_FIELD);
					
					this.writeAttribute(XmlConstants.FIELD_ATTR_NAME, this.rsmd.getColumnName(i));
					
					Object obj = this.rs.getObject(i);
					if ((obj == null) && (flagNullToEmptyString == true)) {
						this.writeCharacters("");
					} else {
						if (obj != null) {
							if (flagTrim == true) {
								this.writeCharacters(obj.toString().trim());														
							} else {
								this.writeCharacters(obj.toString());						
							}
						} else {
							this.writeCharacters("null");						
						}
					}
					this.writeEndElement(XmlConstants.TAG_FIELD);
				}
				this.writeEndElement(XmlConstants.TAG_ROW);
				recordCounter++;
			}
			this.writeEndElement(XmlConstants.TAG_TABLE);
				
			writeEndElement(XmlConstants.TAG_ROOT);

			writeEndDocument(writer);
			writer.flush();

			result = stringWriter.toString();
		} catch (XMLStreamException e) {
			throw new JQuerySQLException(e);
		} catch (SQLException e) {
			throw new JQuerySQLException(e);
		} catch (Exception e) {
			throw new JQuerySQLException(e);
		} finally {
			// close
			if (writer != null) {
				try {
					writer.close();
				} catch (XMLStreamException ex) {
				}
			}
			if (stringWriter != null) {
				try {
					stringWriter.close();
				} catch (IOException ex) {
				}
			}
		}
	  this.output.println(result);
	}

	private void execSelectAsCsv(String sql) throws JQuerySQLException {		
		if (this.flagHeaders) {
			this.printHeaders();
		}
		String virgola = "";
		StringBuffer sb = new StringBuffer("");		
		try {
			while (this.rs.next()) {
				virgola = "";
				sb = new StringBuffer("");
				for (int j = 0, i = 1; j < this.rsmd.getColumnCount(); i++, j++) {
					sb.append(virgola);
					Object obj = this.rs.getObject(i);					
					if ((obj == null) && (flagNullToEmptyString == true)) {
						sb.append("");
					} else if ((obj != null) && this.rsmd.getColumnType(i) == Types.CLOB) {
						int id = rs.getInt(1);
						Clob blob = this.rs.getClob(i);
				        int chunkSize = (int) blob.length();
				        byte[] binaryBuffer = new byte[1024];
				        InputStream blobInputStream = blob.getAsciiStream();
				        
				        File file = new File(".", id + ".xml");      
				        file.delete();
				        System.out.println("Save file " + file.getCanonicalPath());
				        
				        FileOutputStream outputFileOutputStream  = new FileOutputStream(file);
				        
				        int bytesRead = 0;
				        int totBytesRead = 0;
				        int totBytesWritten = 0;
				        while ((bytesRead = blobInputStream.read(binaryBuffer)) != -1) {
				            
				            // Loop through while reading a chunk of data from the BLOB
				            // column using an InputStream. This data will be stored
				            // in a temporary buffer that will be written to disk.
				            outputFileOutputStream.write(binaryBuffer, 0, bytesRead);
				            
				            totBytesRead += bytesRead;
				            totBytesWritten += bytesRead;
				        }
				        outputFileOutputStream.flush();
				        outputFileOutputStream.close();
					} else {
						if ((obj != null) && (flagTrim == true)) {
							sb.append(obj.toString().trim());						
						} else {
							sb.append(obj);						
						}
					}
					virgola = this.sep;
				}
				this.output.println(sb.toString());
			}
		} catch (SQLException e) {
			throw new JQuerySQLException(e);
		} catch (IOException e) {
			throw new JQuerySQLException(e);
		}
	}
	
	private void execSelectAsSql(String sql) throws SQLException {		
		String virgola = "";

		String schema = this.rsmd.getSchemaName(1).trim();
		String table = this.rsmd.getTableName(1).trim();				
		StringBuffer sqlInsertHeader = new StringBuffer("");
		sqlInsertHeader.append("INSERT INTO ");
		sqlInsertHeader.append(schema);
		sqlInsertHeader.append(".");
		sqlInsertHeader.append(table);
		sqlInsertHeader.append(" ");
		for (int j=0, i=1; j < this.rsmd.getColumnCount(); i++, j++) {			
			logger.debug("The schema is " + schema);
			logger.debug("The table is " + table);
		}			
		sqlInsertHeader.append("(");

		virgola = "";
		for (int j=0, i=1; j < this.rsmd.getColumnCount(); i++, j++) {			
			String columnName = this.rsmd.getColumnName(i);
			logger.debug("The column name is " + columnName);
			logger.debug("The column is mapped as " + columnTypeToJavaObject(this.rsmd.getColumnType(i)));
			sqlInsertHeader.append(virgola);
			sqlInsertHeader.append(columnName);
			virgola = ",";
		}			
		sqlInsertHeader.append(") VALUES (");
		
		StringBuffer sb = new StringBuffer();
		while (this.rs.next()) {
			virgola = "";
			sb = new StringBuffer(sqlInsertHeader);
			for (int j = 0, i = 1; j < this.rsmd.getColumnCount(); i++, j++) {
				int javaType = columnTypeToJavaObject(this.rsmd.getColumnType(i));
				sb.append(virgola);
				Object obj = this.rs.getObject(i);
				if ((obj == null) && (flagNullToEmptyString == true)) {
					if ((javaType == 1) || (javaType == 3)) {
						sb.append("'");
					}
					sb.append("");
					if ((javaType == 1) || (javaType == 3)) {
						sb.append("'");
					}
				} else if (obj == null) {
					sb.append(obj);
				} else {
					if ((obj != null) && (flagTrim == true)) {
						if ((javaType == 1) || (javaType == 3)) {
							sb.append("'");
						}
						sb.append(obj.toString().trim());						
						if ((javaType == 1) || (javaType == 3)) {
							sb.append("'");
						}
					} else {
						sb.append(obj);						
					}
				}
				virgola = this.sep;
			}
			sb.append(");");
			this.output.println(sb.toString());
		}
	}

	private String columnTypeToString(int i) {
		String out = "unknown";
		
		// -7
		switch (i) {
		
		case Types.BIT:
			out = "BIT";
			
			// -6 					
		case Types.TINYINT:
			out = "TINYINT";
			break;
			// -5 			
		case Types.BIGINT:
			out = "BIGINT";
			break;
			// -4 			
		case Types.LONGVARBINARY:
			out = "LONGVARBINARY";
			break;
			// -3 			
		case Types.VARBINARY:
			out = "VARBINARY";
			break;
			// -2 			
		case Types.BINARY:
			out = "BINARY";
			break;
			// -1 			
		case Types.LONGVARCHAR:
			out = "LONGVARCHAR";
			break;
			// 0 			
		case Types.NULL:
			out = "NULL";
			break;
			// 1 			
		case Types.CHAR:
			out = "CHAR";
			break;
			// 2 			
		case Types.NUMERIC:
			out = "NUMERIC";
			break;
			// 3 			
		case Types.DECIMAL:
			out = "DECIMAL";
			break;
			// 4 			
		case Types.INTEGER:
			out = "INTEGER";
			break;
			// 5 			
		case Types.SMALLINT:
			out = "SMALLINT";
			break;
			// 6 			
		case Types.FLOAT:
			out = "FLOAT";
			break;
			// 7 			
		case Types.REAL:
			out = "REAL";
			break;
			// 8 			
		case Types.DOUBLE:
			out = "DOUBLE";
			break;
			// 12 			
		case Types.VARCHAR:
			out = "VARCHAR";
			break;
			// 91 			
		case Types.DATE:
			out = "DATE";
			break;
			// 92 			
		case Types.TIME:
			out = "TIME";
			break;
			// 93 			
		case Types.TIMESTAMP:
			out = "TIMESTAMP";
			break;
			
			// 1111  			
		case Types.OTHER:
			out = "OTHER";
			break;
			
		default:
			out = Integer.toString(i);
		}

		return out;
	}

	/**
	 * -1 -> Unknown
	 *  0 -> null
	 *  1 -> String
	 *  2 -> Numeric
	 *  3 -> Date
	 *  4 -> Other
	 * @param i
	 * @return
	 */
	private int columnTypeToJavaObject(int i) {
		int out = -1;
		
		// -7
		switch (i) {
		
		case Types.BIT:
			logger.debug("Column Type is 'BIT' (" + i + ")");
			out = 2;
			
			// -6 					
		case Types.TINYINT:
			logger.debug("Column Type is 'TINYINT' (" + i + ")");
			out = 2;
			break;
			// -5 			
		case Types.BIGINT:
			logger.debug("Column Type is 'BIGINT' (" + i + ")");
			out = 2;
			break;
			// -4 			
		case Types.LONGVARBINARY:
			logger.debug("Column Type is 'LONGVARBINARY' (" + i + ")");
			out = 2;
			break;
			// -3 			
		case Types.VARBINARY:
			logger.debug("Column Type is 'VARBINARY' (" + i + ")");
			out = 2;
			break;
			// -2 			
		case Types.BINARY:
			logger.debug("Column Type is 'BINARY' (" + i + ")");
			out = 2;
			break;
			// -1 			
		case Types.LONGVARCHAR:
			logger.debug("Column Type is 'LONGVARCHAR' (" + i + ")");
			out = 1;
			break;
			// 0 			
		case Types.NULL:
			logger.debug("Column Type is 'NULL' (" + i + ")");
			out = 0;
			break;
			// 1 			
		case Types.CHAR:
			logger.debug("Column Type is 'CHAR' (" + i + ")");
			out = 1;
			break;
			// 2 			
		case Types.NUMERIC:
			logger.debug("Column Type is 'NUMERIC' (" + i + ")");
			out = 2;
			break;
			// 3 			
		case Types.DECIMAL:
			logger.debug("Column Type is 'DECIMAL' (" + i + ")");
			out = 2;
			break;
			// 4 			
		case Types.INTEGER:
			logger.debug("Column Type is 'INTEGER' (" + i + ")");
			out = 2;
			break;
			// 5 			
		case Types.SMALLINT:
			logger.debug("Column Type is 'SMALLINT' (" + i + ")");
			out = 2;
			break;
			// 6 			
		case Types.FLOAT:
			logger.debug("Column Type is 'FLOAT' (" + i + ")");
			out = 2;
			break;
			// 7 			
		case Types.REAL:
			logger.debug("Column Type is 'REAL' (" + i + ")");
			out = 2;
			break;
			// 8 			
		case Types.DOUBLE:
			logger.debug("Column Type is 'DOUBLE' (" + i + ")");
			out = 2;
			break;
			// 12 			
		case Types.VARCHAR:
			logger.debug("Column Type is 'VARCHAR' (" + i + ")");
			out = 1;
			break;
			// 91 			
		case Types.DATE:
			logger.debug("Column Type is 'DATE' (" + i + ")");
			out = 3;
			break;
			// 92 			
		case Types.TIME:
			logger.debug("Column Type is 'TIME' (" + i + ")");
			out = 3;
			break;
			// 93 			
		case Types.TIMESTAMP:
			logger.debug("Column Type is 'TIMESTAMP' (" + i + ")");
			out = 3;
			break;
			
			// 1111  			
		case Types.OTHER:
			logger.debug("Column Type is 'OTHER' (" + i + ")");
			out = 4;
			break;
			
		default:
			logger.debug("Column Type is 'UNKNOWN' (" + i + ")");
			out = -1;
		}

		return out;
	}
	
	public static String[] stringToStringArray(String s, String sep) {
		if (s == null) {
			return null;
		}
		try {
			Vector<String> v = new Vector<String>();
			StringTokenizer t = new StringTokenizer(s, sep);
			while (t.hasMoreTokens()) {
				v.addElement(t.nextToken());
			}
			return (String[]) v.toArray(new String[v.size()]);
		} catch (Exception e) {
		}
		return null;
	}
	
	public String[] getListOfQuery() {
		return listOfQuery;
	}

	public void setListOfQuery(String[] value) {
		this.listOfQuery = value;
	}
	
	private String[] fileToStringArray(File file) throws IOException {
		BufferedReader br = null;
		Vector<String> out = new Vector<String>();
		try {
			logger.info("Try to read file " + file.getCanonicalPath());
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			int counter = 0;
			while ((line = br.readLine()) != null) {
				out.add(line);
				counter++;
			}
			logger.info("Readed " +  counter + " lines.");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
		return (String[]) out.toArray(new String[out.size()]);
	}	

	private void writeStartElement(String elementName) throws XMLStreamException {
		XMLEvent event = eventFactory.createStartElement("", "", elementName);
		writer.add(event);
	}

	private void writeEndElement(String elementName)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createEndElement("", "", elementName);
		writer.add(event);
		writeCharacters(XmlConstants.EOL);
	}

	private void writeAttribute(String key, String value) throws XMLStreamException {
		XMLEvent event = eventFactory.createAttribute(key, value);
		writer.add(event);
	}

	private void writeCharacters(String characters)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createCharacters(characters);
		writer.add(event);
	}

	private void writeStartDocument(XMLEventWriter writer)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createStartDocument();
		writer.add(event);
	}

	private void writeEndDocument(XMLEventWriter writer)
			throws XMLStreamException {
		XMLEvent event = eventFactory.createEndDocument();
		writer.add(event);
		writeCharacters(XmlConstants.EOL);
	}
	
	public final static String DEFAULT_CSV_SEP = ",";
	public final static String DEFAULT_FILE_ENCODING = "UTF-8";
	
	// Variabili
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private ResultSetMetaData rsmd = null;
	private String dbUrl = null;
	private String dbDriver = null;
	private String dbLocale = null;
	private String dbUser = null;
	private String dbPass = null;
	private String sep = DEFAULT_CSV_SEP;

	private File outputFile = null;
	private String fileEncoding = DEFAULT_FILE_ENCODING;
	
	private File listOfSqlCmd = null;
	private boolean flagXml = false;
	private boolean flagSql = false;
	private boolean flagDebug = false;
	private boolean flagVerbose = false;
	private boolean flagAppend = false;
	private boolean flagSkipFailure = false;
	private boolean flagTrim = false;
	private boolean flagHeaders = false;
	private boolean flagNullToEmptyString = false;
	
	private PrintStream output = null;

	private XMLOutputFactory factory = null;
	private XMLEventFactory eventFactory = null;
	private XMLEventWriter writer = null;
	private XMLEvent event = null;

	private String[] listOfQuery = null;
		
	private static String help = " * Mandatory Parameters:" +
			 "\n" +
			 "-driver     JDBC driver" +
			 "\n" +
			 "            com.mysql.jdbc.Driver for MySql" +
			 "\n" +
			 "            oracle.jdbc.driver.OracleDriver for Oracle" +
			 "\n" +
			 "            com.ibm.db2.jcc.DB2Driver for DB2" +
			 "\n" +
			 "-url        URL Driver Connection" +
			 "\n" +
			 "            jdbc:mysql://<HOST>:<PORT>/<SID?user=<USERNAME>&password=<PASSWORD> for Mysql" +
			 "\n" +
			 "            jdbc:oracle:thin:<USERNAME>/<PASSWORD>@<HOST>:<PORT>:<SID> for Oracle" +
			 "\n" +
			 "            jdbc:db2://<HOST>:<PORT>/<SID>" +
			 "\n" +
			 "-query      SQL queries separated by a semicolon (';')" +
			 "\n" +
			 "\n" +
			 " * Optional Parameter:" +
			 "\n" +
			 "-h|-help --> This help" +
			 "\n" +
			 "-sep --> Separator (the default is colon \",\")" +
			 "\n" +
			 "-xml --> Write the output as XML (the default is CSV)" +
			 "\n" +
			 "-sql --> Write INSERT sql commands (the default is CSV)" +
			 "\n" +
			 "-output --> output file (the default is System.out)" +
			 "\n" +
			 "-enc --> output file encoding (the default is 'UTF-8')" +
			 "\n" +
			 "-list --> a list of SQL command to execute (one SQL comand per line with semi-colon at the end of the line)" +
			 "\n" +
			 "-null --> Convert null fields to empty string (the default is false)" +
			 "\n" +
			 "-trim --> If the field is not null, trim the value (default is false)" +
			 "\n" +
			 "-skipFailure --> If the filed is not null, trim the value (default is false)" +
			 "\n" +
			 "-verbose --> Verbose" +
			 "\n" +
			 "-append --> Append mode insted of create a new file" +
			 "\n" +
			 "-debug --> Debug" +
			 "\n" +
			 "-locale --> Locale (the default of the JVM and O.S. is '" + Locale.getDefault() + "')" +
			 "\n" +
			 "-header --> Print the Column Header for SELECT (default is false)";

class JQuerySQLException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8081682431340732511L;

	public JQuerySQLException(String s) {
		super(s);
	}

	public JQuerySQLException(String s, Exception e) {		
		super(s, e);
	}

	public JQuerySQLException(Exception e) {
		super(e);
	}
	
}

interface XmlConstants {
	
	String EOL = System.getProperty("line.separator");
	
	String TAG_ROOT = "jquerysql";

	String TAG_JDBC = "jdbc";
	String TAG_JDBC_DRIVER = "driver";
	String TAG_JDBC_URL = "url";
	String TAG_JDBC_USERNAME = "username";
	String TAG_JDBC_QUERY = "query";
	
	String TAG_FIELD = "field";
	String FIELD_ATTR_NAME = "name";
	
	String TAG_ROW = "row";
	String ROW_ATTR_ID = "id";

	String TAG_STRUCTURE = "structure";
	String TAG_STRUCTURE_FIELD_NAME = "name";
	String TAG_STRUCTURE_FIELD_TYPE = "type";
	String TAG_STRUCTURE_FIELD_JAVA_TYPE = "javaType";
	
	String TAG_TABLE = "table";
	String TABLE_ATTR_RECORDS = "records";

}
}

