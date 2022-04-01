
package com.sd4.controller;

import com.google.zxing.WriterException;
import com.sd4.model.Beer;
import com.sd4.model.Brewery;
import com.sd4.model.Category;
import com.sd4.model.Style;
import com.sd4.service.BeerService;
import com.sd4.service.BreweryService;
import com.sd4.service.CategoryService;
import com.sd4.service.StyleService;
import com.sd4.util.GeneratePdfReport;
import com.sd4.util.QRGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/Beers")
public class BeerController {
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BeerController.class);
    
    @Autowired
    private BeerService BeerService;
    
    BeerController(BeerService repository) {
		this.BeerService = repository;
	}
    
    @Autowired
    private BreweryService breweryService;
    
    @Autowired
    private CategoryService CATService;
    @Autowired
    private StyleService StyleService;
    
    
    
    
 @DeleteMapping(value = "/beer/{id}")
    public void deleteBeer(@PathVariable long id) {
         BeerService.deleteByID(id);
    }
  @PostMapping(value = "/create")
   public void NewBeer(@RequestBody Beer newBeer){
         BeerService.saveBeer(newBeer);
}
    
    @GetMapping("/all")
    public ResponseEntity<CollectionModel<EntityModel<Beer>>> findAll()
    {
            List<EntityModel<Beer>> beers = StreamSupport.stream(BeerService.findAll().spliterator(), false)
				.map(beer -> EntityModel.of(beer, //
						linkTo(methodOn(BeerController.class).findOne(beer.getId())).withSelfRel(), //
						linkTo(methodOn(BeerController.class).findAll()).withRel("all"))) //
				.collect(Collectors.toList());

		return ResponseEntity.ok( //
				CollectionModel.of(beers, //
						linkTo(methodOn(BeerController.class).findAll()).withSelfRel()));
    }
    
    
    @GetMapping("{id}")
    ResponseEntity<EntityModel<Beer>> findOne(@PathVariable long id) {

		return BeerService.findOne(id) //
				.map(beer -> EntityModel.of(beer, //
						linkTo(methodOn(BeerController.class).findOne(beer.getId())).withSelfRel(), //
						linkTo(methodOn(BeerController.class).findAll()).withRel("all"))) //
				.map(ResponseEntity::ok) //
				.orElse(ResponseEntity.notFound().build());
	}
    
    
    @GetMapping(path = "/zip/{beerId}")
	public ResponseEntity<StreamingResponseBody> downloadZip(HttpServletResponse response,
			@PathVariable(name = "beerId") String sampleId) {


		// list of file paths for download
		List<String> paths = Arrays.asList("src/main/resources/static/assets/images/large/1.jpg",
				"src/main/resources/static/assets/images/large/2.jpg",
				"src/main/resources/static/assets/images/large/3.jpg",
				"src/main/resources/static/assets/images/large/4.jpg",
                                "src/main/resources/static/assets/images/large/noimage.jpg");

		int BUFFER_SIZE = 1024;

		StreamingResponseBody streamResponseBody = out -> {

			final ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
			ZipEntry zipEntry = null;
			InputStream inputStream = null;

			try {
				for (String path : paths) {
					File file = new File(path);
					zipEntry = new ZipEntry(file.getName());

					inputStream = new FileInputStream(file);

					zipOutputStream.putNextEntry(zipEntry);
					byte[] bytes = new byte[BUFFER_SIZE];
					int length;
					while ((length = inputStream.read(bytes)) >= 0) {
						zipOutputStream.write(bytes, 0, length);
					}

				}
				// set zip size in response
				response.setContentLength((int) (zipEntry != null ? zipEntry.getSize() : 0));
			} catch (IOException e) {
				logger.error("Exception while reading and streaming data {} ", e);
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
				if (zipOutputStream != null) {
					zipOutputStream.close();
				}
			}

		};

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=Images.zip");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "0");

		return ResponseEntity.ok(streamResponseBody);
	}
        
        
       @GetMapping(value = "/image/{beerId}/{size}" ,produces = {MediaType.IMAGE_JPEG_VALUE , "application/json" })
        public ResponseEntity<BufferedImage> getImage(@PathVariable("beerId") long beerId, @PathVariable("size") String size) throws Exception{
    
            Optional<Beer> optional = BeerService.findOne(beerId);
            if (optional.isEmpty())
            {
                 return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
    
            String Path = "static/assets/images/" + ("thumbnail".equalsIgnoreCase(size) ? "thumbs" : "large") + "/" + optional.get().getImage();
            System.out.println(Path);
            final InputStream stream = new ClassPathResource(Path).getInputStream();
             BufferedImage image = ImageIO.read(stream);
            return ResponseEntity.ok(image);
    
}





 private static final String QR_CODE_IMAGE_PATH = "./src/main/resources/static/images/";

        @GetMapping(value = "/qr/{breweryid}", produces = MediaType.IMAGE_JPEG_VALUE)
        public Object QRBrewery(@PathVariable(value = "breweryid") long id) throws WriterException, IOException
        {
            Brewery brewery = breweryService.findOne(id).get();
            String vCard = "BEGIN:VCARD\n" + "VERSION:3.0" + "\nFN" + brewery.getName() + "\nTEL:" + brewery.getPhone() + "\nADR:" + brewery.getAddress1() + "\nEMAIL:" + brewery.getEmail() + "\nURL:" 
                    + brewery.getWebsite() + "\nEND:VCARD";
            
           byte[] image = new byte[0];  
            image = QRGenerator.getQRCodeImage(vCard,250,250);
            
            return QRGenerator.generateQRCodeImage(vCard,250,250,QR_CODE_IMAGE_PATH);
        }
        
        
        
        
        
        
        
       
        
        @GetMapping(value = "/pdf/{beerid}", produces = MediaType.APPLICATION_PDF_VALUE)
        public Object PDFBeer(@PathVariable(value = "beerid") long id)
        {
         List<Beer> beer = BeerService.findByID(id);
//         List<Beer> beer = BeerService.findAll();
         List<Brewery> brewery = breweryService.findByID(id);
         List<Category> category = CATService.findById(id);
         List<Style> style = StyleService.findById(id);
         
         ByteArrayInputStream bis = GeneratePdfReport.PdfReport(beer , brewery, category, style);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=Beerreport.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
            
        }

}
