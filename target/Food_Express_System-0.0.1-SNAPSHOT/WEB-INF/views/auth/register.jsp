<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../common/header.jsp" %>

<div class="container py-5">
    <div class="row justify-content-center">

        <div class="col-md-5">
            <div class="card p-4 shadow-sm">

                <h3 class="text-center mb-3">Register</h3>

                <!-- Error -->
                <c:if test="${error != null}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <!-- Form -->
                <form action="${pageContext.request.contextPath}/register" method="post">

                    <input type="text" name="fullName" class="form-control mb-2" placeholder="Full Name" required>

                    <input type="email" name="email" class="form-control mb-2" placeholder="Email" required>

                    <input type="password" name="password" class="form-control mb-2" placeholder="Password" required>

                    <button class="btn btn-primary w-100">Register</button>
                </form>

                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/login">Already have account? Login</a>
                </div>

            </div>
        </div>

    </div>
</div>

</body>
<%@ include file="../common/footer.jsp" %>