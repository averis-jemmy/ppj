package tcubes.dbkl.summons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
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
import android.media.audiofx.NoiseSuppressor;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

class PrinterCommands {
	public static byte[] FEED_LINE = {10};

	public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, (byte)64, 3};
	public static byte[] SET_LINE_SPACING_24 = {0x1B, 0x33, 24};
	public static byte[] SET_LINE_SPACING_30 = {0x1B, 0x33, 30};
}

public class PrintHandler {
	
	private static Bitmap b = Bitmap.createBitmap(832, 2000, Bitmap.Config.ARGB_8888);
	private static Canvas c = new Canvas(b);
	
	private static void InitPrinterTraffic()
	{
		b = Bitmap.createBitmap(832, 1800, Bitmap.Config.ARGB_8888);
		c = new Canvas(b);

		c.drawColor(Color.WHITE);
		
		PosY = 0;
	}
	
	private static void InitPrinterGeneral()
	{
		b = Bitmap.createBitmap(832, 1800, Bitmap.Config.ARGB_8888);
		c = new Canvas(b);

		c.drawColor(Color.WHITE);
		
		PosY = 0;
	}
	
	private static void InitPrinterBusLane()
	{
		b = Bitmap.createBitmap(832, 1800, Bitmap.Config.ARGB_8888);
		c = new Canvas(b);

		c.drawColor(Color.WHITE);
		
		PosY = 0;
	}
	
	private static double PosY = 0;
	private static Canvas maincanvas = null;

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
		p.setTextSize(18);
		
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
			stBmp = CacheManager.context.getAssets().open(imgFileName);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Bitmap temp = BitmapFactory.decodeStream(stBmp);
		
		PosY += IncY;
		
		float xPos = (float) PosX * 200;
		float yPos = (float)PosY * 200;
		
		c.drawBitmap(temp, xPos, yPos, p);
		c.save();
	}
	
	private static void PrintBarCode(double PosX, double IncY, String strBarCode)
	{
		Paint p = new Paint();

		AssetManager assetManager = CacheManager.context.getAssets();
		
		Typeface font = Typeface.createFromAsset(assetManager, "Barcode39.otf");
		p.setTypeface(font);
		p.setColor(Color.BLACK);
		p.setTextSize(60);
		
		PosY += IncY;
		
		float xPos = (float) PosX * 200;
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

		//int[] pixels = new int[height * width];
		//int pH = 100;

		//byte pW = (byte) (width / 8);
		//int pL = pH * pW;

		//bmp.getPixels(pixels, 0, width, 0, 0, width, height);

		//byte[] bitArray = ConvertBitArray(pixels, width, height);

		BitSet dots = convertArgbToGrayscale(bmp, width, height);

		CacheManager.mSerialService.write(PrinterCommands.SET_LINE_SPACING_24);
		int offset = 0;
		while (offset < bmp.getHeight()) {
			CacheManager.mSerialService.write(PrinterCommands.SELECT_BIT_IMAGE_MODE);
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

		CacheManager.mSerialService.write(PrinterCommands.FEED_LINE);
		CacheManager.mSerialService.write(PrinterCommands.SET_LINE_SPACING_30);

		/*
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

		CacheManager.mSerialService.write(PrinterCommands.FEED_LINE);
		*/
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
			String file_path = Environment.getExternalStorageDirectory() +
					"/CustomDir";
			File dir = new File(file_path);
			if(!dir.exists())
				dir.mkdirs();
			String format = new SimpleDateFormat("yyyyMMddHHmmss",
					java.util.Locale.getDefault()).format(new Date());
			File file = new File(dir, format + ".png");
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void TestPrint(String MACAddress)
	{
		b = Bitmap.createBitmap(832, 100, Bitmap.Config.ARGB_8888);
		c = new Canvas(b);

		c.drawColor(Color.WHITE);
		
		PosY = 0;

		PrintText(0.2, 0.2, "Arial", 20, true, MACAddress, -1);

		Bitmap bmp = DrawBmp();
		
		PrintImage(bmp);
	}
	
	public static void PrintTraffic(DBKLSummonIssuanceInfo notice){
		
		InitPrinterTraffic();
		
		PrintImage("logo.bmp", 0.8, 0, 0, 0, false);
		
        PrintText(1.2, 0.15, "Arial", 10, true, "DEWAN BANDARAYA KUALA LUMPUR", -1);
        
        PrintText(1.2, 0.15, "Arial", 8, true, "JABATAN KESELAMATAN DAN PENGUATKUASAAN", -1);
        PrintText(1.2, 0.15, "Arial", 8, false, "UNIT                                             No.", -1);
        PrintText(1.45, 0, "Arial", 8, true, " : " + notice.OfficerZone, -1);
        PrintText(3.0, 0, "Arial", 8, true, " : " + notice.NoticeSerialNo, -1);
        PrintBarCode(1.7, 0.35, notice.NoticeSerialNo);
        PrintText(0.95, 0.15, "Arial", 9, true, "NOTIS KESALAHAN SERTA TAWARAN KOMPAUN", -1);
        PrintText(0.1, 0.3, "Arial", 8, false, "NO. KENDERAAN                                                NO. CUKAI JALAN  ", -1);
        PrintText(0.95, 0, "Arial", 8, true, " : " + notice.noKenderaan, 20);
        PrintText(2.95, 0, "Arial", 8, true, " : " + notice.noCukaiJalan, 15);
        PrintText(0.1, 0.15, "Arial", 8, false, "JENAMA / MODEL", -1);
        String makeModel = "";
        if(notice.jenama.length() != 0)
        	makeModel += notice.jenama;
        else
        	makeModel += notice.vehicleMake;
        
        if(notice.model.length() != 0)
        	makeModel += " " + notice.model;
        else
        	makeModel += " " + notice.vehicleModel;
        PrintText(0.95, 0, "Arial", 8, true, " : " + makeModel, -1);
        PrintText(0.1, 0.15, "Arial", 8, false, "JENIS BADAN", -1);
        PrintText(0.95, 0, "Arial", 8, true, " : " + notice.jenisBadan, -1);
        PrintText(0.1, 0.15, "Arial", 8, false, "TEMPAT / JALAN", -1);
        if(notice.offenceLocationArea.length() != 0)
        {
        	PrintText(0.95, 0, "Arial", 8, true, " : " + notice.offenceLocationArea, -1);
        }
        else
        {
        	PrintText(0.95, 0, "Arial", 8, true, " : " + notice.summonLocation, -1);
        }
        if(notice.offenceLocationDetails.length() > 0)
        	PrintText(0.95, 0.15, "Arial", 8, true, " : " + notice.offenceLocationDetails, -1);
        else
        	PrintText(0.95, 0.15, "Arial", 8, true, " : " + "-", -1);
        
        PrintText(0.1, 0.15, "Arial", 8, false, "NO. PETAK/TIANG", -1);
        if(notice.postNo.length() > 0)
        	PrintText(0.95, 0, "Arial", 8, true, " : " + notice.postNo, -1);
        else
        	PrintText(0.95, 0, "Arial", 8, true, " : " + "-", -1);
        
        PrintText(0.1, 0.15, "Arial", 8, false, "TARIKH                                                                                                           WAKTU", -1);
		PrintText(0.95, 0, "Arial", 8, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), -1);
        PrintText(2.95, 0, "Arial", 8, true, " : " + CacheManager.GetTimeString(notice.OffenceDateTime), -1);
        PrintTextFlow("KEPADA PEMUNYA / PEMANDU KENDERAAN TERSEBUT DI ATAS, TUAN / PUAN DI DAPATI TELAH MELAKUKAN KESALAHAN SEPERTI BERIKUT :", 0.1, 0.3);
        PrintText(0.1, 0.3, "Arial", 8, true, "PERUNTUKAN UNDANG-UNDANG:", -1);
        PrintTextFlow(notice.offenceAct, 0.3, 0.15);
        PrintText(0.1, 0.3, "Arial", 8, true, "SEKSYEN / KAEDAH :", -1);
        PrintTextFlow(notice.offenceSection, 0.3, 0.15);
        PrintText(0.1, 0.15, "Arial", 8, true, "KESALAHAN :", -1);
        PrintTextFlow(notice.offence, 0.3, 0.15);
        PrintText(0.1, 0.4, "Arial", 8, true, "BUTIR-BUTIR :", -1);
        if(notice.offenceDetails.length()>0)
        	PrintTextFlow(notice.offenceDetails, 0.3, 0.15);
        else
        	PrintTextFlow("-", 0.3, 0.15);
        
        PrintText(0.1, 0.3, "Arial", 8, false, "DIKELUARKAN OLEH :", -1);
        PrintText(1.1, 0, "Arial", 8, true, CacheManager.officerDetails, -1);
        PrintText(1.0, 0.1, "Arial", 8, false, "PENGUATKUASA/WARDEN LALULINTAS", -1);
        PrintText(3.0, 0, "Arial", 8, false, "TARIKH :", -1);
        PrintText(3.5, 0, "Arial", 8, true, CacheManager.GetDateString(notice.OffenceDateTime), -1);
        PrintText(0.1, 0.1, "Arial", 8, false, "_______________________________________________________________________________________________________________________________", -1);
        PrintText(0.1, 0.2, "Arial", 8, true, "TAWARAN KOMPAUN", -1);
        PrintTextFlow("SAYA BERSEDIA MENGKOMPAUN KESALAHAN SEPERTI YANG DITETAPKAN DALAM MASA 14 HARI (TARIKH TAMAT : "+ CacheManager.GetOtherDateString(notice.CompoundDate) +") DARI TARIKH NOTIS DICETAK. KEGAGALAN MENJELASKAN BAYARAN KOMPAUN AKAN MENYEBABKAN TINDAKAN MAHKAMAH AKAN DIAMBIL.", 0.1, 0.2);
        PrintImage("signature.bmp", 0.2, 0.3, 0, 0, false);
        //PrintText(2.7, 0.1, "Arial", 8, true, "KADAR BAYARAN", -1);
        //PrintText(2.9, 0.1, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount1), -1);
        
        PrintText(2.7, 0.1, "Arial", 8, true, "KADAR BAYARAN", -1);
        if (notice.compoundAmountDesc1 != null && notice.compoundAmountDesc1.length() != 0)
        {
            PrintText(2.5, 0.1, "Arial", 8, true, notice.compoundAmountDesc1, -1);
            PrintText(3.2, 0, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount1), -1);
            if (notice.compoundAmountDesc2 != null && notice.compoundAmountDesc2.length() != 0)
            {
                PrintText(2.5, 0.1, "Arial", 8, true, notice.compoundAmountDesc2, -1);
                PrintText(3.2, 0, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount2), -1);
                PrintText(0, -0.1, "Arial", 8, true, "", -1);
            }
            if (notice.compoundAmountDesc3 != null && notice.compoundAmountDesc3.length() != 0)
            {
                PrintText(2.5, 0.2, "Arial", 8, true, notice.compoundAmountDesc3, -1);
                PrintText(3.2, 0, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount3), -1);
                PrintText(0, -0.2, "Arial", 8, true, "", -1);
            }
            if (notice.compoundAmountDesc4 != null && notice.compoundAmountDesc4.length() != 0)
            {
                PrintText(2.5, 0.3, "Arial", 8, true, notice.compoundAmountDesc4, -1);
                PrintText(3.2, 0, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount4), -1);
                PrintText(0, -0.3, "Arial", 8, true, "", -1);
            }
            if (notice.compoundAmountDesc5 != null && notice.compoundAmountDesc5.length() != 0)
            {
                PrintText(2.5, 0.4, "Arial", 8, true, notice.compoundAmountDesc5, -1);
                PrintText(3.2, 0, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount5), -1);
                PrintText(0, -0.4, "Arial", 8, true, "", -1);
            }
        }
        else
        {
            PrintText(2.9, 0.1, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount1), -1);
        }
        
        PrintText(0.2, 0.3, "Arial", 8, false, "...............................................................", -1);
        PrintText(0.3, 0.1, "Arial", 8, false, "(ROHAYAH BINTI KARIM)", -1);
        PrintText(0.2, 0.1, "Arial", 8, false, "JABATAN HAL EHWAL UNDANG-UNDANG", -1);
        PrintText(0.25, 0.1, "Arial", 8, false, "b.p. DATUK BANDAR KUALA LUMPUR", -1);
        PrintText(0.1, 0.3, "Arial", 8, false, "-------------------------------------------------------------------------------------------------------------------------------", -1);
        PrintImage("logo.bmp", 0.3, 0.15, 0, 0, false);
        PrintText(0.7, 0, "Arial", 8, true, "DEWAN BANDARAYA KUALA LUMPUR", -1);
        PrintText(0.7, 0.1, "Arial", 8, false, "JABATAN KESELAMATAN DAN PENGUATKUASAAN", -1);
        PrintText(0.7, 0.2, "Arial", 7, false, "NO. KEND.", -1);
        PrintText(1.55, 0, "Arial", 7, true, " : " + notice.noKenderaan, -1);
        PrintText(0.7, 0.1, "Arial", 7, false, "PERUNTUKAN", -1);
        PrintText(1.55, 0, "Arial", 7, true, " : " + notice.offenceAct, 50);
        PrintText(0.7, 0.1, "Arial", 7, false, "SEKSYEN/KAEDAH", -1);
        PrintText(1.55, 0, "Arial", 7, true, " : " + notice.offenceSection, -1);
        PrintText(0.7, 0.1, "Arial", 7, false, "TARIKH", -1);
        PrintText(1.55, 0, "Arial", 7, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), -1);
        PrintRect(0.1, 0.2, 4.0, 0.4);
        PrintTextFlow(notice.advertisement, 0.15, -0.3);
        PrintText(2.7, 0.4, "Arial", 7, true, "KERATAN UNTUK CATATAN PEMBAYARAN", -1, true);
        PrintText(2.2, 0.1, "Arial", 7, false, "TERIMA KASIH", -1, true);
        PrintText(3.1, 0.1, "Arial", 7, false, "No. :", -1, true);
        PrintText(4.0, 0, "Arial", 8, true, notice.NoticeSerialNo, -1, true);
		
		Bitmap bmp = DrawBmp();
		
		PrintImage(bmp);
	}
	
	public static void PrintGeneral(DBKLSummonIssuanceInfo notice)
	{
		InitPrinterGeneral();
		
		PrintImage("logo.bmp", 0.8, 0, 0, 0, false);
		PrintText(1.2, 0.15, "Arial", 10, true, "DEWAN BANDARAYA KUALA LUMPUR", -1);
        PrintText(1.2, 0.15, "Arial", 8, true, "JABATAN KESELAMATAN DAN PENGUATKUASAAN", -1);
        PrintText(1.2, 0.15, "Arial", 8, false, "UNIT                                             No.", -1);
        PrintText(1.45, 0, "Arial", 8, true, " : " + notice.OfficerZone, -1);
        PrintText(3.0, 0, "Arial", 8, true, " : " + notice.NoticeSerialNo, -1);
        PrintBarCode(1.7,  0.35, notice.NoticeSerialNo);
        PrintText(0.85, 0.15, "Arial", 9, true, "NOTIS KESALAHAN SERTA TAWARAN KOMPAUN", -1);
        PrintRect(0.1, 0.15, 4.0, 1.00);
        PrintText(0.15, -0.9, "Arial", 8, false, "NAMA / SYARIKAT", -1);
        PrintText(0.95, 0, "Arial", 8, true, " : " + notice.name, 50);
        PrintText(0.15, 0.2, "Arial", 8, false, "NO. K.P. / SYKT", -1);
        PrintText(0.95, 0, "Arial", 8, true, " : " + notice.kptNo, -1);
        PrintText(0.15, 0.2, "Arial", 8, false, "ALAMAT", -1);
        PrintText(0.95, 0, "Arial", 8, true, " : " + notice.address1, -1);
        PrintText(0.95, 0.2, "Arial", 8, true, " : " + notice.address2, -1);
        PrintText(0.95, 0.2, "Arial", 8, true, " : " + notice.address3, -1);
        PrintRect(0.1, 0.2, 4.0, 0.4);
        PrintText(0.15, -0.3, "Arial", 8, false, "NO. KENDERAAN                                                                   NO. CUKAI JALAN", -1);
        PrintText(0.95, 0, "Arial", 8, true, " : " + notice.noKenderaan, 15);
        PrintText(2.95, 0, "Arial", 8, true, " : " + notice.noCukaiJalan, 15);
        PrintText(0.15, 0.2, "Arial", 8, false, "JENAMA / MODEL                                                                      JENIS BADAN", -1);
        String makeModel = "";
        if(notice.jenama.length() != 0)
        	makeModel += notice.jenama;
        else
        	makeModel += notice.vehicleMake;
        
        if(notice.model.length() != 0)
        	makeModel += " " + notice.model;
        else
        	makeModel += " " + notice.vehicleModel;
        PrintText(0.95, 0, "Arial", 8, true, " : " + makeModel, 18);
        PrintText(2.95, 0, "Arial", 8, true, " : " + notice.jenisBadan, 15);
        PrintTextFlow("KEPADA NAMA / SYARIKAT TERSEBUT DI ATAS,  TUAN / PUAN / SYARIKAT DI DAPATI TELAH MELAKUKAN KESALAHAN SEPERTI BERIKUT :", 0.1, 0.25);
        PrintText(0.1, 0.3, "Arial", 8, true, "PERUNTUKAN UNDANG-UNDANG :", -1);
        PrintTextFlow(notice.offenceAct, 0.3, 0.15);
        PrintText(0.1, 0.25, "Arial", 8, true, "SEKSYEN/KAEDAH : ", -1);
        PrintText(1.2, 0, "Arial", 8, true, notice.offenceSection, -1);
        PrintText(0.1, 0.2, "Arial", 8, true, "KESALAHAN :", -1);
        PrintTextFlow(notice.offence, 0.3, 0.15);
        PrintText(0.1, 0.4, "Arial", 8, true, "BUTIR-BUTIR :", -1);
        if(notice.offenceDetails.length()>0)
        	PrintTextFlow(notice.offenceDetails, 0.3, 0.15);
        else
        	PrintTextFlow("-", 0.3, 0.15);
        PrintText(0.1, 0.3, "Arial", 8, false, "TEMPAT / JALAN", -1);
        if(notice.offenceLocationArea.length() != 0)
        {
        	PrintText(1.0, 0, "Arial", 8, true, " : " + notice.offenceLocationArea, -1);
        }
        else
        {
        	PrintText(1.0, 0, "Arial", 8, true, " : " + notice.summonLocation, -1);
        }
        PrintText(0.1, 0.15, "Arial", 8, false, "BUTIRAN LOKASI", -1);
        if(notice.offenceLocationDetails.length() > 0)
        	PrintText(1.0, 0, "Arial", 8, true, " : " + notice.offenceLocationDetails, -1);
        else
        	PrintText(1.0, 0, "Arial", 8, true, " : " + "-", -1);
        PrintText(0.1, 0.15, "Arial", 8, false, "TARIKH                                                                                                    WAKTU", -1);
        PrintText(1.0, 0, "Arial", 8, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), -1);
        PrintText(2.85, 0, "Arial", 8, true, " : " + CacheManager.GetTimeString(notice.OffenceDateTime), -1);
        PrintText(0.1, 0.2, "Arial", 8, false, "DIKELUARKAN OLEH :", -1);
        PrintText(1.15, 0, "Arial", 8, true, CacheManager.officerDetails, -1);
        PrintText(1.0, 0.1, "Arial", 8, false, "PENGUATKUASA/WARDEN LALULINTAS", -1);
        PrintText(3.0, 0, "Arial", 8, false, "TARIKH :", -1);
        PrintText(3.5, 0, "Arial", 8, true, CacheManager.GetDateString(notice.OffenceDateTime), -1);
        PrintText(0.1, 0.1,"Arial",8,true,"_______________________________________________________________________________________________________________________________",-1);
        PrintText(0.1, 0.2, "Arial", 8, true, "TAWARAN KOMPAUN", -1);
		PrintTextFlow("SAYA BERSEDIA MENGKOMPAUN KESALAHAN SEPERTI YANG DITETAPKAN DALAM MASA 14 HARI (TARIKH TAMAT : " + CacheManager.GetOtherDateString(notice.CompoundDate) + ") DARI TARIKH NOTIS DICETAK. KEGAGALAN MENJELASKAN BAYARAN KOMPAUN AKAN MENYEBABKAN TINDAKAN MAHKAMAH AKAN DIAMBIL.",
				0.1, 0.2);
		PrintImage("signature.bmp", 0.2, 0.3, 0, 0, false);
		//PrintText(2.7, 0, "Arial", 8, true, "KADAR BAYARAN", -1);
        //PrintText(2.9, 0.1, "Arial", 8, true, "RM " + offenceStruct.dOffenceCompundAmount.ToString("N2"), -1);
		
		PrintText(2.7, 0.1, "Arial", 8, true, "KADAR BAYARAN", -1);
        if (notice.compoundAmountDesc1 != null && notice.compoundAmountDesc1.length() != 0)
        {
            PrintText(2.5, 0.1, "Arial", 8, true, notice.compoundAmountDesc1, -1);
            PrintText(3.2, 0, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount1), -1);
            if (notice.compoundAmountDesc2 != null && notice.compoundAmountDesc2.length() != 0)
            {
                PrintText(2.5, 0.1, "Arial", 8, true, notice.compoundAmountDesc2, -1);
                PrintText(3.2, 0, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount2), -1);
                PrintText(0, -0.1, "Arial", 8, true, "", -1);
            }
            if (notice.compoundAmountDesc3 != null && notice.compoundAmountDesc3.length() != 0)
            {
                PrintText(2.5, 0.2, "Arial", 8, true, notice.compoundAmountDesc3, -1);
                PrintText(3.2, 0, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount3), -1);
                PrintText(0, -0.2, "Arial", 8, true, "", -1);
            }
            if (notice.compoundAmountDesc4 != null && notice.compoundAmountDesc4.length() != 0)
            {
                PrintText(2.5, 0.3, "Arial", 8, true, notice.compoundAmountDesc4, -1);
                PrintText(3.2, 0, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount4), -1);
                PrintText(0, -0.3, "Arial", 8, true, "", -1);
            }
            if (notice.compoundAmountDesc5 != null && notice.compoundAmountDesc5.length() != 0)
            {
                PrintText(2.5, 0.4, "Arial", 8, true, notice.compoundAmountDesc5, -1);
                PrintText(3.2, 0, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount5), -1);
                PrintText(0, -0.4, "Arial", 8, true, "", -1);
            }
        }
        else
        {
            PrintText(2.9, 0.1, "Arial", 8, true, "RM " + String.valueOf(notice.compoundAmount1), -1);
        }
		
		PrintText(0.2, 0.3, "Arial", 8, false, "...............................................................", -1);
		PrintText(0.3, 0.1, "Arial", 8, false, "(ROHAYAH BINTI KARIM)", -1);
		PrintText(0.2, 0.1, "Arial", 8, false, "JABATAN HAL EHWAL UNDANG-UNDANG", -1);
		PrintText(0.25, 0.1, "Arial", 8, false, "b.p. DATUK BANDAR KUALA LUMPUR", -1);
		PrintText(0.1, 0.3, "Arial", 8, false, "-------------------------------------------------------------------------------------------------------------------------------", -1);
        PrintImage("logo.bmp", 0.3, 0.15, 0, 0, false);
        PrintText(0.7, 0, "Arial", 8, true, "DEWAN BANDARAYA KUALA LUMPUR", -1);
        PrintText(0.7, 0.1, "Arial", 8, false, "JABATAN KESELAMATAN DAN PENGUATKUASAAN", -1);
        PrintText(0.7, 0.2, "Arial", 7, false, "NAMA / SYKT.", -1);
        PrintText(1.4, 0, "Arial", 7, true, " : " + notice.name, -1);
        PrintText(0.7, 0.1, "Arial", 7, false, "PERUNTUKAN", -1);
        PrintText(1.4, 0, "Arial", 7, true, " : " + notice.offenceAct, 50);
        PrintText(0.7, 0.1, "Arial", 7, false, "SEKSYEN / UUK", -1);
        PrintText(1.4, 0, "Arial", 7, true, " : " + notice.offenceSection, -1);
        PrintText(0.7, 0.1, "Arial", 7, false, "TARIKH", -1);
        PrintText(1.4, 0, "Arial", 7, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), -1);
        PrintRect(0.1, 0.1, 4.0, 0.4);
		PrintTextFlow(notice.advertisement, 0.15, -0.3);

        PrintText(2.7, 0.4, "Arial", 7, true, "KERATAN UNTUK CATATAN PEMBAYARAN", -1, true);
        PrintText(2.2, 0.1, "Arial", 7, false, "TERIMA KASIH", -1, true);
        PrintText(3.1, 0.1, "Arial", 7, false, "No. :", -1, true);
        PrintText(4.0, 0, "Arial", 8, true, notice.NoticeSerialNo, -1, true);
        
		Bitmap bmp = DrawBmp();

		PrintImage(bmp);
	}
	
	public static void PrintBusLane(DBKLSummonIssuanceInfo notice)
	{
		InitPrinterBusLane();
		
		PrintImage("logo.bmp", 0.8, 0, 0, 0, false);
		PrintText(1.2, 0.15, "Arial", 10, true, "DEWAN BANDARAYA KUALA LUMPUR", -1,false);
        PrintText(1.2, 0.15, "Arial", 8, true, "JABATAN KESELAMATAN DAN PENGUATKUASAAN", -1,false);
        PrintText(2.55, 0.2, "Arial", 8, false, "No.", 0,false);
        PrintText(2.75, 0, "Arial", 8, true, " : " + notice.NoticeSerialNo, -1,false);
        PrintBarCode(1.7, 0.35, notice.NoticeSerialNo);
        PrintText(2.0, 0.35, "Arial", 10, true, "AKTA PENGANGKUTAN JALAN 1987", 0,false);
        PrintText(2.0, 0.2, "Arial", 9, false, "KAEDAH-KAEDAH LALULINTAS JALAN", 0,false);
        PrintText(2.0, 0.2, "Arial", 9, false, "(BANDARAYA KUALA LUMPUR)", 0,false);
        PrintText(2.0, 0.2, "Arial", 9, false, "(SAMAN) 1977", 0,false);
        PrintText(0.1, 0.3, "Arial", 8, false, "KEPADA", -1);
        PrintText(1.2, 0, "Arial", 8, true, " : " + notice.name, 50);
        PrintText(0.1, 0.2, "Arial", 8, false, "NO. KAD PENGENALAN", -1);
        PrintText(1.2, 0, "Arial", 8, true, " : " + notice.kptNo, -1);
        PrintText(0.1, 0.2, "Arial", 8, false, "ALAMAT", -1);
        PrintText(1.2, 0, "Arial", 8, true, " : " + notice.address1, -1);
        PrintText(1.2, 0.2, "Arial", 8, true, " : " + notice.address2, -1);
        PrintText(1.2, 0.2, "Arial", 8, true, " : " + notice.address3, -1);
        PrintText(0.1, 0.2, "Arial", 8, false, "NO. LESEN MEMANDU                                                                TARIKH TAMAT", -1);
        PrintText(1.2, 0, "Arial", 8, true, " : " + notice.LicenseNo, -1);
        PrintText(2.95, 0, "Arial", 8, true, " : " + CacheManager.GetOtherDateString(notice.licenseExpiryDate), -1);
        PrintText(0.1, 0.2, "Arial", 8, false, "NO. KENDERAAN                                                                    JENAMA / MODEL", -1);
        PrintText(1.2, 0, "Arial", 8, true, " : " + notice.noKenderaan, 20);
        String makeModel = "";
        if(notice.jenama.length() != 0)
        	makeModel += notice.jenama;
        else
        	makeModel += notice.vehicleMake;
        
        if(notice.model.length() != 0)
        	makeModel += " " + notice.model;
        else
        	makeModel += " " + notice.vehicleModel;
        PrintText(2.95, 0, "Arial", 8, true, " : " + makeModel, 20);
        PrintText(0.1, 0.2, "Arial", 8, false, "NO. CUKAI JALAN                                                                     TARIKH TAMAT", -1);
        PrintText(1.2, 0, "Arial", 8, true, " : " + notice.noCukaiJalan, -1);
        PrintText(2.95, 0, "Arial", 8, true, " : " + CacheManager.GetOtherDateString(notice.roadtaxExpiryDate), -1);
        PrintTextFlow("BAHAWASANYA SAYA MEMPUNYAI SEBAB YANG MUNASABAH UNTUK MEMPERCAYAI BAHAWA ANDA TELAH MELAKUKAN KESALAHAN SEPERTI BERIKUT :", 0.1, 0.3);
        PrintTextFlow(notice.offence + " - " + notice.offenceDetails, 0.1, 0.25);
        PrintText(0.1, 0.45, "Arial", 8, false, "DI BAWAH SEKSYEN/KAEDAH/PERENGGAN", -1);
        PrintText(2.1, 0, "Arial", 8, true, " : " + notice.offenceSection, -1);
        PrintTextFlow(notice.offenceAct, 0.1, 0.2);
        PrintText(0.1, 0.35, "Arial", 8, false, "DI (TEMPAT)", -1);
        if(notice.offenceLocationArea.length() != 0)
        {
        	PrintText(1.0, 0, "Arial", 8, true, " : " + notice.offenceLocationArea, -1);
        }
        else
        {
        	PrintText(1.0, 0, "Arial", 8, true, " : " + notice.summonLocation, -1);
        }
        PrintText(0.1, 0.2, "Arial", 8, false, "PADA (TARIKH)                                                                                  JAM", -1);
        PrintText(1.0, 0, "Arial", 8, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), -1);
        PrintText(2.8, 0, "Arial", 8, true, " : " + CacheManager.GetTimeString(notice.OffenceDateTime), -1);
        PrintTextFlow("ANDA ADALAH DIPERINTAH HADIR DENGAN SENDIRI ATAU MELALUI PEGUAM DI HADAPAN MAHKAMAH MAJISTRET (DEWAN BANDARAYA) KUALA LUMPUR, JALAN TUN RAZAK, 50400 KUALA LUMPUR.", 0.1, 0.3);
        PrintText(0.1, 0.5, "Arial", 8, false, "PADA (TARIKH)                                                                                  JAM", -1);
        PrintText(1.0, 0, "Arial", 8, true, " : " + CacheManager.GetDateString(notice.courtDate), -1);
        PrintText(2.8, 0, "Arial", 8, true, " : 9:00 PAGI", -1);
        PrintTextFlow("KESALAHAN INI BOLEH DIKOMPAUNKAN. ANDA BOLEH MENGHADIRKAN DIRI KEPADA PEGAWAI KOMPAUN DI BANGUNAN DEWAN BANDARAYA, JALAN RAJA LAUT, KUALA LUMPUR ATAU PEJABAT JABATAN KESELAMATAN DAN PENGUATKUASAAN, JALAN TUN RAZAK, 50400 KUALA LUMPUR UNTUK MENGKOMPAUNKAN KESALAHAN INI SEBELUM ", 0.1, 0.3);
        PrintText(2.8, 0.45, "Arial", 8, true, ": " + CacheManager.GetDateString(notice.CompoundDate), -1);
        PrintText(0.1, 0.55, "Arial", 8, false, "TARIKH DIKELUARKAN                                                                          JAM", -1);
        PrintText(1.2, 0, "Arial", 8, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), -1);
        PrintText(2.8, 0, "Arial", 8, true, " : " + CacheManager.GetTimeString(notice.OffenceDateTime), -1);
        PrintText(0.1, 0.7, "Arial", 8, false, "...............................................                                  ..............................................................................", -1);
        PrintText(0.1, 0.1, "Arial", 8, false, "TANDATANGAN PENERIMA                                     NAMA & NOMBOR WARDEN LALULINTAS", -1);
        
        PrintText(4.0, 0.1, "Arial", 8, true, CacheManager.officerDetails, -1,true);
        PrintText(0.9, 0.6, "Arial", 8, true, "No. : " + notice.NoticeSerialNo, -1,true);
        Bitmap bmp = DrawBmp();

		PrintImage(bmp);
	}
}
