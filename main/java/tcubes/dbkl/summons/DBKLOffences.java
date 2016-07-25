package tcubes.dbkl.summons;

public class DBKLOffences
{
	/**
	 * The Enum ColumnName.
	 */
	public enum ColumnName {
		CODE,
		DESCRIPTION,
		NO,
		SUBSECTION_NO		
	}

	/** The Code. */
	public String Code;
	public String Description;
	public String No;
	public String SubsectionNo;
	
	

	
	public DBKLOffences(String code, String description, String no, String subsection_no)
	{
		
		this.Code = code;
		this.Description = description;
		this.No = no;
		this.SubsectionNo=subsection_no;
		// TODO Auto-generated constructor stub
	}

}
