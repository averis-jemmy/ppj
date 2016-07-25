package tcubes.dbkl.summons;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.renderscript.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 9/6/2016.
 */

class PrinterHandlerCommands {

    public static int LEFT_JUSTIFICATION = PrinterHandlerCommands.LEFT_JUSTIFICATION;
    public static int CENTER_JUSTIFICATION = 0;
    public static int RIGHT_JUSTIFICATION = 1;

    public static byte[] INITIALIZE_PRINTER = {0x1B, 0x40};
    public static byte[] SELECT_PAGE_MODE = {0x1B, 0x4C};
    public static byte[] SELECT_STANDARD_MODE = {0x1B, 0x53};
    public static byte[] CANCEL_PRINT = {0x18};

    public static byte[] PRINT_FEED_LINE = { 0x0A };
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
}

public class NewPrintHandler {

    public static ArrayList<byte[]> printData = new ArrayList<byte[]>();
    private static double PosY = 0;

    private static void InitPrinter() {
        printData = new ArrayList<byte[]>();

        printData.add(PrinterHandlerCommands.INITIALIZE_PRINTER);
        printData.add(PrinterHandlerCommands.SELECT_PAGE_MODE);
        printData.add(PrinterHandlerCommands.SELECT_PRINT_MODE);

        PosY = 0;
    }

    private static void PrintText(double PosX, double IncY, int FontSize, boolean FontBold, String strVariable, int Justification) {
        byte[] send = new byte[4];
        int pos = (int)(PosY * 208);
        int h = pos / 256;
        int l = pos % 256;
        send[0] = PrinterHandlerCommands.SET_ABSOLUTE_VERTICAL_POSITION[0];
        send[1] = PrinterHandlerCommands.SET_ABSOLUTE_VERTICAL_POSITION[1];
        send[2] = (byte)l;
        send[3] = (byte)h;
        printData.add(send);
        send = new byte[4];
        pos = (int)(PosX * 208);
        h = pos / 256;
        l = pos % 256;
        send[0] = PrinterHandlerCommands.SET_ABSOLUTE_HORIZONTAL_POSITION[0];
        send[1] = PrinterHandlerCommands.SET_ABSOLUTE_HORIZONTAL_POSITION[1];
        send[2] = (byte)l;
        send[3] = (byte)h;
        printData.add(send);

        send = new byte[3];
        send[0] = PrinterHandlerCommands.EMPHASIZE_MODE[0];
        send[1] = PrinterHandlerCommands.EMPHASIZE_MODE[1];
        if(FontBold)
            send[2] = 0x01;
        else
            send[2] = 0x00;
        printData.add(send);

        send = new byte[3];
        send[0] = PrinterHandlerCommands.SELECT_CHARACTER_SIZE[0];
        send[1] = PrinterHandlerCommands.SELECT_CHARACTER_SIZE[1];
        int charSize = FontSize - 8;
        if(charSize < 0)
            charSize = 0;
        send[2]=(byte)(charSize & 0x000000ff);
        /*switch(FontSize) {
            case 9:
                send[2] = 0x10;
                break;
            case 10:
                send[2] = 0x11;
                break;
            case 11:
                send[2] = 0x21;
                break;
            case 12:
                send[2] = 0x22;
                break;
            default:
                send[2] = 0x00;
                break;
        }*/
        printData.add(send);

        byte[] message ;
        try {
            message = strVariable.getBytes("UTF-8");
        } catch (Exception e) {
            message = strVariable.getBytes();
        }
        printData.add(message);

        PosY += IncY;
    }

    private static void Print() {
        for (byte[] data : printData) {
            CacheManager.mSerialService.write(data);
        }

        CacheManager.mSerialService.write(PrinterCommands.FEED_LINE);
    }

    public static void TestPrint(String MACAddress) {
        InitPrinter();

        PrintText(0.2, 0.2, 8, true, MACAddress, PrinterHandlerCommands.LEFT_JUSTIFICATION);

        Print();
    }

    public static void PrintTraffic(DBKLSummonIssuanceInfo notice) {
        InitPrinter();

        //PrintImage("logo.bmp", 0.8, 0, 0, 0, false);

        PrintText(1.2, 0.15, 10, true, "DEWAN BANDARAYA KUALA LUMPUR", PrinterHandlerCommands.LEFT_JUSTIFICATION);

        PrintText(1.2, 0.15, 8, true, "JABATAN KESELAMATAN DAN PENGUATKUASAAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0.15, 8, false, "UNIT                                             No.", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.45, 0, 8, true, " : " + notice.OfficerZone, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(3.0, 0, 8, true, " : " + notice.NoticeSerialNo, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintBarCode(1.7, 0.35, notice.NoticeSerialNo);
        PrintText(0.95, 0.15, 9, true, "NOTIS KESALAHAN SERTA TAWARAN KOMPAUN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.3, 8, false, "NO. KENDERAAN                                                NO. CUKAI JALAN  ", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.95, 0, 8, true, " : " + notice.noKenderaan, 20);
        PrintText(2.95, 0, 8, true, " : " + notice.noCukaiJalan, 15);
        PrintText(0.1, 0.15, 8, false, "JENAMA / MODEL", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        String makeModel = "";
        if(notice.jenama.length() != 0)
            makeModel += notice.jenama;
        else
            makeModel += notice.vehicleMake;

        if(notice.model.length() != 0)
            makeModel += " " + notice.model;
        else
            makeModel += " " + notice.vehicleModel;
        PrintText(0.95, 0, 8, true, " : " + makeModel, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.15, 8, false, "JENIS BADAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.95, 0, 8, true, " : " + notice.jenisBadan, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.15, 8, false, "TEMPAT / JALAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        if(notice.offenceLocationArea.length() != 0)
        {
            PrintText(0.95, 0, 8, true, " : " + notice.offenceLocationArea, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        }
        else
        {
            PrintText(0.95, 0, 8, true, " : " + notice.summonLocation, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        }
        if(notice.offenceLocationDetails.length() > 0)
            PrintText(0.95, 0.15, 8, true, " : " + notice.offenceLocationDetails, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        else
            PrintText(0.95, 0.15, 8, true, " : " + "-", PrinterHandlerCommands.LEFT_JUSTIFICATION);

        PrintText(0.1, 0.15, 8, false, "NO. PETAK/TIANG", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        if(notice.postNo.length() > 0)
            PrintText(0.95, 0, 8, true, " : " + notice.postNo, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        else
            PrintText(0.95, 0, 8, true, " : " + "-", PrinterHandlerCommands.LEFT_JUSTIFICATION);

        PrintText(0.1, 0.15, 8, false, "TARIKH                                                                                                           WAKTU", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.95, 0, 8, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(2.95, 0, 8, true, " : " + CacheManager.GetTimeString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow("KEPADA PEMUNYA / PEMANDU KENDERAAN TERSEBUT DI ATAS, TUAN / PUAN DI DAPATI TELAH MELAKUKAN KESALAHAN SEPERTI BERIKUT :", 0.1, 0.3);
        PrintText(0.1, 0.3, 8, true, "PERUNTUKAN UNDANG-UNDANG:", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow(notice.offenceAct, 0.3, 0.15);
        PrintText(0.1, 0.3, 8, true, "SEKSYEN / KAEDAH :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow(notice.offenceSection, 0.3, 0.15);
        PrintText(0.1, 0.15, 8, true, "KESALAHAN :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow(notice.offence, 0.3, 0.15);
        PrintText(0.1, 0.4, 8, true, "BUTIR-BUTIR :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //if(notice.offenceDetails.length()>0)
            //PrintTextFlow(notice.offenceDetails, 0.3, 0.15);
        //else
            //PrintTextFlow("-", 0.3, 0.15);

        PrintText(0.1, 0.3, 8, false, "DIKELUARKAN OLEH :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.1, 0, 8, true, CacheManager.officerDetails, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.0, 0.1, 8, false, "PENGUATKUASA/WARDEN LALULINTAS", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(3.0, 0, 8, false, "TARIKH :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(3.5, 0, 8, true, CacheManager.GetDateString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.1, 8, false, "_______________________________________________________________________________________________________________________________", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.2, 8, true, "TAWARAN KOMPAUN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow("SAYA BERSEDIA MENGKOMPAUN KESALAHAN SEPERTI YANG DITETAPKAN DALAM MASA 14 HARI (TARIKH TAMAT : "+ CacheManager.GetOtherDateString(notice.CompoundDate) +") DARI TARIKH NOTIS DICETAK. KEGAGALAN MENJELASKAN BAYARAN KOMPAUN AKAN MENYEBABKAN TINDAKAN MAHKAMAH AKAN DIAMBIL.", 0.1, 0.2);
        //PrintImage("signature.bmp", 0.2, 0.3, 0, 0, false);

        PrintText(2.7, 0.1, 8, true, "KADAR BAYARAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        if (notice.compoundAmountDesc1 != null && notice.compoundAmountDesc1.length() != 0)
        {
            PrintText(2.5, 0.1, 8, true, notice.compoundAmountDesc1, PrinterHandlerCommands.LEFT_JUSTIFICATION);
            PrintText(3.2, 0, 8, true, "RM " + String.valueOf(notice.compoundAmount1), PrinterHandlerCommands.LEFT_JUSTIFICATION);
            if (notice.compoundAmountDesc2 != null && notice.compoundAmountDesc2.length() != 0)
            {
                PrintText(2.5, 0.1, 8, true, notice.compoundAmountDesc2, PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(3.2, 0, 8, true, "RM " + String.valueOf(notice.compoundAmount2), PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(0, -0.1, 8, true, "", PrinterHandlerCommands.LEFT_JUSTIFICATION);
            }
            if (notice.compoundAmountDesc3 != null && notice.compoundAmountDesc3.length() != 0)
            {
                PrintText(2.5, 0.2, 8, true, notice.compoundAmountDesc3, PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(3.2, 0, 8, true, "RM " + String.valueOf(notice.compoundAmount3), PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(0, -0.2, 8, true, "", PrinterHandlerCommands.LEFT_JUSTIFICATION);
            }
            if (notice.compoundAmountDesc4 != null && notice.compoundAmountDesc4.length() != 0)
            {
                PrintText(2.5, 0.3, 8, true, notice.compoundAmountDesc4, PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(3.2, 0, 8, true, "RM " + String.valueOf(notice.compoundAmount4), PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(0, -0.3, 8, true, "", PrinterHandlerCommands.LEFT_JUSTIFICATION);
            }
            if (notice.compoundAmountDesc5 != null && notice.compoundAmountDesc5.length() != 0)
            {
                PrintText(2.5, 0.4, 8, true, notice.compoundAmountDesc5, PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(3.2, 0, 8, true, "RM " + String.valueOf(notice.compoundAmount5), PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(0, -0.4, 8, true, "", PrinterHandlerCommands.LEFT_JUSTIFICATION);
            }
        }
        else
        {
            PrintText(2.9, 0.1, 8, true, "RM " + String.valueOf(notice.compoundAmount1), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        }

        PrintText(0.2, 0.3, 8, false, "...............................................................", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.3, 0.1, 8, false, "(ROHAYAH BINTI KARIM)", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.2, 0.1, 8, false, "JABATAN HAL EHWAL UNDANG-UNDANG", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.25, 0.1, 8, false, "b.p. DATUK BANDAR KUALA LUMPUR", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.3, 8, false, "-------------------------------------------------------------------------------------------------------------------------------", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintImage("logo.bmp", 0.3, 0.15, 0, 0, false);
        PrintText(0.7, 0, 8, true, "DEWAN BANDARAYA KUALA LUMPUR", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.7, 0.1, 8, false, "JABATAN KESELAMATAN DAN PENGUATKUASAAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.7, 0.2, 7, false, "NO. KEND.", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.55, 0, 7, true, " : " + notice.noKenderaan, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.7, 0.1, 7, false, "PERUNTUKAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.55, 0, 7, true, " : " + notice.offenceAct, 50);
        PrintText(0.7, 0.1, 7, false, "SEKSYEN/KAEDAH", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.55, 0, 7, true, " : " + notice.offenceSection, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.7, 0.1, 7, false, "TARIKH", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.55, 0, 7, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintRect(0.1, 0.2, 4.0, 0.4);
        //PrintTextFlow(notice.advertisement, 0.15, -0.3);
        PrintText(2.7, 0.4, 7, true, "KERATAN UNTUK CATATAN PEMBAYARAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(2.2, 0.1, 7, false, "TERIMA KASIH", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(3.1, 0.1, 7, false, "No. :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(4.0, 0, 8, true, notice.NoticeSerialNo, PrinterHandlerCommands.LEFT_JUSTIFICATION);

        Print();
    }

    public static void PrintGeneral(DBKLSummonIssuanceInfo notice) {
        InitPrinter();

        //PrintImage("logo.bmp", 0.8, 0, 0, 0, false);
        PrintText(1.2, 0.15, 10, true, "DEWAN BANDARAYA KUALA LUMPUR", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0.15, 8, true, "JABATAN KESELAMATAN DAN PENGUATKUASAAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0.15, 8, false, "UNIT                                             No.", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.45, 0, 8, true, " : " + notice.OfficerZone, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(3.0, 0, 8, true, " : " + notice.NoticeSerialNo, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintBarCode(1.7,  0.35, notice.NoticeSerialNo);
        PrintText(0.85, 0.15, 9, true, "NOTIS KESALAHAN SERTA TAWARAN KOMPAUN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintRect(0.1, 0.15, 4.0, 1.00);
        PrintText(0.15, -0.9, 8, false, "NAMA / SYARIKAT", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.95, 0, 8, true, " : " + notice.name, 50);
        PrintText(0.15, 0.2, 8, false, "NO. K.P. / SYKT", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.95, 0, 8, true, " : " + notice.kptNo, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.15, 0.2, 8, false, "ALAMAT", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.95, 0, 8, true, " : " + notice.address1, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.95, 0.2, 8, true, " : " + notice.address2, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.95, 0.2, 8, true, " : " + notice.address3, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintRect(0.1, 0.2, 4.0, 0.4);
        PrintText(0.15, -0.3, 8, false, "NO. KENDERAAN                                                                   NO. CUKAI JALAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.95, 0, 8, true, " : " + notice.noKenderaan, 15);
        PrintText(2.95, 0, 8, true, " : " + notice.noCukaiJalan, 15);
        PrintText(0.15, 0.2, 8, false, "JENAMA / MODEL                                                                      JENIS BADAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        String makeModel = "";
        if(notice.jenama.length() != 0)
            makeModel += notice.jenama;
        else
            makeModel += notice.vehicleMake;

        if(notice.model.length() != 0)
            makeModel += " " + notice.model;
        else
            makeModel += " " + notice.vehicleModel;
        PrintText(0.95, 0, 8, true, " : " + makeModel, 18);
        PrintText(2.95, 0, 8, true, " : " + notice.jenisBadan, 15);
        //PrintTextFlow("KEPADA NAMA / SYARIKAT TERSEBUT DI ATAS,  TUAN / PUAN / SYARIKAT DI DAPATI TELAH MELAKUKAN KESALAHAN SEPERTI BERIKUT :", 0.1, 0.25);
        PrintText(0.1, 0.3, 8, true, "PERUNTUKAN UNDANG-UNDANG :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow(notice.offenceAct, 0.3, 0.15);
        PrintText(0.1, 0.25, 8, true, "SEKSYEN/KAEDAH : ", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0, 8, true, notice.offenceSection, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.2, 8, true, "KESALAHAN :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow(notice.offence, 0.3, 0.15);
        PrintText(0.1, 0.4, 8, true, "BUTIR-BUTIR :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //if(notice.offenceDetails.length()>0)
            //PrintTextFlow(notice.offenceDetails, 0.3, 0.15);
        //else
            //PrintTextFlow("-", 0.3, 0.15);
        PrintText(0.1, 0.3, 8, false, "TEMPAT / JALAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        if(notice.offenceLocationArea.length() != 0)
        {
            PrintText(1.0, 0, 8, true, " : " + notice.offenceLocationArea, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        }
        else
        {
            PrintText(1.0, 0, 8, true, " : " + notice.summonLocation, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        }
        PrintText(0.1, 0.15, 8, false, "BUTIRAN LOKASI", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        if(notice.offenceLocationDetails.length() > 0)
            PrintText(1.0, 0, 8, true, " : " + notice.offenceLocationDetails, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        else
            PrintText(1.0, 0, 8, true, " : " + "-", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.15, 8, false, "TARIKH                                                                                                    WAKTU", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.0, 0, 8, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(2.85, 0, 8, true, " : " + CacheManager.GetTimeString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.2, 8, false, "DIKELUARKAN OLEH :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.15, 0, 8, true, CacheManager.officerDetails, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.0, 0.1, 8, false, "PENGUATKUASA/WARDEN LALULINTAS", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(3.0, 0, 8, false, "TARIKH :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(3.5, 0, 8, true, CacheManager.GetDateString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.1, 8, true, "_______________________________________________________________________________________________________________________________", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.2, 8, true, "TAWARAN KOMPAUN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow("SAYA BERSEDIA MENGKOMPAUN KESALAHAN SEPERTI YANG DITETAPKAN DALAM MASA 14 HARI (TARIKH TAMAT : " + CacheManager.GetOtherDateString(notice.CompoundDate) + ") DARI TARIKH NOTIS DICETAK. KEGAGALAN MENJELASKAN BAYARAN KOMPAUN AKAN MENYEBABKAN TINDAKAN MAHKAMAH AKAN DIAMBIL.", 0.1, 0.2);
        //PrintImage("signature.bmp", 0.2, 0.3, 0, 0, false);

        PrintText(2.7, 0.1, 8, true, "KADAR BAYARAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        if (notice.compoundAmountDesc1 != null && notice.compoundAmountDesc1.length() != 0)
        {
            PrintText(2.5, 0.1, 8, true, notice.compoundAmountDesc1, PrinterHandlerCommands.LEFT_JUSTIFICATION);
            PrintText(3.2, 0, 8, true, "RM " + String.valueOf(notice.compoundAmount1), PrinterHandlerCommands.LEFT_JUSTIFICATION);
            if (notice.compoundAmountDesc2 != null && notice.compoundAmountDesc2.length() != 0)
            {
                PrintText(2.5, 0.1, 8, true, notice.compoundAmountDesc2, PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(3.2, 0, 8, true, "RM " + String.valueOf(notice.compoundAmount2), PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(0, -0.1, 8, true, "", PrinterHandlerCommands.LEFT_JUSTIFICATION);
            }
            if (notice.compoundAmountDesc3 != null && notice.compoundAmountDesc3.length() != 0)
            {
                PrintText(2.5, 0.2, 8, true, notice.compoundAmountDesc3, PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(3.2, 0, 8, true, "RM " + String.valueOf(notice.compoundAmount3), PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(0, -0.2, 8, true, "", PrinterHandlerCommands.LEFT_JUSTIFICATION);
            }
            if (notice.compoundAmountDesc4 != null && notice.compoundAmountDesc4.length() != 0)
            {
                PrintText(2.5, 0.3, 8, true, notice.compoundAmountDesc4, PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(3.2, 0, 8, true, "RM " + String.valueOf(notice.compoundAmount4), PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(0, -0.3, 8, true, "", PrinterHandlerCommands.LEFT_JUSTIFICATION);
            }
            if (notice.compoundAmountDesc5 != null && notice.compoundAmountDesc5.length() != 0)
            {
                PrintText(2.5, 0.4, 8, true, notice.compoundAmountDesc5, PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(3.2, 0, 8, true, "RM " + String.valueOf(notice.compoundAmount5), PrinterHandlerCommands.LEFT_JUSTIFICATION);
                PrintText(0, -0.4, 8, true, "", PrinterHandlerCommands.LEFT_JUSTIFICATION);
            }
        }
        else
        {
            PrintText(2.9, 0.1, 8, true, "RM " + String.valueOf(notice.compoundAmount1), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        }

        PrintText(0.2, 0.3, 8, false, "...............................................................", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.3, 0.1, 8, false, "(ROHAYAH BINTI KARIM)", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.2, 0.1, 8, false, "JABATAN HAL EHWAL UNDANG-UNDANG", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.25, 0.1, 8, false, "b.p. DATUK BANDAR KUALA LUMPUR", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.3, 8, false, "-------------------------------------------------------------------------------------------------------------------------------", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintImage("logo.bmp", 0.3, 0.15, 0, 0, false);
        PrintText(0.7, 0, 8, true, "DEWAN BANDARAYA KUALA LUMPUR", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.7, 0.1, 8, false, "JABATAN KESELAMATAN DAN PENGUATKUASAAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.7, 0.2, 7, false, "NAMA / SYKT.", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.4, 0, 7, true, " : " + notice.name, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.7, 0.1, 7, false, "PERUNTUKAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.4, 0, 7, true, " : " + notice.offenceAct, 50);
        PrintText(0.7, 0.1, 7, false, "SEKSYEN / UUK", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.4, 0, 7, true, " : " + notice.offenceSection, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.7, 0.1, 7, false, "TARIKH", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.4, 0, 7, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintRect(0.1, 0.1, 4.0, 0.4);
        //PrintTextFlow(notice.advertisement, 0.15, -0.3);

        PrintText(2.7, 0.4, 7, true, "KERATAN UNTUK CATATAN PEMBAYARAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(2.2, 0.1, 7, false, "TERIMA KASIH", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(3.1, 0.1, 7, false, "No. :", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(4.0, 0, 8, true, notice.NoticeSerialNo, PrinterHandlerCommands.LEFT_JUSTIFICATION);

        Print();
    }

    public static void PrintBusLane(DBKLSummonIssuanceInfo notice) {
        InitPrinter();

        //PrintImage("logo.bmp", 0.8, 0, 0, 0, false);
        PrintText(1.2, 0.15, 10, true, "DEWAN BANDARAYA KUALA LUMPUR", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0.15, 8, true, "JABATAN KESELAMATAN DAN PENGUATKUASAAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(2.55, 0.2, 8, false, "No.", 0);
        PrintText(2.75, 0, 8, true, " : " + notice.NoticeSerialNo, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintBarCode(1.7, 0.35, notice.NoticeSerialNo);
        PrintText(2.0, 0.35, 10, true, "AKTA PENGANGKUTAN JALAN 1987", 0);
        PrintText(2.0, 0.2, 9, false, "KAEDAH-KAEDAH LALULINTAS JALAN", 0);
        PrintText(2.0, 0.2, 9, false, "(BANDARAYA KUALA LUMPUR)", 0);
        PrintText(2.0, 0.2, 9, false, "(SAMAN) 1977", 0);
        PrintText(0.1, 0.3, 8, false, "KEPADA", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0, 8, true, " : " + notice.name, 50);
        PrintText(0.1, 0.2, 8, false, "NO. KAD PENGENALAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0, 8, true, " : " + notice.kptNo, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.2, 8, false, "ALAMAT", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0, 8, true, " : " + notice.address1, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0.2, 8, true, " : " + notice.address2, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0.2, 8, true, " : " + notice.address3, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.2, 8, false, "NO. LESEN MEMANDU                                                                TARIKH TAMAT", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0, 8, true, " : " + notice.LicenseNo, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(2.95, 0, 8, true, " : " + CacheManager.GetOtherDateString(notice.licenseExpiryDate), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.2, 8, false, "NO. KENDERAAN                                                                    JENAMA / MODEL", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0, 8, true, " : " + notice.noKenderaan, 20);
        String makeModel = "";
        if(notice.jenama.length() != 0)
            makeModel += notice.jenama;
        else
            makeModel += notice.vehicleMake;

        if(notice.model.length() != 0)
            makeModel += " " + notice.model;
        else
            makeModel += " " + notice.vehicleModel;
        PrintText(2.95, 0, 8, true, " : " + makeModel, 20);
        PrintText(0.1, 0.2, 8, false, "NO. CUKAI JALAN                                                                     TARIKH TAMAT", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0, 8, true, " : " + notice.noCukaiJalan, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(2.95, 0, 8, true, " : " + CacheManager.GetOtherDateString(notice.roadtaxExpiryDate), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow("BAHAWASANYA SAYA MEMPUNYAI SEBAB YANG MUNASABAH UNTUK MEMPERCAYAI BAHAWA ANDA TELAH MELAKUKAN KESALAHAN SEPERTI BERIKUT :", 0.1, 0.3);
        //PrintTextFlow(notice.offence + " - " + notice.offenceDetails, 0.1, 0.25);
        PrintText(0.1, 0.45, 8, false, "DI BAWAH SEKSYEN/KAEDAH/PERENGGAN", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(2.1, 0, 8, true, " : " + notice.offenceSection, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow(notice.offenceAct, 0.1, 0.2);
        PrintText(0.1, 0.35, 8, false, "DI (TEMPAT)", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        if(notice.offenceLocationArea.length() != 0)
        {
            PrintText(1.0, 0, 8, true, " : " + notice.offenceLocationArea, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        }
        else
        {
            PrintText(1.0, 0, 8, true, " : " + notice.summonLocation, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        }
        PrintText(0.1, 0.2, 8, false, "PADA (TARIKH)                                                                                  JAM", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.0, 0, 8, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(2.8, 0, 8, true, " : " + CacheManager.GetTimeString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow("ANDA ADALAH DIPERINTAH HADIR DENGAN SENDIRI ATAU MELALUI PEGUAM DI HADAPAN MAHKAMAH MAJISTRET (DEWAN BANDARAYA) KUALA LUMPUR, JALAN TUN RAZAK, 50400 KUALA LUMPUR.", 0.1, 0.3);
        PrintText(0.1, 0.5, 8, false, "PADA (TARIKH)                                                                                  JAM", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.0, 0, 8, true, " : " + CacheManager.GetDateString(notice.courtDate), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(2.8, 0, 8, true, " : 9:00 PAGI", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        //PrintTextFlow("KESALAHAN INI BOLEH DIKOMPAUNKAN. ANDA BOLEH MENGHADIRKAN DIRI KEPADA PEGAWAI KOMPAUN DI BANGUNAN DEWAN BANDARAYA, JALAN RAJA LAUT, KUALA LUMPUR ATAU PEJABAT JABATAN KESELAMATAN DAN PENGUATKUASAAN, JALAN TUN RAZAK, 50400 KUALA LUMPUR UNTUK MENGKOMPAUNKAN KESALAHAN INI SEBELUM ", 0.1, 0.3);
        PrintText(2.8, 0.45, 8, true, ": " + CacheManager.GetDateString(notice.CompoundDate), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.55, 8, false, "TARIKH DIKELUARKAN                                                                          JAM", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(1.2, 0, 8, true, " : " + CacheManager.GetDateString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(2.8, 0, 8, true, " : " + CacheManager.GetTimeString(notice.OffenceDateTime), PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.7, 8, false, "...............................................                                  ..............................................................................", PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.1, 0.1, 8, false, "TANDATANGAN PENERIMA                                     NAMA & NOMBOR WARDEN LALULINTAS", PrinterHandlerCommands.LEFT_JUSTIFICATION);

        PrintText(4.0, 0.1, 8, true, CacheManager.officerDetails, PrinterHandlerCommands.LEFT_JUSTIFICATION);
        PrintText(0.9, 0.6, 8, true, "No. : " + notice.NoticeSerialNo, PrinterHandlerCommands.LEFT_JUSTIFICATION);

        Print();
    }
}