<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jsp" %>

<div class="container py-5">
    <div class="row justify-content-center">

        <div class="col-md-4">
            <div class="card p-4 shadow-sm">

                <h3 class="text-center mb-3">Forgot Password</h3>

                <!-- Messages -->
                <c:if test="${msg != null}">
                    <div class="alert alert-success">${msg}</div>
                </c:if>

                <c:if test="${error != null}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <!-- Form -->
                <form action="${pageContext.request.contextPath}/forgot-password" method="post">

                    <input type="email" name="email" class="form-control mb-3" placeholder="Enter your email" required>

                    <button class="btn btn-primary w-100">Send Reset Link</button>
                </form>

                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/login">Back to Login</a>
                </div>

            </div>
        </div>

    </div>
</div>

</body>
<%@ include file="../common/footer.jsp" %>