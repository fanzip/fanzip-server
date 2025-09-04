<%--
  Created by IntelliJ IDEA.
  User: hyeni
  Date: 7/30/25
  Time: 9:39 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>결제 성공</title>
</head>
<body>
<h1>결제가 성공했습니다!</h1>
<p>주문번호: ${orderId}</p>
<p>결제금액: ${amount}원</p>
<p>결제키: ${paymentKey}</p>
</body>
</html>