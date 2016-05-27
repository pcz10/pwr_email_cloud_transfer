package email_cloud_app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.UploadErrorException;

public class EmailDownloadingHandler 
{
	public static void main(String[] args)
	{
		new EmailDownloadingHandler();
	}
	
	public EmailDownloadingHandler() 
	{
		Properties properties = new Properties();
		properties.setProperty("mail.store.protocol", "imaps");
		try 
		{	
			Session session = Session.getDefaultInstance(properties, null);
			Store store = session.getStore();
			store.connect(this.hostName, this.userEmailAddress, this.password);

			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
			int messageCount = inbox.getMessageCount();
			
			System.out.println("Total messages: " + messageCount + "\n");
			
			for(Message message : inbox.getMessages())
			{	
				saveMessage(message);
			}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (DbxException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveMessage(Message msg) throws MessagingException, IOException, UploadErrorException, DbxException 
	{
		String contentType = msg.getContentType();
		if(contentType.contains("multipart"));
			saveAttachment(msg);
		Address[] in = msg.getFrom();
		for (Address address : in) 
			System.out.println("FROM:" + address.toString());
		Multipart mp = (Multipart) msg.getContent();
		BodyPart bp = mp.getBodyPart(0);
		System.out.println("SENT DATE:" + msg.getSentDate());
		System.out.println("SUBJECT:" + msg.getSubject());
		System.out.println("CONTENT:" + bp.getContent());
	}
	private void saveAttachment(Message msg) throws MessagingException, IOException, FileNotFoundException, UploadErrorException, DbxException 
	{
			Multipart multiPart = (Multipart) msg.getContent();
			for(int i = 0; i < multiPart.getCount(); i++)
			{
				MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
				if(Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
				{
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
				  
					this.cloudUploader.upload(file,"/"+part.getFileName());
				}
			}
		}
	
	private CloudConnectionHandler cloudUploader = new CloudConnectionHandler();
	private String hostName = "smtp.gmail.com";
	private String userEmailAddress = "pwrjavatest@gmail.com";
	private String password = "qwezxcasd";
}