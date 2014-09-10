package com.kcd.orange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerSocketChannelTest implements Runnable
{
	private Selector selector;
	private int port = 8080;

	public ServerSocketChannelTest() throws IOException
	{
		selector = Selector.open();

		ServerSocketChannel server = ServerSocketChannel.open();

		System.out.println(server);
		System.out.println("--------------------------------------------------");

		ServerSocket socket = server.socket();
		SocketAddress addr = new InetSocketAddress(port);
		socket.bind(addr);

		System.out.println(server);
		System.out.println("--------------------------------------------------");

		server.configureBlocking(false);

		int validOps = server.validOps();

		System.out.print("ServerSocketChannel.validOps() : " + validOps);
		System.out.println(", " + (validOps == SelectionKey.OP_ACCEPT));

		server.register(selector, SelectionKey.OP_ACCEPT);

		System.out.println("**************************************************");
		System.out.println("클라이언트의 접속을 기다리고 있습니다.");
		System.out.println("**************************************************");
	}

	public static void main(String[] args) throws IOException
	{
		ServerSocketChannelTest test = new ServerSocketChannelTest();
		new Thread(test).start();
	}

	@Override
	public void run()
	{
		int socketOps = SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE;
		ByteBuffer buf = null;

		while(true)
		{
			try
			{
				selector.select(3000);
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}

			Set selectedKeys = selector.selectedKeys();
			Iterator iter = selectedKeys.iterator();

			while(iter.hasNext())
			{
				try
				{
					SelectionKey selected = (SelectionKey)iter.next();
					iter.remove();

					SelectableChannel channel = selected.channel();

					//접속 최초(accept)
					if(channel instanceof ServerSocketChannel)	//처음에 요청은 셀렉터가 서버소켓채널로 하여금 ACCEPT 처리
					{
						ServerSocketChannel serverChannel = (ServerSocketChannel)channel;
						SocketChannel socketChannel = serverChannel.accept();	//이 때 한번 더 클라이언트에게 서버로 접속 요청을 하게 하는 것 같음, 처음에는 서버소켓 ACCEPT 대기 상태로 클라이언트로부터 접속 시도가 오면 발동되고 여기서 클라이언트로 접속 요청을 다시 보내서 클라이언트 소켓이 접속 요청을 다시 보냄

						if(socketChannel == null)
						{
							System.out.println("# null server socket");

							continue;
						}

						System.out.println("# socket accepted : " + socketChannel);

						socketChannel.configureBlocking(false);

						int validOps = socketChannel.validOps();

						System.out.print("SocketChannel.validOps() : " + validOps);
						System.out.println(", " + (validOps == socketOps));

						socketChannel.register(selector, socketOps);
					}
					//접속 이 후 단계(connect, read, write)
					else
					{
						SocketChannel socketChannel = (SocketChannel)channel;
						buf = ByteBuffer.allocate(20);

						//accept 다음에 connect 임
						//selectionKey는 Selector 가 주관
						if(selected.isConnectable())
						{
							System.out.println("# scoket connected");

							//Selector가 처리 후 채널에서 처리
							if(socketChannel.isConnectionPending())
							{
								System.out.println("# connection is pending");

								socketChannel.finishConnect();
							}
						}

						if(selected.isReadable())
						{
							socketChannel.read(buf);
							buf.clear();

							System.out.print("# socket read : ");

							while(buf.hasRemaining())
							{
								System.out.print((char)buf.get());
							}
						}

						if(selected.isWritable())
						{
							String s = "Hello Client!!";
							byte[] bytes = s.getBytes();
							buf.put(bytes);
							buf.clear();

							socketChannel.write(buf);

							System.out.println("# socket write : " + s);
						}
					}
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		}
	}
}