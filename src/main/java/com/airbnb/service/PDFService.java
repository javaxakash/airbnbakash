package com.airbnb.service;
import com.airbnb.payload.BookingDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import java.io.FileOutputStream;

@Service
public class PDFService {



    public Boolean generatePdf(String fileName, BookingDto book) throws Exception {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Chunk bookingConfirmation = new Chunk("Booking Confirmation : ", font);
            Chunk guestName = new Chunk("Guest Name : "+book.getGuestname(), font);
            Chunk price = new Chunk("price : "+book.getPrice(), font);
            Chunk totalPrice = new Chunk("Total Price : "+ book.getTotalPrice(), font);
            document.add(bookingConfirmation );
            document.add(new Paragraph("\n"));
            document.add(guestName );
            document.add(new Paragraph("\n"));
            document.add(price );
            document.add(new Paragraph("\n"));
            document.add(totalPrice);
            document.close();
             return true;
        }
        catch( Exception e){
            e.printStackTrace();
        }
        return false;
    }

}