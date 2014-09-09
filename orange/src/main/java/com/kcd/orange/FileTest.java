package com.kcd.orange;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import junit.framework.TestCase;

public class FileTest extends TestCase
{
	public void test() throws Exception
	{
		int met = 4;

		String parentPath = "D:/file/";
		String inFile = "1.avi";
		String outFile = "1-1.avi";

		FileInputStream f_in = new FileInputStream(parentPath + inFile);
		FileChannel in = f_in.getChannel();
		FileOutputStream f_out = new FileOutputStream(parentPath + outFile);
		FileChannel out = f_out.getChannel();

		switch(met)
		{
			case 1 : copyIO(f_in, f_out); break;
			case 2 : copyMap(in, out); break;
			case 3 : copyNIO(in, out); break;
			case 4 : copyTransfer(in, out); break;
		}
	}

	public void copyIO(FileInputStream f_in, FileOutputStream f_out) throws Exception
	{
		long start = System.currentTimeMillis();

		byte[] buf = new byte[1024];

		for(int i; (i = f_in.read(buf)) != -1;)
		{
			f_out.write(buf, 0, i);
		}

		f_in.close();
		f_out.close();

		long end = System.currentTimeMillis();

		System.out.println("[IO] " + (end - start));
	}

	public void copyMap(FileChannel in, FileChannel out) throws Exception
	{
		long start = System.currentTimeMillis();

		MappedByteBuffer m = in.map(FileChannel.MapMode.READ_ONLY, 0, in.size());

		out.write(m);

		in.close();
		out.close();

		long end = System.currentTimeMillis();

		System.out.println("[Map] " + (end - start));
	}

	/**
	 * FileChannel 에서 읽거나 쓰는 건 버퍼의 position과 limit 에 범위를 한번에 사용한다.
	 */
	public void copyNIO(FileChannel in, FileChannel out) throws Exception
	{
		long start = System.currentTimeMillis();

		ByteBuffer buf = ByteBuffer.allocate((int)in.size());
		in.read(buf);

		buf.flip();	//limit -> 현재 position, position -> 0

		out.write(buf);

		in.close();
		out.close();

		long end = System.currentTimeMillis();

		System.out.println("[NIO] " + (end - start));
	}

	public void copyTransfer(FileChannel in, FileChannel out) throws Exception
	{
		long start = System.currentTimeMillis();

		in.transferTo(0, in.size(), out);

		in.close();
		out.close();

		long end = System.currentTimeMillis();

		System.out.println("[Transfer] " + (end - start));
	}
}