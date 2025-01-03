package com.airbnb.controller;

import com.airbnb.entity.Booking;
import com.airbnb.entity.Property;
import com.airbnb.entity.PropertyUser;
import com.airbnb.payload.BookingDto;
import com.airbnb.repository.BookingRepository;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.service.BucketService;
import com.airbnb.service.EmailService;
import com.airbnb.service.PDFService;
import com.airbnb.service.SmsService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RestController
@RequestMapping("api/v1/booking")
public class BookingController {
    private BucketService bucketService;
    private PDFService pdfService;
    private SmsService smsService;
    private EmailService emailService;
    private BookingRepository bookingRepository;
    private PropertyRepository propertyRepository;

    public BookingController(BucketService bucketService, PDFService pdfService, SmsService smsService, EmailService emailService, BookingRepository bookingRepository, PropertyRepository propertyRepository) {
        this.bucketService = bucketService;
        this.pdfService = pdfService;
        this.smsService = smsService;
        this.emailService = emailService;
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
    }
     @PostMapping("/createBooking/{propertyId}")
     public ResponseEntity<?> createBooking(@RequestBody Booking booking,
                                                  @AuthenticationPrincipal PropertyUser user,
                                                  @PathVariable long propertyId) throws Exception {
     booking.setPropertyUser(user);
     Property property = propertyRepository.findById(propertyId).get();
     int propertyPrice= property.getNightlyPrice();
     int totalNights=booking.getTotalNight();
     int totalPrice=propertyPrice*totalNights;
     booking.setProperty(property);
     booking.setTotalPrice(totalPrice);
     Booking createdBooking = bookingRepository.save(booking);
         BookingDto book= new BookingDto();
         book.setBookingId(createdBooking.getId());
         book.setGuestname(createdBooking.getGuestName());
         book.setPrice(propertyPrice);
         book.setTotalPrice(createdBooking.getTotalPrice());

     // create pdf with booking confirmation

          boolean b=   pdfService.generatePdf("G://booking"+"booking-confirmation-id"+createdBooking.getId()+".pdf", book);
         if (b) {
             // upload into bucket
             MultipartFile file= BookingController.convert("G://booking"+"booking-confirmation-id"+createdBooking.getId()+".pdf");
             String bookingUrl= bucketService.uploadFile(file,"akashairbnb");
             smsService.sendSms("7355939758","Your booking is confirmed.Click for more Information : "+bookingUrl);
             emailService.sendEmail(user.getEmail(),"Booking Confirmation Mail","Your booking is confirmed.Click for more Information : ",bookingUrl);
         }
         else{
             return new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
         }
         return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);

     }
    public static MultipartFile convert(String filePath) throws IOException{
        // load the file from the specified path
        File file=new File(filePath);
        // Read the file content into byte array
        byte[] fileContent= Files.readAllBytes(file.toPath());
        // convert byte[] array to resource
        Resource resource= new ByteArrayResource(fileContent);
        //  create multipart from resource
        MultipartFile multipartFile= new MultipartFile() {
            @Override
            public String getName() {
                return file.getName();
            }

            @Override
            public String getOriginalFilename() {
                return file.getName();
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return fileContent.length==0;
            }

            @Override
            public long getSize() {
                return fileContent.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return fileContent;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return resource.getInputStream();
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                Files.write(dest.toPath(),fileContent);
            }
        };
         return multipartFile;
    }

}
