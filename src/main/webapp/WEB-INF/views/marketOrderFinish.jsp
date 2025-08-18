<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>주문 완료</title>
    <style>
        body {
            font-family: 'Malgun Gothic', '맑은 고딕', Arial, sans-serif;
            padding: 20px;
            line-height: 1.6;
            background-color: #f5f5f5;
        }
        
        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #3182f6;
        }
        
        .success-message {
            text-align: center;
            color: #28a745;
            font-size: 18px;
            margin-bottom: 30px;
        }
        
        .order-info {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
        }
        
        .order-info h3 {
            color: #495057;
            margin-bottom: 15px;
        }
        
        .order-info p {
            margin: 5px 0;
            color: #6c757d;
        }
        
        .order-items {
            margin-top: 30px;
        }
        
        .order-items h3 {
            color: #333;
            margin-bottom: 20px;
        }
        
        .item {
            display: flex;
            align-items: center;
            padding: 15px;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            margin-bottom: 15px;
            background-color: #fff;
        }
        
        .item-image {
            width: 80px;
            height: 80px;
            border-radius: 8px;
            margin-right: 15px;
            object-fit: cover;
        }
        
        .item-details {
            flex: 1;
        }
        
        .item-name {
            font-weight: bold;
            color: #333;
            margin-bottom: 5px;
        }
        
        .item-quantity {
            color: #6c757d;
            margin-bottom: 5px;
        }
        
        .item-price {
            color: #3182f6;
            font-weight: bold;
        }
        
        .loading {
            text-align: center;
            color: #6c757d;
            font-style: italic;
        }
        
        .error {
            text-align: center;
            color: #dc3545;
            background-color: #f8d7da;
            padding: 15px;
            border-radius: 8px;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>주문이 완료되었습니다!</h1>
        
        <div class="success-message">
            ✅ 결제가 성공적으로 처리되었습니다.
        </div>
        
        <div class="order-info">
            <h3>주문 정보</h3>
            <p><strong>주문번호:</strong> ${orderId}</p>
            <p><strong>결제금액:</strong> ${amount}원</p>
            <p><strong>결제키:</strong> ${paymentKey}</p>
        </div>
        
        <div class="order-items">
            <h3>주문 상품</h3>
            <div id="orderItemsContainer" class="loading">
                주문 상품을 불러오는 중...
            </div>
        </div>
    </div>

    <script>
        // 주문 상품 조회
        async function loadOrderItems() {
            const orderId = '${orderId}';
            
            try {
                const response = await fetch(`/api/market/orders/${orderId}/items`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                
                if (!response.ok) {
                    throw new Error('주문 상품을 불러올 수 없습니다.');
                }
                
                const orderItems = await response.json();
                displayOrderItems(orderItems);
                
            } catch (error) {
                console.error('Error loading order items:', error);
                document.getElementById('orderItemsContainer').innerHTML = 
                    '<div class="error">주문 상품을 불러오는 중 오류가 발생했습니다.</div>';
            }
        }
        
        function displayOrderItems(items) {
            const container = document.getElementById('orderItemsContainer');
            
            if (!items || items.length === 0) {
                container.innerHTML = '<div class="error">주문 상품이 없습니다.</div>';
                return;
            }
            
            const itemsHtml = items.map(item => `
                <div class="item">
                    <img src="${item.thumbnailImage || '/images/no-image.png'}" 
                         alt="${item.productName}" 
                         class="item-image" 
                         onerror="this.src='/images/no-image.png'">
                    <div class="item-details">
                        <div class="item-name">${item.productName}</div>
                        <div class="item-quantity">수량: ${item.quantity}개</div>
                        <div class="item-price">${item.finalPrice.toLocaleString()}원</div>
                    </div>
                </div>
            `).join('');
            
            container.innerHTML = itemsHtml;
        }
        
        // 페이지 로드 시 주문 상품 조회
        document.addEventListener('DOMContentLoaded', function() {
            loadOrderItems();
        });
    </script>
</body>
</html>