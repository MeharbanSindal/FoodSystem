<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jsp" %>

<div class="container-fluid py-4">
    <div class="row mb-4">
        <div class="col-md-12">
            <h2>📦 Order Management</h2>
            <p class="text-muted">Manage customer orders, assign delivery, and track status</p>
        </div>
    </div>

    <!-- Orders Table -->
    <div class="card shadow-sm">
        <div class="card-header bg-light">
            <h5 class="mb-0">All Orders</h5>
        </div>

        <div class="card-body">
            <c:if test="${empty orders}">
                <div class="alert alert-info">No orders found yet.</div>
            </c:if>

            <c:if test="${not empty orders}">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>#Order ID</th>
                                <th>Customer Name</th>
                                <th>Email</th>
                                <th>Items</th>
                                <th>Amount</th>
                                <th>Status</th>
                                <th>Delivery Boy</th>
                                <th>Delivery Location</th>
                                <th>Payment</th>
                                <th>Order Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="order" items="${orders}">
                                <tr>
                                    <td><strong>#${order.id}</strong></td>
                                    <td>${order.user.fullName}</td>
                                    <td>${order.user.email}</td>
                                    <td>
                                        <c:if test="${not empty order.items}">
                                            ${order.items.size()} item(s)
                                        </c:if>
                                    </td>
                                    <td><strong>Rs. ${order.totalAmount}</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${order.status == 'PENDING'}">
                                                <span class="badge bg-warning">PENDING</span>
                                            </c:when>
                                            <c:when test="${order.status == 'CONFIRMED'}">
                                                <span class="badge bg-info">CONFIRMED</span>
                                            </c:when>
                                            <c:when test="${order.status == 'OUT_FOR_DELIVERY'}">
                                                <span class="badge bg-primary">OUT FOR DELIVERY</span>
                                            </c:when>
                                            <c:when test="${order.status == 'DELIVERED'}">
                                                <span class="badge bg-success">DELIVERED</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">${order.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty order.deliveryBoy}">
                                                ${order.deliveryBoy}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Not Assigned</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty order.deliveryAddress}">
                                                <div>${order.deliveryAddress}</div>
                                                <c:if test="${order.deliveryLatitude != null && order.deliveryLongitude != null}">
                                                    <a href="https://www.google.com/maps?q=${order.deliveryLatitude},${order.deliveryLongitude}" target="_blank" rel="noopener noreferrer">
                                                        Open Map
                                                    </a>
                                                </c:if>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Not provided</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${order.paymentMethod == 'UPI' || order.paymentMethod == 'ONLINE'}">
                                                <span class="badge bg-success">💳 ${order.paymentMethod}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">${order.paymentMethod}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${order.orderDate}</td>
                                    <td>
                                        <button type="button" class="btn btn-sm btn-outline-primary"
                                                data-bs-toggle="modal"
                                                data-bs-target="#updateOrderModal"
                                                data-order-id="${order.id}"
                                                data-order-status="${order.status}"
                                                data-delivery-boy="${order.deliveryBoy}"
                                                data-customer-name="${order.user.fullName}"
                                                data-customer-email="${order.user.email}">
                                            Update
                                        </button>
                                        <div id="orderItems-${order.id}" class="d-none">
                                            <c:choose>
                                                <c:when test="${not empty order.items}">
                                                    <ul class="mb-0">
                                                        <c:forEach var="item" items="${order.items}">
                                                            <li>${item.product.name} x${item.quantity} - Rs. ${item.price * item.quantity}</li>
                                                        </c:forEach>
                                                    </ul>
                                                </c:when>
                                                <c:otherwise>
                                                    <p class="mb-0">No items in this order</p>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>
    </div>
</div>

<!-- Single Reusable Update Modal -->
<div class="modal fade" id="updateOrderModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="updateOrderModalTitle">Update Order</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <form action="${pageContext.request.contextPath}/admin/order/update-status" method="post">
                <div class="modal-body">
                    <input type="hidden" name="orderId" id="modalOrderId">

                    <div class="mb-3">
                        <label class="form-label">Order Status</label>
                        <select name="status" id="modalStatus" class="form-select" required>
                            <option value="PENDING">⏳ Pending</option>
                            <option value="CONFIRMED">✅ Confirmed</option>
                            <option value="OUT_FOR_DELIVERY">🛵 Out for Delivery</option>
                            <option value="DELIVERED">🚚 Delivered</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Assign Delivery Boy</label>
                        <input type="text" name="deliveryBoy" id="modalDeliveryBoy" class="form-control"
                               placeholder="Select delivery partner email" list="deliveryBoyList">
                        <datalist id="deliveryBoyList">
                            <c:forEach var="db" items="${deliveryBoys}">
                                <option value="${db.email}">${db.fullName}</option>
                            </c:forEach>
                        </datalist>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Order Items</label>
                        <div class="alert alert-light" id="modalOrderItems"></div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Customer</label>
                        <p class="form-control-plaintext mb-0">
                            <strong id="modalCustomerName"></strong><br>
                            <span id="modalCustomerEmail"></span>
                        </p>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Update Order</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function () {
    var modal = document.getElementById('updateOrderModal');
    if (!modal) {
        return;
    }

    modal.addEventListener('show.bs.modal', function (event) {
        var button = event.relatedTarget;
        if (!button) {
            return;
        }

        var orderId = button.getAttribute('data-order-id') || '';
        var status = button.getAttribute('data-order-status') || 'PENDING';
        var deliveryBoy = button.getAttribute('data-delivery-boy') || '';
        var customerName = button.getAttribute('data-customer-name') || '';
        var customerEmail = button.getAttribute('data-customer-email') || '';

        document.getElementById('updateOrderModalTitle').textContent = 'Update Order #' + orderId;
        document.getElementById('modalOrderId').value = orderId;
        document.getElementById('modalStatus').value = status;
        document.getElementById('modalDeliveryBoy').value = deliveryBoy;
        document.getElementById('modalCustomerName').textContent = customerName;
        document.getElementById('modalCustomerEmail').textContent = customerEmail;

        var itemsSource = document.getElementById('orderItems-' + orderId);
        document.getElementById('modalOrderItems').innerHTML = itemsSource ? itemsSource.innerHTML : '<p class="mb-0">No items in this order</p>';
    });
});
</script>

<%@ include file="../common/footer.jsp" %>
