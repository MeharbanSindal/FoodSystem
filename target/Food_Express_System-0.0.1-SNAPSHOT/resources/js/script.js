// Dynamic UI elements and validation
document.addEventListener("DOMContentLoaded", function() {
    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });
});

function confirmCancel() {
    return confirm("Are you sure? Note: Only 80% refund will be processed as per policy.");
}