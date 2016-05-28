package email_cloud_app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.UploadErrorException;
import com.sun.media.jfxmedia.logging.Logger;

public class EmailDownloadingHandler 
{
	public static void main(String[] args) throws Exception
	{
		new EmailDownloadingHandler();
	}

	
	public EmailDownloadingHandler() throws Exception 
	{
		try 
		{	
			Session session = Session.getInstance(new Properties(), null);
			Store store = session.getStore("imaps");
			store.connect(this.hostName, this.userEmailAddress, this.password);

			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
			int messageCount = inbox.getMessageCount();
			System.out.println("Total messages: " + messageCount + "\n");
			Message [] messages = inbox.getMessages();
			for(int i = 0; i < messageCount; ++i)
				saveMessage(messages[i]);
			
			inbox.close(false);
			store.close();
		} 
		finally{}
	
}
	private void saveMessage(Message msg) throws Exception 
	{


		handleBodyPart(msg);
	}
	
	private void handleBodyPart(Message msg) throws Exception
	{
		Address[] address = msg.getFrom();
		String fromWho = address[0].toString();
		String subject = msg.getSubject();
		String sentDate = msg.getSentDate().toString();
		String messageContent = "";
		
		Object content = msg.getContent();
		if(content instanceof Multipart)
		{
			MimeMultipart mp = (MimeMultipart)msg.getContent();
			int count = mp.getCount();
			for(int i = 0; i < count; ++i)
			{
				BodyPart mbp =  mp.getBodyPart(i);
				
				if(mbp.getContentType().contains("ALTERNATIVE"))
				{
					MimeMultipart m = (MimeMultipart) mbp.getContent();
					for(int h = 0; h < m.getCount(); ++h)
					{
						MimeBodyPart bp = (MimeBodyPart) m.getBodyPart(h);
						if(bp.getContentType().contains("TEXT/PLAIN"))
						{
							messageContent = bp.getContent().toString();
						}
					}					
				}

	         System.out.println("\t From: " + fromWho);
	         System.out.println("\t Subject: " + subject);
	         System.out.println("\t Sent Date: " + sentDate);
	         System.out.println("\t Message: " + messageContent);
			}
		}
 
}
	
		
	public static void log(String message)
	{

		System.out.println(message);
	}
	
	private static File saveFile(String fileName, Part part) throws IOException, MessagingException
	{
		log("jestem w saveFile");
		ByteArrayOutputStream stream = new ByteArrayOutputStream(4096);
		part.writeTo(stream);
		byte[] buf = stream.toByteArray();
		
		File dir = new File("C:\\projektyJava\\pwr_lab06_Cloud\\bin");
		dir.mkdir();
		File file = new File(dir.toString()+ "/" + part.getFileName() + ".txt");
		
		FileOutputStream fos = new FileOutputStream(file);
	    fos.write(buf);
		fos.close();
		return file;
	}
	private void saveAttachment(Message msg) throws MessagingException, IOException, FileNotFoundException, UploadErrorException, DbxException 
	{
			Multipart multiPart = (Multipart) msg.getContent();
			for(int i = 0; i < multiPart.getCount(); i++)
			{
				MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
				if(Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
				{
					//File file = saveAttachmentFile(part);
				  
				//	this.cloudUploader.upload(file,"/"+part.getFileName());
				}
			}
		}

	private File save(InputStream inputStream,String folderName) throws IOException, MessagingException, FileNotFoundException {

		File dir = new File("C:\\projektyJava\\pwr_lab06_Cloud\\bin");
		dir.mkdir();
		
		File file = new File(dir.toString()+ "/" + folderName);
		
   
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buf = new byte[4096];
		int bytesRead;
		while((bytesRead = inputStream.read(buf))!=-1)
		{
		    fos.write(buf, 0, bytesRead);
		}
		fos.close();
		return file;
	}

	private String saveDirectory = "C:\\projektyJava\\pwr_lab06_Cloud\\bin";
	private CloudConnectionHandler cloudUploader = new CloudConnectionHandler();
	private String hostName = "imap.gmail.com";
	private String userEmailAddress = "pwrjavatest@gmail.com";
	private String password = "qwezxcasd";
}
