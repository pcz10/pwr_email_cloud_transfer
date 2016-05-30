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
import javax.mail.NoSuchProviderException;
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
		Store store = establishConnectionWithEmail();
		Folder inbox = getInbox(store);
		try 
		{	
			for(Message message : inbox.getMessages())
				processMessage(message);
		} 
		finally
		{
			inbox.close(false);
			store.close();
		}
	}

	private Folder getInbox(Store store) throws MessagingException {
		Folder inbox = store.getFolder("inbox");
		inbox.open(Folder.READ_ONLY);
		return inbox;
	}

	private Store establishConnectionWithEmail() throws NoSuchProviderException, MessagingException {
		Session session = Session.getInstance(new Properties(), null);
		Store store = session.getStore("imaps");
		store.connect(this.hostName, this.userEmailAddress, this.password);
		return store;
	}
	
	private void processMessage(Message msg) throws Exception
	{
		Object content = msg.getContent();
		File contentDirectory = new File(saveDirectory + folderNumerator);
		cloudUploader.client.files().createFolder("/email" + folderNumerator);
		String messageContent = "";
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
					messageContent = processTextInMessageWithAttachment(messageContent, mbp);	
				else if(mbp.getContentType().contains("TEXT/PLAIN"))
					messageContent = processTextInMessageWithoutAttachment(mbp);
			}
		}
		String fullMessageContent = generateMessageContent(msg, messageContent);
		saveMessage(fullMessageContent, contentDirectory);
		++folderNumerator;
	}

	private String processTextInMessageWithoutAttachment(BodyPart mbp) throws IOException, MessagingException {
		String messageContent;
		messageContent = mbp.getContent().toString();
		return messageContent;
	}

	private String generateMessageContent(Message msg, String messageContent) throws MessagingException
	{
		Address[] address = msg.getFrom();
		String fromWho = "From: " + address[0].toString() + System.getProperty("line.separator");
		String subject = "Subject: " + msg.getSubject() + System.getProperty("line.separator");
		String sentDate = "Sent date: " + msg.getSentDate().toString() + System.getProperty("line.separator") + "Message: ";
		messageContent = "";
		String fullMessageContent = fromWho + subject + sentDate + messageContent;
		return fullMessageContent;
	}
	
	private String processTextInMessageWithAttachment(String messageContent, BodyPart mbp) throws IOException, MessagingException 
	{
		MimeMultipart m = (MimeMultipart) mbp.getContent();
		for(int h = 0; h < m.getCount(); ++h)
		{
			MimeBodyPart bp = (MimeBodyPart) m.getBodyPart(h);
			if(bp.getContentType().contains("TEXT/PLAIN"))
				messageContent = bp.getContent().toString();
		}
		return messageContent;
	}
	
	private void saveMessage(String messageContent,File contentDirectory) throws Exception
	{
		contentDirectory.mkdir();
		File file = new File(contentDirectory.toString() + "/message" + folderNumerator + ".txt");
		try(
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			)
		{
			writer.write(messageContent);
			writer.flush();
			writer.close();
			converter.convertTextToPDF(file);
		} 
		int numberOfFilesToUpload = 2;
		uploadMessage(file,numberOfFilesToUpload);
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
		    fos.write(buf, 0, bytesRead);
		fos.close();
		
		uploadAttachment(file,part);
	}

	private void uploadMessage(File file,int numberOfFilesToUpload) throws UploadErrorException, DbxException, FileNotFoundException, IOException
	{
		this.cloudUploader.upload(file, "/email" + folderNumerator + "/message.pdf");
		this.cloudUploader.upload(file, "/email" + folderNumerator + "/message" + folderNumerator + ".txt");
	}
	
	private void uploadAttachment(File file, Part part) throws UploadErrorException, DbxException, FileNotFoundException, IOException, MessagingException
	{
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
