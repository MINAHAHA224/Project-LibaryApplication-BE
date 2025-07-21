
---

# 📖 Hệ thống Quản lý Thư viện (Library Management System)

Một hệ thống quản lý thư viện hoàn chỉnh, được xây dựng với kiến trúc hiện đại, tập trung vào việc quản lý nghiệp vụ thư viện và các tác vụ quản trị cơ sở dữ liệu nâng cao. Dự án được tái cấu trúc từ mô hình JSP truyền thống sang kiến trúc REST API mạnh mẽ, kết hợp với một giao diện người dùng linh hoạt được xây dựng bằng React.

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![Ant Design](https://img.shields.io/badge/Ant_Design-5.x-1677FF?style=for-the-badge&logo=ant-design&logoColor=white)
![MS SQL Server](https://img.shields.io/badge/MS_SQL_Server-2022-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)

---

## ✨ Giới thiệu dự án

Dự án này không chỉ là một phần mềm quản lý thư viện thông thường. Đây là một giải pháp toàn diện, được thiết kế để giải quyết các thách thức lớn nhất trong quản trị và vận hành thư viện: từ các nghiệp vụ cơ bản như quản lý sách, độc giả, mượn trả, cho đến các tác vụ quản trị hệ thống phức tạp như sao lưu và phục hồi cơ sở dữ liệu tại một thời điểm.

Bằng cách chuyển đổi sang kiến trúc **REST API (Backend)** và **Single Page Application (Frontend)**, hệ thống mang lại một trải nghiệm mượt mà, nhanh chóng và dễ dàng mở rộng trong tương lai.

---

## 🚀 Các tính năng nổi bật

### 📚 Dành cho Thủ thư (Nghiệp vụ chính)

*   **Quản lý Đầu sách & Sách con:**
    *   Thêm, sửa, xóa thông tin chi tiết của từng đầu sách (ISBN, tên, tác giả, NXB...).
    *   Cho phép sửa cả Mã ISBN.
    *   Upload và quản lý ảnh bìa cho từng đầu sách.
    *   Quản lý danh sách các cuốn sách con thuộc một đầu sách, bao gồm cả việc đánh dấu "Sách gốc" (không cho mượn).
*   **Quản lý Tác giả & Thể loại:** Giao diện CRUD đầy đủ để quản lý danh mục.
*   **Quản lý Độc giả:**
    *   Thêm, sửa, xóa thông tin độc giả.
    *   Hệ thống tự động khóa thẻ khi hết hạn hoặc khi độc giả trả sách muộn/làm mất sách.
*   **Nghiệp vụ Mượn & Trả sách:**
    *   Form lập phiếu mượn thông minh với chức năng tìm kiếm độc giả và sách có sẵn.
    *   Tự động kiểm tra các quy tắc nghiệp vụ: thẻ còn hạn, không mượn quá 3 cuốn, không mượn 2 sách cùng một đầu sách.
    *   Tích hợp chức năng trả sách ngay trên danh sách lịch sử mượn, cho phép trả từng cuốn với các trạng thái khác nhau (Tốt, Hỏng, Mất).
*   **Hoàn tác (Undo):** Hầu hết các chức năng CRUD (Đầu sách, Sách con, Độc giả,...) đều hỗ trợ Undo, cho phép thủ thư hoàn tác lại hành động vừa thực hiện.
*   **Tìm kiếm & Báo cáo:**
    *   Tìm kiếm tức thời trên tất cả các danh sách.
    *   Xuất các báo cáo nghiệp vụ (Danh mục đầu sách, Danh sách độc giả...) ra file Excel và PDF với định dạng chuyên nghiệp.

### ⚙️ Dành cho Quản trị viên (Quản trị hệ thống)

*   **Quản lý Tài khoản SQL Server:**
    *   Tạo tài khoản đăng nhập SQL Server cho Nhân viên và Độc giả mới.
    *   Tự động gán quyền hạn phù hợp cho từng loại tài khoản (Thủ thư có toàn quyền nghiệp vụ, Độc giả chỉ có quyền xem).
*   **Đổi mật khẩu:**
    *   Người dùng có thể tự đổi mật khẩu của mình.
    *   Quản trị viên có thể đổi mật khẩu cho bất kỳ tài khoản nào trong hệ thống.
*   **Sao lưu & Phục hồi Cơ sở dữ liệu:**
    *   Giao diện trực quan để thực hiện sao lưu **FULL** và **Transaction Log**.
    *   Hỗ trợ phục hồi về một bản sao lưu cụ thể (`RESTORE`).
    *   Hỗ trợ tính năng phục hồi tại một thời điểm (Point-in-Time Recovery) để cứu dữ liệu đến từng giây.
    *   Hệ thống tự động xử lý các vấn đề kỹ thuật phức tạp như đóng/mở lại Connection Pool của ứng dụng sau khi restore.

---

## 💻 Công nghệ sử dụng

### Backend (Java - Spring Boot)
*   **Ngôn ngữ:** Java 17
*   **Framework:** Spring Boot 3.x
*   **Cơ sở dữ liệu:** Microsoft SQL Server 2022 Developer Edition
*   **Kiến trúc:** REST API
*   **Xác thực:** Spring Security, JSON Web Token (JWT)
*   **Truy vấn:** Stored Procedures (T-SQL)
*   **Thư viện khác:** Lombok, HikariCP, JavaMailSender, Apache POI (Excel).

### Frontend (Javascript - React)
*   **Framework/Thư viện:** React 18
*   **Công cụ build:** Vite
*   **Thư viện UI:** Ant Design 5.x
*   **Quản lý state:** React Hooks (useState, useEffect, useContext)
*   **Gọi API:** Axios
*   **Routing:** React Router DOM
*   **Tạo PDF:** jsPDF, jsPDF-AutoTable

---

## 🔌 Kiến trúc API và các Điểm nhấn Kỹ thuật

Dự án không chỉ tập trung vào nghiệp vụ mà còn áp dụng nhiều kỹ thuật và kiến trúc nâng cao.

#### 🛡️ **Xác thực & Phân quyền**
-   Sử dụng luồng đăng nhập dựa trên JWT, hoàn toàn stateless.
-   Tận dụng cơ chế **Phân quyền dựa trên vai trò (Role-Based)** ở cả Backend (Spring Security) và Frontend (hiển thị menu/chức năng tương ứng).
-   Mỗi người dùng (thủ thư/độc giả) được ánh xạ với một **SQL Server Login** và **Database User** thực sự, với các quyền hạn được định nghĩa chặt chẽ ở tầng CSDL.

#### 🗃️ **Tương tác với CSDL qua Stored Procedures (SP)**
-   **Toàn bộ logic truy vấn và thay đổi dữ liệu** đều được đóng gói trong các Stored Procedure, đảm bảo tính an toàn, bảo mật và dễ dàng bảo trì.
-   Sử dụng các kỹ thuật tối ưu hóa truy vấn trong SP như **CTE**, **bảng tạm**, và phân tích **kế hoạch thực thi (execution plan)**.
-   Áp dụng các cơ chế phân quyền nâng cao của SQL Server như **`WITH EXECUTE AS OWNER`** và **`Ownership Chaining`** để giải quyết các bài toán về quyền hạn phức tạp một cách an toàn.

#### 🔄 **Sao lưu & Phục hồi nâng cao**
-   Hệ thống sử dụng các **Backup Device** của SQL Server để quản lý file sao lưu một cách chuyên nghiệp.
-   Tách biệt **Backup Device** cho sao lưu FULL và sao lưu Transaction Log.
-   Triển khai thành công tính năng **Point-in-Time Recovery (PITR)**, cho phép phục hồi dữ liệu về bất kỳ thời điểm nào giữa các lần sao lưu log.
-   **Xử lý xung đột kết nối:** Giải quyết triệt để vấn đề "Database is in use" bằng cách quản lý vòng đời của **Connection Pool (HikariCP)**, tự động đóng và khởi tạo lại pool sau khi restore để đảm bảo ứng dụng hoạt động liền mạch.


---

## 🚀 Hướng dẫn Cài đặt & Chạy dự án

Để chạy dự án này trên máy cục bộ, bạn cần thực hiện các bước sau cho cả Backend và Frontend.

### 📋 Yêu cầu hệ thống

-   **Java JDK:** Phiên bản 17 hoặc cao hơn.
-   **Maven:** Phiên bản 3.6 hoặc cao hơn.
-   **Node.js:** Phiên bản 18.x (LTS) hoặc cao hơn.
-   **npm:** (Đi kèm với Node.js).
-   **Microsoft SQL Server:** Phiên bản 2019 hoặc cao hơn (khuyến khích dùng bản **Developer Edition** để có đầy đủ tính năng).
-   **SQL Server Management Studio (SSMS):** Để quản lý và chạy script cho cơ sở dữ liệu.
-   Một IDE cho Java (ví dụ: IntelliJ IDEA, Eclipse) và một trình soạn thảo code cho Frontend (ví dụ: VS Code).

---

### ⚙️ Cài đặt Backend (Spring Boot)

1.  **Thiết lập Cơ sở dữ liệu:**
    *   Mở SSMS, kết nối đến SQL Server instance của bạn.
    *   Tạo một cơ sở dữ liệu mới có tên là `QUANLY_THUVIEN`.
    *   Chạy file script SQL `database_setup.sql` (bạn cần tạo file này bằng cách export toàn bộ schema và data) để tạo tất cả các bảng và dữ liệu mẫu.
    *   Chạy file script SQL `stored_procedures_setup.sql` (bạn cần tạo file này chứa tất cả các SP) để tạo các Stored Procedure cần thiết.
    *   Đảm bảo dịch vụ **SQL Server Agent** đang ở trạng thái "Running" để các Job tự động có thể hoạt động.

2.  **Tạo Backup Device và Thư mục:**
    *   Trên ổ đĩa của bạn, tạo một thư mục để chứa file backup, ví dụ: `D:\SQL_Backups`.
    *   Mở SSMS và chạy các lệnh sau để tạo backup device:
        ```sql
        USE master;
        GO
        -- Tạo device cho backup FULL
        EXEC sp_addumpdevice 'disk', 'ThuVienBackupDevice', 'D:\SQL_Backups\ThuVienApp.bak';
        -- Tạo device cho backup LOG
        EXEC sp_addumpdevice 'disk', 'ThuVienLogDevice', 'D:\SQL_Backups\ThuVienApp_Log.bak';
        GO
        ```

3.  **Cấu hình ứng dụng:**
    *   Mở file `src/main/resources/application.properties`.
    *   Chỉnh sửa các thông tin kết nối cho phù hợp với môi trường của bạn:
        ```properties
        # Cấu hình kết nối chính của ứng dụng
        spring.datasource.url=jdbc:sqlserver://[YourServerName]:[Port];databaseName=QUANLY_THUVIEN;...
        spring.datasource.username=sa
        spring.datasource.password=[Your_SA_Password]

        # Cấu hình kết nối đến master DB (dùng cho restore)
        master.datasource.url=jdbc:sqlserver://[YourServerName]:[Port];databaseName=master;...
        master.datasource.username=[YourAdminLogin] # Ví dụ: ThuVienAdmin
        master.datasource.password=[YourAdminPassword]
        ```

4.  **Chạy Backend:**
    *   Mở dự án bằng IDE của bạn.
    *   Chờ Maven tải về các dependency.
    *   Chạy class chính `ThuVienAplicationApplication.java`.
    *   Server Backend sẽ khởi động trên cổng `8080`.

---

### ⚛️ Cài đặt Frontend (React)

1.  **Điều hướng đến thư mục Frontend:**
    *   Mở Terminal hoặc Command Prompt.
    *   Dùng lệnh `cd` để di chuyển vào thư mục gốc của project Frontend (ví dụ: `cd thu-vien-fe`).

2.  **Cài đặt các gói phụ thuộc:**
    *   Chạy lệnh sau để tải về tất cả các thư viện cần thiết đã được định nghĩa trong `package.json`:
        ```bash
        npm install
        ```

3.  **Cấu hình API Endpoint (nếu cần):**
    *   File `src/services/api.js` đã được cấu hình để kết nối đến `http://localhost:8080/api`. Nếu Backend của bạn chạy ở một địa chỉ hoặc cổng khác, hãy cập nhật lại `baseURL` trong file này.

4.  **Chạy Frontend:**
    *   Sau khi cài đặt xong, chạy lệnh sau để khởi động development server:
        ```bash
        npm run dev
        ```
    *   Terminal sẽ hiển thị một URL, thường là `http://localhost:5173`.
    *   Mở trình duyệt web của bạn và truy cập vào URL đó để bắt đầu sử dụng ứng dụng.

---

### 👤 Tài khoản mặc định

*   **Quản trị viên hệ thống:**
    *   Username: `sa`
    *   Password: (Mật khẩu `sa` của bạn)
*   **Thủ thư (ví dụ):**
    *   Username: `NVHAADMIN`
    *   Password: (Mật khẩu bạn đã tạo)

Sau khi hoàn thành các bước trên, bạn sẽ có một hệ thống hoàn chỉnh đang chạy trên máy cục bộ của mình.
---
---

## 🛠️ Tính năng Quản trị CSDL Nâng cao: Sao lưu & Phục hồi

Điểm nhấn đặc biệt của dự án là việc tích hợp sâu các nghiệp vụ quản trị cơ sở dữ liệu (DBA) vào một giao diện web trực quan, cho phép người quản trị thực hiện các tác vụ phức tạp một cách an toàn và dễ dàng.

### 🧬 Kiến trúc & Thiết kế

-   **Tách biệt quyền hạn:** Hệ thống sử dụng một tài khoản chuyên dụng (`ThuVienAdmin`) với các quyền hạn được cấp phát cẩn thận (`dbcreator`, `diskadmin`, `ALTER ANY DATABASE`) trong `master` DB để thực hiện các tác vụ quản trị, thay vì dùng tài khoản `sa` có quyền lực tối cao.
-   **Sử dụng Backup Device:** Mọi hoạt động sao lưu đều được thực hiện thông qua các **Backup Device** logic (`ThuVienBackupDevice`, `ThuVienLogDevice`) của SQL Server, giúp trừu tượng hóa đường dẫn vật lý, tăng tính linh hoạt và bảo mật.
-   **Đóng gói logic trong Stored Procedure:** Các chuỗi lệnh SQL phức tạp được đóng gói trong Stored Procedure, đảm bảo tính nhất quán và dễ dàng quản lý quyền thực thi. Các SP được thiết kế với mệnh đề `WITH EXECUTE AS OWNER` để vượt qua các rào cản về quyền hạn một cách an toàn.
-   **Xử lý Connection Pool thông minh:** Hệ thống có khả năng xử lý vấn đề "database is in use" kinh điển bằng cách chủ động **đóng và khởi tạo lại `DataSource` bean (HikariCP pool)** của ứng dụng Spring Boot sau mỗi lần phục hồi, đảm bảo ứng dụng hoạt động liền mạch mà không cần khởi động lại toàn bộ server.

### ⚙️ Các API nổi bật

| Phương thức | Endpoint | Chức năng & Điểm nhấn kỹ thuật |
| :--- | :--- | :--- |
| `POST` | `/api/backups` | **Sao lưu FULL:** <br> - Thực thi SP `SP_Backup_database`.<br> - Hỗ trợ tùy chọn `WITH FORMAT` để xóa sạch device và bắt đầu lại chuỗi backup, hoặc `WITH NOINIT` để ghi nối tiếp. <br> - Tên người dùng thực hiện (`NVHa`, `NVThai`...) được ghi lại chính xác vào lịch sử `msdb` thay vì `sa`.|
| `POST` | `/api/backups/log` | **Sao lưu Transaction Log:** <br> - Thực thi SP `SP_BackupTransactionLog`.<br> - Sao lưu vào một **device riêng** (`ThuVienLogDevice`) để duy trì một chuỗi log (log chain) sạch và không bị phá vỡ, đây là điều kiện tiên quyết cho Point-in-Time Recovery. |
| `POST` | `/api/backups/restore` | **Phục hồi Cơ sở dữ liệu:** <br> - Đây là API "thông minh" có khả năng xử lý cả hai kịch bản.<br> 1. **Restore thông thường:** Gọi SP `SP_RestoreDatabase_normal`, sử dụng `WITH FILE = [position]` để chọn đúng bản sao lưu trong device.<br> 2. **Point-in-Time Recovery (PITR):** Khi người dùng chọn chế độ này, API sẽ tự động thực hiện một **tail-log backup** (sao lưu log cuối cùng) để bắt trọn những giao dịch cuối cùng, sau đó mới thực hiện chuỗi `RESTORE DATABASE ... WITH NORECOVERY` và `RESTORE LOG ... WITH STOPAT = [thời_điểm]`. |
| `POST` | `/backups/configure-log-job`| **Cấu hình Job tự động:** <br> - Gọi SP `SP_ConfigureBackupLogJob` để tương tác với **SQL Server Agent**.<br> - Cho phép người dùng từ giao diện web có thể **Bật/Tắt** và **thiết lập tần suất** (số phút) cho Job tự động sao lưu Transaction Log. |

## 🔌 Danh sách API của dự án

Hệ thống cung cấp một bộ RESTful API đầy đủ để tương tác giữa Frontend và Backend. Tất cả các API (trừ API đăng nhập và tra cứu công khai) đều yêu cầu xác thực bằng JWT Token trong Header `Authorization`.

### 🔑 API Xác thực & Quản trị (`/api/auth`)

| Phương thức | Endpoint | Chức năng |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | **(Public)** Đăng nhập vào hệ thống. Nhận `username` và `password`, trả về JWT Token và thông tin người dùng. |
| `POST` | `/api/auth/change-password` | **(Protected)** Cho phép người dùng đang đăng nhập tự đổi mật khẩu của mình. Yêu cầu `oldPassword` và `newPassword`. |
| `POST` | `/api/auth/forgot-password` | **(Public)** Xử lý yêu cầu quên mật khẩu. Nhận `loginName`, tự động tạo mật khẩu mới và gửi qua email. |

### 📚 API Quản lý Đầu sách (`/api/book-titles`)

| Phương thức | Endpoint | Chức năng |
| :--- | :--- | :--- |
| `GET` | `/api/book-titles` | Lấy danh sách tất cả các đầu sách để hiển thị trên bảng chính. |
| `GET` | `/api/book-titles/{isbn}` | Lấy thông tin chi tiết của một đầu sách dựa trên ISBN. |
| `POST` | `/api/book-titles` | Thêm một đầu sách mới (gửi `multipart/form-data` chứa cả dữ liệu JSON và file ảnh). |
| `PUT` | `/api/book-titles/{isbn}` | Cập nhật thông tin của một đầu sách đã có (gửi `multipart/form-data`). |
| `DELETE` | `/api/book-titles/{isbn}` | Xóa một đầu sách và các liên kết liên quan. |
| `POST` | `/api/book-titles/undo` | Hoàn tác lại hành động Thêm/Sửa/Xóa đầu sách cuối cùng. |
| `POST` | `/api/book-titles/{isbn}/upload-image`| Cập nhật ảnh bìa riêng cho một đầu sách đã tồn tại. |

#### API Sách con (thuộc Đầu sách) (`/api/book-titles/{isbn}/books`)

| Phương thức | Endpoint | Chức năng |
| :--- | :--- | :--- |
| `GET` | `/{isbn}/books` | Lấy danh sách tất cả các sách con thuộc một đầu sách. |
| `POST`| `/{isbn}/books` | Thêm một sách con mới cho một đầu sách. |
| `PUT` | `/{isbn}/books/{bookId}` | Cập nhật thông tin một sách con. |
| `DELETE`| `/{isbn}/books/{bookId}` | Xóa một sách con. |
| `POST`| `/books/undo` | Hoàn tác lại hành động Thêm/Sửa/Xóa sách con cuối cùng. |

#### API Dữ liệu phụ cho Form Đầu sách (`/api/book-titles/form-data`)

| Phương thức | Endpoint | Chức năng |
| :--- | :--- | :--- |
| `GET` | `/form-data/authors` | Lấy danh sách tác giả để điền vào dropdown. |
| `GET` | `/form-data/languages` | Lấy danh sách ngôn ngữ. |
| `GET` | `/form-data/book-types` | Lấy danh sách thể loại. |
| `GET` | `/books/drawers` | Lấy danh sách ngăn tủ. |


### 👥 API Quản lý Độc giả (`/api/readers`) & Nhân viên (`/api/staffs`)

*(Các API cho Nhân viên có cấu trúc tương tự)*

| Phương thức | Endpoint | Chức năng |
| :--- | :--- | :--- |
| `GET` | `/api/readers` | Lấy danh sách tất cả độc giả. |
| `POST` | `/api/readers` | Thêm một độc giả mới. |
| `PUT` | `/api/readers/{id}` | Cập nhật thông tin độc giả có mã `id`. |
| `DELETE`| `/api/readers/{id}` | Xóa độc giả có mã `id`. |
| `POST`| `/api/readers/undo` | Hoàn tác hành động Thêm/Sửa/Xóa độc giả cuối cùng. |


### 📦 API Quản lý Nghiệp vụ Mượn/Trả (`/api/rentals`)

| Phương thức | Endpoint | Chức năng |
| :--- | :--- | :--- |
| `GET` | `/api/rentals/new-ticket-id` | Lấy mã phiếu mượn mới để hiển thị trên form. |
| `GET` | `/api/rentals/active-readers`| Lấy danh sách các độc giả hợp lệ (thẻ còn hạn, đang hoạt động) để mượn sách. |
| `GET` | `/api/rentals/available-books`| Lấy danh sách các sách có sẵn trong kho (chưa được mượn, chưa thanh lý, không phải sách gốc). |
| `GET` | `/api/rentals` | Lấy lịch sử tất cả các phiếu mượn. |
| `GET` | `/api/rentals/{id}` | Lấy thông tin chi tiết đầy đủ của một phiếu mượn. |
| `POST`| `/api/rentals` | Lập một phiếu mượn mới. |
| `POST`| `/api/rentals/return-book` | Thực hiện trả một cuốn sách cụ thể trong một phiếu mượn. |

### 🗄️ API Quản lý Tài khoản & Mật khẩu

| Phương thức | Endpoint | Chức năng |
| :--- | :--- | :--- |
| `GET` | `/api/accounts/staffs-no-login`| Lấy danh sách nhân viên chưa có tài khoản SQL Server. |
| `GET` | `/api/accounts/readers-no-login`| Lấy danh sách độc giả chưa có tài khoản SQL Server. |
| `POST`| `/api/accounts/create` | Tạo một tài khoản đăng nhập SQL Server và Database User mới. |
| `GET` | `/api/passwords/created-logins`| Lấy danh sách tất cả tài khoản đã được tạo để đổi mật khẩu. |
| `POST`| `/api/passwords/reset-for-user` | (Quản trị viên) Đổi mật khẩu cho một tài khoản bất kỳ mà không cần mật khẩu cũ. |

### 💾 API Sao lưu & Phục hồi (`/api/backups`)

| Phương thức | Endpoint | Chức năng |
| :--- | :--- | :--- |
| `GET` | `/api/backups/databases`| Lấy danh sách các cơ sở dữ liệu có thể sao lưu. |
| `GET` | `/api/backups/history` | Lấy lịch sử sao lưu của một cơ sở dữ liệu cụ thể. |
| `POST`| `/api/backups` | Thực hiện sao lưu FULL. |
| `POST`| `/api/backups/log` | Thực hiện sao lưu Transaction Log. |
| `POST`| `/api/backups/restore` | Thực hiện phục hồi (cả Normal Restore và Point-in-Time Recovery). |

### 🌐 API Công khai (Dành cho Độc giả tra cứu) (`/api/public`)
| Phương thức | Endpoint | Chức năng |
| :--- | :--- | :--- |
| `GET` | `/api/public/book-titles` | **(Public)** Lấy danh sách tất cả đầu sách cho trang tra cứu. |
| `GET` | `/api/public/book-titles/{isbn}/books` | **(Public)** Lấy danh sách sách con của một đầu sách. |

---

### 🖼️ API Phục vụ File ảnh (`/api/files`)

| Phương thức | Endpoint | Chức năng |
| :--- | :--- | :--- |
| `GET` | `/api/files/dausach/{filename}` | **(Protected)** Trả về file ảnh bìa sách. Yêu cầu xác thực để xem. |