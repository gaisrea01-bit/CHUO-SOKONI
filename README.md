# Chuo Sokoni — Digital Marketplace for Student Entrepreneurship, Investment and Sales

> **"Biashara yako, fursa yako"** — Connecting Students, Empowering Entrepreneurs, Building the Future.

**Chuo Sokoni** is a native Android digital marketplace platform specifically engineered for Tanzanian higher-education campuses. It connects student entrepreneurs, customer buyers, campus growth agents, and wholesale investors in a unified digital ecosystem.

---

## 📱 Application Architecture & 70-Screen UI Catalog

The frontend is built using native Android (Java 11) and comprises **70 distinct fragment screens** grouped across 6 portals:

### 1. Shared / Public Portal (Screens 01 – 09)
- **01: Splash / Landing** — Brand introduction with full-screen campus background and circular logo badge.
- **02: Registration** — Account creation form for student customers, sellers, investors, and agents.
- **03: Referral Code Entry** — Agent referral code attribution (e.g. `CS12345`).
- **04: Login** — Email/Phone authentication with direct links to registration and password recovery.
- **05: Password Recovery** — OTP verification flow for password resets.
- **06: Account Status** — Real-time account verification status banner (Under Review / Approved).
- **07: Onboarding / Welcome** — Multi-page carousel introducing platform capabilities (`RecyclerView` + paging dots).
- **08: Profile & Settings** — Real-time user profile information with vector icons and logout options.
- **09: Notifications** — Live notification feed (`item_notification.xml`) powered by `NotificationAPIService`.

### 2. Customer Portal (Screens 10 – 24)
- **10: Customer Dashboard** — Dynamic product discovery, promotional banners, quick shortcuts, and live catalog.
- **11–15: Discovery & Product Details** — Search with category chips, product specifications, ratings, and seller profile cards.
- **16–21: Checkout, Payment & Order Tracking** — M-Pesa / Airtel Money payment options, live order fulfillment tracking, and digital receipt generation.
- **22–24: Reviews & Support** — Customer review submission and support ticket dispute resolution.

### 3. Seller Portal (Screens 25 – 40)
- **25: Seller Dashboard** — Real-time overview with total sales metrics, active listings count, and interactive MPAndroidChart sales trend graph.
- **26–30: Seller Profile, Verification & Product Management** — Verification document checklists, add/edit product forms with image upload/edit capabilities, stock quantity, and pricing.
- **31–40: Transaction Models & Orders** — Dual transaction models (Direct Retail vs. Wholesale to Investor), negotiation price-locking, order fulfillment tracking, and sales proceeds analytics.

### 4. Investor Portal (Screens 41 – 51)
- **41: Investor Dashboard** — Real-time total investments, active stock count, returns metrics, and interactive investment performance chart.
- **42–51: Wholesale Capital & Resale Management** — Wholesale stock acquisition, ROI calculations, holding fee balance tracking, managed inventory, and resale returns.

### 5. Sales & Growth Agent Portal (Screens 52 – 58)
- **52: Agent Dashboard** — Referral stats, total commissions earned, and quick referral link generation.
- **53–58: Referral Attribution & Commissions** — QR code sharing, referred user list tracking, qualifying transactions, commission payout requests, and withdrawal logs.

### 6. Administrator Portal (Screens 59 – 70)
- **59: Admin Dashboard** — Platform overview metrics (total users, total products) and MPAndroidChart user growth graph.
- **60–70: Platform Moderation & Analytics** — User & role management, document verification queues, market pricing reviews, transaction monitoring, payment reconciliation, disputes arbitration, system reports, and security audit logs.

---

## 🛠️ Technology Stack

### Android Frontend (`app`)
- **Language**: Java 11 / Kotlin
- **UI Architecture**: Single Activity (`MainActivity`) with Navigation Component (`nav_graph_auth.xml`) and vertical `LinearLayout` roots.
- **Design System**: Material Design Components with Chuo Sokoni palette (Navy `#0D1B2A`, Gold `#FFB300`, White `#FFFFFF`, Background `#F8F9FA`).
- **Data & Networking**: Retrofit 2, OkHttp 3 Logging Interceptor, Gson converter.
- **Local Persistence**: Room Database (`ChuoSokoniDatabase`) for offline caching.
- **Data Visualization**: MPAndroidChart (`LineChart`) for interactive sales and growth analytics.

### Backend API (`ChuoSokoniAPI`)
- **Framework**: Java 17 + Spring Boot 3
- **ORM & Database**: Spring Data JPA + PostgreSQL (3NF Normalized ERD)
- **Security**: Spring Security + JWT Authentication
- **Repository Architecture**: Derived query methods adhering strictly to Spring Data JPA relationship navigation (`findByUser_UserId`, `findByOrder_OrderId`, etc.).

---

## 🚀 Getting Started & Setup

### Prerequisites
- **Android Studio**: 2024.1.1+ (Ladybug / Jellyfish)
- **JDK**: Java 11 (for Android Frontend) & Java 17 (for Spring Boot Backend)
- **Database**: PostgreSQL 14+

### 1. Launching the Backend (`ChuoSokoniAPI`)
1. Create a PostgreSQL database named `chuosokonidb`.
2. Configure `src/main/resources/application.properties` with your PostgreSQL username and password.
3. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Backend API will run on `http://localhost:8080/`.

### 2. Building & Launching the Android App (`app`)
1. Open the project in Android Studio.
2. If running on an **Android Emulator**, the app automatically connects to `http://10.0.2.2:8080/`.
3. If running on a **Physical Phone**, update `DEV_BASE_URL` in `ApiClient.java` to your computer's local Wi-Fi IP address (e.g. `http://192.168.1.50:8080/`).
4. Build and run the app:
   ```powershell
   ./gradlew assembleDebug
   ```

---

## 📄 License

Copyright © 2026 Chuo Sokoni Team. All rights reserved.
