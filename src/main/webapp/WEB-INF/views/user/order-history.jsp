<%@ include file="../common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container py-5">

    <h2 class="mb-4">My Orders</h2>

    <div class="card p-4 shadow-sm">

        <table class="table">
            <thead>
                <tr>
                    <th>Order ID</th>
                    <th>Tracking</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th>Invoice</th>
                    <th>Track</th>
                    <th>Action</th>
                </tr>
            </thead>

            <tbody>
                <c:forEach var="ord" items="${orders}">
                    <tr>
                        <td>${ord.id}</td>
                        <td>${ord.trackingCode}</td>
                        <td>Rs. ${ord.totalAmount}</td>
                        <td>${ord.status}</td>

                        <td>
                            <a href="${pageContext.request.contextPath}/user/invoice/${ord.id}"
                               class="btn btn-sm btn-success">
                                Download
                            </a>
                        </td>

                        <td>
                            <a href="${pageContext.request.contextPath}/user/order/track/${ord.id}"
                               class="btn btn-sm btn-outline-primary">
                                Track
                            </a>
                        </td>

                        <td>
                            <form action="${pageContext.request.contextPath}/user/order/cancel/${ord.id}"
                                  method="post"
                                  onsubmit="return confirmCancel();">

                                <button class="btn btn-sm btn-danger">Cancel</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>

        </table>

    </div>

</div>

<script>
function confirmCancel() {
    return confirm("Are you sure? 80% refund will be processed.");
}
</script>

<%@ include file="../common/footer.jsp" %>