package com.example.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.NettyClientService;

@RestController
@RequestMapping("/api/netty-client")
public class NettyController {
	@Autowired
	private NettyClientService nettyClientService;

	@PostMapping("/send")
	public ResponseEntity<String> sendToNettyServer(@RequestBody String message) {
		String response = nettyClientService.sendMessage(message);

		if ("輸入不符".equals(response)) {
			return ResponseEntity.badRequest().body("Netty Server 回應: " + response);
		}

		String hexData = response.substring(3);
		String decodedText = hexToString(hexData);

		return ResponseEntity.ok("Netty Server Response: " + response + "\r\n" + "解碼意思為: " + decodedText);
	}

	public static String hexToString(String hexData) {
		// 先把hexData轉成byte[]
		byte[] bytes = new byte[hexData.length() / 2];
		// 在依照不同的字元編碼表轉換成字串
		for (int i = 0; i < hexData.length(); i += 2) {
			bytes[i / 2] = (byte) Integer.parseInt(hexData.substring(i, i + 2), 16);
		}
		return new String(bytes, StandardCharsets.UTF_8);
	}
}
