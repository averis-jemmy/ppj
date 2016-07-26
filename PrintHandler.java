package id.zenmorf.com.ppjhandheld;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.Environment;

class PrinterCommands {
	public static int LEFT_JUSTIFICATION = -1;
	public static int CENTER_JUSTIFICATION = 0;
	public static int RIGHT_JUSTIFICATION = 1;

	public static byte[] INITIALIZE_PRINTER = {0x1B, 0x40};
	public static byte[] SELECT_PAGE_MODE = {0x1B, 0x4C};
	public static byte[] SELECT_STANDARD_MODE = {0x1B, 0x53};
	public static byte[] CANCEL_PRINT = {0x18};

	public static byte[] PRINT_FEED_LINE = { 0x0A };
	public static byte[] PRINT = { 0x1B, 0x4A, 0x00 };
	public static byte[] PRINT_FEED_STANDARD = { 0x0C };
	public static byte[] PRINT_PAGE_MODE = { 0x1B, 0x0C };

	public static byte[] SET_PRINT_STARTING_POSITION = { 0x1B, 0x4F };
	public static byte[] SET_ABSOLUTE_HORIZONTAL_POSITION = { 0x1B, 0x24 };
	public static byte[] SET_RELATIVE_HORIZONTAL_POSITION = { 0x1B, 0x5C };
	public static byte[] SET_ABSOLUTE_VERTICAL_POSITION = { 0x1D, 0x24 };
	public static byte[] SET_RELATIVE_VERTICAL_POSITION = { 0x1D, 0x5C };

	public static byte[] SET_PRINTING_AREA = { 0x1B, 0x57 };
	public static byte[] SELECT_PRINT_DIRECTION = { 0x1B, 0x54 };

	public static byte[] SELECT_PRINT_MODE = { 0x1B, 0x21, 0x01 };
	public static byte[] RIGHT_CHARACTER_SPACING = { 0x1B, 0x20, 0x02 };
	public static byte[] SELECT_CHARACTER_CODE = { 0x1D, 0x74 };
	public static byte[] SELECT_INTERNATIONAL_CHARACTER_SET = { 0x1D, 0x52 };
	public static byte[] SELECT_CHARACTER_SIZE = { 0x1D, 0x21 };
	public static byte[] EMPHASIZE_MODE = { 0x1B, 0x45 };

	public static byte[] SELECT_BIT_IMAGE_MODE = { 0x1B, 0x2A };
	public static byte[] DEFINE_USER_BIT_IMAGE = { 0x1B, 0x58, 0x34 };

	public static byte[] SET_BARCODE_HEIGHT = { 0x1D, 0x68 };
	public static byte[] SET_BARCODE_WIDTH = { 0x1D, 0x77 };
	public static byte[] PRINT_BARCODE_CODE39 = { 0x1D, 0x6B, 0x45 };
	public static byte[] PRINT_BARCODE_CODE128 = { 0x1D, 0x6B, 0x49 };

	public static byte[] PRINT_LINE_AND_BOX = { 0x1D, 0x69 };

	public static byte[] DEFAULT_LINE_SPACING = { 0x1B, 0x32 };
	public static byte[] SET_LINE_SPACING = { 0x1B, 0x33 };
	public static byte[] SET_RIGHT_SIDE_SPACING = { 0x1B, 0x20 };

	public static byte[] SELECT_BIT_IMAGE_MODE_COMPLETE = {0x1B, 0x2A, 33, (byte)64, 3};

	public static byte[] SET_LINE_SPACING_24 = {0x1B, 0x33, 24};
	public static byte[] SET_LINE_SPACING_30 = {0x1B, 0x33, 30};
}

public class PrintHandler {
	public static ArrayList<byte[]> printData = new ArrayList<byte[]>();
	private static Bitmap b = Bitmap.createBitmap(832, 2000, Bitmap.Config.ARGB_8888);
	private static Canvas c = new Canvas(b);
	
	private static void InitPrinterTraffic()
	{
		b = Bitmap.createBitmap(832, 1800, Bitmap.Config.ARGB_8888);
		c = new Canvas(b);

		c.drawColor(Color.WHITE);

		printData = new ArrayList<byte[]>();

		printData.add(PrinterCommands.INITIALIZE_PRINTER);
		
		PosY = 0;
	}

	public static void TestPrint(String MACAddress)
	{
		b = Bitmap.createBitmap(832, 100, Bitmap.Config.ARGB_8888);
		c = new Canvas(b);

		c.drawColor(Color.WHITE);

		printData = new ArrayList<byte[]>();

		printData.add(PrinterCommands.INITIALIZE_PRINTER);

		PosY = 0;

		PosY = 0;

		PrintText(0.2, 0.2, "Arial", 20, true, MACAddress, -1);

		Bitmap bmp = DrawBmp();

		PrintImage(bmp);
	}
	
	private static double PosY = 0;

	private static Bitmap DrawBmp()
	{
		c.save();

		return b;
	}
	
	private static void PrintText(double PosX, double IncY, String FontType, int FontSize, boolean FontBold, String strVariable, int nLimit) {
		PrintText(PosX, IncY, FontType, FontSize, FontBold, strVariable, nLimit, false);
	}
	
	private static void PrintText(double PosX, double IncY, String FontType, int FontSize, boolean FontBold, String strVariable, int nLimit, boolean RightJustified)
	{
		Paint p = new Paint();

		p.setColor(Color.BLACK);
		p.setTextSize((float)(FontSize * 2.25));
		
		if(FontBold)
			p.setTypeface(Typeface.DEFAULT_BOLD);
		
		if(RightJustified)
			p.setTextAlign(Align.RIGHT);
		
		if(nLimit == 0)
			p.setTextAlign(Align.CENTER);
		
        PosY += IncY;
        
        float xPos = (float)PosX * 200;
        float yPos = (float)PosY * 200;
        
		c.drawText(strVariable, xPos, yPos, p);
		c.save();
	}
	
	private static void PrintTextFlow(String strVariable, double PosX, double IncY)
    {
		Paint p = new Paint();

		p.setColor(Color.BLACK);
		p.setTextSize(20.25f);
		
		p.setTypeface(Typeface.DEFAULT_BOLD);
		
        PosY += IncY;
        
        float xPos = (float)PosX * 200;
        float yPos = (float)PosY * 200;
        
        String strTemp = "";
        while(strVariable.length() != 0)
        {
        	if(p.measureText(strVariable) < 760)
			{
				c.drawText(strVariable, xPos, yPos, p);
				c.save();
				yPos+=20;
				strVariable = strTemp.trim();
				strTemp = "";
			}
        	if(strVariable.length() != 0)
        	{
            	if(p.measureText(strVariable) < 760)
    			{
    				c.drawText(strVariable, xPos, yPos, p);
    				c.save();
    				yPos+=20;
    				strVariable = "";
    				strTemp = "";
    			}
            	else
            	{
		    		strTemp = strVariable.substring(strVariable.lastIndexOf(' ')) + strTemp;
		    		strVariable = strVariable.substring(0, strVariable.lastIndexOf(' '));
            	}
        	}
        }
    }
	
	private static void PrintImage(String imgFileName, double PosX, double IncY, double Width, double Height, boolean Aspect) {
		Paint p = new Paint();

		p.setColor(Color.BLACK);
		p.setTextSize(20);

		InputStream stBmp = null;
		
		try {
			stBmp = CacheManager.mContext.getAssets().open(imgFileName);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Bitmap temp = BitmapFactory.decodeStream(stBmp);
		
		PosY += IncY;
		
		float xPos = (float)PosX * 200;
		float yPos = (float)PosY * 200;
		
		c.drawBitmap(temp, xPos, yPos, p);
		c.save();
	}
	
	private static void PrintBarCode(double PosX, double IncY, String strBarCode) {
		Paint p = new Paint();

		AssetManager assetManager = CacheManager.mContext.getAssets();
		
		Typeface font = Typeface.createFromAsset(assetManager, "Barcode39.otf");
		p.setTypeface(font);
		p.setColor(Color.BLACK);
		p.setTextSize(60);
		
		PosY += IncY;
		
		float xPos = (float)PosX * 200;
		float yPos = (float)PosY * 200;
		
		c.drawText("*" + strBarCode + "*", xPos, yPos, p);
		c.save();
	}
	
	private static void PrintRect(double left, double top, double right, double bottom)
	{
		Paint p = new Paint();

		p.setColor(Color.BLACK);
		
		PosY += top;
 
		float lPos = (float)left * 200;
		float rPos = (float)right * 200;
		float tPos = (float)PosY * 200;
		
		PosY += bottom;
		float bPos = (float)PosY * 200;
		
		c.drawLine(lPos, tPos, rPos, tPos, p);
		c.drawLine(lPos, tPos, lPos, bPos, p);
		c.drawLine(lPos, bPos, rPos, bPos, p);
		c.drawLine(rPos, tPos, rPos, bPos, p);
		c.save();
	}
	
	private static byte[] ConvertBitArray(int[] src, int width, int height)
	{
		int w = width / 8;
		w = w + ((width % 8 > 0) ? 1 : 0);

		byte[] ret = new byte[w * height];

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < w; x++)
			{
				for (int b = 0; b < 8; b++)
				{
					int pos = y * width + x * 8 + b;
					int col = x * 8 + b + 1;
					int monoPos = y * w + x;

					if (col > width)
					{
						ret[monoPos] = (byte) ((ret[monoPos] << 1) + 1);
					}
					else
					{
						if (src[pos] == -1)
							ret[monoPos] = (byte) ((ret[monoPos] << 1));
						else
							ret[monoPos] = (byte) ((ret[monoPos] << 1) + 1);
					}
				}
			}
		}

		return ret;
	}
	
	private static void PrintImage(Bitmap bmp)
	{
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int[] pixels = new int[height * width];
		int pH = 100;

		byte pW = (byte) (width / 8);
		int pL = pH * pW;

		bmp.getPixels(pixels, 0, width, 0, 0, width, height);

		byte[] bitArray = ConvertBitArray(pixels, width, height);

		/*
		BitSet dots = convertArgbToGrayscale(bmp, width, height);

		CacheManager.mSerialService.write(PrinterCommands.SET_LINE_SPACING_24);
		int offset = 0;
		while (offset < bmp.getHeight()) {
			CacheManager.mSerialService.write(PrinterCommands.SELECT_BIT_IMAGE_MODE_COMPLETE);
			for (int x = 0; x < bmp.getWidth(); ++x) {
				for (int k = 0; k < 3; ++k) {
					byte slice = 0;
					for (int b = 0; b < 8; ++b) {
						int y = (((offset / 8) + k) * 8) + b;
						int i = (y * bmp.getWidth()) + x;
						boolean v = false;
						if (i < dots.length()) {
							v = dots.get(i);
						}
						slice |= (byte) ((v ? 1 : 0) << (7 - b));
					}
					CacheManager.mSerialService.write(new byte[] { slice });
				}
			}
			offset += 24;
		}

		CacheManager.mSerialService.write(PrinterCommands.PRINT_FEED_LINE);
		CacheManager.mSerialService.write(PrinterCommands.SET_LINE_SPACING_30);
		*/

		for (int r = 0; r < height / pH; r++)
		{
			byte[] send= new byte[pL+5];
			send[0]=0x1b;
			send[1]=0x58;
			send[2]=0x34;
			send[3]=pW;
			send[4]=100;
			System.arraycopy(bitArray, r * pL, send, 5, pL);

			CacheManager.mSerialService.write(send);
		}

		CacheManager.mSerialService.write(PrinterCommands.PRINT_FEED_LINE);
	}

	private static BitSet convertArgbToGrayscale(Bitmap bmpOriginal, int width,
												 int height) {
		int pixel;
		int k = 0;
		int B = 0, G = 0, R = 0;
		BitSet dots = new BitSet();
		try {

			for (int x = 0; x < height; x++) {
				for (int y = 0; y < width; y++) {
					// get one pixel color
					pixel = bmpOriginal.getPixel(y, x);

					// retrieve color of all channels
					R = Color.red(pixel);
					G = Color.green(pixel);
					B = Color.blue(pixel);
					// take conversion up to one single value by calculating
					// pixel intensity.
					R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
					// set bit into bitset, by calculating the pixel's luma
					if (R < 55) {
						dots.set(k);//this is the bitset that i'm printing
					}
					k++;
				}
			}
		} catch (Exception e) {
		}
		return dots;
	}

	private static void saveBitmap(Bitmap bmp) {
		try {
			String file_path = "/mnt/sdcard/CustomDir";
			File dir = new File(file_path);
			if(!dir.exists())
				dir.mkdirs();
			String format = new SimpleDateFormat("yyyyMMddHHmmss",
					java.util.Locale.getDefault()).format(new Date());
			File file = new File(dir, format + ".png");
			try {
				file.createNewFile();
				file.setReadable(true, false);
				file.setWritable(true, false);
			} catch (Exception e) {

			}
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void PrintTraffic(PPJSummonIssuanceInfo notice){

		int xlSize = 12;
		int lSize = 10;
		int mSize = 9;
		int sSize = 8;

		InitPrinterTraffic();
		
		PrintImage("logo.bmp", 0.8, 0, 0, 0, false);
		
        PrintText(1.2, 0.15, "Arial", xlSize, true, "PERBADANAN PUTRAJAYA", -1);

		PrintText(3.1, 0.15, "Arial", mSize, false, "No. :", -1, true);
        PrintText(4, 0, "Arial", mSize, true, notice.NoticeSerialNo, -1, true);
		PrintText(0, 0.1, "Arial", sSize, false, "", -1);
        PrintBarCode(1.7, 0.4, notice.NoticeSerialNo);
        PrintText(0.95, 0.15, "Arial", lSize, true, "NOTIS KESALAHAN SERTA TAWARAN KOMPAUN", -1);
        PrintText(0.1, 0.3, "Arial", mSize, false, "NO. KENDERAAN", -1);
		PrintText(2.1, 0, "Arial", mSize, false, "NO. CUKAI JALAN", -1);
        PrintText(0.95, 0, "Arial", mSize, true, " : " + notice.VehicleNo, 20);
        PrintText(2.95, 0, "Arial", mSize, true, " : " + notice.RoadTaxNo, 15);
        PrintText(0.1, 0.15, "Arial", mSize, false, "JENAMA / MODEL", -1);
        String makeModel = "";
        if(notice.VehicleMake.length() != 0)
        	makeModel += notice.VehicleMake;
        else
        	makeModel += notice.SelectedVehicleMake;
        
        if(notice.VehicleModel.length() != 0)
        	makeModel += " " + notice.VehicleModel;
        else
        	makeModel += " " + notice.SelectedVehicleModel;
        PrintText(0.95, 0, "Arial", mSize, true, " : " + makeModel, -1);
        PrintText(0.1, 0.15, "Arial", mSize, false, "JENIS BADAN", -1);
        PrintText(0.95, 0, "Arial", mSize, true, " : " + notice.VehicleType, -1);
        PrintText(0.1, 0.15, "Arial", mSize, false, "TEMPAT / JALAN", -1);
        if(notice.OffenceLocationArea.length() != 0)
        {
        	PrintText(0.95, 0, "Arial", mSize, true, " : " + notice.OffenceLocationArea, -1);
        }
        else
        {
        	PrintText(0.95, 0, "Arial", mSize, true, " : " + notice.SummonLocation, -1);
        }
        if(notice.OffenceLocationDetails.length() > 0)
        	PrintText(0.95, 0.15, "Arial", mSize, true, " : " + notice.OffenceLocationDetails, -1);
        else
        	PrintText(0.95, 0.15, "Arial", mSize, true, " : " + "-", -1);
        
        PrintText(0.1, 0.15, "Arial", mSize, false, "NO. PETAK/TIANG", -1);
        if(notice.PostNo.length() > 0)
        	PrintText(0.95, 0, "Arial", mSize, true, " : " + notice.PostNo, -1);
        else
        	PrintText(0.95, 0, "Arial", mSize, true, " : " + "-", -1);
        
        PrintText(0.1, 0.15, "Arial", mSize, false, "TARIKH", -1);
		PrintText(2.1, 0, "Arial", mSize, false, "WAKTU", -1);
		PrintText(0.95, 0, "Arial", mSize, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), -1);
        PrintText(2.95, 0, "Arial", mSize, true, " : " + CacheManager.GetTimeString(notice.OffenceDateTime), -1);
        PrintTextFlow("KEPADA PEMUNYA / PEMANDU KENDERAAN TERSEBUT DI ATAS, TUAN / PUAN DI DAPATI TELAH MELAKUKAN KESALAHAN SEPERTI BERIKUT :", 0.1, 0.3);
        PrintText(0.1, 0.3, "Arial", mSize, true, "PERUNTUKAN UNDANG-UNDANG:", -1);
        PrintTextFlow(notice.OffenceAct, 0.3, 0.15);
        PrintText(0.1, 0.3, "Arial", mSize, true, "SEKSYEN / KAEDAH :", -1);
        PrintTextFlow(notice.OffenceSection, 0.3, 0.15);
        PrintText(0.1, 0.15, "Arial", mSize, true, "KESALAHAN :", -1);
        PrintTextFlow(notice.Offence, 0.3, 0.15);
        PrintText(0.1, 0.4, "Arial", mSize, true, "BUTIR-BUTIR :", -1);
        if(notice.OffenceDetails.length()>0)
        	PrintTextFlow(notice.OffenceDetails, 0.3, 0.15);
        else
        	PrintTextFlow("-", 0.3, 0.15);
        
        PrintText(0.1, 0.3, "Arial", mSize, false, "DIKELUARKAN OLEH :", -1);
        PrintText(1.1, 0, "Arial", mSize, true, CacheManager.officerDetails, -1);
        PrintText(1.0, 0.1, "Arial", mSize, false, "PENGUATKUASA/WARDEN LALULINTAS", -1);
        PrintText(3.0, 0, "Arial", mSize, false, "TARIKH :", -1);
        PrintText(3.5, 0, "Arial", mSize, true, CacheManager.GetDateString(notice.OffenceDateTime), -1);
        PrintText(0.1, 0.1, "Arial", mSize, false, "_______________________________________________________________________________________________________________________________", -1);
        PrintText(0.1, 0.2, "Arial", mSize, true, "TAWARAN KOMPAUN", -1);
        PrintTextFlow("SAYA BERSEDIA MENGKOMPAUN KESALAHAN SEPERTI YANG DITETAPKAN DALAM MASA 14 HARI (TARIKH TAMAT : " + CacheManager.GetOtherDateString(notice.CompoundDate) + ") DARI TARIKH NOTIS DICETAK. KEGAGALAN MENJELASKAN BAYARAN KOMPAUN AKAN MENYEBABKAN TINDAKAN MAHKAMAH AKAN DIAMBIL.", 0.1, 0.2);
        PrintImage("signature.bmp", 0.2, 0.4, 0, 0, false);
        
        PrintText(2.7, 0.1, "Arial", mSize, true, "KADAR BAYARAN", -1);
        if (notice.CompoundAmountDesc1 != null && notice.CompoundAmountDesc1.length() != 0)
        {
            PrintText(2.5, 0.1, "Arial", mSize, true, notice.CompoundAmountDesc1, -1);
            PrintText(3.2, 0, "Arial", mSize, true, "RM " + String.valueOf(notice.CompoundAmount1), -1);
            if (notice.CompoundAmountDesc2 != null && notice.CompoundAmountDesc2.length() != 0)
            {
                PrintText(2.5, 0.1, "Arial", mSize, true, notice.CompoundAmountDesc2, -1);
                PrintText(3.2, 0, "Arial", mSize, true, "RM " + String.valueOf(notice.CompoundAmount2), -1);
                PrintText(0, -0.1, "Arial", mSize, true, "", -1);
            }
            if (notice.CompoundAmountDesc3 != null && notice.CompoundAmountDesc3.length() != 0)
            {
                PrintText(2.5, 0.2, "Arial", mSize, true, notice.CompoundAmountDesc3, -1);
                PrintText(3.2, 0, "Arial", mSize, true, "RM " + String.valueOf(notice.CompoundAmount3), -1);
                PrintText(0, -0.2, "Arial", mSize, true, "", -1);
            }
            if (notice.CompoundAmountDesc4 != null && notice.CompoundAmountDesc4.length() != 0)
            {
                PrintText(2.5, 0.3, "Arial", mSize, true, notice.CompoundAmountDesc4, -1);
                PrintText(3.2, 0, "Arial", mSize, true, "RM " + String.valueOf(notice.CompoundAmount4), -1);
                PrintText(0, -0.3, "Arial", mSize, true, "", -1);
            }
            if (notice.CompoundAmountDesc5 != null && notice.CompoundAmountDesc5.length() != 0)
            {
                PrintText(2.5, 0.4, "Arial", mSize, true, notice.CompoundAmountDesc5, -1);
                PrintText(3.2, 0, "Arial", mSize, true, "RM " + String.valueOf(notice.CompoundAmount5), -1);
                PrintText(0, -0.4, "Arial", mSize, true, "", -1);
            }
        }
        else
        {
            PrintText(2.9, 0.1, "Arial", mSize, true, "RM " + String.valueOf(notice.CompoundAmount1), -1);
        }
        
        PrintText(0.2, 0.2, "Arial", mSize, false, "...............................................................", -1);
        PrintText(0.3, 0.1, "Arial", mSize, false, "(ROHAYAH BINTI KARIM)", -1);
        PrintText(0.2, 0.1, "Arial", mSize, false, "JABATAN HAL EHWAL UNDANG-UNDANG", -1);
        PrintText(0.25, 0.1, "Arial", mSize, false, "b.p. DATUK BANDAR KUALA LUMPUR", -1);
        PrintText(0.1, 0.3, "Arial", mSize, false, "-------------------------------------------------------------------------------------------------------------------------------", -1);
        PrintImage("logo.bmp", 0.3, 0.05, 0, 0, false);
        PrintText(0.7, 0.1, "Arial", mSize, true, "PERBADANAN PUTRAJAYA", -1);
        PrintText(0.7, 0.2, "Arial", sSize, false, "NO. KEND.", -1);
        PrintText(1.55, 0, "Arial", sSize, true, " : " + notice.VehicleNo, -1);
        PrintText(0.7, 0.1, "Arial", sSize, false, "PERUNTUKAN", -1);
        PrintText(1.55, 0, "Arial", sSize, true, " : " + notice.OffenceAct, 50);
        PrintText(0.7, 0.1, "Arial", sSize, false, "SEKSYEN/KAEDAH", -1);
        PrintText(1.55, 0, "Arial", sSize, true, " : " + notice.OffenceSection, -1);
        PrintText(0.7, 0.1, "Arial", sSize, false, "TARIKH", -1);
        PrintText(1.55, 0, "Arial", sSize, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), -1);
        PrintRect(0.1, 0.2, 4.0, 0.4);
        PrintTextFlow(notice.Advertisement, 0.15, -0.3);
        PrintText(2.7, 0.4, "Arial", sSize, true, "KERATAN UNTUK CATATAN PEMBAYARAN", -1, true);
        PrintText(2.2, 0.1, "Arial", sSize, false, "TERIMA KASIH", -1, true);
        PrintText(3.1, 0.1, "Arial", sSize, false, "No. :", -1, true);
        PrintText(4.0, 0, "Arial", mSize, true, notice.NoticeSerialNo, -1, true);
		
		Bitmap bmp = DrawBmp();
		
		PrintImage(bmp);
	}
}
