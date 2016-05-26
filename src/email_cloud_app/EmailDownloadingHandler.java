package email_cloud_app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

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
						
			Message[] messagesArray = inbox.getMessages();
			for (int i = 0; i < messageCount; ++i) 
			{
				System.out.println("Mail Subjects: " + messagesArray[i].getSubject() + "\n");
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

	}



	private Store store;
	private String hostName = "smtp.gmail.com";
	private String userEmailAddress = "pwrjavatest@gmail.com";
	private String password = "qwezxcasd";

}
	
	
/*	
	public static void doit() throws MessagingException, IOException {
		Folder folder = null;
		Store store = null;
		try {
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");

			Session session = Session.getDefaultInstance(props, null);
			// session.setDebug(true);
			store = session.getStore("imaps");
			store.connect("imap.gmail.com", "myemail@gmail.com", "******");
			folder = store.getFolder("Inbox");
		
	
			folder.open(Folder.READ_WRITE);
			Message messages[] = folder.getMessages();
			System.out.println("No of Messages : " + folder.getMessageCount());
			System.out.println("No of Unread Messages : " + folder.getUnreadMessageCount());
			for (int i = 0; i < messages.length; ++i) {
				System.out.println("MESSAGE #" + (i + 1) + ":");
				Message msg = messages[i];
				
				String from = "unknown";
				if (msg.getReplyTo().length >= 1) {
					from = msg.getReplyTo()[0].toString();
				} else if (msg.getFrom().length >= 1) {
					from = msg.getFrom()[0].toString();
				}
				String subject = msg.getSubject();
				System.out.println("Saving ... " + subject + " " + from);
				// you may want to replace the spaces with "_"
				// the TEMP directory is used to store the files
				String filename = "c:/temp/" + subject;
				saveParts(msg.getContent(), filename);
				msg.setFlag(Flags.Flag.SEEN, true);
				// to delete the message
				// msg.setFlag(Flags.Flag.DELETED, true);
			}
		} finally {
			if (folder != null) {
				folder.close(true);
			}
			if (store != null) {
				store.close();
			}
		}
	}

	public static void saveParts(Object content, String filename) throws IOException, MessagingException {
		OutputStream out = null;
		InputStream in = null;
		try {
			if (content instanceof Multipart) {
				Multipart multi = ((Multipart) content);
				int parts = multi.getCount();
				for (int j = 0; j < parts; ++j) {
					MimeBodyPart part = (MimeBodyPart) multi.getBodyPart(j);
					if (part.getContent() instanceof Multipart) {
						// part-within-a-part, do some recursion...
						saveParts(part.getContent(), filename);
					} else {
						String extension = "";
						if (part.isMimeType("text/html")) {
							extension = "html";
						} else {
							if (part.isMimeType("text/plain")) {
								extension = "txt";
							} else {
								// Try to get the name of the attachment
								extension = part.getDataHandler().getName();
							}
							filename = filename + "." + extension;
							System.out.println("... " + filename);
							out = new FileOutputStream(new File(filename));
							in = part.getInputStream();
							int k;
							while ((k = in.read()) != -1) {
								out.write(k);
							}
						}
					}
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	public static void main(String args[]) throws Exception {
		ReceiveMailImap.doit();
	}
}
*/