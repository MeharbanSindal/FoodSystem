<%@ include file="../common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container py-4">
    <h2 class="mb-3">Delivery Dashboard</h2>
    <p class="text-muted">Assigned orders and delivery destinations</p>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="card p-3 shadow-sm">
        <c:choose>
            <c:when test="${empty assignedOrders}">
                <div class="alert alert-info mb-0">No assigned orders yet.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-striped align-middle">
                        <thead>
                        <tr>
                            <th>Order</th>
                            <th>Tracking</th>
                            <th>Customer</th>
                            <th>Address</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="order" items="${assignedOrders}">
                            <tr>
                                <td>#${order.id}</td>
                                <td>${order.trackingCode}</td>
                                <td>${order.user.fullName}<br><small>${order.user.email}</small></td>
                                <td>
                                    <div>${order.deliveryAddress}</div>
                                    <c:if test="${order.deliveryLatitude != null && order.deliveryLongitude != null}">
                                        <a href="https://www.google.com/maps?q=${order.deliveryLatitude},${order.deliveryLongitude}" target="_blank" rel="noopener noreferrer">Navigate</a>
                                        <iframe
                                                src="https://maps.google.com/maps?q=${order.deliveryLatitude},${order.deliveryLongitude}&z=15&output=embed"
                                                width="230"
                                                height="120"
                                                style="border:0; display:block; margin-top:8px;"
                                                loading="lazy">
                                        </iframe>
                                    </c:if>
                                </td>
                                <td>${order.status}</td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/delivery/order/status" method="post" class="d-flex flex-column gap-2">
                                        <input type="hidden" name="orderId" value="${order.id}">
                                        <select name="status" class="form-select form-select-sm" required>
                                            <option value="OUT_FOR_DELIVERY" ${order.status == 'OUT_FOR_DELIVERY' ? 'selected' : ''}>Out for Delivery</option>
                                            <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                        </select>
                                        <input type="text" name="otp" class="form-control form-control-sm" placeholder="Enter customer OTP for Delivered">
                                        <button class="btn btn-sm btn-primary">Update</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
