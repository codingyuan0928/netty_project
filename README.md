# netty_project
Spring Boot + Netty 練習專案，模擬 TCP 訊息收發、十六進位電文解碼，並透過 Jenkins + OpenShift 建立 CI/CD 流程。

# Netty Practice & Netty Server

本專案由 **兩個模組** 組成，用於模擬金融業常見的 **電文傳輸系統**：

1. **netty-practice (Spring Boot)**  
   - 提供 RESTful API (CRUD for Fruits)  
   - 使用 JPA 與 MySQL 連線  
   - 內建 Netty Client，模擬 TCP Client 發送指令  

2. **netty-server (Netty TCP Server)**  
   - 接收 TCP 訊息 (Hex 編碼電文)  
   - 解析電文後呼叫 Spring Boot API  
   - 將 API 回應轉換為電文格式，再回傳給 TCP Client  

---

## 🔧 架構設計
![NETTY 專案流程圖](https://www.websequencediagrams.com/cgi-bin/cdraw?lz=dGl0bGUgTmV0dHkgUHJvamVjdCAoU2ltcGxpZmllZCkKCkNsaWVudC0-AB4GQ29udHJvbGxlcjogUE9TVCBQQVNTQlk8aGV4PgoAExAALAkAPgUgU2VydmljZTogZm9yd2FyZAAoFAAdDQBtCFNlcnZlcgAiFgpub3RlIHJpZ2h0IG9mAIFDBwApCOino-eivCBoZXgg4oaSIHsiZnJ1aXRzTmFtZSI6Iuilv-eTnCJ9XG7lkbzlj6sgU3ByaW5nIEJvb3QKAIFGBwB1Bi0-ABALAIFyBy9hcGkvAEoGAEEYCgBGCy0-TXlTUUw6IElOU0VSVCBGcnVpdAoADwUATw9yZXR1cm4gSUQ9MQAzDgCBfw4AGwwAgR4OAIJfFgCDIwZJRDAxAIFUCACCTx4AJQsAgXAbR0UAgX0NLwCBJA8AgW0HU0VMRUMAgW0HIGJ5IElEAIFeHACCFgVEVE8AgWIcABsJAIFRKkZSVUlUPGhleChKU09OKT4AgVssACsRAIQCHFUAhA4PVXBkYXRlZACBTwkAhAIWVVBEQVRFAIN2InUAQgwAhAUcAGcQAIQBJUVORACBVRIAhwAWAId_DAAhFgCHfgsAiEAGAE8RCg&s=default)

## 🚀 CI/CD 流程

Jenkins：建置 Maven 專案、執行單元測試

OpenShift：部署 Netty Server 與 Spring Boot Service

## 📌 測試方式

啟動 netty-server (TCP 伺服器)

啟動 netty-practice (Spring Boot + Netty Client)

使用 TCP Client 發送指令：

PASSBY<hex>：新增水果

PASSBYID<hex>：查詢水果

PASSBYFRUIT<hex(JSON)>：更新水果

**呼叫範例**
curl -X POST http://localhost:8083/api/netty-client/send \
  -H "Content-Type: text/plain" \
  -d "PASSBYE8A5BFE7939C"

# 輸入 "PASSBY" + "西瓜" 的十六進制 (E8A5BFE7939C)
# 流程：新增水果 → 回傳 ID → 查詢 → 更新 → END
