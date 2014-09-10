package com.kcd.orange;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientSocketChannelTest
{
	public static void main(String[] args) throws Exception
	{
		SocketAddress addr = new InetSocketAddress("localhost", 8080);
		SocketChannel socket = SocketChannel.open(addr);

		System.out.println(socket);
		System.out.println("# isBlocking() : " + socket.isBlocking());

		ByteBuffer buf = ByteBuffer.allocate(20);

		while(true)
		{
			int read = 0;
			socket.read(buf);
			buf.clear();

			System.out.print("# socket read : ");

			while(buf.hasRemaining())
			{
				System.out.print((char)buf.get());
			}

			buf.clear();

			String msg = "Hello Server!!";
			byte[] bytes = msg.getBytes();
			buf.put(bytes);
			buf.clear();

			socket.write(buf);

			System.out.println("\n# socket write : " + msg);

			buf.clear();
		}
	}
}