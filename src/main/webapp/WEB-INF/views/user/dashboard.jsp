<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jsp" %>

<div class="container py-5">
    <div class="d-flex flex-column flex-md-row align-items-md-center justify-content-between mb-4">
        <div>
            <h2 class="fw-bold mb-1">User Dashboard</h2>
            <p class="text-muted mb-0">Order food, track your order, and manage your account from one place.</p>
        </div>
        <div class="mt-3 mt-md-0">
            <a href="${pageContext.request.contextPath}/user/orders" class="btn btn-outline-dark btn-sm me-2">My Orders</a>
            <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-secondary btn-sm">My Profile</a>
        </div>
    </div>

    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <p class="text-muted mb-1">Total Orders</p>
                    <h4 class="mb-0">${orders.size()}</h4>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <p class="text-muted mb-1">Cart Items</p>
                    <h4 class="mb-0"><c:out value="${empty cartCount ? 0 : cartCount}" /></h4>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card shadow-sm h-100">
                <div class="card-body">
                    <p class="text-muted mb-1">Cart Total</p>
                    <h4 class="mb-0">Rs. <c:out value="${empty cartTotal ? 0 : cartTotal}" /></h4>
                </div>
            </div>
        </div>
    </div>

    <div class="row mb-4 align-items-center">
        <div class="col-md-6">
            <h4 class="mb-0">Explore Menu</h4>
        </div>
        <div class="col-md-6">
            <form action="${pageContext.request.contextPath}/user/dashboard" method="get" class="row g-2">
                <div class="col-sm-6">
                    <input type="text" name="search" class="form-control" placeholder="Search food..." value="${search}">
                </div>
                <div class="col-sm-4">
                    <select name="category" class="form-select">
                        <option value="">All Categories</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat}" ${cat == selectedCategory ? 'selected' : ''}>${cat}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-sm-2 d-grid">
                    <button class="btn btn-danger">Search</button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <c:if test="${empty products}">
            <div class="col-12">
                <div class="alert alert-info text-center">No products available right now.</div>
            </div>
        </c:if>

        <c:forEach var="product" items="${products}">
            <div class="col-12 col-sm-6 col-lg-4 mb-4">
                <div class="card h-100 product-card">
                    <c:choose>
                        <c:when test="${not empty product.imageName}">
                            <img src="${product.imageName}"
                                 class="card-img-top"
                                 style="height:200px; object-fit:cover;"
                                 onerror="this.onerror=null;this.src='https://via.placeholder.com/200x200?text=No+Image';">
                        </c:when>
                        <c:otherwise>
                            <img src="https://via.placeholder.com/200x200?text=No+Image"
                                 class="card-img-top"
                                 style="height:200px; object-fit:cover;">
                        </c:otherwise>
                    </c:choose>

                    <div class="card-body">
                        <div>
                            <h5 class="product-name">${product.name}</h5>
                            <span class="product-category">${product.category}</span>
                        </div>

                        <p class="text-muted small product-description">${product.description}</p>

                        <div class="product-actions">
                            <span class="product-price text-danger">Rs. ${product.price}</span>

                            <c:choose>
                                <c:when test="${product.stockQuantity == null || product.stockQuantity <= 0}">
                                    <span class="badge bg-danger">Out of Stock</span>
                                </c:when>
                                <c:when test="${product.stockQuantity <= 5}">
                                    <span class="badge bg-warning text-dark">Limited Availability</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-success">In Stock</span>
                                </c:otherwise>
                            </c:choose>

                            <div class="product-buy-actions">
                                <a href="${pageContext.request.contextPath}/products/${product.id}" class="btn btn-sm btn-outline-secondary me-1">Details</a>
                                <c:choose>
                                    <c:when test="${product.stockQuantity == null || product.stockQuantity <= 0}">
                                        <button type="button" class="btn btn-sm btn-secondary" disabled>Unavailable</button>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/user/cart/add/${product.id}" method="post" class="d-inline-flex">
                                            <input type="hidden" name="quantity" value="1">
                                            <button type="submit" class="btn btn-sm btn-danger">Order Now</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
