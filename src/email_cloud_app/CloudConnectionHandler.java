package email_cloud_app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.UploadErrorException;

public class CloudConnectionHandler 
{
	public CloudConnectionHandler()
	{
		DbxRequestConfig config = new DbxRequestConfig("Emails to cloud transfer", Locale.getDefault().toString());
	   	DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
	   	this.client = client;
	}
	public void upload(File file,String path) throws UploadErrorException, DbxException, FileNotFoundException, IOException
	{
		InputStream inputStream = new FileInputStream(file);
		try
		{
			FileMetadata uploadedFile = this.client.files().uploadBuilder(path).uploadAndFinish(inputStream);
		}
	   	finally{}
	}
	public DbxClientV2 client;
	private static final String ACCESS_TOKEN = "vX9cF0nPkBAAAAAAAAAAJO3A-9oKrfkw7AZ0szsfWYmKezzvfU4hLDvvxFdC4hOC";
}
