/*
 * Create NettyEchoServerJava project in Eclipse.
 * Add netty.jar as extaernal jar in Eclipse
 * Run as java application
 * try "telnet localhost 8080"
 */

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
//import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class Netty {
	public static class EchoServerHandler extends SimpleChannelUpstreamHandler {

		 @Override
	     public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
			 System.out.println("Handle upstream");
			 
	         // Log all channel state changes.
	         if (e instanceof ChannelStateEvent) {
	        	 System.out.println("Channel state changed: " + e);
	         }

	         super.handleUpstream(ctx, e);

	     }

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			// Use channel buffer to get the message from MessageEvent e and copy each character read to a string
			// check for occurence of '\n' or '\r' characters and if yes then print the above string
			Channel ch = e.getChannel();
			ChannelBuffer buf = (ChannelBuffer) e.getMessage();

			// write echo
			ch.write(e.getMessage());
			
			// Print received message
			String msg = "";
		    while(buf.readable()) {
		    	msg = msg + (char)buf.readByte();
		    }
		     
		    System.out.println("Received message: " + msg);

		}

		@Override
		public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
				throws Exception {
			 System.out.println("Channel opened");
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
			e.getCause().printStackTrace();
			System.out.println(e.toString());
			Channel ch = e.getChannel();
			ch.close();
		}
	}

	public static class NettyServer {

		public void startServer() throws Exception {
			ChannelFactory factory = new NioServerSocketChannelFactory(
					Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool());
			// Create and instance of server bootstrap and bind it to InetSocketAddress
			ServerBootstrap bootstrap = new ServerBootstrap( factory );
			
			// Register handler
			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
					public ChannelPipeline getPipeline() {
                		return Channels.pipeline(new EchoServerHandler());
					}
			});
			
			// Bind and start to accept incoming connections.
			bootstrap.bind(new InetSocketAddress("0.0.0.0", portnum));
			
			System.out.println("Server started at port " + portnum + '.');
			System.out.println("Try this command: telnet localhost " + portnum + '.');
			
		}
		
		public void setPort(Integer num) {
			portnum = num;
		}
		
		// Port number
		Integer portnum;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		NettyServer ns = new NettyServer();
		
		if (args.length == 0)
			ns.setPort(8080);
		else
			ns.setPort(Integer.valueOf(args[0]));
		
		ns.startServer();
	}
}