<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Delivery Login - Food Express</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #0ea5e9 0%, #0369a1 100%);
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
        }
        .login-box {
            background: #fff;
            border-radius: 12px;
            width: 100%;
            max-width: 420px;
            padding: 32px;
            box-shadow: 0 16px 35px rgba(0, 0, 0, 0.2);
        }
        .badge-role {
            background: #0369a1;
            color: #fff;
            border-radius: 999px;
            padding: 4px 12px;
            font-size: 12px;
            display: inline-block;
            margin-top: 8px;
        }
    </style>
</head>
<body>
<div class="login-box">
    <h3 class="mb-1">Delivery Partner Login</h3>
    <p class="text-muted mb-3">Food Express Last-Mile Panel</p>
    <span class="badge-role">DELIVERY ACCESS</span>

    <c:if test="${not empty msg}">
        <div class="alert alert-danger mt-3">${msg}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/authenticate" method="post" class="mt-3">
        <input type="text" name="username" class="form-control mb-2" placeholder="Delivery email" required>
        <input type="password" name="password" class="form-control mb-3" placeholder="Password" required>
        <button class="btn btn-primary w-100">Login as Delivery Boy</button>
    </form>

    <div class="mt-3 small">
        <a href="${pageContext.request.contextPath}/">Back to Home</a>
    </div>
</div>
</body>
</html>
