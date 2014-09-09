package com.kcd.orange;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class WriteRead
{
	public static void main(String[] args)
	{
		try
		{
			String parentPath = "D:/file/";
			String s = "Hello, World! 안녕?";

			byte[] by = s.getBytes();

			ByteBuffer buf = ByteBuffer.allocate(by.length);
			buf.put(by);
			buf.clear();	//limit, capacity -> 현재 position, position -> 0

			FileOutputStream f_out = new FileOutputStream(parentPath + "1.txt");
			FileChannel out = f_out.getChannel();
			out.write(buf);
			out.close();

			FileInputStream f_in = new FileInputStream(parentPath + "1.txt");
			FileChannel in = f_in.getChannel();

			ByteBuffer buf2 = ByteBuffer.allocate((int)in.size());
			in.read(buf2);

			byte[] by2 = buf2.array();
			String s2 = new String(by2);

			System.out.println(s2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}