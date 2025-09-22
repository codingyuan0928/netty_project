package com.example.nettyserver;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.example.dto.FruitDTO;
import com.example.dto.ResponseDTO;
import com.example.model.FruitPOJO;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final String ERROR_PREFIX = "發生錯誤: ";
	private String host;

	public NettyServerHandler(String host) {
		this.host = host;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
		ByteBuf in = (ByteBuf) msg;
		try {
			byte[] receivedBytes = new byte[in.readableBytes()];
			in.readBytes(receivedBytes);
			String receivedStr = new String(receivedBytes, StandardCharsets.UTF_8).trim();
			logger.info("[Netty] 收到 TCP 請求: {}", receivedStr);

			if (receivedStr.isEmpty()) {
				ctx.writeAndFlush(Unpooled.copiedBuffer("空白輸入無效", StandardCharsets.UTF_8))
						.addListener(ChannelFutureListener.CLOSE);
				return;
			}
			if (receivedStr.startsWith("PASSBYFRUIT")) {
				logger.info("[Netty] 處理指令: PASSBYFRUIT");
				handlePassbyFruit(ctx, receivedStr.substring(11).trim());
			} else if (receivedStr.startsWith("PASSBYID")) {
				logger.info("[Netty] 處理指令: PASSBYID");
				handlePassbyId(ctx, receivedStr.substring(8).trim());
			} else if (receivedStr.startsWith("PASSBY")) {
				logger.info("[Netty] 處理指令: PASSBY");
				handlePassby(ctx, receivedStr.substring(6).trim());
			} else {
				logger.warn("[Netty] 無效指令: {}", receivedStr);
				ctx.writeAndFlush(Unpooled.copiedBuffer("輸入不符", StandardCharsets.UTF_8))
						.addListener(ChannelFutureListener.CLOSE);
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	// **處理 PASSBYFRUIT**
	private void handlePassbyFruit(ChannelHandlerContext ctx, String hexPart) throws IOException {
		if (!isHexadecimal(hexPart)) {
			logger.warn("[Netty] PASSBYFRUIT - 非 16 進制: {}", hexPart);
			ctx.writeAndFlush(Unpooled.copiedBuffer("輸入不符: hextPart 非 16進制", StandardCharsets.UTF_8))
					.addListener(ChannelFutureListener.CLOSE);
			return;
		}
		byte[] fruitJsonByte = hexStringToByteArray(hexPart);
		FruitDTO updatedFruit = objectMapper.readValue(fruitJsonByte, FruitDTO.class);
		logger.info("[Netty] PASSBYFRUIT - 解析成功, ID: {}, 名稱: {}", updatedFruit.getId(), updatedFruit.getFruitName());
		sendPutRequest(ctx, updatedFruit);
	}

	// **處理 PASSBYID**
	private void handlePassbyId(ChannelHandlerContext ctx, String hexPart) {
		if (!isHexadecimal(hexPart)) {
			logger.warn("[Netty] PASSBYID - 非 16 進制: {}", hexPart);
			ctx.writeAndFlush(Unpooled.copiedBuffer("輸入不符: hexPart 非 16 進制", StandardCharsets.UTF_8))
					.addListener(ChannelFutureListener.CLOSE);
			return;
		}

		int id = Integer.parseInt(hexPart, 16);
		logger.info("[Netty] PASSBYID - 解析成功, ID: {}", id);

		sendGetRequest(ctx, id);
	}

	// **處理 PASSBY**
	private void handlePassby(ChannelHandlerContext ctx, String hexPart) {
		if (!isHexadecimal(hexPart) || (hexPart.length() != 12 && hexPart.length() != 8)) {
			logger.warn("[Netty] PASSBY - 非 16 進制: {}", hexPart);
			ctx.writeAndFlush(Unpooled.copiedBuffer("輸入不符: hexPart 非 16 進制或長度錯誤", StandardCharsets.UTF_8))
					.addListener(ChannelFutureListener.CLOSE);
			return;
		}

		String decodedMessage = hexPart.length() == 12
				? new String(hexStringToByteArray(hexPart), StandardCharsets.UTF_8)
				: new String(hexStringToByteArray(hexPart), Charset.forName("Big5"));

		logger.info("[Netty] PASSBY - 解析成功, 輸入: {}", decodedMessage);

		FruitPOJO fruitPOJO = new FruitPOJO();
		fruitPOJO.setFruitsName(decodedMessage);

		sendPostRequest(ctx, fruitPOJO);
	}

	// **發送 GET 請求至 Controller**
	private void sendGetRequest(ChannelHandlerContext ctx, int id) {
		try {
			logger.info("[Netty → Spring Boot] 發送 GET 請求, ID: {}", id);
			ResponseDTO<FruitDTO> responseDTO = restTemplate.exchange("http://" + host + ":8083/api/fruits/" + id,
					org.springframework.http.HttpMethod.GET, null, // GET
																	// 請求通常不需要
																	// HttpEntity
					new ParameterizedTypeReference<ResponseDTO<FruitDTO>>() {
					}).getBody();
			logger.info("[Spring Boot → Netty] GET 成功, ID: {}, 名稱: {}", responseDTO.getData().getId(),
					responseDTO.getData().getFruitName());
			handleControllerResponse(ctx, responseDTO, "GET");
		} catch (Exception e) {
			logger.error("[Netty → Spring Boot] GET 請求失敗", e);
			ctx.writeAndFlush(Unpooled.copiedBuffer(ERROR_PREFIX + e.getMessage(), StandardCharsets.UTF_8));
		}
	}

	// **發送 POST 請求至 Controller**
	private void sendPostRequest(ChannelHandlerContext ctx, FruitPOJO fruitPOJO) {
		try {
			logger.info("[Netty → Spring Boot] 發送 POST 請求, 名稱: {}", fruitPOJO.getFruitsName());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<FruitPOJO> requestEntity = new HttpEntity<>(fruitPOJO, headers);

			// 發送 POST 請求並接收回應
			ResponseDTO<Integer> responseDTO = restTemplate
					.exchange("http://" + host + ":8083/api/fruits", org.springframework.http.HttpMethod.POST,
							requestEntity, new ParameterizedTypeReference<ResponseDTO<Integer>>() {
							})
					.getBody();
			logger.info("送得路徑:{}",host);
			logger.info("[Spring Boot → Netty] POST 成功, ID: {}", responseDTO.getData());
			handleControllerResponse(ctx, responseDTO, "POST");
		} catch (Exception e) {
			logger.info("送得路徑:{}",host);
			logger.error("[Netty → Spring Boot] POST 請求失敗", e);
			ctx.writeAndFlush(Unpooled.copiedBuffer(ERROR_PREFIX + e.getMessage(), StandardCharsets.UTF_8));
		}
	}

	// **發送 PUT 請求至 Controller**
	private void sendPutRequest(ChannelHandlerContext ctx, FruitDTO updatedFruitDTO) {
		try {
			logger.info("[Netty → Spring Boot] 發送 PUT 請求, ID: {}, 名稱: {}", updatedFruitDTO.getId(),
					updatedFruitDTO.getFruitName());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<FruitDTO> requestEntity = new HttpEntity<>(updatedFruitDTO, headers);

			// 使用 exchange() 來發送 PUT 請求，獲取 Response
			ResponseDTO<FruitDTO> responseDTO = restTemplate
					.exchange("http://" + host + ":8083/api/fruits", org.springframework.http.HttpMethod.PUT,
							requestEntity, new ParameterizedTypeReference<ResponseDTO<FruitDTO>>() {
							})
					.getBody();
			logger.info("[Spring Boot → Netty] PUT 成功, ID: {}, 名稱: {}", updatedFruitDTO.getId(),
					updatedFruitDTO.getFruitName());
			handleControllerResponse(ctx, responseDTO, "PUT");
		} catch (Exception e) {
			logger.error("[Netty → Spring Boot] PUT 請求失敗", e);
			ctx.writeAndFlush(Unpooled.copiedBuffer(ERROR_PREFIX + e.getMessage(), StandardCharsets.UTF_8));
		}
	}

	// **處理 Controller 回應**
	private void handleControllerResponse(ChannelHandlerContext ctx, ResponseDTO<?> responseDTO, String operationType)
			throws IOException {
		if (!"success".equals(responseDTO.getStatus())) {
			logger.warn("[Spring Boot → Netty] {} 失敗: {}", operationType, responseDTO.getMessage());
			ctx.writeAndFlush(Unpooled.copiedBuffer("失敗: " + responseDTO.getMessage(), StandardCharsets.UTF_8));
			return;
		}

		Object data = responseDTO.getData(); // 取得 data

		// **處理 `insertData` 回傳的數據**
		if (data instanceof Integer newId) {
			ctx.writeAndFlush(Unpooled.copiedBuffer("PASSBYID" + String.format("%02X", newId), StandardCharsets.UTF_8));
		}
		// **處理 `Fruit` 物件**
		else if (data instanceof FruitDTO fruit) {
			if ("GET".equals(operationType)) {
				handleFruitGetResponse(ctx, fruit);
			} else if ("PUT".equals(operationType)) {
				handleFruitPutResponse(ctx, fruit);
			}
		} else {
			logger.error("[Spring Boot → Netty] 未知的回應類型: {}", data.getClass().getSimpleName());
			ctx.writeAndFlush(Unpooled.copiedBuffer("未知的回傳類型", StandardCharsets.UTF_8));
		}

	}

	private void handleFruitGetResponse(ChannelHandlerContext ctx, FruitDTO fruitDTO) throws IOException {
		// **更新 `fruitsName`**
		byte[] dataBytes = objectMapper.writeValueAsBytes(updateFruitsName(fruitDTO));
		String hexData = bytesToHex(dataBytes);

		logger.info("[Netty → TCP Client] GET 結果轉換 HEX: {}", hexData);
		ctx.writeAndFlush(Unpooled.copiedBuffer("PASSBYFRUIT" + hexData, StandardCharsets.UTF_8));
	}

	private void handleFruitPutResponse(ChannelHandlerContext ctx, FruitDTO fruitDTO) throws IOException {

		// 轉換為 JSON 並轉換為 16 進制
		byte[] updatedDataBytes = objectMapper.writeValueAsBytes(fruitDTO);
		String updatedHexData = bytesToHex(updatedDataBytes);

		logger.info("[Netty → TCP Client] PUT 結果轉換 HEX: {}", updatedHexData);
		ctx.writeAndFlush(Unpooled.copiedBuffer("END" + updatedHexData, StandardCharsets.UTF_8))
				.addListener(ChannelFutureListener.CLOSE);
	}

	// **檢查是否為 16 進制字串**
	private boolean isHexadecimal(String str) {
		return Pattern.matches("^[0-9A-Fa-f]+$", str);
	}

	// **16 進制字串轉換為 byte[]**
	private byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	// **將 byte[] 轉換成 16 進制字串**
	private String bytesToHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			hexString.append(String.format("%02X", b));
		}
		return hexString.toString();
	}

	// 將回傳的水果更新
	private FruitDTO updateFruitsName(FruitDTO fruit) {
		fruit.setFruitName("更新後的" + fruit.getFruitName());
		return fruit;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("發生錯誤", cause);
		ctx.close();
	}
}
