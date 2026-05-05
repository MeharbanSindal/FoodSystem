<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jsp" %>

<!-- Chart.js Library -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
<div class="container-fluid py-4">

    <h2 class="mb-4">👨‍💼 Admin Dashboard</h2>

    <!-- Stats -->
    <div class="row mb-4">
        <div class="col-md-3">
            <div class="card p-3 shadow-sm border-left border-primary">
                <div class="d-flex justify-content-between">
                    <div>
                        <h6 class="text-muted">Total Orders</h6>
                        <h3 class="mb-0">${totalOrders != null ? totalOrders : 0}</h3>
                    </div>
                    <div style="font-size: 2.5rem; color: #0d6efd;">📦</div>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card p-3 shadow-sm border-left border-success">
                <div class="d-flex justify-content-between">
                    <div>
                        <h6 class="text-muted">Total Revenue</h6>
                        <h3 class="mb-0">Rs. ${revenue != null ? revenue : 0}</h3>
                    </div>
                    <div style="font-size: 2.5rem; color: #198754;">💰</div>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card p-3 shadow-sm border-left border-info">
                <div class="d-flex justify-content-between">
                    <div>
                        <h6 class="text-muted">Total Products</h6>
                        <h3 class="mb-0">${productsCount != null ? productsCount : 0}</h3>
                    </div>
                    <div style="font-size: 2.5rem; color: #0dcaf0;">🛍️</div>
                </div>
            </div>
        </div>

        <div class="col-md-3">
            <div class="card p-3 shadow-sm border-left border-warning">
                <div class="d-flex justify-content-between">
                    <div>
                        <h6 class="text-muted">Pending Orders</h6>
                        <h3 class="mb-0">${pendingOrdersCount != null ? pendingOrdersCount : 0}</h3>
                    </div>
                    <div style="font-size: 2.5rem; color: #ffc107;">⏳</div>
                </div>
            </div>
        </div>
    </div>

    <!-- Quick Actions -->

    <div class="row mb-4">
        <div class="col-md-6">
            <div class="card p-3 shadow-sm border-left border-warning">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-muted mb-1">Low Stock Alerts</h6>
                        <h4 class="mb-0 text-warning">${lowStockCount != null ? lowStockCount : 0}</h4>
                    </div>
                    <div style="font-size: 2rem;">⚠️</div>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card p-3 shadow-sm border-left border-danger">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-muted mb-1">Out of Stock Items</h6>
                        <h4 class="mb-0 text-danger">${outOfStockCount != null ? outOfStockCount : 0}</h4>
                    </div>
                    <div style="font-size: 2rem;">🚫</div>
                </div>
            </div>
        </div>
    </div>

        <!-- Charts Row -->
        <div class="row mb-4">
            <div class="col-md-6">
                <div class="card p-4 shadow-sm">
                    <h5>Revenue Trend (Last 6 Months)</h5>
                    <canvas id="revenueChart"></canvas>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card p-4 shadow-sm">
                    <h5>Orders by Status</h5>
                    <canvas id="statusChart" style="max-height: 300px;"></canvas>
                </div>
            </div>
        </div>
    <div class="row mb-4">
        <div class="col-md-6">
            <div class="card p-4 shadow-sm" style="border-left: 5px solid #0d6efd;">
                <h5>📦 Order Management</h5>
                <p class="text-muted small">Accept orders, update status, and assign delivery boys</p>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-primary btn-sm me-2">
                    ➜ Manage Orders
                </a>
                <a href="${pageContext.request.contextPath}/admin/delivery-boys" class="btn btn-outline-primary btn-sm">
                    Delivery Partners
                </a>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card p-4 shadow-sm" style="border-left: 5px solid #198754;">
                <h5>🛍️ Product Management</h5>
                <p class="text-muted small">Add, edit, or remove items from inventory</p>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-success btn-sm">
                    ➜ Manage Products
                </a>
            </div>
        </div>
    </div>

    <!-- Recent Orders -->

    <c:if test="${not empty lowStockProducts}">
        <div class="card shadow-sm mb-4">
            <div class="card-header bg-warning-subtle">
                <h5 class="mb-0">Low Stock Products (Restock Needed)</h5>
            </div>
            <div class="table-responsive">
                <table class="table table-sm mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>Product</th>
                            <th>Category</th>
                            <th>Available Stock</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="lp" items="${lowStockProducts}">
                            <tr>
                                <td>${lp.name}</td>
                                <td>${lp.category}</td>
                                <td><span class="badge bg-warning text-dark">${lp.stockQuantity}</span></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/product/edit/${lp.id}" class="btn btn-sm btn-outline-primary">Restock</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

    <div class="card shadow-sm">
        <div class="card-header bg-light">
            <h5 class="mb-0">📋 Recent Orders</h5>
        </div>

        <div class="table-responsive">
            <table class="table table-hover mb-0">
                <thead class="table-light">
                    <tr>
                        <th>#Order ID</th>
                        <th>Customer</th>
                        <th>Amount</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>
                    <c:choose>
                        <c:when test="${not empty recentOrders}">
                            <c:forEach var="ord" items="${recentOrders}">
                                <tr>
                                    <td><strong>#${ord.id}</strong></td>
                                    <td>${ord.user.fullName}</td>
                                    <td><strong>Rs. ${ord.totalAmount}</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${ord.status == 'PENDING'}">
                                                <span class="badge bg-warning">PENDING</span>
                                            </c:when>
                                            <c:when test="${ord.status == 'CONFIRMED'}">
                                                <span class="badge bg-info">CONFIRMED</span>
                                            </c:when>
                                            <c:when test="${ord.status == 'DELIVERED'}">
                                                <span class="badge bg-success">DELIVERED</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">${ord.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/orders" 
                                           class="btn btn-sm btn-outline-primary">
                                            View All
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5" class="text-center text-muted py-4">
                                    No orders found yet
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    loadRevenueChart();
    loadStatusChart();
});

function loadRevenueChart() {
    fetch('${pageContext.request.contextPath}/admin/api/analytics/revenue-trend')
        .then(response => response.json())
        .then(data => {
            const ctx = document.getElementById('revenueChart');
            if (!ctx) return;

            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: data.labels,
                    datasets: [{
                        label: 'Revenue (Rs.)',
                        data: data.data,
                        borderColor: '#ff4d4d',
                        backgroundColor: 'rgba(255, 77, 77, 0.15)',
                        borderWidth: 3,
                        fill: true,
                        tension: 0.35,
                        pointBackgroundColor: '#ff4d4d',
                        pointRadius: 4
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: true
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        })
        .catch(error => {
            console.error('Error loading revenue chart:', error);
        });
}

function loadStatusChart() {
    fetch('${pageContext.request.contextPath}/admin/api/analytics/orders-by-status')
        .then(response => response.json())
        .then(data => {
            const ctx = document.getElementById('statusChart');
            if (!ctx) return;

            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: data.labels,
                    datasets: [{
                        data: data.data,
                        backgroundColor: data.backgroundColor,
                        borderWidth: 2,
                        borderColor: '#fff'
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'bottom'
                        }
                    }
                }
            });
        })
        .catch(error => {
            console.error('Error loading status chart:', error);
        });
}
</script>

</body>
<%@ include file="../common/footer.jsp" %>