<%@ include file="../common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container py-5">
    <h2 class="mb-4">Track Order #${order.id}</h2>

    <div class="card p-4 shadow-sm mb-4">
        <div class="row">
            <div class="col-md-6 mb-3">
                <p class="mb-1"><strong>Tracking Code:</strong></p>
                <p>${order.trackingCode}</p>
            </div>
            <div class="col-md-6 mb-3">
                <p class="mb-1"><strong>Status:</strong></p>
                <p>${order.status}</p>
            </div>
            <div class="col-md-6 mb-3">
                <p class="mb-1"><strong>Delivery Partner:</strong></p>
                <p>
                    <c:choose>
                        <c:when test="${not empty deliveryPartner}">
                            ${deliveryPartner.fullName}<br>
                            <small class="text-muted">
                                Mobile:
                                <c:choose>
                                    <c:when test="${not empty deliveryPartner.mobileNumber}">${deliveryPartner.mobileNumber}</c:when>
                                    <c:otherwise>Not available</c:otherwise>
                                </c:choose>
                            </small>
                        </c:when>
                        <c:otherwise>Not assigned yet</c:otherwise>
                    </c:choose>
                </p>
            </div>
            <div class="col-md-6 mb-3">
                <p class="mb-1"><strong>Delivery OTP:</strong></p>
                <p>
                    <c:choose>
                        <c:when test="${order.status == 'OUT_FOR_DELIVERY' && not empty order.deliveryOtp}">
                            <strong>${order.deliveryOtp}</strong> (Share with delivery partner)
                        </c:when>
                        <c:otherwise>Available once order is out for delivery</c:otherwise>
                    </c:choose>
                </p>
            </div>
            <div class="col-md-6 mb-3">
                <p class="mb-1"><strong>Delivery Address:</strong></p>
                <p>
                    <c:choose>
                        <c:when test="${not empty order.deliveryAddress}">${order.deliveryAddress}</c:when>
                        <c:otherwise>Not provided</c:otherwise>
                    </c:choose>
                </p>
                <c:if test="${order.deliveryLatitude != null && order.deliveryLongitude != null}">
                    <iframe
                            src="https://maps.google.com/maps?q=${order.deliveryLatitude},${order.deliveryLongitude}&z=15&output=embed"
                            width="100%"
                            height="180"
                            style="border:0;"
                            loading="lazy">
                    </iframe>
                    <div class="mt-3">
                        <button type="button" class="btn btn-outline-primary btn-sm" id="detectMyLocationBtn">Use My Current Location</button>
                        <p class="mt-2 mb-1"><strong>Approx. distance to delivery point:</strong></p>
                        <p id="distanceText" class="mb-0 text-muted">Click the button to calculate distance from your current location.</p>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <div class="card p-4 shadow-sm mb-4">
        <h5 class="mb-3">Order Progress</h5>
        <ul class="list-group list-group-flush">
            <li class="list-group-item d-flex justify-content-between">
                <span>Order Placed</span>
                <span class="badge bg-success">Done</span>
            </li>
            <li class="list-group-item d-flex justify-content-between">
                <span>Order Confirmed</span>
                <span class="badge ${order.status == 'CONFIRMED' || order.status == 'OUT_FOR_DELIVERY' || order.status == 'DELIVERED' ? 'bg-success' : 'bg-secondary'}">
                    ${order.status == 'CONFIRMED' || order.status == 'OUT_FOR_DELIVERY' || order.status == 'DELIVERED' ? 'Done' : 'Pending'}
                </span>
            </li>
            <li class="list-group-item d-flex justify-content-between">
                <span>Out for Delivery</span>
                <span class="badge ${order.status == 'OUT_FOR_DELIVERY' || order.status == 'DELIVERED' ? 'bg-success' : 'bg-secondary'}">
                    ${order.status == 'OUT_FOR_DELIVERY' || order.status == 'DELIVERED' ? 'Done' : 'Pending'}
                </span>
            </li>
            <li class="list-group-item d-flex justify-content-between">
                <span>Delivered</span>
                <span class="badge ${order.status == 'DELIVERED' ? 'bg-success' : 'bg-secondary'}">
                    ${order.status == 'DELIVERED' ? 'Done' : 'Pending'}
                </span>
            </li>
        </ul>
    </div>

    <a href="${pageContext.request.contextPath}/user/orders" class="btn btn-outline-secondary">Back to Orders</a>
</div>

<c:if test="${order.deliveryLatitude != null && order.deliveryLongitude != null}">
<script>
document.addEventListener('DOMContentLoaded', function () {
    var detectButton = document.getElementById('detectMyLocationBtn');
    var distanceText = document.getElementById('distanceText');

    if (!detectButton || !distanceText) {
        return;
    }

    var destinationLat = ${order.deliveryLatitude};
    var destinationLng = ${order.deliveryLongitude};

    function toRad(value) {
        return value * Math.PI / 180;
    }

    function calculateDistanceKm(lat1, lon1, lat2, lon2) {
        var earthRadiusKm = 6371;
        var dLat = toRad(lat2 - lat1);
        var dLon = toRad(lon2 - lon1);
        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    detectButton.addEventListener('click', function () {
        if (!navigator.geolocation) {
            distanceText.textContent = 'Geolocation is not supported in this browser.';
            return;
        }

        distanceText.textContent = 'Detecting your current location...';

        navigator.geolocation.getCurrentPosition(function (position) {
            var currentLat = position.coords.latitude;
            var currentLng = position.coords.longitude;
            var distanceKm = calculateDistanceKm(currentLat, currentLng, destinationLat, destinationLng);
            distanceText.textContent = 'Your current location is approximately ' + distanceKm.toFixed(2) + ' km away from the delivery point.';
        }, function () {
            distanceText.textContent = 'Unable to access your location. Allow location access and try again.';
        }, {
            enableHighAccuracy: true,
            timeout: 10000
        });
    });
});
</script>
</c:if>

<%@ include file="../common/footer.jsp" %>
