package id.zenmorf.com.ppjhandheld;

import java.util.Date;

public class PPJSummonIssuanceInfo
{
	public String RoadTaxNo = "-";
	public String SelectedVehicleMake = "";
	public String SelectedVehicleModel = "";
	public String VehicleMake = "";
	public int VehicleMakePos = 0;
	public String VehicleModel = "";
	public int VehicleModelPos = 0;
	public String VehicleType = "";
	public int VehicleTypePos = 0;
	public String VehicleNo="";
	public String OffenceAct="";
	public int OffenceActPos=0;
	public String OffenceActCode="";
	public String OffenceSection="";
	public int OffenceSectionPos=0;
	public String OffenceSectionCode="";
	public String Offence="";
	public String OffenceDetails="-";
	public String OffenceLocation="";
	public int OffenceLocationPos=0;
	public String OffenceLocationArea="";
	public String SummonLocation="";
	public int OffenceLocationAreaPos=0;
	public String OffenceLocationDetails="-";
	public float CompoundAmount1=0;
	public float CompoundAmount2=0;
	public float CompoundAmount3=0;
	public float CompoundAmount4=0;
	public float CompoundAmount5=0;
	public String CompoundAmountDescription="";
	public String CompoundAmountDesc1="";
	public String CompoundAmountDesc2="";
	public String CompoundAmountDesc3="";
	public String CompoundAmountDesc4="";
	public String CompoundAmountDesc5="";
	public String[] ImageLocation = new String[5];

	public Date OffenceDateTime = null;
	public String OfficerZone = "";
	public String NoticeSerialNo = "";
	public String Advertisement = "";
	public Date CompoundDate = null;
	public String PostNo = "";
	
	public PPJSummonIssuanceInfo()
	{
		
	}
	public PPJSummonIssuanceInfo(boolean demo)
	{
		RoadTaxNo = "DATA";
		VehicleMake = "DATA";
		VehicleModel = "DATA";
		VehicleType = "DATA";
		VehicleNo = "DATA";
		OffenceAct = "DATA";
		OffenceSection = "DATA";
		Offence = "DATA";
		OffenceDetails = "DATA";
		OffenceLocation = "DATA";
		OffenceLocationArea = "DATA";
		OffenceLocationDetails = "DATA";
		PostNo = "DATA";
	}
	
}
