<%@ include file="../common/header.jsp" %>

<div class="container py-5">

    <h2 class="mb-4">My Profile</h2>

    <div class="card p-4 shadow-sm">

        <p><strong>Name:</strong> ${user.fullName}</p>
        <p><strong>Email:</strong> ${user.email}</p>
        <p><strong>Role:</strong> ${user.role}</p>

    </div>

    <div class="card p-4 shadow-sm mt-4">
        <h4 class="mb-3">Saved Delivery Addresses</h4>

        <form action="${pageContext.request.contextPath}/user/address/add" method="post" class="mb-4">
            <div class="row g-3">
                <div class="col-md-3">
                    <label class="form-label">Label</label>
                    <input type="text" name="label" class="form-control" placeholder="Home / Work / Hostel">
                </div>
                <div class="col-md-9">
                    <label class="form-label">Address Line</label>
                    <input type="text" name="addressLine" class="form-control" placeholder="House no, street, area" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">City</label>
                    <input type="text" name="city" class="form-control">
                </div>
                <div class="col-md-4">
                    <label class="form-label">State</label>
                    <input type="text" name="state" class="form-control">
                </div>
                <div class="col-md-4">
                    <label class="form-label">Pincode</label>
                    <input type="text" name="pincode" class="form-control">
                </div>
                <div class="col-md-8">
                    <label class="form-label">Landmark</label>
                    <input type="text" name="landmark" class="form-control" placeholder="Nearby known place">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Latitude</label>
                    <input type="text" name="latitude" class="form-control">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Longitude</label>
                    <input type="text" name="longitude" class="form-control">
                </div>
                <div class="col-12 d-flex align-items-center justify-content-between">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="defaultAddress" value="true" id="defaultAddress">
                        <label class="form-check-label" for="defaultAddress">
                            Set as default address
                        </label>
                    </div>
                    <button type="submit" class="btn btn-primary">Save Address</button>
                </div>
            </div>
        </form>

        <c:choose>
            <c:when test="${not empty addresses}">
                <div class="table-responsive">
                    <table class="table table-striped align-middle">
                        <thead>
                            <tr>
                                <th>Label</th>
                                <th>Address</th>
                                <th>Map</th>
                                <th>Default</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="addr" items="${addresses}">
                                <tr>
                                    <td>${addr.label != null ? addr.label : 'Address'}</td>
                                    <td>${addr.displayAddress}</td>
                                    <td>
                                        <c:if test="${addr.latitude != null && addr.longitude != null}">
                                            <a href="https://www.google.com/maps?q=${addr.latitude},${addr.longitude}" target="_blank" rel="noopener noreferrer">Open Map</a>
                                        </c:if>
                                        <c:if test="${addr.latitude == null || addr.longitude == null}">
                                            <span class="text-muted">No coords</span>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:if test="${addr.defaultAddress}">
                                            <span class="badge bg-success">Default</span>
                                        </c:if>
                                        <c:if test="${!addr.defaultAddress}">
                                            <form action="${pageContext.request.contextPath}/user/address/default/${addr.id}" method="post">
                                                <button type="submit" class="btn btn-outline-secondary btn-sm">Make Default</button>
                                            </form>
                                        </c:if>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/user/address/delete/${addr.id}" method="post" onsubmit="return confirm('Remove this address?');">
                                            <button type="submit" class="btn btn-outline-danger btn-sm">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <p class="text-muted mb-0">No saved addresses yet. Add one for faster checkout.</p>
            </c:otherwise>
        </c:choose>
    </div>

</div>

<%@ include file="../common/footer.jsp" %>