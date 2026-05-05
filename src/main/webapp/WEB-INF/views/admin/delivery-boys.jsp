<%@ include file="../common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container py-4">
    <h2 class="mb-3">Delivery Partner Management</h2>
    <p class="text-muted">Create and manage delivery accounts</p>

    <div class="row">
        <div class="col-md-5">
            <div class="card p-4 shadow-sm mb-3">
                <h5 class="mb-3">Add Delivery Partner</h5>

                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/admin/delivery-boys" method="post">
                    <input type="text" name="fullName" class="form-control mb-2" placeholder="Full name" required>
                    <input type="email" name="email" class="form-control mb-2" placeholder="Email" required>
                    <input type="tel" name="mobileNumber" class="form-control mb-2" placeholder="Mobile number" required>
                    <input type="password" name="password" class="form-control mb-3" placeholder="Temporary password" required>
                    <button class="btn btn-primary w-100">Create Delivery Account</button>
                </form>
            </div>
        </div>

        <div class="col-md-7">
            <div class="card p-4 shadow-sm">
                <h5 class="mb-3">Delivery Partners</h5>
                <c:choose>
                    <c:when test="${empty deliveryBoys}">
                        <div class="alert alert-info mb-0">No delivery partner found.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-striped">
                                <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Mobile</th>
                                    <th>Role</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="db" items="${deliveryBoys}">
                                    <tr>
                                        <td>${db.fullName}</td>
                                        <td>${db.email}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty db.mobileNumber}">${db.mobileNumber}</c:when>
                                                <c:otherwise><span class="text-muted">Not set</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${db.role}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
