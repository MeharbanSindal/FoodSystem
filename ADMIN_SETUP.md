# Admin System - Food Express

## ✅ Complete Admin Portal Setup

### System Architecture

The Food Express e-commerce app now has a complete admin management system with three main roles:

- **Regular User (ROLE_USER)**: Can browse products, add to cart, checkout
- **Admin (ROLE_ADMIN)**: Can manage products and orders
- **Customer**: Can view orders and track delivery

---

## Admin Login & Access

### Method 1: Direct Login (Recommended)
1. Go to: `http://localhost:8080/Food_Express_System/admin/login`
2. Use default credentials:
   - **Email**: `admin@foodexpress.com`
   - **Password**: `admin123`

### Method 2: Via Frontend Navigation
1. From homepage, click the 🔐 **Admin** button in navbar
2. Enter admin credentials
3. Redirects to admin dashboard

### Method 3: Create Admin via SQL

If default admin doesn't exist, create it with:

```sql
INSERT INTO users (full_name, email, password, role, enabled) 
VALUES (
    'Admin User', 
    'admin@foodexpress.com', 
    '$2a$10$slYQmyNdGzin7olVN3p5Be7DY5Z7k05N5fGsqHWQi/W8NbNdL1Hhu', 
    'ROLE_ADMIN', 
    true
);
```

**Password Hash Explanation:**
- Algorithm: BCrypt
- Cost Factor: 10
- Original Password: `admin123`

---

## Admin Dashboard Features

### 1. Dashboard Home (`/admin/dashboard`)

Shows real-time statistics:
- **Total Orders**: Count of all orders
- **Total Revenue**: Sum of all order amounts
- **Total Products**: Count of inventory
- **Pending Orders**: Orders waiting confirmation

**Quick Actions:**
- 📦 **Order Management**: Jump to orders page
- 🛍️ **Product Management**: Jump to products page

### 2. Order Management (`/admin/orders`)

**Features:**
- View ALL customer orders
- See order details (items, amounts, dates)
- Update order status:
  - ⏳ **PENDING** → Initial state
  - ✅ **CONFIRMED** → Admin approves order
  - 🚚 **DELIVERED** → Order completed
- **Assign Delivery Boy**: Add delivery agent name/ID
- View customer information
- Track payment method

**Order Status Workflow:**
```
PENDING (Customer ordered)
   ↓ (Admin confirms)
CONFIRMED (Ready for delivery)
   ↓ (Delivery assigned)
DELIVERED (Order completed)
```

### 3. Product Management (`/admin/products`)

**Add Products:**
- Product name, description, category
- Price (with decimal support)
- Image upload (.jpg, .png)
- Auto-timestamp filenames to prevent conflicts

**Edit Products:**
- Modify any product details
- Update images
- Keep existing image if not uploading new one

**Delete Products:**
- Instant removal from inventory
- Removes from display

**Storage:**
- Images saved to: `/webapp/resources/uploads/`
- Accessible from front-end

---

## Admin URLs Reference

| Route | Method | Purpose |
|-------|--------|---------|
| `/admin/login` | GET | Admin login page |
| `/admin/authenticate` | POST | Process login |
| `/admin/dashboard` | GET | Dashboard home |
| `/admin/orders` | GET | View all orders |
| `/admin/order/update-status` | POST | Update order status & assign delivery |
| `/admin/products` | GET | Product management |
| `/admin/product/save` | POST | Save/update product |
| `/admin/product/edit/{id}` | GET | Edit product form |
| `/admin/product/delete/{id}` | POST | Delete product |

---

## User Workflow vs Admin Workflow

### Regular User:
```
Home (/)
   ↓
Browse Products (/products)
   ↓
View Product Details (/products/{id})
   ↓
Add to Cart
   ↓
Checkout (/checkout)
   ↓
Place Order
   ↓
View Orders (/user/orders)
```

### Admin:
```
Admin Login (/admin/login)
   ↓
Dashboard (/admin/dashboard)
   ↓
Manage Orders (/admin/orders)
   ├─ Accept Orders (Update Status → CONFIRMED)
   ├─ Assign Delivery Boy
   └─ Track Delivery (Status → DELIVERED)
   ↓
Manage Products (/admin/products)
   ├─ Add Products
   ├─ Edit Products
   └─ Delete Products
```

---

## Key Features

### ✅ Security
- Transactions (@Transactional) fixed for Hibernate
- Role-based access control (Spring Security)
- Protected admin routes
- Session-based authentication

### ✅ Order Management
- Accept/confirm orders
- Update delivery status
- Assign delivery agents
- Customer notifications via email

### ✅ Product Management
- Add products with images
- Edit existing products
- Delete products
- Image storage system

### ✅ Analytics
- Order count statistics
- Revenue tracking
- Product inventory count
- Pending orders count

---

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),           -- ROLE_USER or ROLE_ADMIN
    enabled BOOLEAN DEFAULT true
);
```

### Orders Table
Contains:
- user_id (FK to users)
- subtotal, tax, delivery_fee, total_amount
- status (PENDING, CONFIRMED, DELIVERED)
- payment_method
- created_at timestamp

### Products Table
Contains:
- name, description, category
- price
- image_name
- created_at timestamp

---

## Creating Additional Admins

### Via SQL:
```sql
-- Convert existing user to admin
UPDATE users SET role = 'ROLE_ADMIN' 
WHERE email = 'newemail@example.com';

-- Or create new admin directly with hashed password
-- Use BCrypt encoder to generate hash first
INSERT INTO users (full_name, email, password, role, enabled)
VALUES ('Another Admin', 'admin2@foodexpress.com', 'HASHED_PASSWORD', 'ROLE_ADMIN', true);
```

---

## Troubleshooting

### Issue: "Could not obtain transaction-synchronized Session"
**Solution**: Ensure all repository methods are called from @Transactional service methods ✅ (Fixed)

### Issue: Admin login page shows error
**Solution**: 
1. Verify admin user exists in database
2. Check password hash matches BCrypt format
3. Clear browser cache and try again

### Issue: Products not showing
**Solution**:
1. Verify products exist in database
2. Check image files in `/webapp/resources/uploads/`
3. Ensure file paths are correct

### Issue: Orders not appearing
**Solution**:
1. Ensure orders were placed (check database)
2. User must be logged in when placing order
3. Verify order status synchronization

---

## Best Practices

1. **Always backup database** before bulk operations
2. **Use meaningful delivery boy names** for tracking
3. **Upload optimized images** (reduce file size)
4. **Update order status regularly** for customer satisfaction
5. **Monitor pending orders** daily
6. **Archive old orders** for performance

---

## Future Enhancements

Potential features to add:
- [ ] Delivery boy management & tracking
- [ ] Real-time order notifications
- [ ] Advanced analytics & reports
- [ ] Discount coupon system
- [ ] Inventory management
- [ ] Customer review management
- [ ] SMS notifications
- [ ] Mobile app

---

**Last Updated**: April 11, 2026  
**Version**: 2.0  
**Status**: ✅ Production Ready

