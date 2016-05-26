package email_cloud_app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.FullAccount;

public class CloudConnectionHandler 
{
	public static void main(String[] args) throws DbxException, FileNotFoundException, IOException
	{
	   	DbxRequestConfig config = new DbxRequestConfig("Emails to cloud transfer", Locale.getDefault().toString());
	   	DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
	   	FullAccount account = client.users().getCurrentAccount();
	   	log(account.getName().getDisplayName());
	   	
	   	try
	   	{
	   		FileMetadata metadata = client.files().uploadBuilder("/test2.txt").start().finish();
	   	}
	   	finally{}
	}
	public static void log(String message)
	{
		System.out.println(message);
	}
	private static final String ACCESS_TOKEN = "vX9cF0nPkBAAAAAAAAAAJO3A-9oKrfkw7AZ0szsfWYmKezzvfU4hLDvvxFdC4hOC";
}
