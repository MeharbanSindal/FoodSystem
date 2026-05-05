<%@ include file="../common/header.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container py-5">

    <h2 class="mb-4">Checkout</h2>

    <div class="row">

        <!-- Cart Items -->
        <div class="col-md-6">
            <div class="card p-4 shadow-sm">

                <h5>Cart Items</h5>

                <table class="table table-borderless mb-0">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Qty</th>
                            <th class="text-end">Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cartItems}">
                            <tr>
                                <td>${item.productName}</td>
                                <td>${item.quantity}</td>
                                <td class="text-end">Rs. ${item.subTotal}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

            </div>
        </div>

        <!-- Payment -->
        <div class="col-md-6">
            <div class="card p-4 shadow-sm">

                <h5>Order Summary</h5>

                <p><strong>Subtotal:</strong> Rs. ${order.subtotal}</p>
                <p><strong>GST:</strong> Rs. ${order.gst}</p>
                <p><strong>Delivery:</strong> Rs. ${order.deliveryCharge}</p>

                <hr>

                <h5>Total: Rs. ${order.totalAmount}</h5>

                <div class="mt-4">
                    <h5>Payment</h5>

                    <c:if test="${not empty savedAddresses}">
                        <label class="form-label">Use Saved Address</label>
                        <select id="savedAddressId" name="savedAddressId" class="form-control mb-3">
                            <option value="">Select saved address (optional)</option>
                            <c:forEach var="addr" items="${savedAddresses}">
                                <option value="${addr.id}"
                                        data-address="${addr.displayAddress}"
                                        data-lat="${addr.latitude}"
                                        data-lng="${addr.longitude}">
                                    ${addr.label != null ? addr.label : 'Address'} - ${addr.displayAddress}
                                    <c:if test="${addr.defaultAddress}">(Default)</c:if>
                                </option>
                            </c:forEach>
                        </select>
                    </c:if>

                    <c:if test="${qrCode != null}">
                        <img src="data:image/png;base64,${qrCode}" 
                             style="width:200px;" class="mb-3">
                    </c:if>

                    <form action="${pageContext.request.contextPath}/user/order/place" method="post" id="checkoutForm">
                        <input type="hidden" name="savedAddressId" id="savedAddressInput">
                        <label class="form-label">Delivery Address</label>
                        <textarea name="deliveryAddress" id="deliveryAddress" class="form-control mb-3" rows="3"
                                  placeholder="Enter complete delivery address (house no, street, city, landmark)" required></textarea>

                        <input type="hidden" name="deliveryLatitude" id="deliveryLatitude">
                        <input type="hidden" name="deliveryLongitude" id="deliveryLongitude">

                        <button type="button" class="btn btn-outline-secondary btn-sm mb-3" id="detectLocationBtn">
                            Use Current Location
                        </button>
                        <button type="button" class="btn btn-outline-dark btn-sm mb-3 ms-2" id="validateAddressBtn">
                            Validate Address on Map
                        </button>
                        <small id="locationStatus" class="d-block text-muted mb-3"></small>

                        <select name="paymentMethod" id="paymentMethod" class="form-control mb-3" required>
                            <option value="">Select Payment Method</option>
                            <option value="COD">Cash on Delivery (COD)</option>
                            <option value="QR">Pay via QR</option>
                            <option value="RAZORPAY">Razorpay (Card/UPI/NetBanking)</option>
                        </select>

                        <button type="submit" class="btn btn-primary w-100" id="placeOrderBtn">Place Order</button>
                    </form>
                </div>

            </div>
        </div>

    </div>

</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    var detectLocationBtn = document.getElementById('detectLocationBtn');
    var validateAddressBtn = document.getElementById('validateAddressBtn');
    var latInput = document.getElementById('deliveryLatitude');
    var lngInput = document.getElementById('deliveryLongitude');
    var locationStatus = document.getElementById('locationStatus');
    var checkoutForm = document.getElementById('checkoutForm');
    var paymentMethod = document.getElementById('paymentMethod');
    var savedAddressSelect = document.getElementById('savedAddressId');
    var savedAddressInput = document.getElementById('savedAddressInput');
    var deliveryAddressInput = document.getElementById('deliveryAddress');

    if (!detectLocationBtn) {
        return;
    }

    detectLocationBtn.addEventListener('click', function() {
        if (!navigator.geolocation) {
            locationStatus.textContent = 'Geolocation not supported in this browser.';
            return;
        }

        locationStatus.textContent = 'Detecting location...';

        navigator.geolocation.getCurrentPosition(function(position) {
            latInput.value = position.coords.latitude;
            lngInput.value = position.coords.longitude;
            locationStatus.textContent = 'Location captured successfully.';
        }, function() {
            locationStatus.textContent = 'Unable to fetch location. You can continue with address only.';
        }, {
            enableHighAccuracy: true,
            timeout: 10000
        });
    });

    if (savedAddressSelect) {
        savedAddressSelect.addEventListener('change', function() {
            var selected = this.options[this.selectedIndex];
            if (!selected || !selected.value) {
                savedAddressInput.value = '';
                return;
            }

            savedAddressInput.value = selected.value;
            var selectedAddress = selected.getAttribute('data-address');
            var selectedLat = selected.getAttribute('data-lat');
            var selectedLng = selected.getAttribute('data-lng');

            if (selectedAddress) {
                deliveryAddressInput.value = selectedAddress;
            }
            if (selectedLat) {
                latInput.value = selectedLat;
            }
            if (selectedLng) {
                lngInput.value = selectedLng;
            }
        });
    }

    if (validateAddressBtn) {
        validateAddressBtn.addEventListener('click', function() {
            var rawAddress = (deliveryAddressInput.value || '').trim();
            if (!rawAddress) {
                locationStatus.textContent = 'Please enter delivery address first.';
                return;
            }

            locationStatus.textContent = 'Validating address...';

            var endpoint = 'https://nominatim.openstreetmap.org/search?format=json&limit=1&q=' + encodeURIComponent(rawAddress);
            fetch(endpoint, {
                headers: {
                    'Accept': 'application/json'
                }
            })
            .then(function(response) { return response.json(); })
            .then(function(data) {
                if (!data || !data.length) {
                    locationStatus.textContent = 'Address validation failed. Please provide a more complete address.';
                    return;
                }

                var best = data[0];
                latInput.value = best.lat;
                lngInput.value = best.lon;
                deliveryAddressInput.value = best.display_name;
                locationStatus.innerHTML = 'Address validated. <a href="https://www.google.com/maps?q=' + best.lat + ',' + best.lon + '" target="_blank" rel="noopener noreferrer">Open in Google Maps</a>';
            })
            .catch(function() {
                locationStatus.textContent = 'Could not validate address right now. Try again in a moment.';
            });
        });
    }

    if (checkoutForm) {
        checkoutForm.addEventListener('submit', function(e) {
            if (paymentMethod && paymentMethod.value === 'RAZORPAY') {
                // Keep checkout stable until live Razorpay keys are configured.
                e.preventDefault();
                alert('Razorpay setup pending. Add valid razorpay.key.id and razorpay.key.secret to enable this option.');
            }
        });
    }
});
</script>

<%@ include file="../common/footer.jsp" %>