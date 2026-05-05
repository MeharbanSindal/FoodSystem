<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jsp" %>

<div class="container py-5">
    <div class="row justify-content-center">

        <div class="col-md-4">
            <div class="card p-4 shadow-sm">

                <h3 class="text-center mb-3">Login</h3>

                <!-- Message -->
                <c:if test="${msg != null}">
                    <div class="alert alert-info">${msg}</div>
                </c:if>

                <c:if test="${param.error != null}">
                    <div class="alert alert-danger">Invalid Email or Password</div>
                </c:if>

                <!-- Form -->
                <form action="${pageContext.request.contextPath}/authenticate" method="post">

                    <input type="text" name="username" class="form-control mb-2" placeholder="Email" required>

                    <input type="password" name="password" class="form-control mb-2" placeholder="Password" required>

                    <button class="btn btn-primary w-100">Login</button>
                </form>

                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/forgot-password">Forgot Password?</a>
                </div>

                <div class="text-center mt-2">
                    <a href="${pageContext.request.contextPath}/register">Create Account</a>
                </div>

            </div>
        </div>

    </div>
</div>

</body>
<%@ include file="../common/footer.jsp" %>