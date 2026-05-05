<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Food Express</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>

<body>

<!-- NAVBAR -->
<nav class="navbar navbar-expand-lg bg-white shadow-sm">
    <div class="container">

        <!-- Logo -->
        <a class="navbar-brand fw-bold text-danger" href="${pageContext.request.contextPath}/">
            FOOD EXPRESS
        </a>

        <!-- Menu -->
        <div class="ms-auto d-flex align-items-center">
            <c:choose>
                <c:when test="${not empty sessionScope.loggedUser}">
                    <div class="d-flex align-items-center me-3">
                        <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center"
                             style="width: 35px; height: 35px; font-size: 14px; font-weight: bold;">
                            ${fn:toUpperCase(fn:substring(sessionScope.loggedUser.email, 0, 1))}
                        </div>
                        <div class="ms-2">
                            <small class="text-muted d-block">Welcome back</small>
                            <strong class="text-dark">${sessionScope.loggedUser.email}</strong>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${sessionScope.loggedUser.role == 'ROLE_ADMIN'}">
                            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-danger btn-sm me-2">Dashboard</a>
                            <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline-dark btn-sm me-2">Orders</a>
                            <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-dark btn-sm me-2">Products</a>
                            <a href="${pageContext.request.contextPath}/admin/delivery-boys" class="btn btn-outline-dark btn-sm me-2">Delivery Team</a>
                        </c:when>
                        <c:when test="${sessionScope.loggedUser.role == 'ROLE_DELIVERY'}">
                            <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary btn-sm me-2">Products</a>
                            <a href="${pageContext.request.contextPath}/delivery/dashboard" class="btn btn-outline-info btn-sm me-2">Delivery Dashboard</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-outline-secondary btn-sm me-2">Dashboard</a>
                            <div class="btn-group me-2 cart-dropdown">
                                <button type="button" class="btn btn-outline-dark btn-sm dropdown-toggle"
                                        data-bs-toggle="dropdown" aria-expanded="false">
                                    🛒 Cart (<c:out value="${empty cartCount ? 0 : cartCount}" />)
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end p-3" style="min-width: 320px;">
                                    <c:choose>
                                        <c:when test="${not empty cartItems}">
                                            <c:forEach var="item" items="${cartItems}">
                                                <li class="mb-2">
                                                    <div class="d-flex justify-content-between">
                                                        <span>${item.productName} x${item.quantity}</span>
                                                        <strong>Rs. ${item.subTotal}</strong>
                                                    </div>
                                                </li>
                                            </c:forEach>
                                            <li><hr class="dropdown-divider"></li>
                                            <li class="d-flex justify-content-between fw-bold">
                                                <span>Total</span>
                                                <span>Rs. ${cartTotal}</span>
                                            </li>
                                            <li class="mt-3">
                                                <a href="${pageContext.request.contextPath}/user/cart" class="btn btn-sm btn-dark w-100 mb-2">View Cart</a>
                                                <a href="${pageContext.request.contextPath}/user/checkout" class="btn btn-sm btn-outline-secondary w-100">Checkout</a>
                                            </li>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="dropdown-item text-center text-muted">Your cart is empty</li>
                                        </c:otherwise>
                                    </c:choose>
                                </ul>
                            </div>

                            <a href="${pageContext.request.contextPath}/user/orders" class="btn btn-outline-dark btn-sm me-2">Orders</a>
                            <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-dark btn-sm me-2">Profile</a>
                        </c:otherwise>
                    </c:choose>

                    <form action="${pageContext.request.contextPath}/logout" method="post" style="display:inline;">
                        <button class="btn btn-danger btn-sm">Logout</button>
                    </form>
                </c:when>
                <c:when test="${not empty pageContext.request.userPrincipal}">
                    <div class="d-flex align-items-center me-3">
                        <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center"
                             style="width: 35px; height: 35px; font-size: 14px; font-weight: bold;">
                            <sec:authentication property="principal.username" var="userEmail"/>
                            ${userEmail.substring(0,1).toUpperCase()}
                        </div>
                        <div class="ms-2">
                            <small class="text-muted d-block">Welcome back</small>
                            <strong class="text-dark">${userEmail}</strong>
                        </div>
                    </div>

                    <sec:authorize access="hasRole('ADMIN')">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-danger btn-sm me-2">Dashboard</a>
                        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline-dark btn-sm me-2">Orders</a>
                        <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-dark btn-sm me-2">Products</a>
                        <a href="${pageContext.request.contextPath}/admin/delivery-boys" class="btn btn-outline-dark btn-sm me-2">Delivery Team</a>
                    </sec:authorize>

                    <sec:authorize access="hasRole('DELIVERY')">
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary btn-sm me-2">Products</a>
                        <a href="${pageContext.request.contextPath}/delivery/dashboard" class="btn btn-outline-info btn-sm me-2">Delivery Dashboard</a>
                    </sec:authorize>

                    <sec:authorize access="hasRole('USER')">
                        <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-outline-secondary btn-sm me-2">Dashboard</a>
                        <div class="btn-group me-2 cart-dropdown">
                            <button type="button" class="btn btn-outline-dark btn-sm dropdown-toggle"
                                    data-bs-toggle="dropdown" aria-expanded="false">
                                🛒 Cart (<c:out value="${empty cartCount ? 0 : cartCount}" />)
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end p-3" style="min-width: 320px;">
                                <c:choose>
                                    <c:when test="${not empty cartItems}">
                                        <c:forEach var="item" items="${cartItems}">
                                            <li class="mb-2">
                                                <div class="d-flex justify-content-between">
                                                    <span>${item.productName} x${item.quantity}</span>
                                                    <strong>Rs. ${item.subTotal}</strong>
                                                </div>
                                            </li>
                                        </c:forEach>
                                        <li><hr class="dropdown-divider"></li>
                                        <li class="d-flex justify-content-between fw-bold">
                                            <span>Total</span>
                                            <span>Rs. <c:out value="${empty cartTotal ? 0 : cartTotal}" /></span>
                                        </li>
                                        <li class="mt-3">
                                            <a href="${pageContext.request.contextPath}/user/cart" class="btn btn-sm btn-dark w-100 mb-2">View Cart</a>
                                            <a href="${pageContext.request.contextPath}/user/checkout" class="btn btn-sm btn-outline-secondary w-100">Checkout</a>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="dropdown-item text-center text-muted">Your cart is empty</li>
                                    </c:otherwise>
                                </c:choose>
                            </ul>
                        </div>

                        <a href="${pageContext.request.contextPath}/user/orders" class="btn btn-outline-dark btn-sm me-2">Orders</a>
                        <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-dark btn-sm me-2">Profile</a>
                    </sec:authorize>

                    <form action="${pageContext.request.contextPath}/logout" method="post" style="display:inline;">
                        <button class="btn btn-danger btn-sm">Logout</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary btn-sm me-2">Products</a>
                    <a href="${pageContext.request.contextPath}/admin/login"
                       class="btn btn-outline-secondary btn-sm me-2"
                       title="Admin Portal">
                        🔐 Admin
                    </a>

                          <a href="${pageContext.request.contextPath}/delivery/login"
                              class="btn btn-outline-info btn-sm me-2"
                              title="Delivery Portal">
                                Delivery
                          </a>

                    <a href="${pageContext.request.contextPath}/login"
                       class="btn btn-outline-dark btn-sm me-2">
                        Login
                    </a>

                    <a href="${pageContext.request.contextPath}/register"
                       class="btn btn-danger btn-sm">
                        Register
                    </a>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</nav>