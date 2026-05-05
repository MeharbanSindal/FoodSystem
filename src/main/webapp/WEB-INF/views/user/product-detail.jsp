<%@ include file="../common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container py-5">
    <div class="row">
        <div class="col-md-6">
            <div class="product-detail-hero shadow-sm">
                <div class="card product-detail-card shadow-sm">
                <img src="${pageContext.request.contextPath}/resources/uploads/${product.imageName}"
                     class="card-img-top" style="height: 450px; object-fit: cover;">
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card p-4 shadow-sm product-detail-card">
                <div class="mb-3">
                    <span class="product-detail-badge">${product.category}</span>
                </div>

                <h2 class="mb-3">${product.name}</h2>
                <p>${product.description}</p>

                <c:if test="${not empty product.ingredients}">
                    <div class="product-detail-section mb-3">
                        <strong class="d-block mb-2">Ingredients</strong>
                        <div class="product-detail-ingredients">${product.ingredients}</div>
                    </div>
                </c:if>

                <div class="my-3">
                    <span class="product-detail-price text-danger">Rs. ${product.price}</span>
                </div>

                <div class="mb-3">
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
                </div>

                <c:choose>
                    <c:when test="${product.stockQuantity == null || product.stockQuantity <= 0}">
                        <button type="button" class="btn btn-secondary" disabled>Currently Unavailable</button>
                    </c:when>
                    <c:otherwise>
                        <form action="${pageContext.request.contextPath}/user/cart/add/${product.id}" method="post" class="d-flex align-items-center flex-wrap gap-2">
                            <input type="number" name="quantity" value="1" min="1" class="form-control me-3" style="width: 100px;">
                            <button type="submit" class="btn btn-danger">Order Now</button>
                        </form>
                    </c:otherwise>
                </c:choose>

                <a href="${pageContext.request.contextPath}/products" class="btn btn-link mt-3">Back to shop</a>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
