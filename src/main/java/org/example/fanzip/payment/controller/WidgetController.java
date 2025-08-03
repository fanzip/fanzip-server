package org.example.fanzip.payment.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
public class WidgetController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/api/payments/confirm", method = RequestMethod.POST)
    @ResponseBody
    public String confirmPayment(@RequestBody String jsonBody, HttpServletResponse response) throws Exception {

        JSONParser parser = new JSONParser();
        String orderId;
        String amount;
        String paymentKey;

        try {
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            paymentKey = (String) requestData.get("paymentKey");
            orderId = (String) requestData.get("orderId");
            amount = (String) requestData.get("amount");
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JSONObject errorObj = new JSONObject();
            errorObj.put("error", "Invalid JSON format");
            return errorObj.toString();
        }

        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        // TODO: 개발자센터에 로그인해서 내 결제위젯 연동 키 > 시크릿 키를 입력하세요.
        String widgetSecretKey = "test_sk_jZ61JOxRQVE9LxXBMQmVW0X9bAqw";

        // 토스페이먼츠 API 인증
        String credentials = widgetSecretKey + ":";
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        String authorization = "Basic " + encodedCredentials;

        try {
            // 결제 승인 API 호출
            URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", authorization);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 요청 데이터 전송
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();

            // 응답 처리
            InputStream responseStream = (responseCode == 200) ?
                    connection.getInputStream() : connection.getErrorStream();

            StringBuilder responseBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
            }

            // HTTP 상태 코드 설정
            response.setStatus(responseCode);
            response.setContentType("application/json; charset=UTF-8");

            return responseBody.toString();

        } catch (Exception e) {
            logger.error("결제 승인 API 호출 실패", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorObj = new JSONObject();
            errorObj.put("error", "결제 승인 처리 중 오류가 발생했습니다.");
            return errorObj.toString();
        }
    }

    /**
     * 결제 성공 페이지
     */
    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String paymentSuccess(HttpServletRequest request, Model model) {
        String paymentKey = request.getParameter("paymentKey");
        String orderId = request.getParameter("orderId");
        String amount = request.getParameter("amount");

        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);

        return "success"; // success.jsp로 이동
    }

    /**
     * 메인 페이지 (결제 페이지)
     */
    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public String index(Model model) {
        return "checkout"; // checkout.jsp로 이동
    }

    /**
     * 결제 실패 페이지
     */
    @RequestMapping(value = "/fail", method = RequestMethod.GET)
    public String failPayment(HttpServletRequest request, Model model) {
        String failCode = request.getParameter("code");
        String failMessage = request.getParameter("message");

        model.addAttribute("code", failCode);
        model.addAttribute("message", failMessage);

        return "fail"; // fail.jsp로 이동
    }
}