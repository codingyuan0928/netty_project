package com.example.netty;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
	private final String message;
	private final StringBuilder responseMessage;

	public NettyClientHandler(String message,StringBuilder responseMessage) {
		this.message = message;
		this.responseMessage = responseMessage;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {

		ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
		logger.info("[Client → Netty] 發送請求: {}", message.trim());

	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
		String response = msg.toString(CharsetUtil.UTF_8);
		logger.info("[Netty → Client] 收到伺服器回應: {}", response);
		// 如果收到的是 "PASSBYID"，才發送請求，避免無限循環
		if (response.startsWith("PASSBYID")) {
			logger.info("[Client → Netty] 發送 ID 查詢請求: {}", response);
			ctx.writeAndFlush(Unpooled.copiedBuffer(response, CharsetUtil.UTF_8));
		} else if (response.startsWith("PASSBYFRUIT")) {
			logger.info("[Client → Netty] 更新水果資料: {}", response);
			ctx.writeAndFlush(Unpooled.copiedBuffer(response, CharsetUtil.UTF_8));
		}else {
			responseMessage.append(response);
		}
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("[Client] 發生錯誤", cause);
		responseMessage.append("Error: " + cause.getMessage());
		ctx.close();
	}

}
