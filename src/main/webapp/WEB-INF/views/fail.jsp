<%--
  Created by IntelliJ IDEA.
  User: hyeni
  Date: 7/30/25
  Time: 9:39 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>결제 실패</title>
</head>
<body>
<h1>결제가 실패했습니다.</h1>
<p>에러 코드: ${code}</p>
<p>에러 메시지: ${message}</p>
<a href="/">다시 시도</a>
</body>
</html>