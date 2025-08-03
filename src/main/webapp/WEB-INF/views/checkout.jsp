<%--
  Created by IntelliJ IDEA.
  User: hyeni
  Date: 7/30/25
  Time: 9:39 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>토스 결제</title>
  <script src="https://js.tosspayments.com/v2/standard"></script>
  <style>
    body {
      font-family: 'Malgun Gothic', '맑은 고딕', Arial, sans-serif;
      padding: 20px;
      line-height: 1.6;
    }

    h1 {
      color: #333;
      margin-bottom: 30px;
    }

    #payment-button {
      background-color: #3182f6;
      color: white;
      border: none;
      padding: 12px 24px;
      font-size: 16px;
      border-radius: 8px;
      cursor: pointer;
      font-family: inherit;
    }

    #payment-button:hover {
      background-color: #1b64da;
    }
  </style>
</head>
<body>
<h1>토스 결제 테스트</h1>
<button id="payment-button">결제하기</button>

<script>
  const clientKey = "test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq";
  const tossPayments = TossPayments(clientKey);

  document.getElementById('payment-button').addEventListener('click', function() {
    tossPayments.requestPayment('카드', {
      amount: 10000,
      orderId: 'order_' + Date.now(),
      orderName: '토스 결제 테스트',
      successUrl: window.location.origin + '/success',
      failUrl: window.location.origin + '/fail',
    });
  });
</script>
</body>
</html>