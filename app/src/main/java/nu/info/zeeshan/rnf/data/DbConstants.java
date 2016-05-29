package nu.info.zeeshan.rnf.data;

public class DbConstants {
	public static final String COMMA=",";
	public static final String DOT=".";
	public static final String BRACES_OPEN="(";
	public static final String BRACES_CLOSE=")";
	public static final String SEMICOLON=")";
	public static final String SPACE=" ";
	public static final String EQUALS=" =";
	public static final String UPDATE="UPDATE ";
	public static final String SET=" SET";
	public static final String QUESTION_MARK=" ?";
	public static final String TYPE_TEXT=" TEXT";
	public static final String NOT_NULL=" NOT NULL";
	public static final String TYPE_INT=" INTEGER";
	public static final String TYPE_REAL=" REAL";
	public static final String CONSTRAIN_PRIMARY_KEY=" PRIMARY KEY";
	public static final String CONSTRAIN_FOREIGN_KEY=" FOREIGN KEY";
	public static final String REFERENCES=" REFERENCES ";
	public static final String UNIQUE=" UNIQUE";
	public static final String DESC=" DESC";
	public static final String DEFAULT=" DEFAULT ";
	public static final String AND=" AND ";
	public static final String CONFLICT_POLICY_IGNORE =" ON CONFLICT IGNORE";
	public interface State{
		public int READ=1;
		public int UNREAD=0;
	}
	public interface Type{
		public int FB=2;
		public int NEWS=1;
	}
}
