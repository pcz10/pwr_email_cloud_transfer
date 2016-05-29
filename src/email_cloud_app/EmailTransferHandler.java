package email_cloud_app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.UploadErrorException;

public class EmailTransferHandler 
{
	public static void main(String[] args) throws Exception
	{
		new EmailTransferHandler();
	}
	
	public EmailTransferHandler() throws Exception 
	{
		try 
		{	
			Session session = Session.getInstance(new Properties(), null);
			Store store = session.getStore("imaps");
			store.connect(this.hostName, this.userEmailAddress, this.password);
			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
			for(Message message : inbox.getMessages())
				processMessage(message);
			inbox.close(false);
			store.close();
		} 
		finally{}
	}
	
	private void processMessage(Message msg) throws Exception 
	{
		handleBodyPart(msg);
	}
	
	private void handleBodyPart(Message msg) throws Exception
	{
		Address[] address = msg.getFrom();
		String fromWho = "From: " + address[0].toString() + System.getProperty("line.separator");
		String subject = "Subject: " + msg.getSubject() + System.getProperty("line.separator");
		String sentDate = "Sent date: " + msg.getSentDate().toString() + System.getProperty("line.separator") + "Message: ";
		String messageContent = "";
		Object content = msg.getContent();
		File contentDirectory = new File(saveDirectory+folderNumerator);
		cloudUploader.client.files().createFolder("/email"+folderNumerator);
		
		if(content instanceof Multipart)
		{
			MimeMultipart mp = (MimeMultipart)msg.getContent();
			int count = mp.getCount();
			for(int i = 0; i < count; ++i)
			{
				BodyPart mbp =  mp.getBodyPart(i);
				if(Part.ATTACHMENT.equalsIgnoreCase(mbp.getDisposition()))
					saveAttachment(mbp, contentDirectory); 
				
				if(mbp.getContentType().contains("ALTERNATIVE"))
				{
					MimeMultipart m = (MimeMultipart) mbp.getContent();
					for(int h = 0; h < m.getCount(); ++h)
					{
						MimeBodyPart bp = (MimeBodyPart) m.getBodyPart(h);
						if(bp.getContentType().contains("TEXT/PLAIN"))
							messageContent = bp.getContent().toString();
					}					
				}
				else if(mbp.getContentType().contains("TEXT/PLAIN"))
					messageContent = mbp.getContent().toString();
			}
		}
		String fullMessageContent = fromWho + subject + sentDate + messageContent;
		saveMessage(fullMessageContent, contentDirectory);
		++folderNumerator;
	}
	
	private void saveMessage(String messageContent,File contentDirectory) throws IOException, MessagingException, UploadErrorException, DbxException
	{
		contentDirectory.mkdir();
		File file = new File(contentDirectory.toString() + "/message" + folderNumerator + ".txt");
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(messageContent);
			writer.flush();
			writer.close();
			converter.convertTextToPDF(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		this.cloudUploader.upload(file, "/email" + folderNumerator + "/message.pdf");
	}

	private void saveAttachment(Part part, File contentDirectory) throws IOException, MessagingException, FileNotFoundException, UploadErrorException, DbxException 
	{
		InputStream is = part.getInputStream();
		contentDirectory.mkdir();
		File file = new File(contentDirectory.toString()+ "/" + part.getFileName());
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buf = new byte[4096];
		int bytesRead;
		while((bytesRead = is.read(buf))!=-1)
		{
		    fos.write(buf, 0, bytesRead);
		}
		fos.close();
		this.cloudUploader.upload(file, "/email" + folderNumerator + "/" + part.getFileName());
	}
	private Converter converter = new Converter();
	private static int folderNumerator = 1;
	private String saveDirectory = "C:\\projektyJava\\pwr_lab06_Cloud\\bin\\email";
	private CloudConnectionHandler cloudUploader = new CloudConnectionHandler();
	private String hostName = "imap.gmail.com";
	private String userEmailAddress = "pwrjavatest@gmail.com";
	private String password = "qwezxcasd";
}
