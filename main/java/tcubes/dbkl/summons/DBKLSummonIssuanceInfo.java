package tcubes.dbkl.summons;

import java.util.Date;

public class DBKLSummonIssuanceInfo
{
	public String jenisNotis = "";
	public int jenisNotisCode=0;
	public String kptNo = "";
	public String name = "";
	public String address1 = "";
	public String address2 ="";
	public String address3="";
	public String noCukaiJalan="-";
	public String jenama="";
	public String vehicleMake = "";
	public String vehicleModel = "";
	public int jenamaPos=0;
	public String model="";
	public int modelPos=0;
	public String jenisBadan="";
	public int jenisBadanPos=0;
	public String noKenderaan="";
	public String offenceAct="";
	public int offenceActPos=0;
	public String offenceActCode="";
	public String offenceSection="";
	public int offenceSectionPos=0;
	public String offenceSectionCode="";
	public String offence="";
	public String offenceDetails="-";
	public String offenceLocation="";
	public int offenceLocationPos=0;
	public String offenceLocationArea="";
	public String summonLocation="";
	public int offenceLocationAreaPos=0;
	public String offenceLocationDetails="-";
	public String postNo="";
	public float compoundAmount1=0;
	public float compoundAmount2=0;
	public float compoundAmount3=0;
	public float compoundAmount4=0;
	public float compoundAmount5=0;
	public String compoundAmountDescription="";
	public String compoundAmountDesc1="";
	public String compoundAmountDesc2="";
	public String compoundAmountDesc3="";
	public String compoundAmountDesc4="";
	public String compoundAmountDesc5="";
	/** The image location. */
	public String[] imageLocation = new String[5];
	
	public String LicenseNo="";
	public Date licenseExpiryDate = null;

	/** The court date. */
	public Date courtDate = null;
	public Date roadtaxExpiryDate = null;
	public Date OffenceDateTime = null;
	public String OfficerZone = "";
	public String NoticeSerialNo = "";
	public String advertisement = "";
	public Date CompoundDate = null;
	
	public DBKLSummonIssuanceInfo()
	{
		
	}
	public DBKLSummonIssuanceInfo(boolean demo)
	{
		jenisNotis = "DATA";
		kptNo = "DATA";
		name = "DATA";
		address1 = "DATA";
		address2 = "DATA";
		address3 = "DATA";
		noCukaiJalan = "DATA";
		jenama = "DATA";
		model = "DATA";
		jenisBadan = "DATA";
		noKenderaan = "DATA";
		offenceAct = "DATA";
		offenceSection = "DATA";
		offence = "DATA";
		offenceDetails = "DATA";
		offenceLocation = "DATA";
		offenceLocationArea = "DATA";
		offenceLocationDetails = "DATA";
		postNo = "DATA";
		licenseExpiryDate = new Date(2012, 11, 11, 11, 11, 11);
		roadtaxExpiryDate = new Date(2012, 11, 11, 11, 11, 11);
	}
	
}
