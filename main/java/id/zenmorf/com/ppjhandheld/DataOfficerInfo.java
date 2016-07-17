package id.zenmorf.com.ppjhandheld;

public class DataOfficerInfo
{
	/**
	 * The Enum ColumnName.
	 */
	public enum ColumnName {
		CODE,
		ID,
		RANK,
		RANKNO,
		NAME,
		PASSWORD
	}

	/** The Code. */
	public String Code;
	public String Id;
	public String Rank;
	public String RankNo;
	public String Name;
	public String Password;
	

	
	public DataOfficerInfo(String code, String id, String rank, String rankno, String name, String password)
	{
		
		this.Code = code;
		this.Id = id;
		this.Rank = rank;
		this.RankNo = rankno;
		this.Name = name;
		this.Password = password;
		// TODO Auto-generated constructor stub
	}

}
