package tcubes.dbkl.summons;

public class DBKLOfficerZoneCategory extends BaseEntity
{

	/**
	 * The Enum ColumnName.
	 */
	public enum ColumnName {

		/** The CODE. */
		CODE,
		/** The DESCRIPTION. */
		DESCRIPTION
	}

	/** The Code. */
	public String Code;

	/** The Description. */
	public String Description;

	/**
	 * Instantiates a new Officer Zone category.
	 * 
	 * @param id
	 *            the id
	 * @param text
	 *            the text
	 */
	public DBKLOfficerZoneCategory(String id, String text)
	{
		super(id, text);
		this.Code = id;
		this.Description = text;
		// TODO Auto-generated constructor stub
	}

}