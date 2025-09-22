package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.example.netty.NettyClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

@Service
public class NettyClientService {
	private static final Logger logger = LoggerFactory.getLogger(NettyClientService.class);
		@Value("${netty.server.host}")
		private String host;

    	@Value("${netty.server.port}")
    	private int port;

    public String sendMessage(String message) {
        EventLoopGroup group = new NioEventLoopGroup();
        final StringBuilder responseMessage = new StringBuilder();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ch.pipeline().addLast(new NettyClientHandler(message, responseMessage));
                        }
                    });

            // 連線到 Netty Server
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
            return responseMessage.toString();
        } catch (Exception e) {
            logger.error("[NettyClient] 發送訊息失敗", e);
            return "Error: " + e.getMessage();
        } finally {
            group.shutdownGracefully();
        }
    }
}
