package id.zenmorf.com.ppjhandheld;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DbLocal
{
	public static List<String> GetListForSpinner(Context context, String tableName)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlcommand = "SELECT * FROM "+ tableName + " ORDER BY CODE";

		Cursor cur = obj.Query(sqlcommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				list.add(cur.getString(1));
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return list;
	}
	
	public static String GetAdvertisement(Context context)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlcommand = "SELECT * FROM OFFENCE_NOTICE_ADVERTISEMENT";

		Cursor cur = obj.Query(sqlcommand, null);
		String text = "";
		
		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				text = cur.getString(1);
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return text;
	}
	public static List<String> GetListForOfficerZone(Context context)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlcommand = "SELECT * FROM OFFICER_ZONE ORDER BY DESCRIPTION";

		Cursor cur = obj.Query(sqlcommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				list.add(cur.getString(1));
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return list;
	}
	public static ArrayList<BaseEntity> GetVehicleMake(Context context)
	{
		ArrayList<BaseEntity> list = new ArrayList<BaseEntity>();
		DbUtils db = new DbUtils(context);
		db.Open();

		String sqlcommand = "SELECT * FROM VEHICLE_MAKE ORDER BY DESCRIPTION";

		Cursor cur = db.Query(sqlcommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				BaseEntity obj = new BaseEntity();
				obj.Code=cur.getString(0);
				obj.Text=cur.getString(1);

				list.add(obj);
            } while (cur.moveToNext());
			cur.close();
        }
		db.Close();
		return list;
	}
	public static List<String>GetListForJenamaSpinner(Context context, String tableName)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlcommand = "SELECT * FROM "+ tableName + " ORDER BY DESCRIPTION";

		Cursor cur = obj.Query(sqlcommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				list.add(cur.getString(1));
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return list;
	}

	public static List<String>GetOneFieldListForSpinner(Context context, String fieldName, String tableName)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlcommand = "SELECT " + fieldName +" FROM "+ tableName + " ORDER BY "+ fieldName;

		Cursor cur = obj.Query(sqlcommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				list.add(cur.getString(0));
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return list;
	}
	public static List<String>GetListForOffenceLocationAreaSpinner(Context context, String strlocation)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlcommand = "SELECT OL.DESCRIPTION FROM OFFENCE_LOCATION OL , OFFENCE_LOCATION_AREA OLA WHERE OLA.CODE = OL.OFFENCE_LOCATION_AREA_CODE AND OLA.DESCRIPTION = \""+strlocation+"\" ORDER BY OL.DESCRIPTION";

		Cursor cur = obj.Query(sqlcommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				list.add(cur.getString(0));
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return list;
	}
	public static List<String>GetListForVehicleModelSpinner(Context context, String vehicleMake)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlcommand = "SELECT VMO.DESCRIPTION FROM VEHICLE_MODEL VMO , VEHICLE_MAKE VMA WHERE VMA.CODE=VMO.VEHICLE_MAKE_CODE AND VMA.DESCRIPTION='" + vehicleMake+ "' ORDER BY VMO.DESCRIPTION";
		Log.e("MESAGE",sqlcommand );
		Cursor cur = obj.Query(sqlcommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				list.add(cur.getString(0));
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return list;
	}
	public static Cursor GetListForOffenceSectionSpinner(Context context, String offenceActShortDesc)
	{		
		DbUtils obj = new DbUtils(context);
		obj.Open();
		
		String sqlcommand = "SELECT OA.CODE, OA.DESCRIPTION, OS.NO, OS.SUBSECTION_NO FROM OFFENCE_ACT OA, OFFENCE_SECTION OS WHERE OA.CODE = OS.OFFENCE_ACT_CODE AND OA.SHORT_DESCRIPTION = \"" + offenceActShortDesc + "\" ORDER BY OS.NO";
		Log.e("MESAGE",sqlcommand );
		Cursor cur = obj.Query(sqlcommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{			
			//cur.close();
        }
		obj.Close();
		return cur;
	}
	public static Cursor GetListForOffenceSectionCodeSpinner(Context context, String offenceSectionCode, String offenceActDescription)
	{		
		String strSectionNo;
        String strSubSectionNo;
        if (offenceSectionCode.contains("("))
        {
            strSectionNo = offenceSectionCode.substring(0, offenceSectionCode.indexOf("(") - 1);
            strSubSectionNo = offenceSectionCode.substring(offenceSectionCode.indexOf("("));
        }
        else
        {
            strSectionNo = offenceSectionCode.trim();
            strSubSectionNo = "";
        }
		DbUtils obj = new DbUtils(context);
		obj.Open();
		
		String sqlcommand = "SELECT OS.OFFENCE_ACT_CODE, OS.CODE, OS.DESCRIPTION, OS.COMPOUND_AMOUNT FROM OFFENCE_SECTION OS, OFFENCE_ACT OA WHERE OA.CODE = OS.OFFENCE_ACT_CODE AND OA.SHORT_DESCRIPTION = \"" + offenceActDescription + "\" AND OS.NO =\"" + strSectionNo + "\" AND OS.SUBSECTION_NO = \"" + strSubSectionNo + "\"";
		Log.e("MESAGE",sqlcommand );
		Cursor cur = obj.Query(sqlcommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{			
			//cur.close();
        }
		obj.Close();
		return cur;
	}
	
	public static float GetCompundAmountFromSection(Context context, String offenceSectionCode, String offenceActCode)
	{	
		float amount = 0;
		DbUtils obj = new DbUtils(context);
		obj.Open();
		
		String sqlcommand = "SELECT COMPOUND_AMOUNT FROM OFFENCE_SECTION WHERE OFFENCE_ACT_CODE = \"" + offenceActCode + "\" AND CODE =\"" + offenceSectionCode + "\"";
		Log.e("MESAGE",sqlcommand );
		Cursor cur = obj.Query(sqlcommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{			
			cur.moveToFirst();
			do
			{				
				try
				{
					amount = Float.parseFloat(cur.getString(0));
				}
				catch(Exception ex)
				{
					
				}
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return amount;
	}
	
	public static float GetCompundAmountFromVehicleType(Context context, String vehicleType)
	{	
		float amount = 0;
		DbUtils obj = new DbUtils(context);
		obj.Open();
		
		String sqlcommand = "SELECT COMPOUND_AMOUNT FROM VEHICLE_TYPE WHERE DESCRIPTION = \"" + vehicleType + "\"";
		Log.e("MESAGE",sqlcommand );
		Cursor cur = obj.Query(sqlcommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{			
			cur.moveToFirst();
			do
			{				
				try
				{
					amount = Float.parseFloat(cur.getString(0));
				}
				catch(Exception ex)
				{
					
				}
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return amount;
	}
	
	public static Cursor GetCompundAmountDescription(Context context, String offenceSectionCode, String offenceActCode)
	{	
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			String sqlcommand = "SELECT ZONE1, AMOUNT1, AMOUNT_DESC1, ZONE2, AMOUNT2, AMOUNT_DESC2, ZONE3, AMOUNT3, AMOUNT_DESC3, ZONE4, AMOUNT4, AMOUNT_DESC4, ZONE5, AMOUNT5, AMOUNT_DESC5 FROM OFFENCE_RATE_MASTER WHERE ACT_CODE = \"" + offenceActCode + "\" AND SECTION_CODE =\"" + offenceSectionCode + "\"";
			Log.e("MESAGE",sqlcommand );
			cur = obj.Query(sqlcommand, null);
	
			if( (cur != null) && cur.moveToFirst() )
			{			
				//cur.close();
	        }
		}
		catch(Exception ex)
		{
			
		}
		obj.Close();
		return cur;
	}

	public static int DoLogin(String userId, String password,Context context)
	{
		int bResult = 0;
		String encryptedPassword = "";
		ArrayList<DataOfficerInfo> list = new ArrayList<DataOfficerInfo>();
		list.clear();
		
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlcommand = "SELECT OM.CODE , OM.ID, [OR].RANK , OM.RANK_NO , OM.NAME , PASSWORD FROM OFFICER_MAINTENANCE OM , OFFICER_RANK [OR] WHERE OM.ID = '"+userId + "' AND OM.RANK = [OR].CODE ORDER BY  OM.ID";

		Cursor cur = obj.Query(sqlcommand, null);
		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{
				DataOfficerInfo category = new DataOfficerInfo(
						cur.getString(DataOfficerInfo.ColumnName.CODE.ordinal()),
						cur.getString(DataOfficerInfo.ColumnName.ID.ordinal()),
						cur.getString(DataOfficerInfo.ColumnName.RANK.ordinal()),
						cur.getString(DataOfficerInfo.ColumnName.RANKNO.ordinal()),
						cur.getString(DataOfficerInfo.ColumnName.NAME.ordinal()),
						cur.getString(DataOfficerInfo.ColumnName.PASSWORD.ordinal())
						);

				list.add(category);
			} while (cur.moveToNext());
			cur.close();
		}
		obj.Close();
		if( ( list.size() >= 1 ) && (!list.isEmpty() ) )
		{
			encryptedPassword = list.get(0).Password;
			Log.e("MESAGE",encryptedPassword );
			if(Encryption.VerifyHash(password, encryptedPassword, "MD5"))
			{
				CacheManager.officerCode = list.get(0).Code;
				CacheManager.officerId = list.get(0).Id;
				CacheManager.officerRank = list.get(0).Rank;
				CacheManager.officerRankNo = list.get(0).RankNo;
				CacheManager.officerName = list.get(0).Name;
				if(CacheManager.officerRankNo.length() != 0)
				{
					CacheManager.officerDetails = CacheManager.officerRank + " " + CacheManager.officerRankNo + " " + CacheManager.officerName;
				}
				else
				{
					CacheManager.officerDetails = CacheManager.officerRank + " " + CacheManager.officerName;
				}
				bResult = 2;
			}
			else
			{
				bResult = 1;
			}
		}
		return bResult;
	}
}
