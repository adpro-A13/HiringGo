# AdvProg A13 HiringGo
## Deployment Link (aws dimatikan dulu karena sudah hampir kepakai semua dollarnya): https://hiringgo-a13.koyeb.app/
## Nama Anggota Kelompok A13
- Christian Raphael Heryanto (2306152323)
- Muhammad Radhiya Arshq (2306275885)
- Nobel Julian Bintang (2306202826)
- Christian Yudistira Hermawan (2306241676)
- Akmal Nabil Fikri (2306152084)
- Henry Aditya Kosasi (2306214990)

## Pembagian Tugas
1. Registrasi Akun (⛓️‍💥) (C, R) & Login  ⛓️‍💥 (C, R) [Authentication] **Bersama-sama**
2. Dashboard 🧑‍🏫/🧑‍🎓/🧑‍💻 & Mendaftar Lowongan 🧑‍🎓(C, R) **Christian Yudistira Hermawan**
3. Manajemen Akun 🧑‍💻(C, R, U, D) **Christian Raphael Heryanto**
4. Manajemen Mata Kuliah 🧑‍💻(C, R, U, D) **Muhammad Radhiya Arshq**
5. Manajemen Lowongan 🧑‍🏫 (C, R, U, D) **Henry Aditya Kosasi**
6. Manajemen Log 🧑‍🎓(C, R, U, D) **Nobel Julian Bintang**
7. Periksa Log 🧑‍🏫 (R, U) & Dashboard Honor 🧑‍🎓 **Akmal Nabil Fikri**

### Context Diagram
![](images/contextdiagram.drawio.png)
### Container Diagram
![](images/containerdiagram.drawio.png)
### Deployment Diagram
![](images/deploymentdiagram.drawio.png)
#### Future Architecture
![](images/futurearchitecture.drawio.png)
#### Risk Storming Analysis
![](images/riskstorming.drawio.png)
<br><br>
Berdasarkan identification yang kami lakukan dan consensus yang kami capai, ditemukan beberapa potensi risiko pada arsitektur sistem yang saat ini masih bersifat monolitik. Pada area pertama, yaitu Web Application yang dibangun menggunakan Next.js dan bertanggung jawab untuk menampilkan UI user, beberapa dari kami menyoroti adanya potensi celah keamanan terkait input pengguna yang tidak tervalidasi dengan baik. Risiko seperti Cross-Site Scripting (XSS) dan pengungkapan informasi internal aplikasi melalui tampilan error yang tidak dikonfigurasi secara tepat di lingkungan produksi menjadi sorotan utama. Konsensus menyatakan bahwa area ini memiliki tingkat risiko sedang (medium), dan adanya ketergantungan erat antara UI dan komponen backend juga memperbesar dampak ketika terjadi celah keamanan.
<br><br>
Pada area kedua, yaitu API Application yang menggunakan Spring Boot dan menangani logika bisnis, autentikasi, serta otorisasi, kami menilai adanya potensi risiko tinggi apabila pengelolaan otorisasi tidak dilakukan secara ketat. Ancaman seperti privilege escalation dapat terjadi, terlebih karena seluruh modul bisnis saat ini masih berada dalam satu aplikasi besar yang terintegrasi. Hal ini membuat satu celah di satu bagian bisa berdampak luas ke bagian lain. Risiko terkait autentikasi dan otorisasi yang salah konfigurasi, serta kurangnya isolasi antar domain fungsional, dipandang sebagai risiko tinggi (high).
<br><br>
Sementara itu, di area ketiga yaitu Database yang menggunakan Supabase (PostgreSQL), peserta mengidentifikasi risiko terkait kurangnya segmentasi akses serta enkripsi data yang tidak menyeluruh. Kekhawatiran muncul terhadap kemungkinan akses langsung ke data sensitif oleh pihak yang tidak berwenang, terutama karena seluruh layanan menggunakan koneksi dan skema basis data yang sama. Walaupun database ini tidak secara langsung terekspos ke publik dan hanya diakses melalui API, tim menyepakati bahwa risikonya masih berada pada tingkat sedang (medium).
<br><br>
Sebagai langkah mitigasi terhadap risiko-risiko ini, kami menyadari perlunya perubahan arsitektur secara menyeluruh dari monolitik menuju pendekatan berbasis microservices. Dengan memisahkan Web Application, layanan otentikasi, logika bisnis, dan layanan database ke dalam service-service terpisah, kami dapat menerapkan kontrol keamanan yang lebih spesifik, meningkatkan isolasi antar modul, serta membatasi dampak jika salah satu komponen mengalami kegagalan atau diserang. Selain itu, pengelolaan akses, validasi input, serta konfigurasi produksi akan lebih terfokus dan terkontrol di setiap microservice. Namun, transisi ini tentu tidak tanpa risiko baru, kompleksitas dalam implementasinya, kebutuhan untuk observabilitas dan monitoring yang lebih canggih, serta potensi latency jaringan menjadi tantangan yang perlu dipertimbangkan dan dikelola dengan matang.
<br><br>

### Individual Diagram
### Henry Aditya Kosasi
#### Component Diagram
![](images/componentdiagram-manajemenlowongan.drawio.png)
#### Code Diagram (saya satukan semua komponen di satu image, kecuali auth)
![](images/cdmanajemenlowongan.drawio.png)

### Christian Raphael Heryanto
#### Component Diagram
```mermaid
graph TD
    WebApp[Web Application] -->|HTTP Requests| AuthController[Authentication Controller]
    WebApp -->|HTTP Requests| AccMgmtController[Account Management Controller]

    AuthController -->|Uses| AuthService[Authentication Service]
    AuthController -->|Uses| JwtService[JWT Service]
    
    AccMgmtController -->|Uses| AccMgmtService[Account Management Service]
    
    AuthService -->|Calls| JwtService
    AuthService -->|Uses| UserFactory[User Factory]
    AuthService -->|Reads/Writes| UserRepo[User Repository]
    
    AccMgmtService -->|Uses| UserFactory
    AccMgmtService -->|Reads/Writes| UserRepo
    AccMgmtService -->|Uses| PasswordEncoder[Password Encoder]
    
    UserRepo -->|Stores| DB[(Database)]
    
    subgraph "Spring Security"
        JwtFilter[JWT Authentication Filter]
        SecurityConfig[Security Configuration]
        PasswordEncoder
    end
    
    JwtFilter -->|Validates| JwtService
    
    classDef component fill:#85BBF0,stroke:#5d8eb5,color:black
    classDef database fill:#48A646,stroke:#275E26,color:white
    classDef security fill:#F08585,stroke:#b55d5d,color:black
    class AuthController,AuthService,AccMgmtController,AccMgmtService,UserFactory,UserRepo,JwtService component
    class DB database
    class JwtFilter,SecurityConfig,PasswordEncoder security
```
#### Code Diagrams
##### Authentication Controller Component
```mermaid
classDiagram
    class AuthenticationController {
        -authenticationService: AuthenticationService
        -jwtService: JwtService
        +register(RegisterUserDto): ResponseEntity
        +authenticate(LoginUserDto): ResponseEntity
    }
    
    class RegisterUserDto {
        -email: String
        -password: String
        -userRole: UserRoleEnums
        -nim: String
        -name: String
        +getters()
        +setters()
    }
    
    class LoginUserDto {
        -email: String
        -password: String
        +getters()
        +setters()
    }
    
    class AuthResponseDto {
        -token: String
        -user: Map~String,Object~
        +getters()
        +setters()
    }
    
    AuthenticationController ..> RegisterUserDto: uses
    AuthenticationController ..> LoginUserDto: uses
    AuthenticationController ..> AuthResponseDto: returns
```

##### Account Management Controller Component
```mermaid
classDiagram
    class AccountManagementController {
        -accountManagementService: AccountManagementService
        +getAllUsers(): ResponseEntity
        +getUserById(String): ResponseEntity
        +createDosenAccount(DosenDto): ResponseEntity
        +createAdminAccount(AdminDto): ResponseEntity
        +changeUserRole(String, ChangeRoleDto): ResponseEntity
        +deleteUser(String): ResponseEntity
    }
    
    class DosenDto {
        -email: String
        -password: String
        -nip: String
        -fullName: String
        +getters()
        +setters()
    }
    
    class AdminDto {
        -email: String
        -password: String
        +getters()
        +setters()
    }
    
    class ChangeRoleDto {
        -newRole: UserRoleEnums
        +getters()
        +setters()
    }
    
    class UserResponseDto {
        -id: String
        -email: String
        -role: String
        -nim: String
        -nip: String
        -fullName: String
        +getters()
        +setters()
    }
    
    AccountManagementController ..> DosenDto: uses
    AccountManagementController ..> AdminDto: uses
    AccountManagementController ..> ChangeRoleDto: uses
    AccountManagementController ..> UserResponseDto: returns
```

##### Authentication Service Component
```mermaid
classDiagram
    class AuthenticationService {
        -userRepository: UserRepository
        -jwtService: JwtService
        -passwordEncoder: PasswordEncoder
        +signup(RegisterUserDto): User
        +authenticate(LoginUserDto): User
        +verifyToken(String): User
        -createUser(RegisterUserDto): User
    }
    
    class UserFactory {
        <<static>>
        +createUser(UserRoleEnums, String, String, String, String): User
    }
    
    class AuthenticationException {
        +AuthenticationException(String)
    }
    
    class UserAlreadyExistsException {
        +UserAlreadyExistsException(String)
    }
    
    AuthenticationService --> UserFactory: uses
    AuthenticationService ..> AuthenticationException: throws
    AuthenticationService ..> UserAlreadyExistsException: throws
```
##### Account Management Service Component
```mermaid
classDiagram
    class AccountManagementService {
        -userRepository: UserRepository
        -passwordEncoder: PasswordEncoder
        +getAllUsers(): List~UserResponseDto~
        +getUserById(String): UserResponseDto
        +createDosenAccount(DosenDto): UserResponseDto
        +createAdminAccount(AdminDto): UserResponseDto
        +changeUserRole(String, ChangeRoleDto): UserResponseDto
        +deleteUser(String): void
        -mapUserToDto(User): UserResponseDto
    }
    
    class UserFactory {
        <<static>>
        +createUser(UserRoleEnums, String, String, String, String): User
    }
    
    class UserNotFoundException {
        +UserNotFoundException(String)
    }
    
    class UserAlreadyExistsException {
        +UserAlreadyExistsException(String)
    }
    
    AccountManagementService --> UserFactory: uses
    AccountManagementService ..> UserNotFoundException: throws
    AccountManagementService ..> UserAlreadyExistsException: throws
```
##### JWT Service Component
```mermaid
classDiagram
    class JwtService {
        -secretKey: String
        -jwtExpiration: long
        +extractUsername(String): String
        +extractClaim(String, Function): T
        +generateToken(UserDetails): String
        +generateToken(Map, UserDetails): String
        +isTokenValid(String, UserDetails): boolean
        +isTokenExpired(String): boolean
        +extractExpiration(String): Date
        +getExpirationTime(): long
        -getSignInKey(): Key
    }
    
    class Claims {
        <<interface>>
    }
    
    JwtService ..> Claims: manipulates
```
##### User Repository
```mermaid
classDiagram
    class UserRepository {
        <<interface>>
        +findByEmail(String): Optional~User~
        +findById(UUID): Optional~User~
        +save(User): User
        +findAll(): List~User~
        +delete(User): void
    }
    
    class User {
        <<abstract>>
        -id: UUID
        -email: String
        -password: String
        +getId(): UUID
        +getEmail(): String
        +getPassword(): String
        +getAuthorities(): Collection~GrantedAuthority~
        +isAccountNonExpired(): boolean
        +isAccountNonLocked(): boolean
        +isCredentialsNonExpired(): boolean
        +isEnabled(): boolean
    }
    
    class Mahasiswa {
        -nim: String
        -fullName: String
        +getNim(): String
        +getFullName(): String
        +getAuthorities(): Collection~GrantedAuthority~
    }
    
    class Dosen {
        -nip: String
        -fullName: String
        +getNip(): String
        +getFullName(): String
        +getAuthorities(): Collection~GrantedAuthority~
    }
    
    class Admin {
        +getAuthorities(): Collection~GrantedAuthority~
    }
    
    class UserRoleEnums {
        <<enumeration>>
        ADMIN
        DOSEN
        MAHASISWA
    }
    
    User <|-- Mahasiswa: extends
    User <|-- Dosen: extends
    User <|-- Admin: extends
    User ..|> UserDetails: implements
    UserRepository ..> User: manages
    User ..> UserRoleEnums: uses
```
##### Security Configuration Component
```mermaid
classDiagram
    class SecurityConfiguration {
        -authenticationProvider: AuthenticationProvider
        -jwtAuthenticationFilter: JwtAuthenticationFilter
        +securityFilterChain(HttpSecurity): SecurityFilterChain
        +authenticationProvider(): AuthenticationProvider
        +passwordEncoder(): PasswordEncoder
        +corsConfigurationSource(): CorsConfigurationSource
    }
    
    class JwtAuthenticationFilter {
        -jwtService: JwtService
        -userDetailsService: UserDetailsService
        +doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain): void
        -isPublicEndpoint(String): boolean
    }
    
    class ApplicationConfig {
        -userRepository: UserRepository
        +userDetailsService(): UserDetailsService
        +authenticationProvider(): AuthenticationProvider
        +authenticationManager(AuthenticationConfiguration): AuthenticationManager
        +passwordEncoder(): PasswordEncoder
    }
    
    SecurityConfiguration --> JwtAuthenticationFilter: configures
    SecurityConfiguration --> ApplicationConfig: uses
    JwtAuthenticationFilter --> JwtService: uses
```

### Christian Yudistira Hermawan
##### Components diagram dashboard
![Image](https://github.com/user-attachments/assets/a70f0686-952f-4ecb-8a9d-cd9362dedc45)

##### Code diagram Dashboard Response
![Image](https://github.com/user-attachments/assets/81bff292-f1a1-439f-a0d5-6095200dfea8)

##### Code diagram Dashboard Service
![Image](https://github.com/user-attachments/assets/a89502fd-c208-47f4-ae34-be9d70834d38)

##### Code diagram Pendaftaran (Lowongan)
![Image](https://github.com/user-attachments/assets/2345c5ce-192d-4d1b-9da2-376e4a82a386)
