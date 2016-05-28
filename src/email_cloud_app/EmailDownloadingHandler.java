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
		catch (NoSuchProviderException e) 
		{
			System.err.println("invalid provider name");
		} 
		catch (MessagingException e) 
		{
			System.err.println("messaging exception");
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (UploadErrorException e) 
		{
			e.printStackTrace();
		}
		catch (DbxException e) 
		{
			e.printStackTrace();
		}
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
		String attachFiles = "";
		
		//log(getTextFromMessage(msg));
		
		Object content = msg.getContent();

		if(content instanceof Multipart)
		{
			MimeMultipart mp = (MimeMultipart)msg.getContent();
			int count = mp.getCount();
			for(int i = 0; i < count; ++i)
			{
				MimeBodyPart mbp = (MimeBodyPart) mp.getBodyPart(i);
				log(mbp.getContentType()+" ssss ");
				if(mbp.getContentType().contains("ALTERNATIVE"))
				{
					DataHandler dh = mbp.getDataHandler();
					dh.writeTo(System.out);
					messageContent = mbp.getContent().toString();
				}
					//(mbp.isMimeType("te") ){
					
					//mbp.writeTo(System.out);
				}// else 
                 //{
				//	 if(mbp.isMimeType("text/html"))
				//		 mbp.writeTo(System.out);
                 //}
			}



         // print out details of each message
         System.out.println("\t From: " + fromWho);
         System.out.println("\t Subject: " + subject);
         System.out.println("\t Sent Date: " + sentDate);
         System.out.println("\t Message: " + messageContent);
         System.out.println("\t Attachments: " + attachFiles);
 
}
	private String getTextFromMessage(Message message) throws Exception {
		if (message.isMimeType("text/plain")) {
			return message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			String result = "";
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			int count = mimeMultipart.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bodyPart = mimeMultipart.getBodyPart(i);
				if (bodyPart.isMimeType("text/plain")) {
					result = result + "\n" + bodyPart.getContent();
					break; // without break same text appears twice in my tests
				} //else if (bodyPart.isMimeType("text/html")) {
					//String html = (String) bodyPart.getContent();
					//result = result + "\n" + Jsoup.parse(html).text();

				//}
			}
			return result;
		}
		return "";
	}
	//	if (disposition == null) 
		//{ 
       //     if ((contentType.length() >= 10) && (contentType.toLowerCase().substring(0, 10).equals("text/plain")))
      //          saveFile(part.getFileName() + "1.txt", part);
      //      else 
      //      	saveFile(part.getFileName() + "1.txt", part);
     //   } 
		
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
					File file = saveAttachmentFile(part);
				  
					this.cloudUploader.upload(file,"/"+part.getFileName());
				}
			}
		}

	private File saveAttachmentFile(MimeBodyPart part) throws IOException, MessagingException, FileNotFoundException {
		InputStream is = part.getInputStream();
		File dir = new File("C:\\projektyJava\\pwr_lab06_Cloud\\bin");
		dir.mkdir();
		
		File file = new File(dir.toString()+ "/" + part.getFileName());
		
   
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buf = new byte[4096];
		int bytesRead;
		while((bytesRead = is.read(buf))!=-1)
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
