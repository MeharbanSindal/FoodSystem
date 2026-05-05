<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jsp" %>

<div class="container py-5">

    <h2 class="mb-4">Manage Products</h2>

    <!-- Product Form -->
    <div class="card p-4 mb-4 shadow-sm">
        <h5>${product.id != null ? 'Update Product' : 'Add New Product'}</h5>

        <form action="${pageContext.request.contextPath}/admin/product/save"
              method="post" enctype="multipart/form-data">

            <input type="hidden" name="id" value="${product.id}" />
            <input type="text" name="name" value="${product.name}" class="form-control mb-2" placeholder="Product Name" required>

            <textarea name="description" class="form-control mb-2" placeholder="Description">${product.description}</textarea>

            <textarea name="ingredients" class="form-control mb-2" placeholder="Ingredients">${product.ingredients}</textarea>

            <input type="number" step="0.01" name="price" value="${product.price}" class="form-control mb-2" placeholder="Price" required>

            <input type="number" min="0" name="stockQuantity" value="${product.stockQuantity}" class="form-control mb-2" placeholder="Stock Quantity" required>

            <input type="text" name="category" value="${product.category}" class="form-control mb-2" placeholder="Category">

            <input type="file" name="imageFile" class="form-control mb-2" accept="image/*">
            <c:if test="${not empty product.imageName}">
                <p class="small mb-2">Current image: ${product.imageName}</p>
            </c:if>

            <button class="btn btn-primary">${product.id != null ? 'Update Product' : 'Save Product'}</button>
            <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary ms-2">New Product</a>
        </form>
    </div>

    <!-- Product List -->
    <div class="card p-4 shadow-sm">

        <h5>All Products</h5>

        <div class="table-responsive mt-3">
            <table class="table product-table align-middle">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Ingredients</th>
                    <th>Price</th>
                    <th>Category</th>
                    <th>Stock</th>
                    <th>Image</th>
                    <th>Actions</th>
                </tr>
            </thead>

            <tbody>
                <c:forEach var="p" items="${products}">
                    <tr>
                        <td class="fw-semibold">${p.name}</td>
                        <td class="small text-muted description-cell" title="${p.description}">
                            <span class="product-description-full">${p.description}</span>
                        </td>
                        <td class="ingredients-cell small text-muted" title="${p.ingredients}">
                            <span class="product-ingredients-full">${p.ingredients}</span>
                        </td>
                        <td>Rs. ${p.price}</td>
                        <td>${p.category}</td>
                        <td>
                            <c:choose>
                                <c:when test="${p.stockQuantity == null || p.stockQuantity <= 0}">
                                    <span class="badge bg-danger">Out of Stock</span>
                                </c:when>
                                <c:when test="${p.stockQuantity <= 5}">
                                    <span class="badge bg-warning text-dark">Low (${p.stockQuantity})</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-success">${p.stockQuantity} in stock</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="image-cell">
                            <c:choose>
                                <c:when test="${not empty p.imageName}">
                                    <button type="button"
                                            class="admin-thumb-btn"
                                            data-bs-toggle="modal"
                                            data-bs-target="#productImageModal"
                                            data-image-src="${pageContext.request.contextPath}/resources/uploads/${p.imageName}"
                                            data-image-name="${p.name}">
                                        <img src="${pageContext.request.contextPath}/resources/uploads/${p.imageName}"
                                             alt="${p.name}"
                                             class="admin-product-thumb"
                                             style="width:58px;height:58px;object-fit:cover;"
                                             onerror="this.onerror=null;this.src='https://via.placeholder.com/80x80?text=No+Image';">
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <img src="https://via.placeholder.com/80x80?text=No+Image" alt="No Image" class="admin-product-thumb">
                                </c:otherwise>
                            </c:choose>
                            <div class="product-image-name text-truncate" title="${p.imageName}">${p.imageName}</div>
                        </td>
                        <td class="actions-cell">
                            <a href="${pageContext.request.contextPath}/admin/product/edit/${p.id}"
                               class="btn btn-sm btn-warning me-1">Edit</a>
                            <form action="${pageContext.request.contextPath}/admin/product/delete/${p.id}"
                                  method="post" style="display:inline;">
                                <button type="submit" class="btn btn-sm btn-danger"
                                        onclick="return confirm('Delete this product?');">
                                    Delete
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>

        </table>
        </div>

    </div>

</div>

<div class="modal fade" id="productImageModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="productImageModalTitle">Product Image</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body text-center">
                <img id="productImageModalPreview" class="admin-product-modal-img" alt="Product Image Preview">
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function () {
    var imageModal = document.getElementById('productImageModal');
    if (!imageModal) {
        return;
    }

    imageModal.addEventListener('show.bs.modal', function (event) {
        var triggerButton = event.relatedTarget;
        if (!triggerButton) {
            return;
        }

        var imageSrc = triggerButton.getAttribute('data-image-src');
        var imageName = triggerButton.getAttribute('data-image-name') || 'Product Image';

        var modalTitle = document.getElementById('productImageModalTitle');
        var modalImage = document.getElementById('productImageModalPreview');

        if (modalTitle) {
            modalTitle.textContent = imageName;
        }
        if (modalImage) {
            modalImage.src = imageSrc;
            modalImage.alt = imageName;
            modalImage.onerror = function () {
                modalImage.src = 'https://via.placeholder.com/500x300?text=No+Image';
            };
        }
    });
});
</script>

<%@ include file="../common/footer.jsp" %>