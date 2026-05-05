<%@ include file="../common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container py-5">
    <h2 class="mb-4">My Cart</h2>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <c:choose>
        <c:when test="${not empty cartItems}">
            <div class="card p-4 shadow-sm mb-4">
                <table class="table align-middle">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Price</th>
                            <th>Quantity</th>
                            <th>Subtotal</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cartItems}">
                            <tr>
                                <td class="d-flex align-items-center">
                                    <img src="${item.productImage}"
                                         alt="${item.productName}"
                                         style="width: 70px; height: 70px; object-fit: cover; margin-right: 10px;">
                                    <div>
                                        <strong>${item.productName}</strong>
                                    </div>
                                </td>
                                <td>Rs. ${item.unitPrice}</td>
                                <td style="width: 140px;">
                                    <form action="${pageContext.request.contextPath}/user/cart/update" method="post" class="d-flex">
                                        <input type="hidden" name="productId" value="${item.productId}">
                                        <input type="number" name="quantity" value="${item.quantity}" min="1" class="form-control form-control-sm me-2">
                                        <button type="submit" class="btn btn-sm btn-primary">Update</button>
                                    </form>
                                </td>
                                <td>Rs. ${item.subTotal}</td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/user/cart/remove/${item.productId}" method="post">
                                        <button type="submit" class="btn btn-sm btn-danger">Remove</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="row">
                <div class="col-md-4 offset-md-8">
                    <div class="card p-4 shadow-sm">
                        <h5>Cart Total</h5>
                        <p class="fw-bold fs-4">Rs. ${cartTotal}</p>
                        <a href="${pageContext.request.contextPath}/user/checkout" class="btn btn-danger w-100">Proceed to Checkout</a>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary w-100 mt-2">Continue Shopping</a>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card p-4 shadow-sm text-center">
                <h5>Your cart is empty.</h5>
                <a href="${pageContext.request.contextPath}/" class="btn btn-dark mt-3">Continue Shopping</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="../common/footer.jsp" %>
