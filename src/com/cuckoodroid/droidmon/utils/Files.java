package com.cuckoodroid.droidmon.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Files {
	
	public static String readFile(String filename) throws FileNotFoundException,IOException
	{
		//Get the text file
		File file = new File(filename);

		//Read text from file
		StringBuilder text = new StringBuilder();

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;

		while ((line = br.readLine()) != null) 
		{
			text.append(line);
			text.append('\n');
		}
		
		br.close();
		return text.toString();
	}
	

}