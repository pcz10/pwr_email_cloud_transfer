package email_cloud_app;

import java.io.File;
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
	}

	private void saveMessage(Message msg) throws MessagingException, IOException 
	{
		String contentType = msg.getContentType();
		if(contentType.contains("multipart"));
		{
			Multipart multiPart = (Multipart) msg.getContent();
			for(int i = 0; i < multiPart.getCount(); i++)
			{
				MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
				if(Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
				{
					InputStream is = part.getInputStream();
					File dir = new File("Attachment_" + part.getFileName() + "/");
					dir.mkdir();
					File f = new File(dir.toString()+ "/" + part.getFileName());
				    FileOutputStream fos = new FileOutputStream(f);
				    byte[] buf = new byte[4096];
				    int bytesRead;
				    while((bytesRead = is.read(buf))!=-1)
				    {
				        fos.write(buf, 0, bytesRead);
				    }
				    fos.close();
				}
			}
		}
		Address[] in = msg.getFrom();
		for (Address address : in) 
		{
			System.out.println("FROM:" + address.toString());
		}
		Multipart mp = (Multipart) msg.getContent();
		BodyPart bp = mp.getBodyPart(0);
		System.out.println("SENT DATE:" + msg.getSentDate());
		System.out.println("SUBJECT:" + msg.getSubject());
		System.out.println("CONTENT:" + bp.getContent());
	}
	private String hostName = "smtp.gmail.com";
	private String userEmailAddress = "pwrjavatest@gmail.com";
	private String password = "qwezxcasd";
}