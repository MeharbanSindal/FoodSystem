# Food Express - Fixed 404 Error - Redeployment Guide

## What Was Fixed

The 404 error was caused by:
1. **Missing web.xml** - Created proper Jakarta EE6 web.xml with explicit DispatcherServlet configuration
2. **MvcConfig enhancement** - Added DefaultServletHandlerConfigurer for proper resource handling
3. **AppConfig scanning** - Enhanced component scanning for services and DAOs
4. **Spring Security integration** - Properly configured DelegatingFilterProxy for security

---

## Redeployment Steps

### Step 1: Stop Tomcat
```bash
# Windows
catalina.bat stop

# Or via Services
Stop-Service Tomcat10
```

### Step 2: Remove Old Deployment
```bash
# Delete old WAR and extracted folder
Remove-Item "C:\Program Files\Apache Tomcat 10.1\webapps\Food_Express_System*" -Recurse -Force
```

### Step 3: Clean and Build
```bash
cd C:\Users\ss\Documents\PRACTICE\MPIF_ITEP_16\Spring\Spring_Project

# Using Maven (if installed)
mvn clean package

# Or build as WAR manually through IDE
```

### Step 4: Deploy New WAR
```bash
# Copy WAR to Tomcat webapps directory
Copy-Item "target/Food_Express_System-0.0.1-SNAPSHOT.war" `
    "C:\Program Files\Apache Tomcat 10.1\webapps\"
```

### Step 5: Start Tomcat
```bash
# Windows
catalina.bat start

# Or via Services
Start-Service Tomcat10
```

### Step 6: Verify Deployment
Wait 30-60 seconds for Tomcat to extract and deploy the WAR, then test:

```
http://localhost:8080/Food_Express_System/
```

---

## If Still Getting 404

### Check Tomcat Logs
```bash
# View latest log
Get-Content "C:\Program Files\Apache Tomcat 10.1\logs\catalina.log" -Tail 50
```

### Look for Errors
- ✅ Should see: "DispatcherServlet initialized"
- ✅ Should see: "Tomcat started"
- ❌ If you see: "ServletException" or "BeanCreationException", note the error

### Verify Files
```bash
# Check if WAR was extracted
Test-Path "C:\Program Files\Apache Tomcat 10.1\webapps\Food_Express_System"

# Check if web.xml is present
Test-Path "C:\Program Files\Apache Tomcat 10.1\webapps\Food_Express_System\WEB-INF\web.xml"

# Check WEB-INF contents
Get-ChildItem "C:\Program Files\Apache Tomcat 10.1\webapps\Food_Express_System\WEB-INF"
```

### Verify Database Connection
```bash
# Test MySQL connection
mysql -u root -proot -e "USE food_express_db; SELECT COUNT(*) FROM users;"
```

---

## Files Modified for 404 Fix

1. **NEW:** `src/main/webapp/WEB-INF/web.xml` - Explicit servlet config
2. **UPDATED:** `src/main/java/com/foodexpress/config/MvcConfig.java` - Added DefaultServletHandlerConfigurer
3. **UPDATED:** `src/main/java/com/foodexpress/config/AppConfig.java` - Enhanced component scanning
4. **UPDATED:** `src/main/java/com/foodexpress/config/SecurityConfig.java` - Added DaoAuthenticationProvider
5. **CREATED:** `src/main/java/com/foodexpress/service/CustomUserDetailsService.java` - User authentication

---

## Verification Checklist After Redeployment

- [ ] Application deploys without errors
- [ ] Home page loads: `http://localhost:8080/Food_Express_System/`
- [ ] Shows list of products from database
- [ ] Login page accessible: `http://localhost:8080/Food_Express_System/login`
- [ ] Admin login works with admin@foodexpress.com / admin123
- [ ] User registration works
- [ ] All panels accessible

---

## Quick Tomcat Location Reference

```
Installation: C:\Program Files\Apache Tomcat 10.1
Webapps:     C:\Program Files\Apache Tomcat 10.1\webapps
Logs:        C:\Program Files\Apache Tomcat 10.1\logs
Config:      C:\Program Files\Apache Tomcat 10.1\conf
```

---

## Need to Find Tomcat?

```powershell
# Find Tomcat installation
Get-ChildItem "C:\Program Files\" -Filter "*Tomcat*" -Directory

# Or check running services
Get-Service | Where-Object {$_.DisplayName -like "*Tomcat*"}

# Or check Tomcat port
netstat -ano | findstr :8080
```

---

**After redeployment, the 404 error should be resolved and all panels should work properly!**
